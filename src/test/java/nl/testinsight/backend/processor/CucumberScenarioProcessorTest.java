package nl.testinsight.backend.processor;

import tools.jackson.databind.JsonNode;
import nl.testinsight.backend.dto.CucumberScenarioData;
import nl.testinsight.backend.enums.TestStatus;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class CucumberScenarioProcessorTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CucumberScenarioProcessor processor =
            new CucumberScenarioProcessor();

    @Test
    void shouldReturnPassedWhenAllStepsPass() throws Exception {

        String json = """
                [
                  {
                    "id": "login-feature",
                    "name": "Login",
                    "elements": [
                      {
                        "id": "login-feature;successful-login",
                        "name": "Successful login",
                        "type": "scenario",
                        "steps": [
                          {
                            "name": "Enter username",
                            "result": {"status": "passed"}
                          },
                          {
                            "name": "Click login",
                            "result": {"status": "passed"}
                          }
                        ]
                      }
                    ]
                  }
                ]
                """;

        JsonNode report = objectMapper.readTree(json);

        List<CucumberScenarioData> scenarios = processor.process(report);

        assertEquals(1, scenarios.size());
        assertEquals(TestStatus.PASSED, scenarios.get(0).status());
        assertNull(scenarios.get(0).failedStep());
        assertNull(scenarios.get(0).errorMessage());
    }

    @Test
    void shouldReturnFailedWhenStepFails() throws Exception {

        String json = """
                [
                  {
                    "id": "login-feature",
                    "name": "Login",
                    "elements": [
                      {
                        "id": "login-feature;failed-login",
                        "name": "Failed login",
                        "type": "scenario",
                        "steps": [
                          {
                            "name": "Click login",
                            "result": {
                              "status": "failed",
                              "error_message": "Login button not found"
                            }
                          }
                        ]
                      }
                    ]
                  }
                ]
                """;

        JsonNode report = objectMapper.readTree(json);

        List<CucumberScenarioData> scenarios = processor.process(report);

        assertEquals(TestStatus.FAILED, scenarios.get(0).status());
        assertEquals("Click login", scenarios.get(0).failedStep());
        assertEquals("Login button not found",
                scenarios.get(0).errorMessage());
    }

    @Test
    void shouldReturnUnknownWhenNoStepsHaveResults() throws Exception {

        String json = """
                [
                  {
                    "id": "login-feature",
                    "name": "Login",
                    "elements": [
                      {
                        "id": "login-feature;empty-scenario",
                        "name": "Empty scenario",
                        "type": "scenario",
                        "steps": []
                      }
                    ]
                  }
                ]
                """;

        JsonNode report = objectMapper.readTree(json);

        List<CucumberScenarioData> scenarios = processor.process(report);

        assertEquals(TestStatus.UNKNOWN, scenarios.get(0).status());
    }
}




