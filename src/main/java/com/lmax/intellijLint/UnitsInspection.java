package com.lmax.intellijLint;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.SpecialAnnotationsUtil;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.siyeh.ig.ui.ExternalizableStringSet;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

@SuppressWarnings("WeakerAccess") //Needs to be public as is used in plugin.
@Storage("com.lmax.intellijLint.units.xml")
public class UnitsInspection extends BaseJavaLocalInspectionTool implements PersistentStateComponent<UnitsInspection.State> {
    private static final Logger LOG = Logger.getInstance("#intellijLint.UnitsInspection");

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Mismatched units";
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "LMAX";
    }

    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Assigning %s to variable of type %s";

    @SuppressWarnings("PublicField")
    public final List<String> subTypeAnnotations = new ArrayList<>();

    private final static WeakHashMap<String, Boolean> subTypeCache = new WeakHashMap<>();

    boolean isSubType(PsiAnnotation annotation)
    {
        if (annotation.getQualifiedName() == null)
        {
            LOG.warn("Couldn't get qualified name for annotation: " + annotation.getText());
            return false;
        }
        return subTypeCache.computeIfAbsent(annotation.getQualifiedName(),
                (x) -> annotationClassHasSubtypeAnnotation(resolve(annotation)));
    }

    @Nullable String getSubTypeFQN(PsiModifierList modifierList)
    {
        if (modifierList == null)
        {
            return null;
        }

        for (PsiAnnotation annotation : modifierList.getAnnotations())
        {
            if(isSubType(annotation))
            {
                return annotation.getQualifiedName();
            }
        }
        return null;
    }

    @Nullable String getSubTypeFQN(PsiExpression expression)
    {
        if (expression instanceof PsiCall)
        {
            PsiMethod psiMethod = ((PsiCall) expression).resolveMethod();
            if (psiMethod == null)
            {
                return null;
            }
            return getSubTypeFQN(psiMethod.getModifierList());
        }

        //TODO more cases

        return null;
    }

    private boolean annotationClassHasSubtypeAnnotation(@Nullable PsiClass aClass) {
        if (aClass == null)
        {
            return false;
        }

        final PsiModifierList modifierList = aClass.getModifierList();

        return modifierList != null && AnnotationUtil.isAnnotated(aClass, subTypeAnnotations);
    }

    private PsiClass resolve(PsiAnnotation annotation) {
        final String qualifiedName = annotation.getQualifiedName();
        if (qualifiedName == null)
        {
            return null;
        }
        return JavaPsiFacade.getInstance(annotation.getProject())
                .findClass(qualifiedName, annotation.getResolveScope());
    }

    private PsiMethod walkUpToWrappingMethod(PsiElement element)
    {
        PsiElement parent = element.getParent();
        if (parent == null)
        {
            return null;
        }

        if (parent instanceof PsiMethod) {
            return (PsiMethod) parent;
        } else {
            return walkUpToWrappingMethod(parent);
        }
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);

                String declaredSubTypeFQN = getSubTypeFQN(expression.getLExpression());
                inspect(expression.getRExpression(), declaredSubTypeFQN, holder);
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);

                final PsiExpression initializer = field.getInitializer();

                final String declaredSubTypeFQN = getSubTypeFQN(field.getModifierList());
                inspect(initializer, declaredSubTypeFQN, holder);
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);

                final PsiExpression initializer = variable.getInitializer();

                final String declaredSubTypeFQN = getSubTypeFQN(variable.getModifierList());
                inspect(initializer, declaredSubTypeFQN, holder);
            }

            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);

                final PsiExpression returnValue = statement.getReturnValue();

                PsiMethod psiMethod = walkUpToWrappingMethod(returnValue);
                final String declaredSubTypeFQN = getSubTypeFQN(psiMethod != null ? psiMethod.getModifierList() : null);

                inspect(returnValue, declaredSubTypeFQN, holder, "Returning %s when expecting %s");
            }
        };
    }

    private void inspect(PsiExpression initializer, String declaredSubTypeFQN, @NotNull ProblemsHolder holder) {
        inspect(initializer, declaredSubTypeFQN, holder, DESCRIPTION_TEMPLATE);
    }

    private void inspect(PsiExpression initializer, String declaredSubTypeFQN, @NotNull ProblemsHolder holder, String descriptionTemplate) {
        if (initializer != null)
        {
            String subTypeFQN = getSubTypeFQN(initializer);
            if (!Objects.equals(subTypeFQN, declaredSubTypeFQN))
            {
                String description = String.format(descriptionTemplate, subTypeFQN, declaredSubTypeFQN);
                holder.registerProblem(initializer, description);
            }
        }
    }

    public JComponent createOptionsPanel() {
        return SpecialAnnotationsUtil.createSpecialAnnotationsListControl(
                subTypeAnnotations, "Sub Type annotations");
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    @Nullable
    @Override
    public UnitsInspection.State getState() {
        State state = new State();
        state.subTypeAnnotations = new HashSet<>(this.subTypeAnnotations);
        return state;
    }

    @Override
    public void loadState(UnitsInspection.State state) {
        this.subTypeAnnotations.addAll(state.subTypeAnnotations);
    }

    public class State {
        public State()
        {
            subTypeAnnotations = new ExternalizableStringSet("org.checkerframework.framework.qual.SubtypeOf");
        }

        public Set<String> subTypeAnnotations;
    }
}
