package nl.testinsight.backend.common.mapper;

import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.entity.ApplicationEntity;
import nl.testinsight.backend.common.entity.TestCaseEntity;
import nl.testinsight.backend.common.entity.TestRawDataEntity;
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
