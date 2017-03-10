package com.lmax.intellijLint.UnitsInspectionTests;

public class Variables extends Base {

    public void testMismatchedUnitsOnVariableInitialization() throws Exception {
        expectInspection("Assigning bar to variable of type foo");
    }

    public void testRightUntypedOnVariableInitialization() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testLeftUntypedOnVariableInitialization() throws Exception {
        expectInspection("Assigning foo to variable of type null");
    }

    public void testMismatchedUnitsOnVariableAssignment() throws Exception {
        expectInspection("Assigning bar to variable of type foo");
    }

    public void testRightUntypedOnVariableAssignment() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }
}
