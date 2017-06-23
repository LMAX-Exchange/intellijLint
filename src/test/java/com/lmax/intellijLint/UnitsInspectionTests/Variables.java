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

public class Variables extends Base {

    public void testMismatchedUnitsOnVariableInitialization() throws Exception {
        expectInspection("Assigning bar to variable of type foo");
    }

    public void testRightUntypedOnVariableInitialization() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testLeftUntypedOnVariableInitialization() throws Exception {
        expectNoInspections();
    }

    public void testLeftUntypedOnVariableAssignment() throws Exception {
        expectNoInspections();
    }

    public void testMismatchedUnitsOnVariableAssignment() throws Exception {
        expectInspection("Assigning bar to variable of type foo");
    }

    public void testRightUntypedOnVariableAssignment() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }
}
