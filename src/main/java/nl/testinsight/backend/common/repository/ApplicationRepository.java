package nl.testinsight.backend.common.repository;

import nl.testinsight.backend.common.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository
        extends JpaRepository<ApplicationEntity, Integer> {
    Optional<ApplicationEntity> findByName(String name);
}
