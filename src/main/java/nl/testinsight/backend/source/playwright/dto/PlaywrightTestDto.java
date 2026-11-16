package nl.testinsight.backend.source.playwright.dto;

import java.util.List;

public record PlaywrightTestDto(String projectId, List<PlaywrightResultDto> results) {
}
