package nl.testinsight.backend.dto.Cucumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CucumberResultDto(
        String status,
        Long duration,

        @JsonProperty("error_message")
        String errorMessage
) {
}