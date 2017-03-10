package com.lmax.intellijLint.UnitsInspectionTests;

public class Misc extends Base {
    public void testReturnVariable() throws Exception {
        expectNoInspections();
    }
//    TODO: bubble up mismatches when resolving ternaries.
//    public void testTernary() throws Exception {
//        expectInspection("Assigning null to variable of type foo");
//    }

    public void testReturnUntyped() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testUninitializedField() throws Exception {
        expectNoInspections();
    }
//    TODO: lower severity, more informative message.
//    public void testEquality() throws Exception {
//        expectInspection("Left side of expression is null and right side is foo");
//    }

    public void testCorrectEquality() throws Exception {
        expectNoInspections();
    }
//    TODO: same as equality.
//    public void testPrefixedEquality() throws Exception {
//        expectInspection("Left side of expression is null and right side is foo");
//    }

    public void testCorrectPrefixedEquality() throws Exception {
        expectNoInspections();
    }

    public void testEnumEquality() throws Exception {
        expectNoInspections();
    }
//  TODO: bubble up mismatches inside parens.
//    public void testParenthesized() throws Exception {
//        expectInspection("Left side of expression is null and right side is foo");
//    }

    public void testCorrectParenthesized() throws Exception {
        expectNoInspections();
    }

    public void testReturnThis() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testCorrectReturnThis() throws Exception {
        expectNoInspections();
    }

    public void testPolyadicStringConversion() throws Exception {
        expectNoInspections();
    }
//    TODO: check method calls
//    public void testLambda() throws Exception {
//        expectInspection("Found null when expecting foo");
//    }

    public void testCorrectLambda() throws Exception {
        expectNoInspections();
    }

    public void testCastDuringAssignment() throws Exception {
        expectNoInspections();
    }
}
