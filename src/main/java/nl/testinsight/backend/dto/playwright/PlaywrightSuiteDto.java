package nl.testinsight.backend.dto.playwright;

import java.util.List;

public record PlaywrightSuiteDto (String title, List<PlaywrightSpecDto> specs, List<PlaywrightSuiteDto> suites) // List<PlaywrightSuiteDto is recursive that means that he calls him self again until there is nothing left to progress
{
}
