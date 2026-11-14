package nl.testinsight.backend.service;

import nl.testinsight.backend.dto.ImportResponse;
import nl.testinsight.backend.enums.SourceType;
import tools.jackson.databind.JsonNode;


public interface TestResultImportService {

    boolean supports(SourceType sourceType);
    ImportResponse importResults(String applicationName, JsonNode report);
}