package nl.testinsight.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.testinsight.backend.dto.Cucumber.CucumberFeatureDto;
import nl.testinsight.backend.dto.Cucumber.CucumberReportDto;
import nl.testinsight.backend.dto.ImportResponse;
import nl.testinsight.backend.dto.playwright.PlaywrightReportDto;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.service.TestResultImportCucumberService;
import nl.testinsight.backend.service.TestResultImportPlaywrightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;

@RestController
// Spring boot uses this to recognize the class, it is a component that handles HTTP requests and responses.
@RequestMapping("/api/import")  // Endpoint
@Tag(name = "Test result imports", description = "Import test results from test frameworks")

public class TestResultImportController {

    private final Logger logger = LoggerFactory.getLogger(TestResultImportController.class); // Logger for logging messages, useful for debugging and monitoring the application.
    private final TestResultImportPlaywrightService testResultImportPlaywrightService; // Handles the business logic
    private final TestResultImportCucumberService testResultImportCucumberService;
    private final ObjectMapper objectMapper;

    public TestResultImportController(
            TestResultImportPlaywrightService testResultImportPlaywrightService,
            TestResultImportCucumberService testResultImportCucumberService,
            ObjectMapper objectMapper
    ) {
        this.testResultImportPlaywrightService = testResultImportPlaywrightService;
        this.testResultImportCucumberService = testResultImportCucumberService;
        this.objectMapper = objectMapper;
    }


    @PostMapping("/cucumber") // Every test framework had its own endpoint
    @Operation(summary = "Import Cucumber results", description = "Stores a list of Cucumber test results.")
    public ImportResponse importCucumberResults(

            @RequestHeader("X-Application-Name") // The @RequestHeader annotation tells Spring to bind the value of the X-Application-Name HTTP header to the applicationName parameter. This allows the client to specify the name of the application that is sending the test results.
            String applicationName,
            @RequestBody JsonNode jsonReport) { // The @RequestBody annotation tells Spring to bind the incoming HTTP request body to the report parameter. The report is expected to be a JSON object representing the Cucumber test results.

        // Convert JSON array to a cucumberFeatureDto s
        CucumberFeatureDto[] cucumberFeatureDtos = objectMapper.treeToValue(jsonReport, CucumberFeatureDto[].class);

        CucumberReportDto cucumberReportDto = new CucumberReportDto(Arrays.asList(cucumberFeatureDtos));

        return testResultImportCucumberService.importCucumberResults(
                applicationName,
                cucumberReportDto,
                SourceType.CUCUMBER,
                jsonReport
        );
    }

    @PostMapping("/playwright") // Every test framework had its own endpoint
    @Operation(summary = "Import Playwright results", description = "Stores a list of Playwright test results.")
    public ImportResponse importPlaywrightJsonResults(

            @RequestHeader("X-Application-Name") // The @RequestHeader annotation tells Spring to bind the value of the X-Application-Name HTTP header to the applicationName parameter. This allows the client to specify the name of the application that is sending the test results.
            String applicationName,
            @RequestBody JsonNode jsonReport) { // The @RequestBody annotation tells Spring to bind the incoming HTTP request body to the report parameter. The report is expected to be a JSON object representing the Cucumber test results.

        PlaywrightReportDto playwrightReportDto = objectMapper.treeToValue(jsonReport, PlaywrightReportDto.class);

        return testResultImportPlaywrightService.importPlaywrightResults(applicationName, playwrightReportDto, SourceType.PLAYWRIGHT, jsonReport);
    }
}
