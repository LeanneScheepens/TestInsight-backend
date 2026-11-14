package nl.testinsight.backend.enums;

public enum TestStatus {

    PASSED(1),
    SKIPPED(2),
    UNKNOWN(3),
    PENDING(4),
    UNDEFINED(5),
    AMBIGUOUS(6),
    FAILED(7);

    private final int priority;

    TestStatus(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
