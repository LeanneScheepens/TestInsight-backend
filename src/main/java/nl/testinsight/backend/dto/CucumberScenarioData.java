package nl.testinsight.backend.dto;
import tools.jackson.databind.JsonNode;
import nl.testinsight.backend.enums.TestStatus;

public record CucumberScenarioData (

        String featureId,
        String scenarioId,

        String name,
        TestStatus status,
        String failedStep,
        String errorMessage,
        JsonNode sourcePayload
) {
}
