package com.lmax.intellijLint;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess") //Needs to be public as is used in plugin.
public class UnitsInspection extends BaseJavaLocalInspectionTool {
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
    private static final String DESCRIPTION_TEMPLATE = "";

    private static final String[] stAnnos = new String[]{"org.checkerframework.framework.qual.SubtypeOf"};

    final WeakHashMap<String, Boolean> subTypeCache = new WeakHashMap<>();

    boolean isSubType(PsiAnnotation annotation)
    {
        if (annotation.getQualifiedName() == null)
        {
            LOG.warn("Couldn't get qualified name for annotation: " + annotation.getText());
            return false;
        }
        return subTypeCache.computeIfAbsent(annotation.getQualifiedName(),
                (x) -> annotationClassHasSubtypeAnnotation(resolveAnnotation(annotation)));
    }

    boolean isSubType(PsiModifierList modifiers)
    {
        if (modifiers == null)
        {
            return false;
        }

        for (PsiAnnotation annotation : modifiers.getAnnotations())
        {
            if(isSubType(annotation))
            {
                return true;
            }
        }
        return false;
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

    @Nullable String getSubTypeFQN(PsiAnnotation[] annotations)
    {
        for (PsiAnnotation annotation : annotations)
        {
            if (isSubType(annotation))
            {
                return annotation.getQualifiedName();
            }
        }
        return null;
    }

    private boolean annotationClassHasSubtypeAnnotation(@Nullable PsiClass aClass) {
        if (aClass == null)
        {
            return false;
        }

        final PsiModifierList modifierList = aClass.getModifierList();

        return modifierList != null && modifierListContainsAnnotation(modifierList, stAnnos);
    }

    private boolean modifierListContainsAnnotation(PsiModifierList modifiers, String... fqAnnotationName)
    {
        final HashSet<String> annotationsNameSet = new HashSet<>(Arrays.asList(fqAnnotationName));
        //List is probably short enough that stream overhead is non-negligible.
        for (PsiAnnotation annotation: modifiers.getAnnotations()) {
            if (annotationsNameSet.contains(annotation.getQualifiedName())){
                return true;
            }
        }
        return false;
    }

    private PsiClass resolveAnnotation(PsiAnnotation annotation) {
        final String qualifiedName = annotation.getQualifiedName();
        if (qualifiedName == null)
        {
            return null;
        }
        return JavaPsiFacade.getInstance(annotation.getProject())
                .findClass(qualifiedName, annotation.getResolveScope());
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);

                final PsiType assignmentTargetType = expression.getLExpression().getType();
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);

                final PsiExpression initializer = field.getInitializer();

                final String declaredSubTypeFQN = getSubTypeFQN(field.getModifierList());
                if (declaredSubTypeFQN != null && initializer != null)
                {
                    final PsiType initializedType = initializer.getType();
                    if (initializedType != null && !Objects.equals(getSubTypeFQN(initializedType.getAnnotations()), declaredSubTypeFQN))
                    {
                        holder.registerProblem(initializer, DESCRIPTION_TEMPLATE, null); //TODO: details.
                    }
                }
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);

                final PsiExpression initializer = variable.getInitializer();

                final String declaredSubTypeFQN = getSubTypeFQN(variable.getModifierList());
                if (declaredSubTypeFQN != null && initializer != null)
                {
                    final PsiType initializedType = initializer.getType();
                    if (initializedType != null && !Objects.equals(getSubTypeFQN(initializedType.getAnnotations()), declaredSubTypeFQN))
                    {
                        holder.registerProblem(initializer, DESCRIPTION_TEMPLATE, null); //TODO: details.
                    }
                }
            }
        };
    }
}
