package com.lmax.intellijLint.UnitsInspectionTests;

public class Variables extends Base {

    public void testMismatchedUnitsOnVariableInitialization() throws Exception {
        expectAssignmentInspection("bar", "foo");
    }

    public void testRightUntypedOnVariableInitialization() throws Exception {
        expectAssignmentInspection("null", "foo");
    }

    public void testLeftUntypedOnVariableInitialization() throws Exception {
        expectAssignmentInspection("foo", "null");
    }

    public void testMismatchedUnitsOnVariableAssignment() throws Exception {
        expectAssignmentInspection("bar", "foo");
    }

    public void testRightUntypedOnVariableAssignment() throws Exception {
        expectAssignmentInspection("null", "foo");
    }

    public void testLeftUntypedOnVariableAssignment() throws Exception {
        expectAssignmentInspection("foo", "null");
    }
}
