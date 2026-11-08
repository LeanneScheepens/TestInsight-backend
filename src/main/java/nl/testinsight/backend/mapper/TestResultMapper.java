package nl.testinsight.backend.mapper;

import java.time.LocalDateTime;

import nl.testinsight.backend.dto.CucumberResultDto;
import nl.testinsight.backend.dto.PlaywrightResultDto;
import nl.testinsight.backend.entity.TestResult;
import nl.testinsight.backend.dto.CucumberScenarioData;
import nl.testinsight.backend.entity.TestCase;
import nl.testinsight.backend.enums.TestStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Component
public class TestResultMapper {

    public TestResult fromCucumber(
            CucumberScenarioData scenario,
            TestCase testCase,
            UUID runId,
            LocalDateTime receivedAt) {

        // Create a new TestResult entity.
        TestResult result = new TestResult();

        result.setTestCase(testCase);
        result.setRunId(runId);
        result.setStatus(scenario.status());
        result.setFailedStep(scenario.failedStep());
        result.setErrorMessage(scenario.errorMessage());
        result.setExecutedAt(receivedAt);
        result.setCreatedAt(LocalDateTime.now());


        return result;
    }

    public TestStatus mapPlaywrightStatus(String status) {

        if (status == null || status.isBlank()) {
            return TestStatus.UNKNOWN;
        }

        return switch (status.toUpperCase(Locale.ROOT)) {
            case "PASS", "PASSED", "EXPECTED" -> TestStatus.PASSED;
            case "FAIL", "FAILED", "UNEXPECTED" -> TestStatus.FAILED;
            case "SKIP", "SKIPPED" -> TestStatus.SKIPPED;
            default -> TestStatus.UNKNOWN;
        };
    }
}
