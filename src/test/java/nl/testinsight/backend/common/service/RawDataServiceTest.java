package nl.testinsight.backend.common.service;

import nl.testinsight.backend.common.entity.TestRawDataEntity;
import nl.testinsight.backend.common.repository.RawDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawDataServiceTest {
    @Mock
    private RawDataRepository rawDataRepository;
    @InjectMocks
    RawDataService rawDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSaveRawData() {
        // Arrange
        JsonNode report = objectMapper.readTree(
                "{\"name\":\"TestInsight\"}"
        );
        when(rawDataRepository.save(any(TestRawDataEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        TestRawDataEntity result = rawDataService.saveRawData(report);

        // Assert
        assertNotNull(result);
        assertEquals(report, result.getSourcePayload());

        ArgumentCaptor<TestRawDataEntity> captor =
                ArgumentCaptor.forClass(TestRawDataEntity.class);

        verify(rawDataRepository, times(1)).save(captor.capture());

        assertEquals(report, captor.getValue().getSourcePayload());
        assertSame(captor.getValue(), result);
    }
}
