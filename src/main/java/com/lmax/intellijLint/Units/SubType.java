package com.lmax.intellijLint.Units;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.lmax.intellijLint.Units.ResolutionFailureReason.NONE;

public class SubType {
    private final PsiElement element;
    private final @Nullable String subtypeFQN;
    private final boolean resolved;
    private final ResolutionFailureReason resolutionFailureReason;

    final static List<String> subTypeAnnotations = new ArrayList<>();
    private final static WeakHashMap<String, Boolean> subTypeCache = new WeakHashMap<>();

    //Could not resolve
    @SuppressWarnings("NullableProblems")
    private SubType(PsiElement element, ResolutionFailureReason resolutionFailureReason)
    {
        this.element = element;
        subtypeFQN = null;
        resolved = false;
        this.resolutionFailureReason = resolutionFailureReason;
    }

    private SubType(PsiElement element)
    {
        this.element = element;
        subtypeFQN = null;
        resolved = true;
        resolutionFailureReason = NONE;
    }

    private SubType(PsiElement element, @Nullable String subtypeFQN, boolean resolved)
    {
        this.element = element;
        this.subtypeFQN = subtypeFQN;
        this.resolved = resolved;
        resolutionFailureReason = NONE;
    }

    @Nullable
    public String getSubtypeFQN()
    {
        return subtypeFQN;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubType))
        {
            return false;
        }

        final SubType other = (SubType) obj;

        if (!this.resolved || !other.resolved)
        {
            return false;
        }

        return other.subtypeFQN != null && other.subtypeFQN.equals(this.subtypeFQN);
    }

    private static @Nullable SubType getSubType(PsiElement element, PsiModifierList modifierList)
    {
        if (modifierList == null)
        {
            return new SubType(element);
        }

        return getSubType(element, modifierList.getAnnotations());
    }

    private static SubType getSubType(PsiElement element, @NotNull PsiAnnotation[] annotations)
    {
        if (annotations.length == 0)
        {
            return new SubType(element);
        }

        int failedToGetQualNameCounter = 0;
        int notSubTypeCounter = 0;

        for (PsiAnnotation annotation : annotations)
        {
            if (annotation.getQualifiedName() == null)
            {
                failedToGetQualNameCounter++;
            }

            if(isSubType(annotation))
            {
                return new SubType(element, annotation.getQualifiedName(), true);
            }
            else
            {
                notSubTypeCounter++;
            }
        }

        if (notSubTypeCounter == annotations.length)
        {
            return new SubType(element);
        }

        if (failedToGetQualNameCounter > 0)
        {
            return new SubType(element, ResolutionFailureReason.COULD_NOT_RESOLVE_ANNOTATION);
        }

        throw new IllegalStateException("Attempted to get subtype from annotation list but failed for unknown reason.\n" +
                "Annotation list is: [" + Arrays.stream(annotations).map(PsiElement::toString).collect(Collectors.joining(", ")) + "]\n" +
                "PsiElement is: " + element.getText());
    }

    private static boolean isSubType(PsiAnnotation annotation)
    {
        return subTypeCache.computeIfAbsent(annotation.getQualifiedName(),
                (x) -> annotationClassHasSubtypeAnnotation(resolve(annotation)));
    }

    private static boolean annotationClassHasSubtypeAnnotation(@Nullable PsiClass aClass) {
        if (aClass == null)
        {
            return false;
        }

        return AnnotationUtil.isAnnotated(aClass, subTypeAnnotations);
    }

    private static PsiClass resolve(PsiAnnotation annotation) {
        final String qualifiedName = annotation.getQualifiedName();
        return JavaPsiFacade.getInstance(annotation.getProject())
                .findClass(qualifiedName, annotation.getResolveScope());
    }

    public static SubType getSubType(PsiElement element)
    {
        if (element == null)
        {
            return new SubType(null, ResolutionFailureReason.PSI_ELEMENT_NULL);
        }

        if (element instanceof PsiCall)
        {
            PsiMethod psiMethod = ((PsiCall) element).resolveMethod();
            if (psiMethod == null)
            {
                return new SubType(element, ResolutionFailureReason.COULD_NOT_RESOLVE_METHOD);
            }
            return getSubType(element, psiMethod.getModifierList().getAnnotations());
        }

        if (element instanceof PsiTypeCastExpression)
        {
            PsiTypeElement castingTo = ((PsiTypeCastExpression) element).getCastType();
            if (castingTo == null)
            {
                return new SubType(element, ResolutionFailureReason.COULD_NOT_RESOLVE_CAST_TYPE);
            }

            return getSubType(element, castingTo.getAnnotations());
        }

        if (element instanceof PsiConditionalExpression)
        {
            //Differences between sides of expression are handled in visitor.
            return getSubType(((PsiConditionalExpression) element).getThenExpression());
        }

        if (element instanceof PsiVariable)
        {
            return getSubType(element, ((PsiVariable) element).getModifierList());
        }

        if (element instanceof PsiReferenceExpression)
        {
            return getSubType(((PsiReferenceExpression) element).resolve());
        }

        return new SubType(element, ResolutionFailureReason.UNEXPECTED_PSI_ELEMENT_TYPE);
    }

    public static void setAnnotations(Collection<String> subTypeAnnotations) {
        SubType.subTypeAnnotations.clear();
        SubType.subTypeAnnotations.addAll(subTypeAnnotations);
    }
}
