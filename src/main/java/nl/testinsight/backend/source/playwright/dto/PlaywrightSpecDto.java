package nl.testinsight.backend.source.playwright.dto;

import java.util.List;

public record PlaywrightSpecDto(String id, String file, String title, List<PlaywrightTestDto> tests) {
}
