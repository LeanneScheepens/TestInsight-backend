package nl.testinsight.backend.source.cucumber;

import nl.testinsight.backend.common.SourceType;
import nl.testinsight.backend.common.dto.ImportResponseDto;
import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.service.TestResultService;
import nl.testinsight.backend.source.cucumber.dto.CucumberReportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestResultImportCucumberServiceTest {
    @Mock
    private CucumberTestProcessor cucumberTestProcessor;
    @Mock
    private TestResultService testResultService;
    @Mock
    private CucumberReportValidator cucumberReportValidator;

    @InjectMocks
    private TestResultImportCucumberService importService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSupportCucumber() {
        // Act
        boolean result = importService.supports(SourceType.CUCUMBER);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotSupportPlaywright() {
        // Act
        boolean result = importService.supports(SourceType.PLAYWRIGHT);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldImportValidCucumberReport() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("""
                [
                  {
                    "name": "Login feature",
                    "elements": [
                      {
                        "name": "Successful login",
                        "steps": []
                      }
                    ]
                  }
                ]
                """);

        TestDataDto testData = mock(TestDataDto.class);
        List<TestDataDto> testResults = List.of(testData);

        UUID runId = UUID.randomUUID();

        when(cucumberTestProcessor.process(any(CucumberReportDto.class)))
                .thenReturn(testResults);

        when(testResultService.saveTestResultData(
                eq(report),
                eq(testResults),
                eq(applicationName)
        )).thenReturn(runId);

        // Act
        ImportResponseDto response =
                importService.importResults(applicationName, report);

        // Assert
        assertNotNull(response);

        verify(cucumberReportValidator).validate(report);

        ArgumentCaptor<CucumberReportDto> captor =
                ArgumentCaptor.forClass(CucumberReportDto.class);

        verify(cucumberTestProcessor).process(captor.capture());

        assertEquals(1, captor.getValue().features().size());

        verify(testResultService).saveTestResultData(
                report,
                testResults,
                applicationName
        );
    }

    @Test
    void shouldImportReportWithMultipleFeatures() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("""
                [
                  {
                    "name": "Login feature",
                    "elements": []
                  },
                  {
                    "name": "Register feature",
                    "elements": []
                  }
                ]
                """);

        List<TestDataDto> testResults = List.of(
                mock(TestDataDto.class),
                mock(TestDataDto.class)
        );

        UUID runId = UUID.randomUUID();

        when(cucumberTestProcessor.process(any(CucumberReportDto.class)))
                .thenReturn(testResults);

        when(testResultService.saveTestResultData(
                report, testResults, applicationName
        )).thenReturn(runId);

        // Act
        ImportResponseDto response =
                importService.importResults(applicationName, report);

        // Assert
        assertNotNull(response);

        ArgumentCaptor<CucumberReportDto> captor =
                ArgumentCaptor.forClass(CucumberReportDto.class);

        verify(cucumberTestProcessor).process(captor.capture());

        assertEquals(2, captor.getValue().features().size());

        verify(testResultService).saveTestResultData(
                report, testResults, applicationName
        );
    }

    @Test
    void shouldRejectInvalidCucumberReport() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("{}");

        doThrow(new IllegalArgumentException("Invalid Cucumber report"))
                .when(cucumberReportValidator)
                .validate(report);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importService.importResults(
                        applicationName, report
                )
        );

        assertEquals(
                "Invalid Cucumber report",
                exception.getMessage()
        );

        verify(cucumberReportValidator).validate(report);

        verifyNoInteractions(cucumberTestProcessor);
        verifyNoInteractions(testResultService);
    }

    @Test
    void shouldPropagateExceptionWhenSavingFails() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("""
                [
                  {
                    "name": "Login feature",
                    "elements": []
                  }
                ]
                """);

        List<TestDataDto> testResults =
                List.of(mock(TestDataDto.class));

        when(cucumberTestProcessor.process(any(CucumberReportDto.class)))
                .thenReturn(testResults);

        when(testResultService.saveTestResultData(
                report, testResults, applicationName
        )).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> importService.importResults(
                        applicationName, report
                )
        );

        assertEquals("Database error", exception.getMessage());

        verify(cucumberReportValidator).validate(report);

        verify(cucumberTestProcessor)
                .process(any(CucumberReportDto.class));

        verify(testResultService).saveTestResultData(
                report, testResults, applicationName
        );
    }
}