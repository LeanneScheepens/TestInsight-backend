package nl.testinsight.backend.Validator;

import nl.testinsight.backend.enums.SourceType;
import tools.jackson.databind.JsonNode;

public interface ReportValidator {
    SourceType supports();

    void validate(JsonNode report);
}
