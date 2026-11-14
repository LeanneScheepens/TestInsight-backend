
package nl.testinsight.backend.processor;

import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.dto.playwright.PlaywrightReportDto;
import nl.testinsight.backend.dto.playwright.PlaywrightSuiteDto;
import nl.testinsight.backend.dto.playwright.PlaywrightSpecDto;
import nl.testinsight.backend.dto.playwright.PlaywrightTestDto;
import nl.testinsight.backend.dto.playwright.PlaywrightResultDto;
import nl.testinsight.backend.dto.playwright.PlaywrightStepDto;
import nl.testinsight.backend.dto.playwright.PlaywrightErrorDto;

import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.enums.TestStatus;
import nl.testinsight.backend.mapper.TestStatusMapper;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaywrightTestProcessor implements TestReportProcessor {

    private final TestStatusMapper testStatusMapper;

    public PlaywrightTestProcessor(
            TestStatusMapper testStatusMapper
) {
        this.testStatusMapper = testStatusMapper;
    }

    public SourceType supports() {
        return SourceType.PLAYWRIGHT;
    }

    /**
     * Converts a Playwright report into normalized test case data.
     */
    public List<TestDataDto> process(
            PlaywrightReportDto report
    ) {

        List<TestDataDto> testCases = new ArrayList<>();

        if (report.suites() == null) {
            return testCases;
        }

        for (PlaywrightSuiteDto suite : report.suites()) {
            processSuite(suite, testCases);
        }

        return testCases;
    }

    /**
     * Processes the current suite and its nested suites.
     */
    private void processSuite(
            PlaywrightSuiteDto suite,
            List<TestDataDto> testCases
    ) {

        // The current suite is the direct parent of its specs.
        String groupId = suite.title();

        if (suite.specs() != null) {

            for (PlaywrightSpecDto spec : suite.specs()) {
                processSpec(groupId, spec, testCases);
            }
        }

        // Recursively process child suites.
        if (suite.suites() != null) {

            for (PlaywrightSuiteDto childSuite : suite.suites()) {
                processSuite(childSuite, testCases);
            }
        }
    }

    /**
     * Processes every execution attempt of a Playwright test.
     */
    private void processSpec(
            String groupId,
            PlaywrightSpecDto spec,
            List<TestDataDto> testCases
    ) {

        if (spec.tests() == null) {
            return;
        }


        for (PlaywrightTestDto test : spec.tests()) {

            if (test.results() == null) {
                continue;
            }

            for (PlaywrightResultDto result : test.results()) {

                // Normalize the execution status.
                TestStatus status =
                        testStatusMapper.mapStatus(result.status());

                // Extract execution details.
                String failedStep =
                        findFailedStep(result.steps());

                String errorMessage =
                        getErrorMessage(result);

                String executedAt =
                        result.startTime();


                // Convert to the shared DTO with MapStruct.
                TestDataDto testCaseData =
                        new TestDataDto(
                                groupId,
                                spec.id(),
                                spec.title(),
                                status,
                                result.retry(),
                                failedStep,
                                errorMessage,
                                SourceType.PLAYWRIGHT,
                                executedAt
                        );

                testCases.add(testCaseData);
            }
        }
    }

    /**
     * Finds the first failed step at the top level.
     */
    private String findFailedStep(
            List<PlaywrightStepDto> steps
    ) {

        if (steps == null) {
            return null;
        }

        for (PlaywrightStepDto step : steps) {

            if (step != null && step.error() != null) {
                return step.title();
            }
        }

        return null;
    }

    /**
     * Returns the first available error message.
     */
    private String getErrorMessage(
            PlaywrightResultDto result
    ) {

        if (result.error() != null
                && result.error().message() != null) {

            return result.error().message();
        }

        if (result.errors() != null) {

            for (PlaywrightErrorDto error : result.errors()) {

                if (error != null && error.message() != null) {
                    return error.message();
                }
            }
        }

        return null;
    }
}
