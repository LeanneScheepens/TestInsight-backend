package nl.testinsight.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.testinsight.backend.dto.CucumberResultDto;
import nl.testinsight.backend.service.TestResultImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/import")
@Tag(name = "Test result imports", description = "Import test results from test frameworks")
public class TestResultImportController {
    private final Logger logger = LoggerFactory.getLogger(TestResultImportController.class);
    private final TestResultImportService testResultImportService;

    public TestResultImportController(TestResultImportService testResultImportService) {
        this.testResultImportService = testResultImportService;
    }

    @PostMapping("/cucumber")
    @Operation(summary = "Import Cucumber results", description = "Stores a list of Cucumber test results.")

    public void importCucumberResults(
            @RequestBody List<CucumberResultDto> results) {
        logger.info("Importing Cucumber results: " + results);

        testResultImportService.importCucumberResults(results);
    }
}
