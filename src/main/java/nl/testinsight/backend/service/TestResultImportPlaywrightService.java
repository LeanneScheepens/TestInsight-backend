package nl.testinsight.backend.service;

import nl.testinsight.backend.dto.ImportResponse;
import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.dto.playwright.PlaywrightReportDto;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.processor.PlaywrightTestProcessor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Service
public class TestResultImportPlaywrightService {

    private final PlaywrightTestProcessor playwrightTestProcessor;
    private final TestResultService testResultService;

    public TestResultImportPlaywrightService(

            PlaywrightTestProcessor playwrightTestProcessor,
            TestResultService testResultService
    ) {

        this.playwrightTestProcessor = playwrightTestProcessor;
        this.testResultService = testResultService;
    }

    public ImportResponse importPlaywrightResults(
            String applicationName,
            PlaywrightReportDto report,
            SourceType sourceType,
            JsonNode jsonReport) {

        List<TestDataDto> testData = playwrightTestProcessor.process(report);

        testResultService.saveTestResultData(jsonReport, testData, applicationName);

        return null;
    }
}

