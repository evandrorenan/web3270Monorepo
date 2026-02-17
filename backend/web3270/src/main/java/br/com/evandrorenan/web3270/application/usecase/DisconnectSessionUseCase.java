package br.com.evandrorenan.web3270.application.usecase;

import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DisconnectSessionUseCase {
    private final SessionRepository repository;
    private final TerminalConnection connection;

    public DisconnectSessionUseCase(SessionRepository repository, TerminalConnection connection) {
        this.repository = repository;
        this.connection = connection;
    }

    public void execute(String sessionId) {
        log.info("Executing DisconnectSessionUseCase for session: {}", sessionId);
        SessionId id = new SessionId(sessionId);
        repository.findById(id).ifPresent(session -> {
            log.info("Disconnecting and deleting session {}", sessionId);
            connection.disconnect();
            session.disconnect();
            repository.delete(id);
        });
    }
}
