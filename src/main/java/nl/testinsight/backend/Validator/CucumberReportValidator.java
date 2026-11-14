package nl.testinsight.backend.Validator;
import nl.testinsight.backend.enums.SourceType;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;


@Component
public class CucumberReportValidator {
    public void validate(JsonNode report) {
        // Validate the Cucumber JSON structure
        if (report == null || report.isNull()) {
            throw new IllegalArgumentException(
                    "report cannot be null"
            );
        }

        if (!report.isArray()) {
            throw new IllegalArgumentException(
                    "report must be a JSON array"
            );
        }

        if (report.isEmpty()) {
            throw new IllegalArgumentException(
                    "report cannot be empty"
            );
        }

        for (JsonNode feature : report) { // Validate each feature in the report, feature is a JSON object that contains scenarios and steps.

            if (!feature.isObject()) {
                throw new IllegalArgumentException(
                        "Each feature must be a JSON object"
                );
            }

            if (!feature.hasNonNull("name") // Jackson method
                    || feature.path("name").asText().isBlank()) {
                throw new IllegalArgumentException(
                        "Feature name is required"
                );
            }

            JsonNode elements = feature.path("elements"); // elements is a JSON array that contains the scenarios for the feature.

            if (!elements.isArray()) {
                throw new IllegalArgumentException(
                        "Feature elements must be an array"
                );
            }

            for (JsonNode scenario : elements) {

                if (!scenario.isObject()) {
                    throw new IllegalArgumentException(
                            "Each scenario must be a JSON object"
                    );
                }

                if (!scenario.hasNonNull("name")
                        || scenario.path("name").asText().isBlank()) {
                    throw new IllegalArgumentException(
                            "Scenario name is required"
                    );
                }

                JsonNode steps = scenario.path("steps");

                if (!steps.isMissingNode() && !steps.isArray()) { // Look if it can be generic
                    throw new IllegalArgumentException(
                            "Scenario steps must be an array"
                    );
                }
            }
        }
    }

//    @Override
//    public SourceType supports() {
//        return SourceType.CUCUMBER;
//    }

//    @Override
//    public void validate(JsonNode report) {
//
//    }
}
