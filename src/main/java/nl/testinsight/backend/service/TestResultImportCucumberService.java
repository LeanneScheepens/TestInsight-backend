package nl.testinsight.backend.service;

import nl.testinsight.backend.dto.Cucumber.CucumberReportDto;
import nl.testinsight.backend.dto.ImportResponse;
import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.processor.CucumberTestProcessor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Service
public class TestResultImportCucumberService {

    private final CucumberTestProcessor cucumberTestProcessor;
    private final TestResultService testResultService;

    public TestResultImportCucumberService(
            CucumberTestProcessor cucumberTestProcessor,
            TestResultService testResultService
    ) {
        this.cucumberTestProcessor = cucumberTestProcessor;
        this.testResultService = testResultService;
    }

    public ImportResponse importCucumberResults(
            String applicationName,
            CucumberReportDto report,
            SourceType sourceType,
            JsonNode jsonReport
    ) {
        List<TestDataDto> testData = cucumberTestProcessor.process(report);

        testResultService.saveTestResultData(jsonReport, testData, applicationName);

        return null;
    }
}
