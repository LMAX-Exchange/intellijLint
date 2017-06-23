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

public class Arithmetic extends Base {
//    TODO: bubble up mismatches when resolving arithmetic
//    public void testArithmetic() throws Exception {
//        expectInspection("Assigning null to variable of type foo");
//    }

    public void testCorrectAddition() throws Exception {
        expectNoInspections();
    }

    public void testAdditionAssignment() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testCorrectAdditionAssignment() throws Exception {
        expectNoInspections();
    }

    public void testCorrectMultiplication() throws Exception {
        expectNoInspections();
    }
// TODO: handle bad maths
//    public void testMultiplication() throws Exception {
//        expectInspection("Assigning null when expecting foo");
//    }

    public void testCorrectDivision() throws Exception {
        expectNoInspections();
    }
// TODO: handle bad maths
//    public void testDivision() throws Exception {
//        expectInspection("Assigning null when expecting foo");
//    }

//  TODO: bubble up mismatches when polyadic operation and assigning.
//    public void testPolyadicAddition() throws Exception {
//        expectInspection("Found null when rest of expression is foo");
//    }

    public void testCorrectPolyadicAddition() throws Exception {
        expectNoInspections();
    }
}
