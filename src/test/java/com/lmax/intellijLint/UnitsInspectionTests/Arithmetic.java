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
