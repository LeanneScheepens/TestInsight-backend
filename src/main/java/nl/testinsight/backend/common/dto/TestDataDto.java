package nl.testinsight.backend.common.dto;

import nl.testinsight.backend.common.SourceType;
import nl.testinsight.backend.common.TestStatus;

public record TestDataDto(
        String groupId,
        String scenarioId,
        String name,
        TestStatus status,
        Integer retryIndex, // Integer because it can be null if not a retry, for Cucucumber it is always null
        String failedStep,
        String errorMessage,
        SourceType sourceType,
        String executedAt) {
    // Constructor without retryIndex.
    // Defaults to 0 for the first execution.
}
