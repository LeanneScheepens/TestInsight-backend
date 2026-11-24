package nl.testinsight.backend.common.repository;

import nl.testinsight.backend.common.dto.TestStatusCountDto;
import nl.testinsight.backend.common.entity.TestResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResultEntity, Long> {

    @Query(value = """
            SELECT status, COUNT(*) AS count
            FROM (
                SELECT test_result.*,
                       ROW_NUMBER() OVER(
                          PARTITION BY test_case_id
                          ORDER BY executed_at DESC, id DESC
                       ) AS row_number
                   FROM public.test_result
                ) AS ranked_test_results
                WHERE row_number = 1
                GROUP BY status;
    """, nativeQuery = true)
    List<TestStatusCountDto> findLatestTestResultsWithStatus();
}
