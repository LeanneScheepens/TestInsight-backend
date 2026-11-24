package nl.testinsight.backend.common.service;

import nl.testinsight.backend.common.dto.TestDataDto;
import nl.testinsight.backend.common.entity.ApplicationEntity;
import nl.testinsight.backend.common.entity.TestCaseEntity;
import nl.testinsight.backend.common.entity.TestRawDataEntity;
import nl.testinsight.backend.common.mapper.TestCaseMapper;
import nl.testinsight.backend.common.repository.TestCaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestCaseServiceTest {
    @Mock
    private TestCaseMapper testCaseMapper;
    @Mock
    private TestCaseRepository testCaseRepository;
    @InjectMocks
    private TestCaseService testCaseService;
    @Mock
    private TestDataDto data;
    @Mock
    private ApplicationEntity applicationEntity;
    @Mock
    private TestRawDataEntity rawData;

    @Test
    void shouldReturnExistingTestCase() {
        // Arrange
        TestCaseEntity existingTestCase = new TestCaseEntity();

        when(applicationEntity.getId()).thenReturn(1);

        when(testCaseRepository
                .findByApplicationEntity_IdAndSourceTypeAndFeatureIdAndScenarioId(
                        eq(1), any(), any(), any()
                ))
                .thenReturn(Optional.of(existingTestCase));

        // Act
        TestCaseEntity result =
                testCaseService.findOrCreateTestCase(
                        data, applicationEntity, rawData
                );

        // Assert
        assertSame(existingTestCase, result);

        verify(testCaseRepository, never())
                .save(any(TestCaseEntity.class));

        verifyNoInteractions(testCaseMapper);
    }

    @Test
    void shouldCreateTestCaseWhenNotFound() {
        // Arrange
        TestCaseEntity newTestCase = new TestCaseEntity();

        when(applicationEntity.getId()).thenReturn(1);

        when(testCaseRepository
                .findByApplicationEntity_IdAndSourceTypeAndFeatureIdAndScenarioId(
                        eq(1),
                        any(),
                        any(),
                        any()
                ))
                .thenReturn(Optional.empty());

        when(testCaseMapper.mapTestCase(
                data, applicationEntity, rawData
        )).thenReturn(newTestCase);

        when(testCaseRepository.save(newTestCase))
                .thenReturn(newTestCase);

        // Act
        TestCaseEntity result =
                testCaseService.findOrCreateTestCase(
                        data, applicationEntity, rawData
                );

        // Assert
        assertNotNull(result);
        assertSame(newTestCase, result);

        verify(testCaseMapper, times(1))
                .mapTestCase(data, applicationEntity, rawData);

        verify(testCaseRepository, times(1))
                .save(newTestCase);
    }

    @Test
    void shouldNotCallMapperWhenTestCaseExists() {
        // Arrange
        TestCaseEntity existingTestCase = new TestCaseEntity();

        when(applicationEntity.getId()).thenReturn(1);

        when(testCaseRepository
                .findByApplicationEntity_IdAndSourceTypeAndFeatureIdAndScenarioId(
                        eq(1),
                        any(),
                        any(),
                        any()
                ))
                .thenReturn(Optional.of(existingTestCase));

        // Act
        testCaseService.findOrCreateTestCase(
                data, applicationEntity, rawData
        );

        // Assert
        verifyNoInteractions(testCaseMapper);

        verify(testCaseRepository, never())
                .save(any(TestCaseEntity.class));
    }
}