package nl.testinsight.backend.service;

import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.dto.playwright.PlaywrightReportDto;
import nl.testinsight.backend.Validator.CucumberReportValidator;
import nl.testinsight.backend.enums.SourceType;
import nl.testinsight.backend.mapper.TestResultMapper;
import nl.testinsight.backend.processor.PlaywrightTestProcessor;
import nl.testinsight.backend.repository.TestCaseRepository;
import nl.testinsight.backend.repository.TestResultRepository;
import org.springframework.stereotype.Service;
import nl.testinsight.backend.dto.ImportResponse;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Service
public class TestResultImportPlaywrightService {

    private final TestResultRepository testResultRepository;
    private final ApplicationService applicationService;
    private final TestResultMapper testResultMapper;
    private final CucumberReportValidator cucumberReportValidator;
    private final TestCaseRepository testCaseRepository;
    private final CucumberAttachmentService cucumberAttachmentService;
    private final PlaywrightTestProcessor playwrightTestProcessor;
    private final TestResultService testResultService;

    public TestResultImportPlaywrightService(
            TestResultRepository testResultRepository,
            TestResultMapper testResultMapper,
            ApplicationService applicationService,
            CucumberReportValidator cucumberReportValidator,
            TestCaseRepository testCaseRepository,
            CucumberAttachmentService cucumberAttachmentService,
            PlaywrightTestProcessor playwrightTestProcessor, TestResultService testResultService
            ) {


        this.testResultRepository = testResultRepository;
        this.testResultMapper = testResultMapper;
        this.applicationService= applicationService;
        this.cucumberReportValidator = cucumberReportValidator;
        this.testCaseRepository = testCaseRepository; // kijken of je hem kan weglaten
        this.cucumberAttachmentService = cucumberAttachmentService; // kijken of je hem kan weglaten
        this.playwrightTestProcessor = playwrightTestProcessor;
        this.testResultService = testResultService;
    }

    public ImportResponse importPlaywrightResults(
            String applicationName,
            PlaywrightReportDto report,
            SourceType sourceType, JsonNode jsonReport) {

        List<TestDataDto> testData = playwrightTestProcessor.process(report);

        testResultService.saveTestResultData(jsonReport, testData, applicationName);

        return null;
    }


//    @Transactional
//    public ImportResponse importCucumberResults(
//            String applicationName,
//            JsonNode report,
//            SourceType sourceType) {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        CucumberScenarioDataDto cucumberScenarioDataDto = objectMapper.convertValue(report, CucumberScenarioDataDto.class);
//
//
//        LocalDateTime receivedAt = LocalDateTime.now();
//
//        cucumberReportValidator.validate(report);
//
//        Application application = applicationService.findOrCreateApplication(applicationName);
//
//        List<CucumberScenarioDataDto> scenarios = null;
////                cucumberScenarioProcessor.process(report);
//
//        UUID runId = UUID.randomUUID();
//
//        int featuresImported = report.size();
//        int scenariosImported = scenarios.size();
//
//        List<Path> storedFiles = new ArrayList<>();
//
//        registerAttachmentCleanup(storedFiles);
//
//        for (CucumberScenarioDataDto scenario : scenarios) {
//
//            TestCase testCase =
//                    findOrCreateTestCase(application, scenario, sourceType);
//
//            CucumberAttachmentService.StoredScenario storedScenario;
//
//            try {
//                storedScenario =
//                        cucumberAttachmentService.processAttachments(
//                                scenario.sourcePayload()
//                        );
//
//            } catch (IOException exception) {
//
//                throw new UncheckedIOException(
//                        "Failed to store Cucumber attachments",
//                        exception
//                );
//            }
//
//            storedFiles.addAll(storedScenario.storedFiles());
//
//            saveTestResult(
//                    testCase,
//                    scenario,
//                    runId,
//                    receivedAt,
//                    storedScenario.sourcePayload()
//            );
//        }
//        return new ImportResponse(
//                runId,
//                featuresImported,
//                scenariosImported
//        );
//
//    }

//
//    private void registerAttachmentCleanup(List<Path> storedFiles) {
//
//
//        if (!TransactionSynchronizationManager
//                .isSynchronizationActive()) {
//
//            throw new IllegalStateException(
//                    "No active transaction synchronization"
//            );
//        }
//
//        TransactionSynchronizationManager.registerSynchronization(
//                new TransactionSynchronization() {
//
//                    @Override
//                    public void afterCompletion(int status) {
//
//
//                        if (status != STATUS_COMMITTED) {
//
//                            cucumberAttachmentService
//                                    .deleteFiles(storedFiles);
//                        }
//                    }
//                }
//        );
//    }
//
//
//
//
//    private TestCase findOrCreateTestCase(
//            Application application,
//            CucumberScenarioDataDto scenario,
//            SourceType sourceType) {
//
//
//        return testCaseRepository
//                .findByApplication_IdAndSourceTypeAndFeatureIdAndScenarioId(
//                        application.getId(),
//                        sourceType,
//                        scenario.featureId(),
//                        scenario.scenarioId()
//                )
//                .orElseGet(() -> {
//
//
//                    TestCase testCase = new TestCase();
//
//                    // Associate the test case with its application.
//                    testCase.setApplication(application);
//
//                    // Store the scenario name.
//                    testCase.setName(scenario.name());
//
//                    // Identify the source framework.
//                    testCase.setSourceType(sourceType);
//
//                    // Store the original Cucumber identifiers.
//                    testCase.setFeatureId(scenario.featureId());
//                    testCase.setScenarioId(scenario.scenarioId());
//
//                    return testCaseRepository.save(testCase);
//                });
//
//    }
//
//
//    private void saveTestResult(
//                    TestCase testCase,
//            CucumberScenarioDataDto scenario,
//            UUID runId,
//            LocalDateTime receivedAt,
//            JsonNode safeSourcePayload) {
//
//        TestResult result = testResultMapper.fromCucumber(
//                scenario,
//                testCase,
//                runId,
//                receivedAt
//        );
//
//        result.setSourcePayload(safeSourcePayload);
//
//        testResultRepository.save(result);
//    }
}
