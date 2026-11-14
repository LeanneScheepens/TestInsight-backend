
package nl.testinsight.backend.dto.playwright;

import java.util.List;

public record PlaywrightConfigDto(
        String rootDir,
        List<PlaywrightProjectDto> projects
) {
}
