package nl.testinsight.backend.source.cucumber;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class CucumberReportValidatorTest {

    private final CucumberReportValidator validator = new CucumberReportValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldRejectNullReport() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(null)
        );
        assertEquals("report cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectReportThatIsNotArray(){
       // Arrange
      JsonNode report = objectMapper.readTree("""     
        {
            "name" : "login feature"
        }
        """);
      // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(report)
        );
        // Assert
        assertEquals("report must be a JSON array",
                exception.getMessage());
    }

    @Test
    void shouldRejectEmptyReport(){
        // Arrange
        JsonNode report = objectMapper.readTree("[]");

        //Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(report)
        );
        // Asserts
        assertEquals("report cannot be empty",
               exception.getMessage());
    }

    @Test
    void shouldfRejectFeatureThatIsNotObject(){
        JsonNode report = objectMapper.readTree("""
                ["invalid feature"]
                """);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(report)
        );
        assertEquals("Each feature must be a JSON object",
                exception.getMessage());
    }

    @Test
    void shouldRejectFeatureWithoutName(){
        JsonNode report = objectMapper.readTree(
                """
                        [{"feature" : [] }]""");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> validator.validate(report)
        );
        assertEquals("Feature name is required", exception.getMessage());
    }

    @Test
    void shouldRejectElementThatIsNotArray(){
        JsonNode report = objectMapper.readTree("""
                [
                    {
                        "name": "Login feature",
                        "elements": "invalid"
                    }
                ]
                """);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> validator.validate(report)
        );
        assertEquals("Feature elements must be an array", exception.getMessage());
    }

    @Test
    void shouldRejectScenarioThatIsNotObject(){
        JsonNode report = objectMapper.readTree("""
                [
                    {
                    "name" : "login feature",
                    "elements" : ["invalid scenario"]
                    }
                ]
                """
        );
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->validator.validate(report)
        );
        assertEquals( "Each scenario must be a JSON object", exception.getMessage());
    }

    @Test
    void shouldRejectScenarioThatIsNull(){
        JsonNode report = objectMapper.readTree("""
                [
                {
                "name" : "login feature",
                "elements" : [{}]
                }
                ]"""
        );
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->validator.validate(report)
        );
        assertEquals("Scenario name is required", exception.getMessage());
    }

    @Test
    void shouldRejectScenarioThatIsNotArray(){
        JsonNode report = objectMapper.readTree("""
                [
                {
                "name" : "login feature",
                "elements" : [{"name" : "invalid scenario", "steps" : "invalid steps"}]
                }
                ]""");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> validator.validate(report)
        );
        assertEquals("Scenario steps must be an array", exception.getMessage());
    }
}