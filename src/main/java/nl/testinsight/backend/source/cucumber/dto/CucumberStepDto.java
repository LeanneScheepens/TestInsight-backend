package nl.testinsight.backend.source.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CucumberStepDto(
        String keyword,
        String name,
        Boolean hidden,
        CucumberResultDto result
) {
}