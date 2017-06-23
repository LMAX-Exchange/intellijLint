/*
 *    Copyright 2017 LMAX Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lmax.intellijLint;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;

import java.util.List;

public class OptionalNullInspectionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/OptionalNullInspectionTest";
    }

    public void testOptionalNullAssignmentInMethod() throws Exception {
        doTest();
    }

    public void testOptionalNullAssignmentInFieldInitialization() throws Exception {
        doTest();
    }

    public void testOptionalNullAssignmentInVariableInitialization() throws Exception {
        doTest();
    }

    public void testOptionalLongNullAssignmentInMethod() throws Exception {
        doTest();
    }

    public void testOptionalLongNullAssignmentInFieldInitialization() throws Exception {
        doTest();
    }

    public void testOptionalLongNullAssignmentInVariableInitialization() throws Exception {
        doTest();
    }

    private void doTest() {
        doTest(getTestDirectoryName());
    }

    private void doTest(String filename) {
        myFixture.configureByFile(filename + ".java");
        myFixture.enableInspections(new OptionalNullInspection());

        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        Assert.assertEquals(
                1,
                highlightInfoList.stream()
                        .filter(x -> "Assigning null to optional".equals(x.getDescription()))
                        .count());

        myFixture.getAllQuickFixes()
                .stream()
                .filter(x -> x.getText().equals("Replace null assignment with optional.empty()"))
                .forEach(intention -> myFixture.launchAction(intention));

        myFixture.checkResultByFile(filename + "Fixed.java");
    }
}
