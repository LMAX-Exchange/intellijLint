package intellijLint;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OptionalNullInspection extends BaseJavaLocalInspectionTool {
    private static final Logger LOG = Logger.getInstance("#intellijLint.OptionalNullInspection");

    private final static Set<String> OPTIONAL_TYPES = new HashSet<>(Arrays.asList(
            "java.util.OptionalInt",
            "java.util.OptionalLong",
            "java.util.OptionalDouble",
            CommonClassNames.JAVA_UTIL_OPTIONAL));

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Assigning null to optional";
    }

    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Assigning null to optional";

    private ReplaceWithEmptyQuickFix quickFix = new ReplaceWithEmptyQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);

                final PsiType assignmentTargetType = expression.getLExpression().getType();
                boolean isOptional = false;
                if (assignmentTargetType != null)
                {
                    String typeText = assignmentTargetType.getCanonicalText();
                    final int angleBracketIndex = typeText.indexOf('<');
                    if (angleBracketIndex != -1){
                        typeText = typeText.substring(0, angleBracketIndex);
                    }

                    for (String o : OPTIONAL_TYPES)
                    {
                        if (o.equals(typeText))
                        {
                            isOptional = true;
                            break;
                        }
                    }
                }

                if (isOptional)
                {
                    final PsiExpression rExpression = expression.getRExpression();
                    if (rExpression != null && rExpression instanceof PsiLiteralExpression && "null".equals(rExpression.getText()))
                    {
                        holder.registerProblem(expression, DESCRIPTION_TEMPLATE, quickFix);
                    }
                }
            }
        };
    }

    private static class ReplaceWithEmptyQuickFix implements LocalQuickFix {
        @NotNull
        @Override
        public String getName() {
            return "Replace null assignment with optional.empty()";
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
            try {
                final PsiExpression assignmentValue = ((PsiAssignmentExpression) problemDescriptor.getPsiElement()).getRExpression();
                if (assignmentValue == null)
                {
                    return;
                }

                final PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

                PsiMethodCallExpression emptyCall = (PsiMethodCallExpression) factory.createExpressionFromText("Optional.empty()", null);

                assignmentValue.replace(emptyCall);
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }
    }

}
