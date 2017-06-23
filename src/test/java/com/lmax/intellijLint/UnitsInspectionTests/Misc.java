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

package com.lmax.intellijLint.UnitsInspectionTests;

public class Misc extends Base {
//    TODO: bubble up mismatches when resolving ternaries.
//    public void testTernary() throws Exception {
//        expectInspection("Assigning null to variable of type foo");
//    }

    public void testUninitializedField() throws Exception {
        expectNoInspections();
    }
//    TODO: lower severity, more informative message.
//    public void testEquality() throws Exception {
//        expectInspection("Left side of expression is null and right side is foo");
//    }

    public void testCorrectEquality() throws Exception {
        expectNoInspections();
    }
//    TODO: same as equality.
//    public void testPrefixedEquality() throws Exception {
//        expectInspection("Left side of expression is null and right side is foo");
//    }

    public void testCorrectPrefixedEquality() throws Exception {
        expectNoInspections();
    }

    public void testEnumEquality() throws Exception {
        expectNoInspections();
    }
//  TODO: bubble up mismatches inside parens.
//    public void testParenthesized() throws Exception {
//        expectInspection("Left side of expression is null and right side is foo");
//    }

    public void testCorrectParenthesized() throws Exception {
        expectNoInspections();
    }

    public void testPolyadicStringConversion() throws Exception {
        expectNoInspections();
    }

    public void testCastDuringAssignment() throws Exception {
        expectNoInspections();
    }

    public void testSuper() throws Exception {
        expectNoInspections();
    }

    public void testReturnFromLambda() throws Exception {
        expectNoInspections();
    }
}
