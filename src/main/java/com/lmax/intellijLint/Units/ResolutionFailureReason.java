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
    PREFIX_WITHOUT_OPERAND,
    PARENTHESIZED_WITHOUT_INNER_EXPRESSION,
    NONE
}
