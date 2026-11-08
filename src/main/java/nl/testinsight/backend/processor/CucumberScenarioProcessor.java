package nl.testinsight.backend.processor;
import tools.jackson.databind.JsonNode;
import nl.testinsight.backend.dto.CucumberScenarioData;
import nl.testinsight.backend.enums.TestStatus;
import nl.testinsight.backend.exception.InvalidCucumberReportException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



@Component
public class CucumberScenarioProcessor {

    public List<CucumberScenarioData> process(JsonNode report) {

        List<CucumberScenarioData> scenarios = new ArrayList<>();

        // Loop through all features in the JSON array.
        for (JsonNode feature : report) {

            String featureId = feature.path("id").asText("");

            // Get the elements belonging to this feature, so you know which scenario belongs to which test case.
            JsonNode elements = feature.path("elements");

            // Loop through the elements of the feature.
            for (JsonNode element : elements) {

                // Read the element type.
                String type = element.path("type").asText("");

                // Only process actual scenarios. Hooks are not separate test cases.
                if (!type.equals("scenario")
                        && !type.equals("scenario_outline")) {
                    continue;
                }

                // Read the scenario identifier.
                String scenarioId = element.path("id").asText("");

                if (featureId.isBlank() || scenarioId.isBlank()) {
                    throw new InvalidCucumberReportException(
                            "Feature or scenario ID is missing"
                    );
                }

                // Read the scenario name.
                String scenarioName = element.path("name").asText();


                //Calculate the complete scenario status.
                ScenarioOutcome outcome = calculateOutcome(element);

                 //Create an intermediate scenario object, it is used to store the scenario data before it is mapped to a test result entity.
                CucumberScenarioData scenario =
                        new CucumberScenarioData(
                                featureId,
                                scenarioId,
                                scenarioName,
                                outcome.status(),
                                outcome.failedStep(),
                                outcome.errorMessage(),
                                element.deepCopy()
                        );

                scenarios.add(scenario);
            }
        }

        return scenarios;
    }

    // Calculates the final status of a Cucumber scenario by evaluating normal steps as well as Before and After hooks.
    private ScenarioOutcome calculateOutcome(JsonNode scenario) {

        List<JsonNode> executions = new ArrayList<>();

        // Collect normal scenario steps.
        JsonNode steps = scenario.path("steps");

        if (steps.isArray()) {
            steps.forEach(executions::add);
        }

        // Collect Before hooks, if available.
        JsonNode beforeHooks = scenario.path("before");

        if (beforeHooks.isArray()) {
            beforeHooks.forEach(executions::add);
        }

        // Collect After hooks, if available.
        JsonNode afterHooks = scenario.path("after");

        if (afterHooks.isArray()) {
            afterHooks.forEach(executions::add);
        }

        TestStatus finalStatus = null;
        String failedStep = null;
        String errorMessage = null;


        // Loop through all executions (steps and hooks) to determine the final status of the scenario.
        for (JsonNode execution : executions) {

            JsonNode result = execution.path("result");

            String originalStatus =
                    result.path("status").asText("");

            if (originalStatus.isBlank()) {
                continue;
            }

            TestStatus currentStatus = mapStatus(originalStatus);

           // Keep the highest priority status as the final status of the scenario. The priority is defined in the TestStatus enum.
            if (finalStatus == null
                    || priority(currentStatus) > priority(finalStatus)) {

                finalStatus = currentStatus;
            }

            // Store the failed execution step and error message
            if (currentStatus == TestStatus.FAILED
                    && failedStep == null) {

                String stepName =
                        execution.path("name").asText("");

                failedStep = stepName.isBlank()
                        ? "Hook or unnamed step failed"
                        : stepName;

                JsonNode error = result.path("error_message");

                if (error.isTextual()) {
                    errorMessage = error.asText();
                }
            }
        }

        if (finalStatus == null) {
            finalStatus = TestStatus.UNKNOWN;
        }

        return new ScenarioOutcome(
                finalStatus,
                failedStep,
                errorMessage
        );
    }

    //Mapps the original Cucumber status strings to the internal TestStatus enum.
    private TestStatus mapStatus(String status) {

        return switch (status.toLowerCase(Locale.ROOT)) {
            case "passed" -> TestStatus.PASSED;
            case "failed" -> TestStatus.FAILED;
            case "skipped" -> TestStatus.SKIPPED;
            case "pending" -> TestStatus.PENDING;
            case "undefined" -> TestStatus.UNDEFINED;
            case "ambiguous" -> TestStatus.AMBIGUOUS;
            default -> TestStatus.UNKNOWN;
        };
    }

    private int priority(TestStatus status) {

        return switch (status) {
            case FAILED -> 7;
            case AMBIGUOUS -> 6;
            case UNDEFINED -> 5;
            case PENDING -> 4;
            case SKIPPED -> 3;
            case UNKNOWN -> 2;
            case PASSED -> 1;
        };
    }

    private record ScenarioOutcome(
            TestStatus status,
            String failedStep,
            String errorMessage
    ) {
    }
}





