
package nl.testinsight.backend.dto;

import java.util.UUID;

public record ImportResponse(

        UUID runId,

        int featuresImported,
        int scenariosImported

) {
}
