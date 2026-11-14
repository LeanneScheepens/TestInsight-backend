package nl.testinsight.backend.service;

import nl.testinsight.backend.dto.Cucumber.CucumberFeatureDto;
import nl.testinsight.backend.dto.ImportResponse;
import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.dto.playwright.PlaywrightReportDto;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.processor.PlaywrightTestProcessor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class TestResultImportPlaywrightService implements TestResultImportService {

    private final PlaywrightTestProcessor playwrightTestProcessor;
    private final TestResultService testResultService;

    public TestResultImportPlaywrightService(
            PlaywrightTestProcessor playwrightTestProcessor,
            TestResultService testResultService
    ) {

        this.playwrightTestProcessor = playwrightTestProcessor;
        this.testResultService = testResultService;
    }

    public boolean supports(SourceType sourceType) {
        return SourceType.PLAYWRIGHT == sourceType;
    }

    @Override
    public ImportResponse importResults(
            String applicationName,
            JsonNode jsonReport) {
        final ObjectMapper objectMapper = new ObjectMapper();

        PlaywrightReportDto playwrightReportDto = objectMapper.treeToValue(jsonReport, PlaywrightReportDto.class);
        List<TestDataDto> testData = playwrightTestProcessor.process(playwrightReportDto);

        testResultService.saveTestResultData(jsonReport, testData, applicationName);

        return null;
    }
}

