package nl.testinsight.backend.dto;

import nl.testinsight.backend.enums.TestStatus;

public record TestOutcome(
        TestStatus status,
        String failedStep,
        String errorMessage
) {
}
