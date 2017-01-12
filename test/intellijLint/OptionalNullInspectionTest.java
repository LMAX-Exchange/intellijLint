package intellijLint;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;

import java.util.List;

public class OptionalNullInspectionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "./testdata/OptionalNullInspectionTest/";
    }

    public void testShouldFindNullAssignment() throws Exception {
        myFixture.configureByFile("HasOptionalNulls.java");
        myFixture.enableInspections(new OptionalNullInspection());

        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
        Assert.assertTrue(highlightInfos.stream()
                .anyMatch(x -> "Assigning null to optional".equals(x.getDescription())));

        final IntentionAction intention = myFixture.getAllQuickFixes()
                .stream()
                .filter(x -> x.getText().equals("Replace null assignment with optional.empty()"))
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(intention);

        myFixture.launchAction(intention);

        myFixture.checkResultByFile("HasOptionalNullsFixed.java");
    }
}
