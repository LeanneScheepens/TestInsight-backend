package nl.testinsight.backend.dto.playwright;

import java.util.List;

public record PlaywrightSpecDto ( String id, String file, String title, List<PlaywrightTestDto> tests){
}
