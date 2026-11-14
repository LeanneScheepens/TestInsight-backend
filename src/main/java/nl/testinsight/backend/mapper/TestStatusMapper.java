package nl.testinsight.backend.mapper;

import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.enums.TestStatus;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TestStatusMapper {
    public TestStatus mapStatus(String status) {

        // Return UNKNOWN if the status is missing or empty.
        if (status == null || status.isBlank()) {
            return TestStatus.UNKNOWN;
        }

        // Normalize the status to lowercase.
        String normalizedStatus =
                status.trim().toLowerCase(Locale.ROOT);

        // Map framework statuses to the internal TestStatus enum.
        return switch (normalizedStatus) {
            case "passed" -> TestStatus.PASSED;
            case "failed", "timedout" -> TestStatus.FAILED;
            case "skipped" -> TestStatus.SKIPPED;
            case "interrupted" -> TestStatus.UNKNOWN;
            case "pending" -> TestStatus.PENDING;
            case "undefined" -> TestStatus.UNDEFINED;
            case "ambiguous" -> TestStatus.AMBIGUOUS;
            default -> TestStatus.UNKNOWN;
        };
    }
}