package nl.testinsight.backend.common.model;

import nl.testinsight.backend.common.dto.ImportResponseDto;
import nl.testinsight.backend.common.SourceType;
import tools.jackson.databind.JsonNode;


public interface TestResultImportService {

    /**
     * Checks whether this importer handles the given source type.
     *
     * @param sourceType framework that produced the report
     * @return {@code true} if this importer supports the source type
     */
    boolean supports(SourceType sourceType);

    /**
     * Validates, processes, and stores a test report.
     *
     * @param applicationName application associated with the imported results
     * @param report raw JSON test report
     * @return summary of the completed import
     */
    ImportResponseDto importResults(String applicationName, JsonNode report);
}