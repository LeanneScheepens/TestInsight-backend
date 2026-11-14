package nl.testinsight.backend.dto.Cucumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CucumberElementDto(
        String id,
        String name,
        String description,
        String keyword,
        String type,
        List<CucumberStepDto> steps,
        List<CucumberTagDto> tags
) {
}