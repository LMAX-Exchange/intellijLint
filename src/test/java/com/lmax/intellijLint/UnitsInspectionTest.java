package com.lmax.intellijLint;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;

import java.util.List;

public class UnitsInspectionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/UnitsInspectionTest";
    }

    public void testMismatchedUnitsOnVariableInitialization() throws Exception {
        doTest();
    }

    public void testRightUntypedOnVariableInitialization() throws Exception {
        doTest("null");
    }

    public void testLeftUntypedOnVariableInitialization() throws Exception {
        doTest("foo", "null");
    }

    private void doTest() {
        doTest("bar");
    }

    private void doTest(String assignmentType) {
        doTest(assignmentType, "foo");
    }

    private void doTest(String assignmentType, String targetType) {
        doTest(getTestDirectoryName(), assignmentType, targetType);
    }

    private void doTest(String filename, String assignmentType, String targetType) {
        myFixture.configureByFile(filename + ".java");
        UnitsInspection unitsInspection = new UnitsInspection();
        unitsInspection.subTypeAnnotations.add("org.checkerframework.framework.qual.SubtypeOf");
        myFixture.enableInspections(unitsInspection);

        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        Assert.assertEquals(
                1,
                highlightInfoList.stream()
                        .filter(x -> x.getDescription() != null)
                        .filter(x -> String.format("Assigning %s to variable of type %s", assignmentType, targetType)
                                .equals(x.getDescription()))
                        .count());
    }
}
