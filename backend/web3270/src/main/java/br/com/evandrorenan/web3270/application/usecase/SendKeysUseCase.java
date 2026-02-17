package br.com.evandrorenan.web3270.application.usecase;

import br.com.evandrorenan.web3270.application.port.SendKeysRequest;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.exception.SessionNotFoundException;
import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendKeysUseCase {
    private final SessionRepository repository;
    private final TerminalConnection connection;

    public SendKeysUseCase(SessionRepository repository, TerminalConnection connection) {
        this.repository = repository;
        this.connection = connection;
    }

    public void execute(String sessionId, SendKeysRequest request) {
        log.debug("Executing SendKeysUseCase for session: {}", sessionId);
        repository.findById(new SessionId(sessionId))
            .orElseThrow(() -> {
                log.warn("SendKeys failed: Session {} not found", sessionId);
                return new SessionNotFoundException(new SessionId(sessionId));
            });

        connection.sendKeys(request.keys());
        log.info("Keys sent successfully to session {}", sessionId);
    }
}
