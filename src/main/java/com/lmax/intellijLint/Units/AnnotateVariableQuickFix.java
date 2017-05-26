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
