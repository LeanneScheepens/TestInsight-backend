package nl.testinsight.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class TestResult {
    @Id
    @GeneratedValue
    private Long id;

    private String testName;

    private String status;

    private String framework;

    private LocalDateTime executionDate;

    public TestResult() {
    }

    public TestResult(String testName, String status, String framework, LocalDateTime executionDate) {
        this.testName = testName;
        this.status = status;
        this.framework = framework;
        this.executionDate = executionDate;
    }

    public Long getId() {
        return id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }
}
