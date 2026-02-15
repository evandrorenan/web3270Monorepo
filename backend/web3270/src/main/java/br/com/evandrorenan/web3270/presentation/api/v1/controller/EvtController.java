package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
public class EvtController {
    // This will be refactored to use Event Use Cases
    @GetMapping
    public ResponseEntity<String> getEvents() {
        return ResponseEntity.ok("Events controller refactored to Presentation layer");
    }
}
