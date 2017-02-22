package com.lmax.intellijLint.Units;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.SpecialAnnotationsUtil;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import com.intellij.psi.*;
import com.siyeh.ig.ui.ExternalizableStringSet;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

@SuppressWarnings("WeakerAccess") //Needs to be public as is used in plugin.
@Storage("com.lmax.intellijLint.units.xml")
public class UnitsInspection extends BaseJavaLocalInspectionTool implements PersistentStateComponent<UnitsInspection.State> {
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

    public static final String DESCRIPTION_TEMPLATE = "Assigning %s to variable of type %s";
    public static final String BINARY_EXPRESSION_DESCRIPTION_TEMPLATE = "Left side of expression is %s and right side is %s";
    public static final String RETURNING_DESCRIPTION_TEMPLATE = "Returning %s when expecting %s";

    @SuppressWarnings("PublicField")
    public final List<String> subTypeAnnotations = new ArrayList<>();

    private PsiMethod walkUpToWrappingMethod(PsiElement element)
    {
        if (element == null)
        {
            return null;
        }

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

                String declaredSubTypeFQN = SubType.getSubType(expression.getLExpression()).getSubtypeFQN();
                inspect(expression.getRExpression(), declaredSubTypeFQN, holder);
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);

                final PsiExpression initializer = field.getInitializer();

                final String declaredSubTypeFQN = SubType.getSubType(field).getSubtypeFQN();
                inspect(initializer, declaredSubTypeFQN, holder);
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);

                final PsiExpression initializer = variable.getInitializer();

                final String declaredSubTypeFQN = SubType.getSubType(variable).getSubtypeFQN();
                inspect(initializer, declaredSubTypeFQN, holder);
            }

            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);

                final PsiExpression returnValue = statement.getReturnValue();

                PsiMethod psiMethod = walkUpToWrappingMethod(returnValue);
                final String declaredSubTypeFQN = SubType.getSubType(psiMethod).getSubtypeFQN();

                inspect(returnValue, declaredSubTypeFQN, holder, RETURNING_DESCRIPTION_TEMPLATE);
            }

            @Override
            public void visitBinaryExpression(PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);

                PsiExpression rOperand = expression.getROperand();
                if (rOperand == null)
                {
                    return;
                }

                inspect(expression, SubType.getSubType(expression.getLOperand()).getSubtypeFQN(), SubType.getSubType(rOperand).getSubtypeFQN(), holder, BINARY_EXPRESSION_DESCRIPTION_TEMPLATE);
            }

            @Override
            public void visitConditionalExpression(PsiConditionalExpression expression) {
                super.visitConditionalExpression(expression);

                PsiExpression elseExpression = expression.getElseExpression();
                if (elseExpression == null)
                {
                    return;
                }

                inspect(expression, SubType.getSubType(expression.getThenExpression()).getSubtypeFQN(), SubType.getSubType(elseExpression).getSubtypeFQN(), holder, BINARY_EXPRESSION_DESCRIPTION_TEMPLATE);
            }
        };
    }

    private void inspect(PsiExpression initializer, String declaredSubTypeFQN, @NotNull ProblemsHolder holder) {
        inspect(initializer, declaredSubTypeFQN, holder, DESCRIPTION_TEMPLATE);
    }

    private void inspect(@Nullable PsiExpression initializer, @Nullable String declaredSubTypeFQN, @NotNull ProblemsHolder holder, String descriptionTemplate) {
        if (initializer != null)
        {
            String subTypeFQN = SubType.getSubType(initializer).getSubtypeFQN();
            inspect(initializer, subTypeFQN, declaredSubTypeFQN, holder, descriptionTemplate);
        }
    }

    private void inspect(PsiExpression potentiallyProblematicExpression, String leftSubtypeFQN, String rightSubTypeFQN, @NotNull ProblemsHolder holder, String descriptionTemplate) {
        if (!Objects.equals(leftSubtypeFQN, rightSubTypeFQN)) {
            String description = String.format(descriptionTemplate, leftSubtypeFQN, rightSubTypeFQN);
            holder.registerProblem(potentiallyProblematicExpression, description);
        }
    }

    public JComponent createOptionsPanel() {
        return SpecialAnnotationsUtil.createSpecialAnnotationsListControl(
                SubType.subTypeAnnotations, "Sub Type annotations");
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
        SubType.setAnnotations(state.subTypeAnnotations);
    }

    public class State {
        public State()
        {
            subTypeAnnotations = new ExternalizableStringSet("org.checkerframework.framework.qual.SubtypeOf");
        }

        public Set<String> subTypeAnnotations;
    }
}
