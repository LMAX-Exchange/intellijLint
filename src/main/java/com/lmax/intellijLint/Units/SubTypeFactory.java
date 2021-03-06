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

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class SubTypeFactory {
    private final static WeakHashMap<String, Boolean> subTypeCache = new WeakHashMap<>();
    private final List<String> subTypeAnnotations;

    public SubTypeFactory(List<String> subTypeAnnotations)
    {
        this.subTypeAnnotations = subTypeAnnotations;
    }

    private @Nullable SubType getSubType(PsiElement element, PsiModifierList modifierList)
    {
        if (modifierList == null)
        {
            return new SubType(element);
        }

        return getSubType(element, modifierList.getAnnotations());
    }

    private SubType getSubType(PsiElement element, @NotNull PsiAnnotation[] annotations)
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

    private boolean isSubType(PsiAnnotation annotation)
    {
        return subTypeCache.computeIfAbsent(annotation.getQualifiedName(),
                (x) -> annotationClassHasSubtypeAnnotation(resolve(annotation)));
    }

    private boolean annotationClassHasSubtypeAnnotation(@Nullable PsiClass aClass) {
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

    public SubType getSubType(@NotNull PsiElement elementToResolve)
    {
        if (elementToResolve instanceof PsiNewExpression ||
                elementToResolve instanceof PsiThisExpression ||
                elementToResolve instanceof PsiInstanceOfExpression ||
                elementToResolve instanceof PsiClassObjectAccessExpression ||
                elementToResolve instanceof PsiClass)
        {
            //Classes can't be annotated (there's no point).
            return new SubType(elementToResolve); //TODO: wrapping types, i.e. optionalLong etc
        }

        if (elementToResolve instanceof PsiEnumConstant)
        {
            //Enums can't have subtypes?
            return new SubType(elementToResolve);
        }

        if (elementToResolve instanceof PsiAssignmentExpression)
        {
            //TODO: Means we got an assignment inside something else?
            return new SubType(elementToResolve);
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
            final PsiJavaToken operator = ((PsiBinaryExpression) elementToResolve).getOperationSign();

            final SubType leftSubType = getSubType(left);
            if (operator.textMatches("/") || operator.textMatches("*") || operator.textMatches("%"))
            {
                return leftSubType;
            }

            if (right == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.ONE_SIDED_BINARY_EXPRESSION);
            }
            final SubType thenSubType = getSubType(right);

            if (!Objects.equals(leftSubType, thenSubType)) {
                //Differences between sides of expression are handled in visitor.
                return new SubType(elementToResolve, ResolutionFailureReason.MISMATCHED_BINARY_EXPRESSION);
            }
            return thenSubType;
        }

        if (elementToResolve instanceof PsiPolyadicExpression)
        {
            //Differences between parts of expression are handled in visitor.
            return getSubType(((PsiPolyadicExpression) elementToResolve).getOperands()[0]);
        }

        if (elementToResolve instanceof PsiPrefixExpression)
        {
            final PsiExpression operand = ((PsiPrefixExpression) elementToResolve).getOperand();
            if (operand == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.PREFIX_WITHOUT_OPERAND);
            }
            return getSubType(operand);
        }

        if (elementToResolve instanceof PsiPostfixExpression)
        {
            final PsiExpression operand = ((PsiPostfixExpression) elementToResolve).getOperand();
            return getSubType(operand);
        }

        if (elementToResolve instanceof PsiParenthesizedExpression)
        {
            final PsiExpression expression = ((PsiParenthesizedExpression) elementToResolve).getExpression();
            if (expression == null)
            {
                return new SubType(elementToResolve, ResolutionFailureReason.PARENTHESIZED_WITHOUT_INNER_EXPRESSION);
            }
            return getSubType(expression);
        }

        if (elementToResolve instanceof PsiArrayAccessExpression)
        {
            //TODO
            return new SubType(elementToResolve);
        }

        if (elementToResolve instanceof PsiLambdaExpression)
        {
            //TODO
            return new SubType(elementToResolve);
        }

        return new SubType(elementToResolve, ResolutionFailureReason.UNEXPECTED_PSI_ELEMENT_TYPE);
    }
}
