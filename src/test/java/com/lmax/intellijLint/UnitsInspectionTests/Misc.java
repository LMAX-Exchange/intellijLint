package com.lmax.intellijLint.UnitsInspectionTests;

public class Misc extends Base {
    public void testReturnVariable() throws Exception {
        expectNoInspections();
    }

    public void testArithmetic() throws Exception {
        expectInspection("Left side of expression is null and right side is foo");
    }

    public void testCorrectArithmetic() throws Exception {
        expectNoInspections();
    }

    public void testArithmeticAssignment() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testCorrectArithmeticAssignment() throws Exception {
        expectNoInspections();
    }

    public void testTernary() throws Exception {
        expectInspection("Left side of expression is null and right side is foo");
    }

    public void testReturnUntyped() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testUninitializedField() throws Exception {
        expectNoInspections();
    }

    public void testEquality() throws Exception {
        expectInspection("Left side of expression is null and right side is foo");
    }

    public void testCorrectEquality() throws Exception {
        expectNoInspections();
    }

    public void testPrefixedEquality() throws Exception {
        expectInspection("Left side of expression is null and right side is foo");
    }

    public void testCorrectPrefixedEquality() throws Exception {
        expectNoInspections();
    }
}
