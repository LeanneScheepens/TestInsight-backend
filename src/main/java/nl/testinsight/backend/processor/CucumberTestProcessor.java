package nl.testinsight.backend.processor;

import nl.testinsight.backend.dto.Cucumber.CucumberElementDto;
import nl.testinsight.backend.dto.Cucumber.CucumberFeatureDto;
import nl.testinsight.backend.dto.Cucumber.CucumberReportDto;
import nl.testinsight.backend.dto.Cucumber.CucumberStepDto;
import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.enums.TestStatus;
import nl.testinsight.backend.mapper.TestStatusMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CucumberTestProcessor implements TestReportProcessor<CucumberReportDto> {
    private final TestStatusMapper testStatusMapper;

    public CucumberTestProcessor(
            TestStatusMapper testStatusMapper
    ) {
        this.testStatusMapper = testStatusMapper;
    }

    @Override
    public SourceType supports() {
        return SourceType.CUCUMBER;
    }

    @Override
    public List<TestDataDto> process(
            CucumberReportDto report
    ) {

        List<TestDataDto> testCases = new ArrayList<>();

        if (report == null || report.features() == null) {
            return testCases;
        }

        for (CucumberFeatureDto feature : report.features()) {
            if (feature != null) {
                processFeature(feature, testCases);
            }
        }

        return testCases;
    }

    // Processes the current feature
    private void processFeature(
            CucumberFeatureDto feature,
            List<TestDataDto> testCases
    ) {

        String groupId = feature.id();

        if (feature.elements() != null) {

            for (CucumberElementDto element : feature.elements()) {
                if (element != null) {
                    processElement(groupId, element, testCases);
                }
            }
        }
    }

    // Processes every scenario of a Cucumber feature
    private void processElement(
            String groupId,
            CucumberElementDto element,
            List<TestDataDto> testCases
    ) {

        if (element.steps() == null) {
            return;
        }

        // Calculate the overall scenario status
        TestStatus status =
                calculateStatus(element.steps());

        // Extract execution details
        String failedStep =
                findFailedStep(element.steps());

        String errorMessage =
                getErrorMessage(element.steps());

        // Cucumber does not contain retry information
        int retryIndex = 0;

        // Execution timestamp is not available in this report
        String executedAt = Instant.now().toString();

        // Convert to the shared DTO
        TestDataDto testCaseData =
                new TestDataDto(
                        groupId,
                        element.id(),
                        element.name(),
                        status,
                        retryIndex,
                        failedStep,
                        errorMessage,
                        SourceType.CUCUMBER,
                        executedAt
                );

        testCases.add(testCaseData);
    }

    private TestStatus calculateStatus(
            List<CucumberStepDto> steps
    ) {

        TestStatus highestStatus = null;

        for (CucumberStepDto step : steps) {

            if (step == null || step.result() == null) {
                continue;
            }

            TestStatus stepStatus = testStatusMapper.mapStatus(
                    step.result().status()
            );

            if (highestStatus == null
                    || stepStatus.getPriority() > highestStatus.getPriority()) {

                highestStatus = stepStatus;
            }
        }

        return highestStatus != null
                ? highestStatus
                : TestStatus.UNKNOWN;
    }

    // Finds the first failed step
    private String findFailedStep(
            List<CucumberStepDto> steps
    ) {

        if (steps == null) {
            return null;
        }

        for (CucumberStepDto step : steps) {

            if (step == null || step.result() == null) {
                continue;
            }

            TestStatus status = testStatusMapper.mapStatus(
                    step.result().status()
            );

            if (status == TestStatus.FAILED) {
                return step.name() != null
                        ? step.name()
                        : step.keyword();
            }
        }

        return null;
    }

    // Returns the first available error message
    private String getErrorMessage(
            List<CucumberStepDto> steps
    ) {

        if (steps == null) {
            return null;
        }

        for (CucumberStepDto step : steps) {

            if (step != null
                    && step.result() != null
                    && step.result().errorMessage() != null) {

                return step.result().errorMessage();
            }
        }

        return null;
    }


}