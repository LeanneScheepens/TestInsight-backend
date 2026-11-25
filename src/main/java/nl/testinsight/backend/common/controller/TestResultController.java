package nl.testinsight.backend.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import nl.testinsight.backend.common.dto.TestStatusCountDto;
import nl.testinsight.backend.common.dto.TestStatusTrendDto;
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
    @Operation(summary = "Retrieve latest test status counts", description = "Retrieves the latest result per test case " +
                    "and aggregates the results by test status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test status counts successfully retrieved"), // Handled by Spring Boot
            @ApiResponse(responseCode = "406", description = "Not acceptable media type"), // Handled by Spring Boot
            @ApiResponse(responseCode = "500", description = "Internal server error") // Handled by Spring Boot
    })
    public List<TestStatusCountDto> getLatestTestResults(){
        return testResultService.getLatestTestResultsWithStatus();
    }

    @GetMapping("/statusTrend")
    @Operation(summary = "Retrieve test status trends over time", description = "Retrieves daily test status counts for the last 30 days " +
                    "using the latest known result per test case for each day. " +
                    "Days without new test executions retain the latest known test statuses.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test status trends successfully retrieved"), // Handled by Spring Boot
            @ApiResponse(responseCode = "406", description = "Not acceptable media type"), // Handled by Spring Boot
            @ApiResponse(responseCode = "500", description = "Internal server error") // Handled by Spring Boot
    })
    public List<TestStatusTrendDto> getStatusTrendResults(){
        return testResultService.getStatusTrendResultsWithExecutionDate();
    }
}
