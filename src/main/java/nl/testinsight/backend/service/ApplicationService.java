package nl.testinsight.backend.service;

import nl.testinsight.backend.entity.Application;
import nl.testinsight.backend.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository; //

    // Constructor injection
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }


    public Application findOrCreateApplication(String applicationName) {

        if (applicationName == null || applicationName.isBlank()) {
            throw new IllegalArgumentException(
                    "Application name is required"
            );
        }

        return applicationRepository
                .findByName(applicationName)
                .orElseGet(() -> {

                    Application application =
                            new Application(applicationName);

                    return applicationRepository.save(application);
                });
    }
}
