package br.com.evandrorenan.web3270.presentation.api.v1.controller;

import br.com.evandrorenan.web3270.application.usecase.CreateSessionUseCase;
import br.com.evandrorenan.web3270.application.usecase.DisconnectSessionUseCase;
import br.com.evandrorenan.web3270.application.usecase.SendKeysUseCase;
import br.com.evandrorenan.web3270.application.dto.SessionResponse;
import br.com.evandrorenan.web3270.presentation.api.v1.request.CreateSessionRequest;
import br.com.evandrorenan.web3270.presentation.api.v1.request.SendKeysRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {
    private final CreateSessionUseCase createSession;
    private final DisconnectSessionUseCase disconnect;
    private final SendKeysUseCase sendKeys;

    public SessionController(
            CreateSessionUseCase createSession,
            DisconnectSessionUseCase disconnect,
            SendKeysUseCase sendKeys) {
        this.createSession = createSession;
        this.disconnect = disconnect;
        this.sendKeys = sendKeys;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        br.com.evandrorenan.web3270.application.port.CreateSessionRequest appRequest = 
            new br.com.evandrorenan.web3270.application.port.CreateSessionRequest(
                request.host(), request.port(), request.type(), request.codePage()
            );
        return ResponseEntity.ok(createSession.execute(appRequest));
    }

    @PostMapping("/{sessionId}/keys")
    public ResponseEntity<Void> sendKeys(@PathVariable String sessionId, @RequestBody SendKeysRequest request) {
        sendKeys.execute(sessionId, new br.com.evandrorenan.web3270.application.port.SendKeysRequest(request.keys()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> disconnect(@PathVariable String sessionId) {
        disconnect.execute(sessionId);
        return ResponseEntity.noContent().build();
    }
}
