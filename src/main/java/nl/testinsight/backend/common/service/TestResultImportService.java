package nl.testinsight.backend.common.service;

import nl.testinsight.backend.common.dto.ImportResponseDto;
import nl.testinsight.backend.common.enums.SourceType;
import tools.jackson.databind.JsonNode;


public interface TestResultImportService {

    boolean supports(SourceType sourceType);
    ImportResponseDto importResults(String applicationName, JsonNode report);
}