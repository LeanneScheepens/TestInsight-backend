package nl.testinsight.backend.dto.playwright;

public record PlaywrightStatsDto(
        String startTime,
        Long duration,
        Integer expected,
        Integer skipped,
        Integer unexpected,
        Integer flaky
) {
}
