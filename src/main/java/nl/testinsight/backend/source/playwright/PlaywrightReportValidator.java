package nl.testinsight.backend.source.playwright;


import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class PlaywrightReportValidator {
    public void validate(JsonNode report) {
        if (report == null || report.isNull()) {
            throw new IllegalArgumentException("report cannot be null");
        }

        if (!report.isObject()) {
            throw new IllegalArgumentException("report must be a JSON object");
        }

        JsonNode suites = report.path("suites");
        if (!suites.isArray() || suites.isEmpty()) {
            throw new IllegalArgumentException("report suites must be a non-empty array");
        }

        validateSuites(suites);
    }

    private void validateSuites(JsonNode suites) {
        for (JsonNode suite : suites) {
            if (!suite.isObject()) {
                throw new IllegalArgumentException("Each suite must be a JSON object");
            }

            requireText(suite, "title", "Suite title is required");

            JsonNode specs = suite.path("specs");
            if (!specs.isMissingNode()) {
                if (!specs.isArray()) {
                    throw new IllegalArgumentException("Suite specs must be an array");
                }
                validateSpecs(specs);
            }

            JsonNode childSuites = suite.path("suites");
            if (!childSuites.isMissingNode()) {
                if (!childSuites.isArray()) {
                    throw new IllegalArgumentException("Suite children must be an array");
                }
                validateSuites(childSuites);
            }
        }
    }

    private void validateSpecs(JsonNode specs) {
        for (JsonNode spec : specs) {
            if (!spec.isObject()) {
                throw new IllegalArgumentException("Each spec must be a JSON object");
            }

            requireText(spec, "title", "Spec title is required");

            JsonNode tests = spec.path("tests");
            if (!tests.isArray()) {
                throw new IllegalArgumentException("Spec tests must be an array");
            }

            for (JsonNode test : tests) {
                if (!test.isObject()) {
                    throw new IllegalArgumentException("Each test must be a JSON object");
                }

                JsonNode results = test.path("results");
                if (!results.isArray()) {
                    throw new IllegalArgumentException("Test results must be an array");
                }

                for (JsonNode result : results) {
                    if (!result.isObject()) {
                        throw new IllegalArgumentException("Each result must be a JSON object");
                    }

                    JsonNode steps = result.path("steps");
                    if (!steps.isMissingNode() && !steps.isArray()) {
                        throw new IllegalArgumentException("Result steps must be an array");
                    }
                }
            }
        }
    }

    private void requireText(JsonNode node, String field, String message) {
        if (!node.hasNonNull(field) || node.path(field).asText().isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}