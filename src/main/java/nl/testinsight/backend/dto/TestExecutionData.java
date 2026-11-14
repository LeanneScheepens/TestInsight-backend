package nl.testinsight.backend.dto;

import nl.testinsight.backend.enums.TestStatus;

public record TestExecutionData(
        TestStatus status,
        String stepName,
        String errorMessage
) {
}