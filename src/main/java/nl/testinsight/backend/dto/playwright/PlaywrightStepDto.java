package nl.testinsight.backend.dto.playwright;

import java.util.List;

public record PlaywrightStepDto ( String title, PlaywrightErrorDto error, List<PlaywrightStepDto> steps){
}
