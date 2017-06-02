package com.lmax.intellijLint.UnitsInspectionTests;

public class Constructors extends Base {

    public void testBadAssignment() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testBadParameter() throws Exception {
        expectInspection("Assigning null to variable of type foo");
    }

    public void testBadCall() throws Exception {
        expectInspection("Passing null when expecting a parameter of type foo");
    }
}
