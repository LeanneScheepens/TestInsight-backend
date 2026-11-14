package nl.testinsight.backend.dto.Cucumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CucumberFeatureDto(
        String id,
        String name,
        String description,
        String keyword,
        String uri,
        List<CucumberElementDto> elements
) {
}