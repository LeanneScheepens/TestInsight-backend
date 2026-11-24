package nl.testinsight.backend.source.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CucumberTagDto(
        String name,
        Integer line
) {
}