package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import br.com.evandrorenan.web3270.application.usecase.GetSessionScreenUseCase;
import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/screen")
public class ScreenController {
    private final GetSessionScreenUseCase getScreen;

    public ScreenController(GetSessionScreenUseCase getScreen) {
        this.getScreen = getScreen;
    }

    @GetMapping
    public ResponseEntity<ScreenResponse> getScreen(@PathVariable String sessionId) {
        return ResponseEntity.ok(getScreen.execute(sessionId));
    }
}
