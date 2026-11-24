package nl.testinsight.backend.common.service;

import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.entity.ApplicationEntity;
import nl.testinsight.backend.common.entity.TestCaseEntity;
import nl.testinsight.backend.common.entity.TestRawDataEntity;
import nl.testinsight.backend.common.mapper.TestCaseMapper;
import nl.testinsight.backend.common.repository.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestCaseService {

    public final TestCaseMapper testCaseMapper;
    public final TestCaseRepository testCaseRepository;

    public TestCaseService(
            TestCaseMapper testCaseMapper,
            TestCaseRepository testCaseRepository
    ) {
        this.testCaseMapper = testCaseMapper;
        this.testCaseRepository = testCaseRepository;
    }

    public TestCaseEntity findOrCreateTestCase( TestDataDto data, ApplicationEntity applicationEntity, TestRawDataEntity rawData) {

        return testCaseRepository.findByApplicationEntity_IdAndSourceTypeAndFeatureIdAndScenarioId(applicationEntity.getId(), data.sourceType(), data.groupId(), data.scenarioId())
                .orElseGet(() -> testCaseRepository.save(testCaseMapper.mapTestCase(data, applicationEntity, rawData)));
    }

}