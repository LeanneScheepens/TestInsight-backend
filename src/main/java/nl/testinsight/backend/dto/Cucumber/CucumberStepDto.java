package nl.testinsight.backend.dto.Cucumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CucumberStepDto(
        String keyword,
        String name,
        Boolean hidden,
        CucumberResultDto result
) {
}