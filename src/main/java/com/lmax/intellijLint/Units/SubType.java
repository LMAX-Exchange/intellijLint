package com.lmax.intellijLint.Units;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.lmax.intellijLint.Units.ResolutionFailureReason.NONE;

public class SubType {
    private final @NotNull PsiElement element;
    private final @Nullable String subtypeFQN;
    private final boolean resolved;
    private final ResolutionFailureReason resolutionFailureReason;

    final static List<String> subTypeAnnotations = new ArrayList<>();
    private final static WeakHashMap<String, Boolean> subTypeCache = new WeakHashMap<>();

    private SubType(@NotNull PsiElement element, ResolutionFailureReason resolutionFailureReason)
    {
        this.element = element;
        subtypeFQN = null;
        resolved = false;
        this.resolutionFailureReason = resolutionFailureReason;
    }

    private SubType(@NotNull PsiElement element)
    {
        this.element = element;
        subtypeFQN = null;
        resolved = true;
        resolutionFailureReason = NONE;
    }

    private SubType(@NotNull PsiElement element, @Nullable String subtypeFQN, boolean resolved)
    {
        this.element = element;
        this.subtypeFQN = subtypeFQN;
        this.resolved = resolved;
        resolutionFailureReason = NONE;
    }

    public @Nullable String getSubtypeFQN()
    {
        return subtypeFQN;
    }

    public static void setAnnotations(Collection<String> subTypeAnnotations) {
        SubType.subTypeAnnotations.clear();
        SubType.subTypeAnnotations.addAll(subTypeAnnotations);
    }

    public @NotNull PsiElement getPsiElement() {
        return element;
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

        //noinspection SimplifiableIfStatement easier to read, imo.
        if (this.subtypeFQN == null && other.subtypeFQN == null)
        {
            return true;
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
        return aClass != null && AnnotationUtil.isAnnotated(aClass, subTypeAnnotations);
    }

    private static PsiClass resolve(PsiAnnotation annotation) {
        final String qualifiedName = annotation.getQualifiedName();
        if (qualifiedName == null)
        {
            throw new IllegalArgumentException("Annotation may not be null"); //Checked in getSubType(PsiElement, PsiAnnotation[])
        }
        return JavaPsiFacade.getInstance(annotation.getProject())
                .findClass(qualifiedName, annotation.getResolveScope());
    }

    public static SubType getSubType(@NotNull PsiElement elementToResolve)
    {
        if (elementToResolve instanceof PsiNewExpression)
        {
            //Classes can't be annotated (there's no point).
            return new SubType(elementToResolve); //TODO: wrapping types, i.e. optionalLong etc
        }

        if (elementToResolve instanceof PsiCall)
        {
            PsiMethod psiMethod = ((PsiCall) elementToResolve).resolveMethod();
            if (psiMethod == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.COULD_NOT_RESOLVE_METHOD);
            }
            return getSubType(elementToResolve, psiMethod.getModifierList().getAnnotations());
        }

        if (elementToResolve instanceof PsiTypeCastExpression)
        {
            PsiTypeElement castingTo = ((PsiTypeCastExpression) elementToResolve).getCastType();
            if (castingTo == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.COULD_NOT_RESOLVE_CAST_TYPE);
            }

            return getSubType(elementToResolve, castingTo.getAnnotations());
        }

        if (elementToResolve instanceof PsiConditionalExpression)
        {
            final PsiExpression elseExpression = ((PsiConditionalExpression) elementToResolve).getElseExpression();
            final PsiExpression thenExpression = ((PsiConditionalExpression) elementToResolve).getThenExpression();

            if (thenExpression == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.CONDITIONAL_WITHOUT_THEN_BLOCK);
            }
            final SubType thenSubType = getSubType(thenExpression);

            if (elseExpression != null && !Objects.equals(getSubType(elseExpression), thenSubType)) {
                //Differences between sides of expression are handled in visitor.
                return new SubType(elementToResolve, ResolutionFailureReason.MISMATCHED_CONDITIONAL);
            }
            return thenSubType;
        }

        if (elementToResolve instanceof PsiVariable)
        {
            return getSubType(elementToResolve, ((PsiVariable) elementToResolve).getModifierList());
        }

        if (elementToResolve instanceof PsiReferenceExpression)
        {
            final PsiElement resolvedReference = ((PsiReferenceExpression) elementToResolve).resolve();
            if (resolvedReference == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.COULD_NOT_RESOLVE_REFERENCE);
            }

            return getSubType(resolvedReference);
        }

        if (elementToResolve instanceof PsiMethod)
        {
            return getSubType(elementToResolve, ((PsiMethod) elementToResolve).getModifierList());
        }

        if (elementToResolve instanceof PsiLiteral)
        {
            //Should be a cast expr if we want it to be annotated.
            return new SubType(elementToResolve);
        }

        if (elementToResolve instanceof PsiBinaryExpression)
        {
            final PsiExpression left = ((PsiBinaryExpression) elementToResolve).getLOperand();
            final PsiExpression right = ((PsiBinaryExpression) elementToResolve).getROperand();

            if (right == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.ONE_SIDED_BINARY_EXPRESSION);
            }
            final SubType thenSubType = getSubType(right);

            if (!Objects.equals(getSubType(left), thenSubType)) {
                //Differences between sides of expression are handled in visitor.
                return new SubType(elementToResolve, ResolutionFailureReason.MISMATCHED_BINARY_EXPRESSION);
            }
            return thenSubType;
        }

        return new SubType(elementToResolve, ResolutionFailureReason.UNEXPECTED_PSI_ELEMENT_TYPE);
    }

    public ResolutionFailureReason getFailureReason() {
        return resolutionFailureReason;
    }
}
