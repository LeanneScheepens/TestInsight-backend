package nl.testinsight.backend.service;

import tools.jackson.databind.JsonNode;
import nl.testinsight.backend.Validator.CucumberReportValidator;
import nl.testinsight.backend.dto.CucumberScenarioData;
import nl.testinsight.backend.entity.Application;
import nl.testinsight.backend.entity.TestCase;
import nl.testinsight.backend.entity.TestResult;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.mapper.TestResultMapper;
import nl.testinsight.backend.repository.TestCaseRepository;
import nl.testinsight.backend.repository.TestResultRepository;
import org.springframework.stereotype.Service;
import nl.testinsight.backend.processor.CucumberScenarioProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import nl.testinsight.backend.dto.ImportResponse;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TestResultImportService {

    private final TestResultRepository testResultRepository;
    private final ApplicationService applicationService;
    private final TestResultMapper testResultMapper;
    private final CucumberReportValidator cucumberReportValidator;
    private final TestCaseRepository testCaseRepository;
    private final CucumberScenarioProcessor cucumberScenarioProcessor;
    private final CucumberAttachmentService cucumberAttachmentService;

    public TestResultImportService(
            TestResultRepository testResultRepository,
            TestResultMapper testResultMapper,
            ApplicationService applicationService,
            CucumberReportValidator cucumberReportValidator,
            TestCaseRepository testCaseRepository,
            CucumberScenarioProcessor cucumberScenarioProcessor,
            CucumberAttachmentService cucumberAttachmentService) {


        this.testResultRepository = testResultRepository;
        this.testResultMapper = testResultMapper;
        this.applicationService= applicationService;
        this.cucumberReportValidator = cucumberReportValidator;
        this.testCaseRepository = testCaseRepository;
        this.cucumberScenarioProcessor = cucumberScenarioProcessor;
        this.cucumberAttachmentService = cucumberAttachmentService;
    }

    @Transactional
    public ImportResponse importCucumberResults( String applicationName, JsonNode report) {
        LocalDateTime receivedAt = LocalDateTime.now();

        cucumberReportValidator.validate(report);

        Application application = applicationService.findOrCreateApplication(applicationName);

        List<CucumberScenarioData> scenarios =
                cucumberScenarioProcessor.process(report);

        UUID runId = UUID.randomUUID();

        int featuresImported = report.size();
        int scenariosImported = scenarios.size();

        List<Path> storedFiles = new ArrayList<>();

        registerAttachmentCleanup(storedFiles);

        for (CucumberScenarioData scenario : scenarios) {

            TestCase testCase =
                    findOrCreateTestCase(application, scenario);

            CucumberAttachmentService.StoredScenario storedScenario;

            try {
                storedScenario =
                        cucumberAttachmentService.processAttachments(
                                scenario.sourcePayload()
                        );

            } catch (IOException exception) {

                throw new UncheckedIOException(
                        "Failed to store Cucumber attachments",
                        exception
                );
            }

            storedFiles.addAll(storedScenario.storedFiles());

            saveTestResult(
                    testCase,
                    scenario,
                    runId,
                    receivedAt,
                    storedScenario.sourcePayload()
            );
        }
        return new ImportResponse(
                runId,
                featuresImported,
                scenariosImported
        );

    }


    private void registerAttachmentCleanup(List<Path> storedFiles) {


        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {

            throw new IllegalStateException(
                    "No active transaction synchronization"
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {


                        if (status != STATUS_COMMITTED) {

                            cucumberAttachmentService
                                    .deleteFiles(storedFiles);
                        }
                    }
                }
        );
    }




    private TestCase findOrCreateTestCase(
            Application application,
            CucumberScenarioData scenario) {


        return testCaseRepository
                .findByApplication_IdAndSourceTypeAndFeatureIdAndScenarioId(
                        application.getId(),
                        SourceType.CUCUMBER, // the hardcoded source type for Cucumber scenarios need to change this
                        scenario.featureId(),
                        scenario.scenarioId()
                )
                .orElseGet(() -> {


                    TestCase testCase = new TestCase();

                    // Associate the test case with its application.
                    testCase.setApplication(application);

                    // Store the scenario name.
                    testCase.setName(scenario.name());

                    // Identify the source framework.
                    testCase.setSourceType(SourceType.CUCUMBER);

                    // Store the original Cucumber identifiers.
                    testCase.setFeatureId(scenario.featureId());
                    testCase.setScenarioId(scenario.scenarioId());

                    return testCaseRepository.save(testCase);
                });
        
    }


    private void saveTestResult(
                    TestCase testCase,
            CucumberScenarioData scenario,
            UUID runId,
            LocalDateTime receivedAt,
            JsonNode safeSourcePayload) {

        TestResult result = testResultMapper.fromCucumber(
                scenario,
                testCase,
                runId,
                receivedAt
        );

        result.setSourcePayload(safeSourcePayload);

        testResultRepository.save(result);
    }
}
