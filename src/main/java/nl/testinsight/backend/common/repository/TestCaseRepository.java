package nl.testinsight.backend.common.repository;

import nl.testinsight.backend.common.entity.TestCaseEntity;
import nl.testinsight.backend.common.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestCaseRepository
        extends JpaRepository<TestCaseEntity, Integer> {

    //Finds an existing test case using the application ID, testing framework, Cucumber feature ID, and Cucumber scenario ID.
    Optional<TestCaseEntity>
    findByApplicationEntity_IdAndSourceTypeAndFeatureIdAndScenarioId(
            Integer applicationId,
            SourceType sourceType,
            String featureId,
            String scenarioId
    );
}
