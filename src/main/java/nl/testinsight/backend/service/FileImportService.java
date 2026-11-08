package nl.testinsight.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileImportService {
    public String readCucumberReport()
            throws IOException {

        Path path = Path.of(
                "reports/cucumber-report.json");

        return Files.readString(path);
    }

}
