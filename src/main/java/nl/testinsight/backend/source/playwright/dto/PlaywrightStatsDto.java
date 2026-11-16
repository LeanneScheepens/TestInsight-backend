package nl.testinsight.backend.source.playwright.dto;

public record PlaywrightStatsDto(
        String startTime,
        Long duration,
        Integer expected,
        Integer skipped,
        Integer unexpected,
        Integer flaky
) {
}
