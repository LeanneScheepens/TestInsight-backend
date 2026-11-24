package nl.testinsight.backend.common.service;

import nl.testinsight.backend.common.entity.ApplicationEntity;
import nl.testinsight.backend.common.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository; //

    // Constructor injection
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }


    public ApplicationEntity findOrCreateApplication(String applicationName) {

        if (applicationName == null || applicationName.isBlank()) {
            throw new IllegalArgumentException(
                    "Application name is required"
            );
        }

        return applicationRepository
                .findByName(applicationName)
                .orElseGet(() -> {

                    ApplicationEntity applicationEntity =
                            new ApplicationEntity(applicationName);

                    return applicationRepository.save(applicationEntity);
                });
    }
}
