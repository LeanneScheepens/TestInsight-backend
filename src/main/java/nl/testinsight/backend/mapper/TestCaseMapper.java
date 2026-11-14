package nl.testinsight.backend.mapper;

import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.entity.ApplicationEntity;
import nl.testinsight.backend.entity.TestCaseEntity;
import nl.testinsight.backend.entity.TestRawDataEntity;
import org.springframework.stereotype.Component;

@Component
public class TestCaseMapper {

    public TestCaseEntity mapTestCase(TestDataDto testcase, ApplicationEntity applicationEntity, TestRawDataEntity rawDataEntity) {
        TestCaseEntity entity = new TestCaseEntity();
        entity.setName(testcase.name());
        entity.setApplicationEntity(applicationEntity);
        entity.setSourceType(testcase.sourceType());
        entity.setFeatureId(testcase.groupId());
        entity.setScenarioId(testcase.scenarioId());
        entity.setTestRawDataEntity(rawDataEntity);
        return entity;
    }
}
