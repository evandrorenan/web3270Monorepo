package br.com.evandrorenan.web3270.application.usecase;

import br.com.evandrorenan.web3270.application.dto.SessionResponse;
import br.com.evandrorenan.web3270.application.port.CreateSessionRequest;
import br.com.evandrorenan.web3270.application.service.ApplicationException;
import br.com.evandrorenan.web3270.domain.session.Session;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.SessionProperties;
import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateSessionUseCase {
    private final SessionRepository repository;
    private final TerminalConnection connection;

    public CreateSessionUseCase(SessionRepository repository, TerminalConnection connection) {
        this.repository = repository;
        this.connection = connection;
    }

    public SessionResponse execute(CreateSessionRequest request) {
        log.info("Attempting to create a new terminal session for host: {}, port: {}", request.host(), request.port());
        SessionProperties props = new SessionProperties(
            request.host(),
            request.port(),
            request.type(),
            request.codePage()
        );

        Session session = new Session(SessionId.random(), props);

        try {
            connection.connect(props);
            session.connect();
            repository.save(session);
            log.info("Session {} created and connected successfully", session.getId().value());
            return SessionResponse.from(session);
        } catch (Exception e) {
            log.error("Failed to create session for host: {}", request.host(), e);
            throw new ApplicationException("Failed to create session", e);
        }
    }
}
