package nl.testinsight.backend.common.service;

import jakarta.transaction.Transactional;
import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.dto.TestStatusCountDto;
import nl.testinsight.backend.common.dto.TestStatusTrendDto;
import nl.testinsight.backend.common.entity.ApplicationEntity;
import nl.testinsight.backend.common.entity.TestCaseEntity;
import nl.testinsight.backend.common.entity.TestRawDataEntity;
import nl.testinsight.backend.common.entity.TestResultEntity;
import nl.testinsight.backend.common.mapper.TestResultMapper;
import nl.testinsight.backend.common.repository.TestResultRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TestResultService {

    public final TestResultRepository testResultRepository;
    public final ApplicationService applicationService;
    public final TestResultMapper testResultMapper;
    public final RawDataService rawDataService;
    public final TestCaseService testCaseService;

    public TestResultService(
            TestResultRepository testResultRepository,
            ApplicationService applicationService,
            TestResultMapper testResultMapper,
            RawDataService rawDataService,
            TestCaseService testCaseService
    ) {
        this.testResultRepository = testResultRepository;
        this.applicationService = applicationService;
        this.testResultMapper = testResultMapper;
        this.rawDataService = rawDataService;
        this.testCaseService = testCaseService;
    }


    @Transactional
    public UUID saveTestResultData(JsonNode jsonReport, List<TestDataDto> testData, String applicationName) {


        UUID runId = UUID.randomUUID();

        ApplicationEntity applicationEntity = applicationService.findOrCreateApplication(applicationName);

        TestRawDataEntity rawData =
                rawDataService.saveRawData(jsonReport);

        List<TestResultEntity> resultEntities = new ArrayList<>();

        for (TestDataDto data : testData) {

            TestCaseEntity testCase =
                    testCaseService.findOrCreateTestCase(
                            data, applicationEntity, rawData
                    );

            TestResultEntity result =
                    testResultMapper.mapTestResult(
                            data, testCase, runId
                    );

            resultEntities.add(result);
        }
        testResultRepository.saveAll(resultEntities);

        return runId;
    }

    public List<TestStatusCountDto> getLatestTestResultsWithStatus() {
        return testResultRepository.countLatestTestResultsWithStatus();
    }

    public List<TestStatusTrendDto> getStatusTrendResultsWithExecutionDate(){
        return testResultRepository.findTestStatusTrend();
    }
}
