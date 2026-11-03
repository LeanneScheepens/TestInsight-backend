package nl.testinsight.backend.mapper;

import java.time.LocalDateTime;

import nl.testinsight.backend.dto.CucumberResultDto;
import nl.testinsight.backend.dto.PlaywrightResultDto;
import nl.testinsight.backend.entity.TestResult;
import org.springframework.stereotype.Component;

@Component
public class TestResultMapper {

    public TestResult fromCucumber(
            CucumberResultDto dto) {

        return new TestResult(dto.getName(), dto.getStatus(), "CUCUMBER", LocalDateTime.now());
    }

    public TestResult fromPlaywright(
            PlaywrightResultDto dto) {

        return new TestResult(dto.getTitle(), dto.getOutcome(), "PLAYWRIGHT", LocalDateTime.now());
    }
}
