package nl.testinsight.backend.common.dto;

import java.util.UUID;

public record ImportResponseDto(

        UUID runId,

        int featuresImported,
        int scenariosImported

) {
}
