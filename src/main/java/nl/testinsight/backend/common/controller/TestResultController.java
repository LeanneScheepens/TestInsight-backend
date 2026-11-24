package nl.testinsight.backend.common.controller;

import nl.testinsight.backend.common.dto.TestStatusCountDto;
import nl.testinsight.backend.common.entity.TestResultEntity;
import nl.testinsight.backend.common.service.TestResultService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/testresults")
public class TestResultController {

    private final TestResultService testResultService;

    private TestResultController(TestResultService testResultService){
        this.testResultService = testResultService;
    }

    @GetMapping("/latest")
    public List<TestStatusCountDto> getLatestTestResults(){
        return testResultService.getLatestTestResultsWithStatus();
    }
}
