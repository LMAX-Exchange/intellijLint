package com.lmax.intellijLint.UnitsInspectionTests;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.lmax.intellijLint.Units.SubType;
import com.lmax.intellijLint.Units.UnitsInspection;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class Base extends LightCodeInsightFixtureTestCase {
    private static final Stream<String> DESCRIPTION_TEMPLATES = Stream.of(
            UnitsInspection.DESCRIPTION_TEMPLATE,
            UnitsInspection.RETURNING_DESCRIPTION_TEMPLATE,
            UnitsInspection.BINARY_EXPRESSION_DESCRIPTION_TEMPLATE,
            UnitsInspection.FAILED_TO_RESOLVE,
            UnitsInspection.POLYADIC_MISMATCH,
            UnitsInspection.ARGUMENT_TEMPLATE);
    private static final String TEMPLATES_AS_REGEX_GROUPS = DESCRIPTION_TEMPLATES
            .map(s -> s.replace("%s", ".*"))
            .map(s -> "(" + s + ")")
            .reduce((s1, s2) -> String.join("|", s1, s2)).get();
    private static final Pattern UNITS_DESCRIPTIONS = Pattern.compile("^(" + TEMPLATES_AS_REGEX_GROUPS + ")$");

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/UnitsInspectionTest/" + this.getClass().getSimpleName();
    }

    void expectInspection(String expectedMessage) {
        final Pattern pattern = Pattern.compile("^" + Pattern.quote(expectedMessage) + "$");
        expectInspectionMatching(pattern, 1);
    }

    void expectNoInspections() {
        expectInspectionMatching(UNITS_DESCRIPTIONS, 0);
    }

    private void expectInspectionMatching(Pattern pattern, int count) {
        myFixture.configureByFile(getTestDirectoryName() + ".java");
        UnitsInspection unitsInspection = new UnitsInspection();
        SubType.setAnnotations(Arrays.asList("org.checkerframework.framework.qual.SubtypeOf"));
        myFixture.enableInspections(unitsInspection);

        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        final List<HighlightInfo> matchingInspections = highlightInfoList.stream()
                .filter(x -> x.getDescription() != null)
                .filter(x -> pattern.matcher(x.getDescription()).matches())
                .collect(Collectors.toList());

        final List<HighlightInfo> allUnitInspections = highlightInfoList.stream()
                .filter(x -> x.getDescription() != null)
                .filter(x -> UNITS_DESCRIPTIONS.matcher(x.getDescription()).matches())
                .collect(Collectors.toList());

        if (count == 0) {
            Assert.assertEquals("Expected no inspections matching " + pattern.toString(),
                    Collections.emptyList(),
                    matchingInspections);
        } else {
            Assert.assertEquals("Did not find expected number of inspections matching " +
                            pattern.toString() +
                            "\n " +
                            "Did find:\n " +
                            "[" +
                            allUnitInspections
                                    .stream()
                                    .map(HighlightInfo::toString)
                                    .collect(Collectors.joining(",\n"))
                            + "]\n",
                    count,
                    matchingInspections.size());
        }

        Assert.assertEquals("Found unexpected Units inspections:", matchingInspections, allUnitInspections);
    }
}
