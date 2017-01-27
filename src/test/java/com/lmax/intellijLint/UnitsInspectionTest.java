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

    public void testUntypedOnVariableInitialization() throws Exception {
        doTest("null");
    }

    private void doTest()
    {
        doTest("bar");
    }

    private void doTest(String assignmentType)
    {
        doTest(getTestDirectoryName(), assignmentType);
    }

    private void doTest(String filename, String assignmentType) {
        myFixture.configureByFile(filename + ".java");
        myFixture.enableInspections(new UnitsInspection());

        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        Assert.assertEquals(
                1,
                highlightInfoList.stream()
                        .filter(x -> x.getDescription() != null)
                        .filter(x -> String.format("Assigning %s to variable of type foo", assignmentType).equals(x.getDescription()))
                        .count());
    }
}
