package nl.testinsight.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Dashboard", description = "Dashboard and backend status endpoints")
public class DashboardController {

    @GetMapping("/dashboard")
    @Operation(summary = "Check backend status")
    public String health() {
        return "Backend is running";
    }
}