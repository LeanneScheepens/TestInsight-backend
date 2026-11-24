package nl.testinsight.backend.source.cucumber.dto;

import java.util.List;

public record CucumberReportDto(
        List<CucumberFeatureDto> features
) {
}