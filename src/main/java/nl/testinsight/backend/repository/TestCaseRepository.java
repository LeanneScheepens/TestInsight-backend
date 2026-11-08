package nl.testinsight.backend.repository;

import nl.testinsight.backend.entity.TestCase;
import nl.testinsight.backend.enums.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestCaseRepository
        extends JpaRepository<TestCase, Integer> {

    //Finds an existing test case using the application ID, testing framework, Cucumber feature ID, and Cucumber scenario ID.
    Optional<TestCase>
    findByApplication_IdAndSourceTypeAndFeatureIdAndScenarioId(
            Integer applicationId,
            SourceType sourceType,
            String featureId,
            String scenarioId
    );
}
