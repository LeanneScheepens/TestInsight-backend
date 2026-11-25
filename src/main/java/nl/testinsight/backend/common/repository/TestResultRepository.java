package nl.testinsight.backend.common.repository;

import nl.testinsight.backend.common.dto.TestStatusCountDto;
import nl.testinsight.backend.common.dto.TestStatusTrendDto;
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
    List<TestStatusCountDto> countLatestTestResultsWithStatus();

    @Query(value = """
        WITH days AS (
            SELECT generate_series( 
                    CURRENT_DATE - 29, 
                    CURRENT_DATE,
                    interval '1 day'
                    )::date AS day
            ),
                    
        ranked_test_results AS (
            SELECT
                days.day, test_result.status,
                ROW_NUMBER() OVER (
                    PARTITION BY days.day, test_result.test_case_id
                    ORDER BY test_result.executed_at DESC,
                             test_result.id DESC
                ) AS row_number
                    
            FROM days
            JOIN public.test_result
                ON test_result.executed_at < days.day + interval '1 day'
        )
            SELECT
            status,
            COUNT(*) AS
            count,
            day AS date
            FROM ranked_test_results
            WHERE row_number = 1
            GROUP BY day, status
            ORDER BY day ASC, status ASC
                
        """, nativeQuery = true)
    List<TestStatusTrendDto> findTestStatusTrend();
}
