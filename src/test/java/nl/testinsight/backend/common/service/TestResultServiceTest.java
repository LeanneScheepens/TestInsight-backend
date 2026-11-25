package nl.testinsight.backend.common.service;

import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.dto.TestStatusCountDto;
import nl.testinsight.backend.common.entity.ApplicationEntity;
import nl.testinsight.backend.common.entity.TestCaseEntity;
import nl.testinsight.backend.common.entity.TestRawDataEntity;
import nl.testinsight.backend.common.entity.TestResultEntity;
import nl.testinsight.backend.common.mapper.TestResultMapper;
import nl.testinsight.backend.common.repository.TestResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import nl.testinsight.backend.common.dto.TestStatusTrendDto;
import java.time.LocalDate;

import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestResultServiceTest {
    @Mock
    private TestResultRepository testResultRepository;
    @Mock
    private ApplicationService applicationService;
    @Mock
    private TestResultMapper testResultMapper;
    @Mock
    private RawDataService rawDataService;
    @Mock
    private TestCaseService testCaseService;
    @InjectMocks
    private TestResultService testResultService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSaveTestResultData() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree(
                "{\"name\":\"TestInsight\"}"
        );

        TestDataDto data = mock(TestDataDto.class);

        ApplicationEntity application = new ApplicationEntity(applicationName);
        TestRawDataEntity rawData = new TestRawDataEntity();
        TestCaseEntity testCase = new TestCaseEntity();
        TestResultEntity testResult = new TestResultEntity();

        when(applicationService.findOrCreateApplication(applicationName))
                .thenReturn(application);

        when(rawDataService.saveRawData(report))
                .thenReturn(rawData);

        when(testCaseService.findOrCreateTestCase(
                data, application, rawData
        )).thenReturn(testCase);

        when(testResultMapper.mapTestResult(
                eq(data),
                eq(testCase),
                any(UUID.class)
        )).thenReturn(testResult);

        // Act
        UUID runId = testResultService.saveTestResultData(
                report,
                List.of(data),
                applicationName
        );

        // Assert
        assertNotNull(runId);

        verify(applicationService).findOrCreateApplication(applicationName);
        verify(rawDataService).saveRawData(report);
        verify(testCaseService).findOrCreateTestCase(data, application, rawData);
        verify(testResultMapper).mapTestResult(data, testCase, runId);
        verify(testResultRepository).saveAll(List.of(testResult));
    }

    @Test
    void shouldSaveMultipleTestResultsWithSameRunId() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("{}");

        TestDataDto data1 = mock(TestDataDto.class);
        TestDataDto data2 = mock(TestDataDto.class);

        ApplicationEntity application = new ApplicationEntity(applicationName);
        TestRawDataEntity rawData = new TestRawDataEntity();

        TestCaseEntity testCase1 = new TestCaseEntity();
        TestCaseEntity testCase2 = new TestCaseEntity();

        TestResultEntity result1 = new TestResultEntity();
        TestResultEntity result2 = new TestResultEntity();

        when(applicationService.findOrCreateApplication(applicationName))
                .thenReturn(application);

        when(rawDataService.saveRawData(report))
                .thenReturn(rawData);

        when(testCaseService.findOrCreateTestCase(
                data1, application, rawData
        )).thenReturn(testCase1);

        when(testCaseService.findOrCreateTestCase(
                data2, application, rawData
        )).thenReturn(testCase2);

        when(testResultMapper.mapTestResult(
                eq(data1), eq(testCase1), any(UUID.class)
        )).thenReturn(result1);

        when(testResultMapper.mapTestResult(
                eq(data2), eq(testCase2), any(UUID.class)
        )).thenReturn(result2);

        // Act
        UUID runId = testResultService.saveTestResultData(
                report,
                List.of(data1, data2),
                applicationName
        );

        // Assert
        assertNotNull(runId);

        verify(testCaseService, times(2))
                .findOrCreateTestCase(
                        any(TestDataDto.class),
                        eq(application),
                        eq(rawData)
                );

        verify(testResultMapper)
                .mapTestResult(data1, testCase1, runId);

        verify(testResultMapper)
                .mapTestResult(data2, testCase2, runId);

        verify(testResultRepository)
                .saveAll(List.of(result1, result2));
    }

    @Test
    void shouldSaveEmptyListWhenNoTestDataExists() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("{}");

        ApplicationEntity application = new ApplicationEntity(applicationName);
        TestRawDataEntity rawData = new TestRawDataEntity();

        when(applicationService.findOrCreateApplication(applicationName))
                .thenReturn(application);

        when(rawDataService.saveRawData(report))
                .thenReturn(rawData);

        // Act
        UUID runId = testResultService.saveTestResultData(
                report,
                List.of(),
                applicationName
        );

        // Assert
        assertNotNull(runId);

        verify(applicationService)
                .findOrCreateApplication(applicationName);

        verify(rawDataService)
                .saveRawData(report);

        verifyNoInteractions(testCaseService);
        verifyNoInteractions(testResultMapper);

        verify(testResultRepository)
                .saveAll(List.of());
    }

    @Test
    void shouldPropagateExceptionWhenSavingFails() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("{}");

        TestDataDto data = mock(TestDataDto.class);

        ApplicationEntity application = new ApplicationEntity(applicationName);
        TestRawDataEntity rawData = new TestRawDataEntity();
        TestCaseEntity testCase = new TestCaseEntity();
        TestResultEntity testResult = new TestResultEntity();

        when(applicationService.findOrCreateApplication(applicationName))
                .thenReturn(application);

        when(rawDataService.saveRawData(report))
                .thenReturn(rawData);

        when(testCaseService.findOrCreateTestCase(
                data, application, rawData
        )).thenReturn(testCase);

        when(testResultMapper.mapTestResult(
                eq(data), eq(testCase), any(UUID.class)
        )).thenReturn(testResult);

        when(testResultRepository.saveAll(anyList()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> testResultService.saveTestResultData(
                        report,
                        List.of(data),
                        applicationName
                )
        );

        assertEquals("Database error", exception.getMessage());

        verify(testResultRepository, times(1))
                .saveAll(anyList());
    }

    @Test
    void shouldReturnLatestTestResultsWithStatus(){
        // Arange
        List<TestStatusCountDto> expectedResults = List.of(
                new TestStatusCountDto("PASSED", 15),
                new TestStatusCountDto("FAILED", 5),
                new TestStatusCountDto("SKIPPED", 1)
        );
        when(testResultRepository.countLatestTestResultsWithStatus()).thenReturn(expectedResults);

        // Act
        List<TestStatusCountDto> acttualResults = testResultService.getLatestTestResultsWithStatus();

        // Assert
        assertEquals(expectedResults, acttualResults);
        verify(testResultRepository, times(1)).countLatestTestResultsWithStatus();
    }
    @Test
    void shouldReturnEmptyListWhenNoTestResultsExist() {
        // Arrange
        when(testResultRepository.countLatestTestResultsWithStatus()).thenReturn(List.of());

        // Act
        List<TestStatusCountDto> actualResults = testResultService.getLatestTestResultsWithStatus();

        // Assert
        assertTrue(actualResults.isEmpty());

        verify(testResultRepository, times(1)).countLatestTestResultsWithStatus();
    }
    @Test
    void shouldPropagateExceptionWhenRepositoryFails() {
        // Arrange
        when(testResultRepository.countLatestTestResultsWithStatus())
                .thenThrow(new DataAccessResourceFailureException(
                        "Database connection failed"
                ));

        // Act & Assert
        assertThrows(
                DataAccessResourceFailureException.class,
                () -> testResultService.getLatestTestResultsWithStatus()
        );

        verify(testResultRepository, times(1))
                .countLatestTestResultsWithStatus();
    }

    @Test
    void shouldReturnStatusTrendResultsWithExecutionDate() {
        // Arrange
        List<TestStatusTrendDto> expectedResults = List.of(
                new TestStatusTrendDto(
                        "PASSED", 15, LocalDate.of(2026, 11, 13)
                ),
                new TestStatusTrendDto(
                        "FAILED", 5, LocalDate.of(2026, 11, 13)
                ),
                new TestStatusTrendDto(
                        "PASSED", 18, LocalDate.of(2026, 11, 14)
                ),
                new TestStatusTrendDto(
                        "FAILED", 2, LocalDate.of(2026, 11, 14)
                ),
                new TestStatusTrendDto(
                        "SKIPPED", 1, LocalDate.of(2026, 11, 14)
                )
        );

        when(testResultRepository.findTestStatusTrend())
                .thenReturn(expectedResults);

        // Act
        List<TestStatusTrendDto> actualResults =
                testResultService.getStatusTrendResultsWithExecutionDate();

        // Assert
        assertEquals(expectedResults, actualResults);

        verify(testResultRepository, times(1))
                .findTestStatusTrend();
    }
    @Test
    void shouldReturnEmptyListWhenNoStatusTrendResultsExist() {
        // Arrange
        when(testResultRepository.findTestStatusTrend())
                .thenReturn(List.of());

        // Act
        List<TestStatusTrendDto> actualResults =
                testResultService.getStatusTrendResultsWithExecutionDate();

        // Assert
        assertTrue(actualResults.isEmpty());

        verify(testResultRepository, times(1))
                .findTestStatusTrend();
    }
    @Test
    void shouldPropagateExceptionWhenStatusTrendRepositoryFails() {
        // Arrange
        when(testResultRepository.findTestStatusTrend())
                .thenThrow(new DataAccessResourceFailureException(
                        "Database connection failed"
                ));

        // Act & Assert
        assertThrows(
                DataAccessResourceFailureException.class,
                () -> testResultService
                        .getStatusTrendResultsWithExecutionDate()
        );

        verify(testResultRepository, times(1))
                .findTestStatusTrend();
    }
}