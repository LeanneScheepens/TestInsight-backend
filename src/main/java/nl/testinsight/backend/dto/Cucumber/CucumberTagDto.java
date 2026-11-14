package nl.testinsight.backend.dto.Cucumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CucumberTagDto(
        String name,
        Integer line
) {
}