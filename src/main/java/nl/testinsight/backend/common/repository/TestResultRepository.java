package nl.testinsight.backend.common.repository;

import nl.testinsight.backend.common.entity.TestResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRepository extends JpaRepository<TestResultEntity, Long> {

}
