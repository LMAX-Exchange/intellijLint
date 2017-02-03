package com.lmax.intellijLint;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;

import java.util.List;
import java.util.Objects;

public class UnitsInspectionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/UnitsInspectionTest";
    }

    public void testMismatchedUnitsOnVariableInitialization() throws Exception {
        doTest("bar", "foo");
    }

    public void testRightUntypedOnVariableInitialization() throws Exception {
        doTest("null", "foo");
    }

    public void testLeftUntypedOnVariableInitialization() throws Exception {
        doTest("foo", "null");
    }

    public void testMismatchedUnitsOnOptional() throws Exception {
        doTest("bar", "foo");
    }

    public void testRightUntypedOnOptional() throws Exception {
        doTest("null", "foo");
    }

    public void testLeftUntypedOnOptional() throws Exception {
        doTest("foo","null");
    }

    public void testReturnUntyped() throws Exception {
        doTest("Returning null when expecting foo");
    }

    public void testReturnVariable() throws Exception {
        //TODO: figure out how to get a count of my errors, and check it's zero.
        //doTest()
    }

    public void testArithmetic() throws Exception {
        doTest("Left side of expression is null and right side is foo");
    }

    public void testTernary() throws Exception {
        doTest("Left side of expression is null and right side is foo");
    }

    private void doTest(String assignmentType, String targetType) {
        doTest(String.format("Assigning %s to variable of type %s", assignmentType, targetType));
    }

    private void doTest(String expectedMessage) {
        myFixture.configureByFile(getTestDirectoryName() + ".java");
        UnitsInspection unitsInspection = new UnitsInspection();
        unitsInspection.subTypeAnnotations.add("org.checkerframework.framework.qual.SubtypeOf");
        myFixture.enableInspections(unitsInspection);

        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        Assert.assertEquals(
                1,
                highlightInfoList.stream()
                        .filter(x -> x.getDescription() != null)
                        .filter(x -> expectedMessage.equals(x.getDescription()))
                        .count());
    }
}
