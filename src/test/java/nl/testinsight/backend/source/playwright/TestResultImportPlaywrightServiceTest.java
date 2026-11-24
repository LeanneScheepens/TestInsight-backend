package nl.testinsight.backend.source.playwright;

import nl.testinsight.backend.common.SourceType;
import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.service.TestResultService;
import nl.testinsight.backend.source.playwright.dto.PlaywrightReportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestResultImportPlaywrightServiceTest {
    @Mock
    private PlaywrightTestProcessor playwrightTestProcessor;
    @Mock
    private TestResultService testResultService;
    @Mock
    private PlaywrightReportValidator playwrightReportValidator;
    @InjectMocks
    private TestResultImportPlaywrightService importService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSupportPlaywright() {
        // Act
        boolean result = importService.supports(SourceType.PLAYWRIGHT);
        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotSupportCucumber() {
        // Act
        boolean result = importService.supports(SourceType.CUCUMBER);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldImportValidPlaywrightReport() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("""
                {
                  "suites": []
                }
                """);

        TestDataDto data = mock(TestDataDto.class);
        List<TestDataDto> testResults = List.of(data);

        when(playwrightTestProcessor.process(
                any(PlaywrightReportDto.class)
        )).thenReturn(testResults);

        // Act
        importService.importResults(applicationName, report);

        // Assert
        verify(playwrightReportValidator).validate(report);

        verify(playwrightTestProcessor)
                .process(any(PlaywrightReportDto.class));

        verify(testResultService).saveTestResultData(
                report,
                testResults,
                applicationName
        );
    }

    @Test
    void shouldRejectInvalidPlaywrightReport() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("{}");

        doThrow(new IllegalArgumentException("Invalid Playwright report"))
                .when(playwrightReportValidator)
                .validate(report);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importService.importResults(
                        applicationName, report
                )
        );

        assertEquals(
                "Invalid Playwright report",
                exception.getMessage()
        );

        verify(playwrightReportValidator).validate(report);

        verifyNoInteractions(playwrightTestProcessor);
        verifyNoInteractions(testResultService);
    }

    @Test
    void shouldPropagateExceptionWhenSavingFails() {
        // Arrange
        String applicationName = "TestInsight";

        JsonNode report = objectMapper.readTree("""
                {
                  "suites": []
                }
                """);

        List<TestDataDto> testResults =
                List.of(mock(TestDataDto.class));

        when(playwrightTestProcessor.process(
                any(PlaywrightReportDto.class)
        )).thenReturn(testResults);

        when(testResultService.saveTestResultData(
                report,
                testResults,
                applicationName
        )).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> importService.importResults(
                        applicationName, report
                )
        );

        assertEquals("Database error", exception.getMessage());

        verify(playwrightReportValidator).validate(report);

        verify(playwrightTestProcessor)
                .process(any(PlaywrightReportDto.class));

        verify(testResultService).saveTestResultData(
                report,
                testResults,
                applicationName
        );
    }
}