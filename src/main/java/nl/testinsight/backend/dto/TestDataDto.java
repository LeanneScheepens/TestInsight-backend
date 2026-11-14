package nl.testinsight.backend.dto;

import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.enums.TestStatus;

public record TestDataDto(
        String groupId,
        String scenarioId,
        String name,
        TestStatus status,
        Integer retryIndex, // Integer because it can be null if not a retry
        String failedStep,
        String errorMessage,
        SourceType sourceType,
        String executedAt) {
    // Constructor without retryIndex.
    // Defaults to 0 for the first execution.
}
