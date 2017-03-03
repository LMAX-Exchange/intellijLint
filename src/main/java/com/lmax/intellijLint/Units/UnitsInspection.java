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
    public static final String FAILED_TO_RESOLVE = "Failed to resolve subtype on %s due to %s";

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

                final PsiExpression initalizerExpr = expression.getRExpression();
                if (initalizerExpr == null)
                {
                    return;
                }

                SubType declared = SubType.getSubType(expression.getLExpression());
                SubType assigned = SubType.getSubType(initalizerExpr);
                inspect(assigned, declared, holder);
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);

                final PsiExpression initializerExpr = field.getInitializer();
                if (initializerExpr == null)
                {
                    return;
                }

                final SubType initializer = SubType.getSubType(initializerExpr);
                final SubType declared = SubType.getSubType(field);
                inspect(initializer, declared, holder);
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);

                final PsiExpression initializerExpression = variable.getInitializer();

                if (initializerExpression == null)
                {
                    return;
                }

                final SubType initializer = SubType.getSubType(initializerExpression);

                final SubType declared = SubType.getSubType(variable);
                inspect(initializer, declared, holder);
            }

            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);

                final PsiExpression returnValueExpr = statement.getReturnValue();

                if (returnValueExpr == null)
                {
                    return; // void return, won't have annotation.
                }

                final SubType returnValue = SubType.getSubType(returnValueExpr);

                PsiMethod psiMethod = walkUpToWrappingMethod(returnValueExpr);
                final SubType declared = SubType.getSubType(psiMethod);

                inspect(returnValue, declared, holder, RETURNING_DESCRIPTION_TEMPLATE);
            }

            @Override
            public void visitBinaryExpression(PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);

                PsiExpression rOperand = expression.getROperand();
                if (rOperand == null)
                {
                    return;
                }

                inspect(expression, SubType.getSubType(expression.getLOperand()), SubType.getSubType(rOperand), holder, BINARY_EXPRESSION_DESCRIPTION_TEMPLATE);
            }

            @Override
            public void visitConditionalExpression(PsiConditionalExpression expression) {
                super.visitConditionalExpression(expression);

                PsiExpression elseExpression = expression.getElseExpression();
                if (elseExpression == null)
                {
                    return;
                }

                final PsiExpression thenExpression = expression.getThenExpression();
                if (thenExpression == null)
                {
                    throw new IllegalStateException("No then expr on conditional?!");
                }
                inspect(expression, SubType.getSubType(thenExpression), SubType.getSubType(elseExpression), holder, BINARY_EXPRESSION_DESCRIPTION_TEMPLATE);
            }
        };
    }

    private void inspect(SubType potentiallyProblematic, SubType checkAgainst, @NotNull ProblemsHolder holder)
    {
        inspect(potentiallyProblematic, checkAgainst, holder, DESCRIPTION_TEMPLATE);
    }

    private void inspect(SubType potentiallyProblematic, SubType checkAgainst, @NotNull ProblemsHolder holder, String descriptionTemplate)
    {
        inspect(potentiallyProblematic.getPsiElement(), potentiallyProblematic, checkAgainst, holder, descriptionTemplate);
    }

    private void inspect(PsiElement element, SubType left, SubType right, @NotNull ProblemsHolder holder, String descriptionTemplate)
    {
        reportResolutionFailure(left, holder);
        reportResolutionFailure(right, holder);

        if (!Objects.equals(left, right)) {
            if (isIgnoredResolutionFailureReason(left) || isIgnoredResolutionFailureReason(right))
            {
                //Will get caught by visitConditionalExpression
                return;
            }
            final String description = String.format(descriptionTemplate, left.getSubtypeFQN(), right.getSubtypeFQN());
            holder.registerProblem(element, description);
        }
    }

    private void reportResolutionFailure(SubType subType, @NotNull ProblemsHolder holder) {
        if (subType.getFailureReason() != ResolutionFailureReason.NONE && !isIgnoredResolutionFailureReason(subType))
        {
            final String description = String.format(FAILED_TO_RESOLVE, subType.getPsiElement(), subType.getFailureReason());
            holder.registerProblem(subType.getPsiElement(), description);
        }
    }

    private boolean isIgnoredResolutionFailureReason(SubType subType) {
        final ResolutionFailureReason faliureReason = subType.getFailureReason();
        return faliureReason == ResolutionFailureReason.MISMATCHED_CONDITIONAL ||
                faliureReason == ResolutionFailureReason.MISMATCHED_BINARY_EXPRESSION;
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
