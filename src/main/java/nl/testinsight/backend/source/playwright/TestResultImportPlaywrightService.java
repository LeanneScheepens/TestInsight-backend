package nl.testinsight.backend.source.playwright;

import nl.testinsight.backend.common.dto.ImportResponseDto;
import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.service.TestResultImportService;
import nl.testinsight.backend.common.service.TestResultService;
import nl.testinsight.backend.source.playwright.dto.PlaywrightReportDto;
import nl.testinsight.backend.common.enums.SourceType;
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
    public ImportResponseDto importResults(
            String applicationName,
            JsonNode jsonReport) {
        final ObjectMapper objectMapper = new ObjectMapper();

        PlaywrightReportDto playwrightReportDto = objectMapper.treeToValue(jsonReport, PlaywrightReportDto.class);
        List<TestDataDto> testData = playwrightTestProcessor.process(playwrightReportDto);

        testResultService.saveTestResultData(jsonReport, testData, applicationName);

        return null;
    }
}

