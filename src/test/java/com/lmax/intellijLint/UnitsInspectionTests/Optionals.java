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

public class Optionals extends Base {
    public void testRightUntypedOnOptional() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testLeftUntypedOnOptional() throws Exception {
        expectNoInspections();
    }

    public void testMismatchedUnitsOnOptional() throws Exception {
        expectInspection("Assigning bar to variable of type foo");
    }

    /*
    TODO: fix these tests. Handling of optionals is incorrect atm.
    public void testOptionalOfUntyped() throws Exception {
        expectAssignmentInspection("null", "foo");
    }

    public void testCorrectOptionalOf() throws Exception {
        expectNoInspections();
    }

    public void testOptionalOfIncorrectType() throws Exception {
        expectAssignmentInspection("bar", "foo");
    }

    public void testOptionalEmpty() throws Exception {
        expectNoInspections();
    }
    */
}
