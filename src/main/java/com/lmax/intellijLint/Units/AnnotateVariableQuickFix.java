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

package com.lmax.intellijLint.Units;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AnnotateVariableQuickFix implements LocalQuickFix {

    private final SmartPsiElementPointer<PsiVariable> variableToAnnotatePtr;
    private final SubType annotationToApply;
    private final String name;

    public AnnotateVariableQuickFix(PsiVariable variableToAnnotate, SubType annotationToApply) {
        this.variableToAnnotatePtr = SmartPointerManager.getInstance(variableToAnnotate.getProject()).createSmartPsiElementPointer(variableToAnnotate);
        this.annotationToApply = annotationToApply;
        this.name = String.format("Annotate variable %s with %s", variableToAnnotate.getName(), annotationToApply.getSubtypeFQN());
    }

    public static boolean canApply(PsiVariable variableToAnnotate)
    {
        return variableToAnnotate != null && variableToAnnotate.getModifierList() != null;
    }

    @Nls
    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Annotate variable";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        final PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        //noinspection ConstantConditions
        final PsiAnnotation annotationFromText = factory.createAnnotationFromText("@" + annotationToApply.getSubtypeFQN(), variableToAnnotatePtr.getContainingFile());
        final PsiVariable variableToAnnotate = variableToAnnotatePtr.getElement();
        if (variableToAnnotate != null && variableToAnnotate.getModifierList() != null) {
            variableToAnnotate.getModifierList().add(annotationFromText);
        }
    }
}
