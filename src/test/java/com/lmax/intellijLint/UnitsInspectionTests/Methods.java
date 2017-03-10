package com.lmax.intellijLint.UnitsInspectionTests;

public class Methods extends Base {
    public void testReturnVariable() throws Exception {
        expectNoInspections();
    }

    public void testReturnUntyped() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testReturnThis() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testCorrectReturnThis() throws Exception {
        expectNoInspections();
    }

//    TODO: check method calls
//    public void testLambda() throws Exception {
//        expectInspection("Found null when expecting foo");
//    }

    public void testCorrectLambda() throws Exception {
        expectNoInspections();
    }
}
