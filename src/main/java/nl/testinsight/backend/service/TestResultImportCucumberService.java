package nl.testinsight.backend.service;

import nl.testinsight.backend.dto.Cucumber.CucumberFeatureDto;
import nl.testinsight.backend.dto.Cucumber.CucumberReportDto;
import nl.testinsight.backend.dto.ImportResponse;
import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.processor.CucumberTestProcessor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@Service
public class TestResultImportCucumberService implements TestResultImportService {

    private final CucumberTestProcessor cucumberTestProcessor;
    private final TestResultService testResultService;

    public TestResultImportCucumberService(
            CucumberTestProcessor cucumberTestProcessor,
            TestResultService testResultService
    ) {
        this.cucumberTestProcessor = cucumberTestProcessor;
        this.testResultService = testResultService;
    }

    @Override
    public boolean supports(SourceType sourceType) {
        return SourceType.CUCUMBER == sourceType;
    }

    @Override
    public ImportResponse importResults(
            String applicationName,
            JsonNode jsonReport) {
        final ObjectMapper objectMapper = new ObjectMapper();

        CucumberFeatureDto[] cucumberFeatureDtos = objectMapper.treeToValue(jsonReport, CucumberFeatureDto[].class);
        CucumberReportDto cucumberReportDto = new CucumberReportDto(Arrays.asList(cucumberFeatureDtos));

        List<TestDataDto> testData = cucumberTestProcessor.process(cucumberReportDto);

        testResultService.saveTestResultData(jsonReport, testData, applicationName);

        return null;
    }
}
