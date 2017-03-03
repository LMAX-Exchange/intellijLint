package com.lmax.intellijLint.UnitsInspectionTests;

public class Optionals extends Base {
    public void testRightUntypedOnOptional() throws Exception {
        expectAssignmentInspection("null", "foo");
    }

    public void testLeftUntypedOnOptional() throws Exception {
        expectAssignmentInspection("foo", "null");
    }

    public void testMismatchedUnitsOnOptional() throws Exception {
        expectAssignmentInspection("bar", "foo");
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
