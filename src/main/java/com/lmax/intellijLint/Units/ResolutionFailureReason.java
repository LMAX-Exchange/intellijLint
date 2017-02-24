package com.lmax.intellijLint.Units;

public enum ResolutionFailureReason {
    COULD_NOT_RESOLVE_ANNOTATION,
    COULD_NOT_RESOLVE_METHOD,
    UNEXPECTED_PSI_ELEMENT_TYPE,
    COULD_NOT_RESOLVE_CAST_TYPE,
    CONDITIONAL_WITHOUT_THEN_BLOCK,
    COULD_NOT_RESOLVE_REFERENCE,
    MISMATCHED_CONDITIONAL,
    ONE_SIDED_BINARY_EXPRESSION,
    MISMATCHED_BINARY_EXPRESSION,

    NONE
}