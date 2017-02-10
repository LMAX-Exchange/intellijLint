package com.lmax.intellijLint;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;
import org.junit.Ignore;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class UnitsInspectionTest extends LightCodeInsightFixtureTestCase {
    private static final Stream<String> DESCRIPTION_TEMPLATES = Stream.of(
            UnitsInspection.DESCRIPTION_TEMPLATE,
            UnitsInspection.RETURNING_DESCRIPTION_TEMPLATE,
            UnitsInspection.BINARY_EXPRESSION_DESCRIPTION_TEMPLATE);
    private static final String TEMPLATES_AS_REGEX_GROUPS = DESCRIPTION_TEMPLATES
            .map(s -> s.replace("%s", ".*"))
            .map(s -> "(" + s + ")")
            .reduce((s1, s2) -> String.join("|", s1, s2)).get();
    private static final Pattern UNITS_DESCRIPTIONS = Pattern.compile("^(" + TEMPLATES_AS_REGEX_GROUPS + ")$");

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
        doTest("foo", "null");
    }

    public void testReturnUntyped() throws Exception {
        doTest("Returning null when expecting foo");
    }

//    public void testReturnVariable() throws Exception {
//        doTest(UNITS_DESCRIPTIONS, 0);
//    }

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
        final Pattern pattern = Pattern.compile("^" + Pattern.quote(expectedMessage) + "$");
        doTest(pattern, 1);
    }

    private void doTest(Pattern pattern, int count) {
        myFixture.configureByFile(getTestDirectoryName() + ".java");
        UnitsInspection unitsInspection = new UnitsInspection();
        unitsInspection.subTypeAnnotations.add("org.checkerframework.framework.qual.SubtypeOf");
        myFixture.enableInspections(unitsInspection);


        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        Assert.assertEquals(
                count,
                highlightInfoList.stream()
                        .filter(x -> x.getDescription() != null)
                        .filter(x -> pattern.matcher(x.getDescription()).matches())
                        .count());
    }
}
