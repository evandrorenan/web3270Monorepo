package br.com.evandrorenan.web3270.infrastructure.configuration;

import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import br.com.evandrorenan.web3270.infrastructure.persistence.CachedSessionRepository;
import br.com.evandrorenan.web3270.infrastructure.terminal.PcommTerminalAdapter;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class HexagonalConfiguration {

    @Bean
    public SessionRepository sessionRepository() {
        return new CachedSessionRepository();
    }

    @Bean
    public TerminalConnection terminalConnection(SimpMessagingTemplate messagingTemplate, IScreenService screenService) {
        return new PcommTerminalAdapter(messagingTemplate, screenService);
    }
}
