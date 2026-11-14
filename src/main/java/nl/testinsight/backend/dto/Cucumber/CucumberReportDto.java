package nl.testinsight.backend.dto.Cucumber;

import java.util.List;

public record CucumberReportDto(
        List<CucumberFeatureDto> features
) {
}