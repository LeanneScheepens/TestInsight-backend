package nl.testinsight.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.testinsight.backend.enums.SourceType;

@Setter
@Getter
@Entity
@Table(
        name = "test_case"//,
//        uniqueConstraints = @UniqueConstraint(
//                name = "uk_test_case_identity",
//                columnNames = {
//                        "application_id",
//                        "source_type",
//                        "feature_id",
//                        "scenario_id"
//                }
//        )
)
public class TestCaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicationEntity applicationEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raw_data_id", nullable = false)
    private TestRawDataEntity testRawDataEntity;

    @Column(nullable = false)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;


    @Column(name = "feature_id", length = 500)
    private String featureId;


    @Column(name = "scenario_id", length = 500)
    private String scenarioId;


    public TestCaseEntity() {
    }
}