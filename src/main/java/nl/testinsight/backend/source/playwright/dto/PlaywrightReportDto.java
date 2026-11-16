package nl.testinsight.backend.source.playwright.dto;

import java.util.List;

public record PlaywrightReportDto(PlaywrightConfigDto config,
                                  List<PlaywrightSuiteDto> suites,
                                  List<PlaywrightErrorDto> errors,
                                  PlaywrightStatsDto stats) {
}
