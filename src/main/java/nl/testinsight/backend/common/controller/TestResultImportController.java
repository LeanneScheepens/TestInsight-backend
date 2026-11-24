package nl.testinsight.backend.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nl.testinsight.backend.common.dto.ImportResponseDto;
import nl.testinsight.backend.common.SourceType;
import nl.testinsight.backend.common.model.TestResultImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;

import java.util.List;


@RestController
// Spring boot uses this to recognize the class, it is a component that handles HTTP requests and responses.
@RequestMapping("/api/import")  // Endpoint
@Tag(name = "Test result imports", description = "Import test results from test frameworks")

public class TestResultImportController {

    private final Logger logger = LoggerFactory.getLogger(TestResultImportController.class); // Logger for logging messages, useful for debugging and monitoring the application.


    private final List<TestResultImportService> importServices;

    public TestResultImportController(
            List<TestResultImportService> importServices) {
        this.importServices = importServices;
    }

    @PostMapping(
            value =  "/testresult",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Import test results", description = "Stores the test results for the given SourceType. " +
            "Supported sourcetypes are: CUCUMBER, PLAYWRIGHT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Test results imported"), // Handled by @responseStatus(HttpStatus.CREATED in the controller
            @ApiResponse(responseCode = "400", description = "Invalid request or report"), // Handled by GlobalExceptionHandler and validator
            @ApiResponse(responseCode = "413", description = "Request body exceeds 10 MiB"), // Handled by RequestSizeFilter
            @ApiResponse(responseCode = "415", description = "Unsupported media type"), // Handled by Spring boot
            @ApiResponse(responseCode = "500", description = "Internal server error") // Handled by spring boot
    })


    public ImportResponseDto importTestResults(

            @RequestHeader("X-Application-Name") // The @RequestHeader annotation tells Spring to bind the value of the X-Application-Name HTTP header to the applicationName parameter. This allows the client to specify the name of the application that is sending the test results.
                @NotBlank
                @Size(max = 100)
            String applicationName,

            @RequestParam
                @NotNull
            SourceType sourceType,

            @RequestBody JsonNode jsonReport) { // The @RequestBody annotation tells Spring to bind the incoming HTTP request body to the report parameter. The report is expected to be a JSON object representing the Cucumber test results.

        for (TestResultImportService importService : importServices) {
            if (importService.supports(sourceType)) {
                logger.info("Json request will be imported using the importService: %s".formatted(importService.getClass()));
                return importService.importResults(applicationName, jsonReport);
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported source type:" + sourceType);
    }
}
