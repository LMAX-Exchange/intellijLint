/*
 *    Copyright 2017 LMAX Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lmax.intellijLint;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess") //Needs to be public as is used in plugin.
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

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Java";
    }

    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Assigning null to optional";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);

                final PsiType assignmentTargetType = expression.getLExpression().getType();
                String optionalTypeString = getOptionalTypeString(assignmentTargetType);

                if (!optionalTypeString.isEmpty())
                {
                    final PsiExpression assignmentValue = expression.getRExpression();
                    if (isLiteralNull(assignmentValue))
                    {
                        holder.registerProblem(assignmentValue, DESCRIPTION_TEMPLATE, new ReplaceWithEmptyQuickFix(optionalTypeString));
                    }
                }
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);

                final PsiExpression initializer = field.getInitializer();
                final String optionalTypeString = getOptionalTypeString(field.getType());
                if (!optionalTypeString.isEmpty() && isLiteralNull(initializer))
                {
                    holder.registerProblem(initializer, DESCRIPTION_TEMPLATE, new ReplaceWithEmptyQuickFix(optionalTypeString));
                }
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);

                final PsiExpression initializer = variable.getInitializer();
                final String optionalTypeString = getOptionalTypeString(variable.getType());
                if (!optionalTypeString.isEmpty() && isLiteralNull(initializer))
                {
                    holder.registerProblem(initializer, DESCRIPTION_TEMPLATE, new ReplaceWithEmptyQuickFix(optionalTypeString));
                }
            }
        };
    }

    private static boolean isLiteralNull(PsiExpression expression) {
        return expression != null && expression instanceof PsiLiteralExpression && "null".equals(expression.getText());
    }

    private static String getOptionalTypeString(PsiType assignmentTargetType) {
        String optionalTypeString = "";
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
                    optionalTypeString = typeText;
                    break;
                }
            }
        }
        return optionalTypeString;
    }

    private class ReplaceWithEmptyQuickFix implements LocalQuickFix {
        private final String optionalTypeString;

        private ReplaceWithEmptyQuickFix(String optionalTypeString) {
            this.optionalTypeString = optionalTypeString;
        }

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
                final PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

                PsiMethodCallExpression emptyCall = (PsiMethodCallExpression) factory.createExpressionFromText(optionalTypeString + ".empty()", null);

                problemDescriptor.getPsiElement().replace(emptyCall);
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }
    }

}
