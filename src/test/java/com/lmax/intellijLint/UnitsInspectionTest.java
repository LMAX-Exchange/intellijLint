package com.lmax.intellijLint;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;

import java.util.List;

public class UnitsInspectionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testdata/UnitsInspectionTest";
    }

    public void testMismatchedUnitsOnVariableInitialization() throws Exception {
        doTest();
    }

    private void doTest() {
        doTest(getTestDirectoryName());
    }

    private void doTest(String filename) {
        doTest(filename, 1);
    }

    private void doTest(String filename, int inspectionCount) {
        myFixture.configureByFile(filename + ".java");
        myFixture.enableInspections(new UnitsInspection());

        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
        Assert.assertEquals(
                inspectionCount,
                highlightInfos.stream()
                        .filter(x -> "Mismatched units".equals(x.getDescription()))
                        .count());

//        myFixture.getAllQuickFixes()
//                .stream()
//                .filter(x -> x.getText().equals("Replace null assignment with optional.empty()"))
//                .forEach(intention -> myFixture.launchAction(intention));
//
//        myFixture.checkResultByFile(filename + "Fixed.java");
    }
}
