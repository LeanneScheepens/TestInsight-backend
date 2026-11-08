package nl.testinsight.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.testinsight.backend.service.TestResultImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import nl.testinsight.backend.dto.ImportResponse;

@RestController // Spring boot uses this to recognize the class, it is a component that handles HTTP requests and responses.
@RequestMapping("/api/import")  // Endpoint
@Tag(name = "Test result imports", description = "Import test results from test frameworks")

public class TestResultImportController {

    private final Logger logger = LoggerFactory.getLogger(TestResultImportController.class); // Logger for logging messages, useful for debugging and monitoring the application.
    private final TestResultImportService testResultImportService; // Handles the business logic

    public TestResultImportController(TestResultImportService testResultImportService) {
        this.testResultImportService = testResultImportService;
    }


    @PostMapping("/cucumber") // Every test framework had its own endpoint
    @Operation(summary = "Import Cucumber results", description = "Stores a list of Cucumber test results.")

    public ImportResponse importCucumberResults(

            @RequestHeader("X-Application-Name") // The @RequestHeader annotation tells Spring to bind the value of the X-Application-Name HTTP header to the applicationName parameter. This allows the client to specify the name of the application that is sending the test results.
            String applicationName,
            @RequestBody JsonNode report) { // The @RequestBody annotation tells Spring to bind the incoming HTTP request body to the report parameter. The report is expected to be a JSON object representing the Cucumber test results.

        logger.info("Importing Cucumber results: ");

        return testResultImportService.importCucumberResults(applicationName, report);
    }
}
