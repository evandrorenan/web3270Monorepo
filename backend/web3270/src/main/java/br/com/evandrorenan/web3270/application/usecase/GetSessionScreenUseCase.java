package br.com.evandrorenan.web3270.application.usecase;

import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import br.com.evandrorenan.web3270.domain.screen.Screen;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.exception.SessionNotFoundException;
import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetSessionScreenUseCase {
    private final SessionRepository repository;
    private final TerminalConnection connection;

    public GetSessionScreenUseCase(SessionRepository repository, TerminalConnection connection) {
        this.repository = repository;
        this.connection = connection;
    }

    public ScreenResponse execute(String sessionId) {
        log.debug("Executing GetSessionScreenUseCase for session: {}", sessionId);
        repository.findById(new SessionId(sessionId))
            .orElseThrow(() -> {
                log.warn("GetSessionScreen failed: Session {} not found", sessionId);
                return new SessionNotFoundException(new SessionId(sessionId));
            });

        String content = connection.getScreen();
        // For now, returning a simple screen without parsed fields
        // In a real scenario, the adapter would provide the parsed screen
        Screen screen = new Screen(content, 0, Collections.emptyList());
        log.info("Screen retrieved successfully for session {}", sessionId);
        return ScreenResponse.from(screen);
    }
}
