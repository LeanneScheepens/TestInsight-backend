package nl.testinsight.backend.repository;
import nl.testinsight.backend.entity.ApplicationEntity;
import nl.testinsight.backend.entity.TestRawDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RawDataRepository
        extends JpaRepository<TestRawDataEntity, Integer> {
}
