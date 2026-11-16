package nl.testinsight.backend.source.playwright.dto;

import java.util.List;

public record PlaywrightResultDto(String status, Integer retry, PlaywrightErrorDto error,
                                  List<PlaywrightErrorDto> errors, List<PlaywrightStepDto> steps, String startTime) {
}
