package com.bengous.e2e.common.assertions;

import com.bengous.e2e.common.Prettifier;
import io.qameta.allure.Allure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.xmlunit.assertj3.XmlAssert;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.*;
import org.xmlunit.placeholder.PlaceholderDifferenceEvaluator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlAssertion {
    private static class IgnoreChildNodesOrderEvaluator implements DifferenceEvaluator {
        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            return comparison.getType() == ComparisonType.CHILD_NODELIST_SEQUENCE ? ComparisonResult.EQUAL : outcome;
        }
    }

    private static final DifferenceEvaluator DIFFERENCE_EVALUATORS = DifferenceEvaluators.chain(
            DifferenceEvaluators.Default,
            new IgnoreChildNodesOrderEvaluator(),
            new PlaceholderDifferenceEvaluator()
    );

    public static void checkActualAndExpectedNodesAreSimilar(
            String stepName, String actualEnvelope, String expectedEnvelope
    ) {
        Allure.step(stepName, () -> {
            Allure.addAttachment("Expected", "text/plain", Prettifier.prettyPrintXml(expectedEnvelope));
            Allure.addAttachment("Actual", "text/plain", Prettifier.prettyPrintXml(actualEnvelope));
            try {
                XmlAssert
                        .assertThat(Input.fromString(actualEnvelope))
                        .and(Input.fromString(expectedEnvelope))
                        .ignoreWhitespace()
                        .normalizeWhitespace()
                        .ignoreComments()
                        .ignoreElementContentWhitespace()
                        .ignoreChildNodesOrder()
                        .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                        .withDifferenceEvaluator(DIFFERENCE_EVALUATORS)
                        .as("L'enveloppe SOAP envoyée doit être similaire à celle du template")
                        .areSimilar();
            } catch (AssertionError e) {
                var diff = DiffBuilder
                        .compare(Input.fromString(expectedEnvelope))
                        .withTest(Input.fromString(actualEnvelope))
                        .ignoreWhitespace()
                        .normalizeWhitespace()
                        .ignoreComments()
                        .ignoreElementContentWhitespace()
                        .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                        .withDifferenceEvaluator(DIFFERENCE_EVALUATORS)
                        .checkForSimilar()
                        .build();

                var diffHtml = generateHtmlDiffTable(diff);
                byte[] diffBytes = diffHtml.getBytes(StandardCharsets.UTF_8);
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(diffBytes)) {
                    Allure.addAttachment("Table des différences", "text/html", inputStream, "html");
                } catch (IOException err) {
                    throw e; // juste propager l'AssertionError en cas d'erreur de création du diff visuel
                }

                throw e; // propager l'AssertionError
            }
        });
    }

    @Language("HTML")
    private static final String DIFF_HEADER = """
            <html lang="fr">
            <style>
                table {
                    width: 100%;
                    border-collapse: collapse;
                }
                th, td {
                    border: 1px solid black;
                    padding: 8px;
                    text-align: left;
                }
                th {
                    background-color: #f2f2f2;
                }
            </style>
            <body>
                <table>
                    <tr><th>XPath</th><th>Expected</th><th>Actual</th></tr>
            """;

    @Language("HTML")
    private static final String DIFF_ROW = """
            <tr>
                <td>%s</td>
                <td style='background-color: green; color: white; font-weight: bold'>%s</td>
                <td style='background-color: red; color: white; font-weight: bold'>%s</td>
            </tr>
            """;

    @Language("HTML")
    private static final String DIFF_FOOTER = """
                </table>
            </body>
            </html>
            """;

    private static String generateHtmlDiffTable(Diff diff) {
        var builder = new StringBuilder();
        builder.append(DIFF_HEADER);

        for (Difference difference : diff.getDifferences()) {
            var comparison = difference.getComparison();
            var controlDetails = comparison.getControlDetails();
            var testDetails = comparison.getTestDetails();

            var row = String.format(
                    DIFF_ROW,
                    controlDetails.getXPath(),
                    controlDetails.getValue(),
                    testDetails.getValue()
            );
            builder.append(row);
        }

        builder.append(DIFF_FOOTER);
        return builder.toString();
    }
}
