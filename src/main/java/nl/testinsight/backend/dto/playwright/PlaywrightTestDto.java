package nl.testinsight.backend.dto.playwright;

import java.util.List;

public record PlaywrightTestDto(String projectId, List<PlaywrightResultDto> results) {
}
