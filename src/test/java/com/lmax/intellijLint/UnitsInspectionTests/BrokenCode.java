package com.lmax.intellijLint.UnitsInspectionTests;

public class BrokenCode extends Base {
    public void testMethodDoesNotExist() throws Exception
    {
        expectNoInspections();
    }

    public void testReferenceDoesNotExist() throws Exception
    {
        expectNoInspections();
    }

    public void testAnnotationDoesNotExist() throws Exception
    {
        expectNoInspections();
    }

    public void testCastTargetDoesNotExist() throws Exception
    {
        expectNoInspections();
    }
}
