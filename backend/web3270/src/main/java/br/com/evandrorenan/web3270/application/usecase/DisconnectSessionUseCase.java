package br.com.evandrorenan.web3270.application.usecase;

import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import org.springframework.stereotype.Component;

@Component
public class DisconnectSessionUseCase {
    private final SessionRepository repository;
    private final TerminalConnection connection;

    public DisconnectSessionUseCase(SessionRepository repository, TerminalConnection connection) {
        this.repository = repository;
        this.connection = connection;
    }

    public void execute(String sessionId) {
        SessionId id = new SessionId(sessionId);
        repository.findById(id).ifPresent(session -> {
            connection.disconnect();
            session.disconnect();
            repository.delete(id);
        });
    }
}
