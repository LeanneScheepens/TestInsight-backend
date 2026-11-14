package nl.testinsight.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

@Setter
@Getter
@Entity
@Table(
        name = "test_raw_data"
)
public class TestRawDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_payload")
    private JsonNode sourcePayload;

    public TestRawDataEntity() {
    }
}