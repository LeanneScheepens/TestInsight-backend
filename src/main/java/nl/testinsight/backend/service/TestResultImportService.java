package nl.testinsight.backend.service;

import nl.testinsight.backend.entity.TestResult;
import nl.testinsight.backend.mapper.TestResultMapper;
import nl.testinsight.backend.repository.TestResultRepository;
import nl.testinsight.backend.dto.CucumberResultDto;
import nl.testinsight.backend.dto.PlaywrightResultDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestResultImportService {

    private final TestResultRepository repository;
    private final TestResultMapper mapper;

    public TestResultImportService(TestResultRepository repository, TestResultMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    private void validate(
            String testName,
            String status) {

        if(testName == null || testName.isBlank()) {
            throw new RuntimeException(
                    "Test name missing");
        }

        if(status == null || status.isBlank()) {
            throw new RuntimeException(
                    "Status missing");
        }
    }
    private String cleanStatus(
            String status) {

        switch(status.toUpperCase()) {

            case "PASSED":
            case "PASS":
                return "PASS";

            case "FAILED":
            case "FAIL":
                return "FAIL";

            default:
                return "UNKNOWN";
        }
    }


    public void importCucumberResults(
            List<CucumberResultDto> results) {

        for(CucumberResultDto dto : results) {

            validate(
                    dto.getName(),
                    dto.getStatus());

            dto.setStatus(
                    cleanStatus(dto.getStatus()));

            TestResult entity =
                    mapper.fromCucumber(dto);

            repository.save(entity);
        }
    }
    public void importPlaywrightResults(
            List<PlaywrightResultDto> results) {

        for(PlaywrightResultDto dto : results) {

            validate(
                    dto.getTitle(),
                    dto.getOutcome());

            dto.setOutcome(
                    cleanStatus(dto.getOutcome()));

            TestResult entity =
                    mapper.fromPlaywright(dto);

            repository.save(entity);
        }
    }

}
