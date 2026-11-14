package nl.testinsight.backend.dto.playwright;

import java.util.List;

public record PlaywrightReportDto ( PlaywrightConfigDto config,
                                    List<PlaywrightSuiteDto> suites,
                                    List<PlaywrightErrorDto> errors,
                                    PlaywrightStatsDto stats)
{
}
