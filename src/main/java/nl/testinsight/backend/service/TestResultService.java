package nl.testinsight.backend.service;

import jakarta.transaction.Transactional;
import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.entity.ApplicationEntity;
import nl.testinsight.backend.entity.TestCaseEntity;
import nl.testinsight.backend.entity.TestRawDataEntity;
import nl.testinsight.backend.entity.TestResultEntity;
import nl.testinsight.backend.mapper.TestCaseMapper;
import nl.testinsight.backend.mapper.TestResultMapper;
import nl.testinsight.backend.repository.RawDataRepository;
import nl.testinsight.backend.repository.TestCaseRepository;
import nl.testinsight.backend.repository.TestResultRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TestResultService {

    public final TestResultRepository testResultRepository;
    public final ApplicationService applicationService;
    public final TestCaseMapper testCaseMapper;
    public final TestResultMapper testResultMapper;
    public final TestCaseRepository testCaseRepository;
    public final RawDataRepository rawDataRepository;

    public TestResultService(
            TestResultRepository testResultRepository,
            ApplicationService applicationService,
            TestCaseMapper testCaseMapper,
            TestResultMapper testResultMapper,
            TestCaseRepository testCaseRepository, RawDataRepository rawDataRepository
    ) {
        this.testResultRepository = testResultRepository;
        this.applicationService = applicationService;
        this.testCaseMapper = testCaseMapper;
        this.testResultMapper = testResultMapper;
        this.testCaseRepository = testCaseRepository;
        this.rawDataRepository = rawDataRepository;
    }


    @Transactional
    public void saveTestResultData(JsonNode jsonReport, List<TestDataDto> testData, String applicationName) {


        UUID runId = UUID.randomUUID();

        ApplicationEntity applicationEntity = applicationService.findOrCreateApplication(applicationName);
        TestRawDataEntity testRawDataEntity = new TestRawDataEntity();
        testRawDataEntity.setSourcePayload(jsonReport);
        rawDataRepository.save(testRawDataEntity);

        List<TestResultEntity> resultEntities = new ArrayList<>();
        TestCaseEntity testCaseEntity;
        for (TestDataDto data : testData) {
            // Opzoeken in test_case tabel en meegeven ipv de null. Of een nieuwe entity als je hem niet vindt.
            Optional<TestCaseEntity> storedTestCaseEntity = testCaseRepository.findByApplicationEntity_IdAndSourceTypeAndFeatureIdAndScenarioId(applicationEntity.getId(), data.sourceType(), data.groupId(), data.scenarioId());
            testCaseEntity = storedTestCaseEntity.orElseGet(() -> testCaseMapper.mapTestCase(data, applicationEntity, testRawDataEntity));
            testCaseRepository.save(testCaseEntity);

            TestResultEntity testResultEntity = testResultMapper.mapTestResult(data, testCaseEntity, runId);

            resultEntities.add(testResultEntity);
        }

//        testCaseRepository.saveAll(testCaseResults.values());
        testResultRepository.saveAll(resultEntities);


    }
}
