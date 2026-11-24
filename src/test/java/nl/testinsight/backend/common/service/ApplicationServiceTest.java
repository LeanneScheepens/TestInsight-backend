package nl.testinsight.backend.common.service;

import com.fasterxml.jackson.annotation.JacksonInject;
import jakarta.validation.constraints.Null;
import nl.testinsight.backend.common.entity.ApplicationEntity;
import nl.testinsight.backend.common.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


    @ExtendWith(MockitoExtension.class)
    class ApplicationServiceTest{

        @Mock
        ApplicationService applicationService;
        @Mock
        ApplicationRepository applicationRepository;

        @BeforeEach
        void setUp(){
            applicationService = new ApplicationService(applicationRepository);
        }

        @Test
        void shouldFindExistingApplication(){
            //Arrange
            ApplicationEntity existingApplication = new ApplicationEntity("Parabank");

            when(applicationRepository.findByName("Parabank"))
                .thenReturn(Optional.of(existingApplication));

            //Act
            ApplicationEntity result = applicationService.findOrCreateApplication("Parabank");

            //Assert
            assertEquals(existingApplication, result);
            verify(applicationRepository).findByName("Parabank");
            verify(applicationRepository, never()).save(any(ApplicationEntity.class));
        }
        @Test
        void shouldCreateApplicationWhenNotFound(){
            //Arrange
            String applicationName = "Parabank";
            when(applicationRepository.findByName("Parabank"))
                    .thenReturn(Optional.empty());
            when(applicationRepository.save(any(ApplicationEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            //Act
            ApplicationEntity result = applicationService.findOrCreateApplication(applicationName);
            //Assert
            assertNotNull(result);
            assertEquals(applicationName, result.getName());

            verify(applicationRepository).findByName(applicationName);
            verify(applicationRepository).save(any(ApplicationEntity.class));
        }
        @Test void shouldRejectBlankApplicationName(){
            //Act
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> applicationService.findOrCreateApplication(""));
            //Assert
            assertEquals("Application name is required", exception.getMessage());
            verifyNoInteractions(applicationRepository);
        }
        @Test void shouldRejectNullApplicationName(){
            //Act
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> applicationService.findOrCreateApplication(null));
            //Assert
            assertEquals("Application name is required", exception.getMessage());
            verifyNoInteractions(applicationRepository);
        }
}


