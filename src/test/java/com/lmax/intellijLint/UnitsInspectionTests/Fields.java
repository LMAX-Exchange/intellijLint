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
}
