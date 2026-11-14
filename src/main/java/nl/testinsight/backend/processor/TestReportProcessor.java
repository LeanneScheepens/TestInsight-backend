package nl.testinsight.backend.processor;

import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.dto.playwright.PlaywrightReportDto;
import nl.testinsight.backend.enums.SourceType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TestReportProcessor {
    SourceType supports();
    List<TestDataDto> process(PlaywrightReportDto report);
}
