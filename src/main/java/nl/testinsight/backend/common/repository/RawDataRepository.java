package nl.testinsight.backend.common.repository;

import nl.testinsight.backend.common.entity.TestRawDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawDataRepository
        extends JpaRepository<TestRawDataEntity, Integer> {
}
