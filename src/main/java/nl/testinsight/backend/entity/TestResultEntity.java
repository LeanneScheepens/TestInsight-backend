package nl.testinsight.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.testinsight.backend.enums.TestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "test_result")
public class TestResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCaseEntity testCaseEntity;

    @Column(name = "run_id", nullable = false)
    private UUID runId;

    @Column(name = "retry_index", nullable = false)
    private int retryIndex = 0;

//    @Column(name = "expected_result")
//    private String expectedResult;
//
//    @Column(name = "actual_result")
//    private String actualResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TestStatus status;

    @Column(name = "failed_step", columnDefinition = "TEXT")
    private String failedStep;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;


//    @Column(name = "created_at", nullable = false)  // Maybe for in the future when Jira is implemented
//    private LocalDateTime createdAt;

//    @Column(name = "original_report") // The JSON report of just one test
//    private String originalReport;

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(name = "source_payload")
//    private JsonNode sourcePayload;


    public TestResultEntity() {
    }

}
