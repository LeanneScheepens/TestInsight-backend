package nl.testinsight.backend.source.playwright.dto;

import java.util.List;

public record PlaywrightStepDto(String title, PlaywrightErrorDto error, List<PlaywrightStepDto> steps) {
}
