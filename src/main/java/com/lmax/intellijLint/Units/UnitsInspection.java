package com.lmax.intellijLint.Units;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.SpecialAnnotationsUtil;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.siyeh.ig.ui.ExternalizableStringSet;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess") //Needs to be public as is used in plugin.
@State(name = "unitsInspection", storages = {@Storage("com.lmax.intellijLint.units.xml")})
public class UnitsInspection extends BaseJavaLocalInspectionTool implements PersistentStateComponent<UnitsInspection.State>, com.intellij.openapi.components.ProjectComponent {
    private static final Logger LOG = Logger.getInstance("#intellijLint.OptionalNullInspection");

    private static String printableMethod(PsiMethod m) {
        final PsiClass containingClass = m.getContainingClass();
        return (containingClass != null ? containingClass.getName() : "") +
                "." + m.getName() + " " +
                printableParamList(m);
    }

    private static String printableParamList(PsiMethod m) {
        return String.join(",", Arrays.stream(m.getParameterList().getParameters())
                .map(param -> {
                    String result = "";
                    final PsiModifierList modifierList = param.getModifierList();
                    if (modifierList != null) {
                        for (PsiAnnotation anno : modifierList.getAnnotations()) {
                            if (anno.getQualifiedName() != null) {
                                result += "@" + anno.getQualifiedName();
                            }
                        }
                    }

                    final PsiTypeElement typeElement = param.getTypeElement();
                    if (typeElement != null) {
                        result += " " + typeElement.getType().getCanonicalText();
                    }
                    return result;
                })
                .collect(Collectors.toList()));
    }

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
        return "Java";
    }

    public static final String DESCRIPTION_TEMPLATE = "Assigning %s to variable of type %s";
    public static final String ARGUMENT_TEMPLATE = "Passing %s when expecting a parameter of type %s";
    public static final String BINARY_EXPRESSION_DESCRIPTION_TEMPLATE = "Left side of expression is %s and right side is %s";
    public static final String RETURNING_DESCRIPTION_TEMPLATE = "Returning %s when expecting %s";
    public static final String FAILED_TO_RESOLVE = "Failed to resolve subtype on %s due to %s";
    public static final String POLYADIC_MISMATCH = "Found %s when rest of expression is %s";
    public static final String INTERFACE_TEMPLATE = "Implementation subtype (%s) should match interface (%s)";

    private static final ArrayList<String> defaultSubTypes = new ArrayList<>(Collections.singletonList("org.checkerframework.framework.qual.SubtypeOf"));
    public final static List<String> subTypeAnnotations = defaultSubTypes;
    private final static SubTypeFactory subTypeFactory = new SubTypeFactory(subTypeAnnotations);

    /**
     * Returns either a {@link PsiMethod} or {@link PsiLambdaExpression}
     */
    private PsiElement walkUpToWrappingMethod(PsiElement element) {
        if (element == null) {
            return null;
        }

        PsiElement parent = element.getParent();
        if (parent == null) {
            return null;
        }

        if (parent instanceof PsiMethod || parent instanceof PsiLambdaExpression) {
            return parent;
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
                if (initalizerExpr == null) {
                    return;
                }

                SubType declared = subTypeFactory.getSubType(expression.getLExpression());
                if (!declared.isResolved()) {
                    reportResolutionFailure(declared, holder);
                    return;
                }

                if (declared.hasSubtype()) {
                    SubType assigned = subTypeFactory.getSubType(initalizerExpr);
                    inspect(expression, assigned, declared, holder, DESCRIPTION_TEMPLATE);
                }
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);
                final SubType declared = subTypeFactory.getSubType(field);
                if (!declared.hasSubtype()) {
                    return;
                }

                final PsiExpression initializerExpr = field.getInitializer();
                if (initializerExpr == null) {
                    return;
                }

                final SubType initializer = subTypeFactory.getSubType(initializerExpr);
                inspect(field, initializer, declared, holder, DESCRIPTION_TEMPLATE);
            }

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);

                final PsiClass containingClass = method.getContainingClass();

                if (containingClass == null) {
                    return;
                }

                final PsiReferenceList implementsList = containingClass.getImplementsList();
                if (implementsList != null) {
                    final List<PsiMethod> implemented =
                            Arrays.stream(implementsList.getReferencedTypes())
                                    .map(PsiClassType::resolve)
                                    .map(psiClass -> psiClass.findMethodsBySignature(method, true))
                                    .flatMap(Arrays::stream)
                                    .filter(psiMethod -> psiMethod.getName().equals(method.getName()))
                                    .collect(Collectors.toList());
                    if (implemented.size() > 1) {
                        final String implementedString = String.join(", ", implemented.stream()
                                .map(UnitsInspection::printableMethod)
                                .collect(Collectors.toList()));
                        LOG.warn("Matched more than one method during impl search: " +
                                implementedString);
                    }

                    if (implemented.size() > 0)
                    {
                        final PsiMethod interfaceMethod = implemented.get(0);

                        final PsiParameter[] interfaceParameters = interfaceMethod.getParameterList().getParameters();
                        final PsiParameter[] methodParameters = method.getParameterList().getParameters();

                        checkParametersMatch(interfaceParameters, methodParameters, holder, INTERFACE_TEMPLATE);
                    }
                }
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);

                final SubType declared = subTypeFactory.getSubType(variable);
                if (!declared.hasSubtype()) {
                    return;
                }

                final PsiExpression initializerExpression = variable.getInitializer();

                if (initializerExpression == null) {
                    return;
                }

                final SubType initializer = subTypeFactory.getSubType(initializerExpression);
                inspect(variable, initializer, declared, holder, DESCRIPTION_TEMPLATE);
            }

            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);

                final PsiExpression returnValueExpr = statement.getReturnValue();

                if (returnValueExpr == null) {
                    return; // void return, won't have annotation.
                }

                PsiElement psiMethod = walkUpToWrappingMethod(returnValueExpr);
                if (psiMethod == null) {
                    LOG.warn("Unable to locate wrapping method for return statement " + statement.getText() + " in: " + statement.getContainingFile());
                    return;
                }
                final SubType declared = subTypeFactory.getSubType(psiMethod);

                if (!declared.isResolved()) {
                    reportResolutionFailure(declared, holder);
                    return;
                }

                if (declared.hasSubtype()) {
                    final SubType returnValue = subTypeFactory.getSubType(returnValueExpr);
                    inspect(statement, returnValue, declared, holder, RETURNING_DESCRIPTION_TEMPLATE);
                }
            }

            @Override
            public void visitCallExpression(PsiCallExpression expression) {
                super.visitCallExpression(expression);

                final PsiMethod psiMethod = expression.resolveMethod();

                if (psiMethod == null && "super".equals(expression.getText())) {
                    //TODO: this causes resolution failures in some cases.
                    return;
                } else if (psiMethod == null) {
                    //TODO: Might be a lambda. Deal with that somehow.
                    //reportResolutionFailure(expression, "being unable to resolve method", holder);
                    return;
                }

                final PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
                final PsiExpressionList argumentList = expression.getArgumentList();
                if (argumentList == null) {
                    //TODO: Probably want to report everything as a failure.
                    return;
                }
                final PsiExpression[] argExprs = argumentList.getExpressions();

                checkParametersMatch(parameters, argExprs, holder, ARGUMENT_TEMPLATE);
            }
        };
    }

    private void checkParametersMatch(PsiParameter[] interfaceParameters, PsiElement[] methodParameters, @NotNull ProblemsHolder holder, String argumentTemplate) {
        for (int i = 0; (i < interfaceParameters.length && i < methodParameters.length); i++) {
            final SubType paramSubType = subTypeFactory.getSubType(interfaceParameters[i]);
            if (!paramSubType.hasSubtype()) {
                continue;
            }
            final SubType argSubType = subTypeFactory.getSubType(methodParameters[i]);

            inspect(methodParameters[i], argSubType, paramSubType, holder, argumentTemplate);
        }
    }

    private void inspect(PsiElement element, SubType found, SubType required, @NotNull ProblemsHolder holder, String descriptionTemplate) {
        if (reportResolutionFailure(required, holder) || reportResolutionFailure(found, holder)) {
            return;
        }

        if (!Objects.equals(required, found)) {
            final String description = String.format(descriptionTemplate, found.getSubtypeFQN(), required.getSubtypeFQN());
            if (element.isValid() && found.getPsiElement().isValid() && required.getPsiElement().isValid()) {
                holder.registerProblem(element, description, makeQuickFixes(required, found));
            }
        }
    }

    private LocalQuickFix[] makeQuickFixes(SubType required, SubType found) {
        List<LocalQuickFix> fixes = new ArrayList<>();
        if (found.getPsiElement() instanceof PsiVariable &&
                AnnotateVariableQuickFix.canApply((PsiVariable) found.getPsiElement())) {
            fixes.add(new AnnotateVariableQuickFix((PsiVariable) found.getPsiElement(), required));
        }

        return fixes.toArray(new LocalQuickFix[0]);
    }

    private boolean reportResolutionFailure(SubType subType, @NotNull ProblemsHolder holder) {
        if (subType.getFailureReason() != ResolutionFailureReason.NONE && !isIgnoredResolutionFailureReason(subType)) {
            reportResolutionFailure(subType.getPsiElement(), subType.getFailureReason().toString(), holder);
            return true;
        }

        return isIgnoredResolutionFailureReason(subType);
    }

    private void reportResolutionFailure(PsiElement element, String failureReason, @NotNull ProblemsHolder holder) {
        final String description = String.format(FAILED_TO_RESOLVE, element, failureReason);

        //Sanity check, similar to what's in the problem holder.
        if (element.getTextRange().getStartOffset() >= element.getTextRange().getEndOffset()) {
            if (!(element instanceof PsiFile)) {
                holder.registerProblem(element, description);
            }
        }
    }

    private boolean isIgnoredResolutionFailureReason(SubType subType) {
        final ResolutionFailureReason faliureReason = subType.getFailureReason();
        return faliureReason == ResolutionFailureReason.MISMATCHED_CONDITIONAL ||
                faliureReason == ResolutionFailureReason.MISMATCHED_BINARY_EXPRESSION ||
                faliureReason == ResolutionFailureReason.COULD_NOT_RESOLVE_ANNOTATION ||
                faliureReason == ResolutionFailureReason.COULD_NOT_RESOLVE_CAST_TYPE ||
                faliureReason == ResolutionFailureReason.COULD_NOT_RESOLVE_METHOD ||
                faliureReason == ResolutionFailureReason.COULD_NOT_RESOLVE_REFERENCE;
    }

    public JComponent createOptionsPanel() {
        return SpecialAnnotationsUtil.createSpecialAnnotationsListControl(
                subTypeAnnotations, "Sub Type annotations");
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    @Nullable
    @Override
    public UnitsInspection.State getState() {
        State state = new State();
        state.subTypeAnnotations = new HashSet<>(subTypeAnnotations);
        return state;
    }

    @Override
    public void loadState(UnitsInspection.State state) {
        subTypeAnnotations.clear();
        subTypeAnnotations.addAll(state.subTypeAnnotations);
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "UnitsInspection";
    }

    public static class State {
        public State() {
            subTypeAnnotations = new ExternalizableStringSet(defaultSubTypes.toArray(new String[defaultSubTypes.size()]));
        }

        public Set<String> subTypeAnnotations;
    }
}
