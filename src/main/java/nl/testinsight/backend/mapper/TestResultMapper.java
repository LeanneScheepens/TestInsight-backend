package nl.testinsight.backend.mapper;

import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.entity.TestCaseEntity;
import nl.testinsight.backend.entity.TestResultEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class TestResultMapper {
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public TestResultEntity mapTestResult(TestDataDto testData, TestCaseEntity testCaseEntity, UUID runId) {
        TestResultEntity entity = new TestResultEntity();


        LocalDateTime executedAt = LocalDateTime.ofInstant(
                Instant.from(FORMATTER.parse(testData.executedAt())),
                ZoneOffset.UTC);

        entity.setTestCaseEntity(testCaseEntity);
        entity.setExecutedAt(executedAt); // "startTime": "2026-11-13T09:02:00.000Z" is the time .parse doesn't work with this
        entity.setRetryIndex(testData.retryIndex() == null
                ? 0
                : testData.retryIndex());
        entity.setStatus(testData.status());
        entity.setFailedStep(testData.failedStep());
        entity.setErrorMessage(testData.errorMessage());
        entity.setRunId(runId);

        return entity;
    }

}
