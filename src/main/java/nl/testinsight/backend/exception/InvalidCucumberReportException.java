package nl.testinsight.backend.exception;

public class InvalidCucumberReportException
        extends RuntimeException {


    public InvalidCucumberReportException(String message) {
        super(message);
    }
}