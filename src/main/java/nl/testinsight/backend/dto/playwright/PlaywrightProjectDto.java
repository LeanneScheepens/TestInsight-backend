package nl.testinsight.backend.dto.playwright;

import tools.jackson.databind.JsonNode;

import java.util.List;

public record PlaywrightProjectDto(
        String outputDir,
        Integer repeatEach,
        Integer retries,
        JsonNode metadata,
        String id,
        String name,
        String testDir,
        List<String> testIgnore,
        List<String> testMatch,
        Integer timeout
) {
}
