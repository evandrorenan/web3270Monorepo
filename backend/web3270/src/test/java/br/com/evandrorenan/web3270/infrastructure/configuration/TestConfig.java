package br.com.evandrorenan.web3270.infrastructure.configuration;

import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import br.com.evandrorenan.web3270.infrastructure.terminal.TerminalConnectionMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@org.springframework.context.annotation.Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public TerminalConnection terminalConnection(
            org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate,
            br.com.evandrorenan.web3270.session._interface.IScreenService screenService) {
        return new TerminalConnectionMock(messagingTemplate, screenService);
    }
}
