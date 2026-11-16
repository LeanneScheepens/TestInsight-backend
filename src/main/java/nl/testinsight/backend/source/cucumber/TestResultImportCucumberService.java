package nl.testinsight.backend.source.cucumber;

import nl.testinsight.backend.common.service.TestResultImportService;
import nl.testinsight.backend.common.service.TestResultService;
import nl.testinsight.backend.source.cucumber.dto.CucumberFeatureDto;
import nl.testinsight.backend.source.cucumber.dto.CucumberReportDto;
import nl.testinsight.backend.common.dto.ImportResponseDto;
import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.enums.SourceType;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@Service
public class TestResultImportCucumberService implements TestResultImportService {

    private final CucumberTestProcessor cucumberTestProcessor;
    private final TestResultService testResultService;
    private final CucumberReportValidator cucumberReportValidator;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TestResultImportCucumberService(
            CucumberTestProcessor cucumberTestProcessor,
            TestResultService testResultService, CucumberReportValidator cucumberReportValidator
    ) {
        this.cucumberTestProcessor = cucumberTestProcessor;
        this.testResultService = testResultService;
        this.cucumberReportValidator = cucumberReportValidator;
    }

    @Override
    public boolean supports(SourceType sourceType) {
        return SourceType.CUCUMBER == sourceType;
    }

    @Override
    public ImportResponseDto importResults(
            String applicationName,
            JsonNode jsonReport) {
        cucumberReportValidator.validate(jsonReport);

        CucumberFeatureDto[] cucumberFeatureDtos = objectMapper.treeToValue(jsonReport, CucumberFeatureDto[].class);
        CucumberReportDto cucumberReportDto = new CucumberReportDto(Arrays.asList(cucumberFeatureDtos));

        List<TestDataDto> testData = cucumberTestProcessor.process(cucumberReportDto);

        testResultService.saveTestResultData(jsonReport, testData, applicationName);

//        return new ImportResponse();
        return null;
    }
}
