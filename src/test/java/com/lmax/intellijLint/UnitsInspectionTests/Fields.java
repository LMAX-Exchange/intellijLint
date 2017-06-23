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

public class Fields extends Base {

    public void testMismatchedUnitsOnFieldInitialization() throws Exception {
        expectInspection("Assigning bar to variable of type foo");
    }

    public void testRightUntypedOnFieldInitialization() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testLeftUntypedOnFieldInitialization() throws Exception {
        expectNoInspections();
    }

    public void testLeftUntypedOnFieldAssignment() throws Exception {
        expectNoInspections();
    }

    public void testMismatchedUnitsOnFieldAssignment() throws Exception {
        expectInspection("Assigning bar to variable of type foo");
    }

    public void testRightUntypedOnFieldAssignment() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testTypedGetterAndUntypedField() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testUseUntypedFieldInTypedMethod() throws Exception {
        expectInspection("Passing null when expecting a parameter of type foo", 171);
    }
}
