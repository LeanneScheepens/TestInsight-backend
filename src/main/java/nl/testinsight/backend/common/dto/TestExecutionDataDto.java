package nl.testinsight.backend.common.dto;

import nl.testinsight.backend.common.enums.TestStatus;

public record TestExecutionDataDto(
        TestStatus status,
        String stepName,
        String errorMessage
) {
}