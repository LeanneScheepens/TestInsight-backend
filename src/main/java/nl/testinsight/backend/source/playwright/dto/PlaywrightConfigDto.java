package nl.testinsight.backend.source.playwright.dto;

import java.util.List;

public record PlaywrightConfigDto(
        String rootDir,
        List<PlaywrightProjectDto> projects
) {
}
