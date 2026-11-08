package nl.testinsight.backend.entity;

import tools.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.testinsight.backend.enums.TestStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "test_result")
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCase testCase;

    @Column(name = "run_id", nullable = false)
    private UUID runId;

    @Column(name = "expected_result")
    private String expectedResult;

    @Column(name = "actual_result")
    private String actualResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TestStatus status;

    @Column(name = "failed_step", columnDefinition = "TEXT")
    private String failedStep;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "original_report")
    private String originalReport;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_payload")
    private JsonNode sourcePayload;


    public TestResult() {
    }

}
