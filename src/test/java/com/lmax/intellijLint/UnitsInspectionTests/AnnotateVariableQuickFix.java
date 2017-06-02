package com.lmax.intellijLint.UnitsInspectionTests;

import java.io.IOException;

public class AnnotateVariableQuickFix extends Base {

    public void testMethod() throws Exception {
        expectInspection("Returning null when expecting foo");
        applyQuickFix();
    }

    private void applyQuickFix() throws IOException {
        applyQuickFix(getTestDirectoryName());
    }

    private void applyQuickFix(String filename) throws IOException {
        myFixture.getAllQuickFixes()
                .stream()
                .filter(x -> x.getText().startsWith("Annotate variable"))
                .forEach(intention -> myFixture.launchAction(intention));

        expectNoInspections();

        myFixture.checkResultByFile(filename + "Fixed.java");
    }
}
