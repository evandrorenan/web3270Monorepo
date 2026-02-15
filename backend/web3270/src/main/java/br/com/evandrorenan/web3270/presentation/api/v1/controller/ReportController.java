package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    // This will be refactored to use Report Use Cases
    @GetMapping
    public ResponseEntity<String> getReports() {
        return ResponseEntity.ok("Reports controller refactored to Presentation layer");
    }
}
