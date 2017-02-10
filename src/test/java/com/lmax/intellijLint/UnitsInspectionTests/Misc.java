package com.lmax.intellijLint.UnitsInspectionTests;

public class Misc extends Base {
    public void testReturnVariable() throws Exception {
        expectNoInspections();
    }

/*  TODO: these are emitting both a left-right mismatch and a assigning null. Should only emit a left-right mismatch.
    public void testArithmetic() throws Exception {
        expectInspection("Left side of expression is null and right side is foo");
    }

    public void testTernary() throws Exception {
        expectInspection("Left side of expression is null and right side is foo");
    }
*/

    public void testReturnUntyped() throws Exception {
        expectInspection("Returning null when expecting foo");
    }
}
