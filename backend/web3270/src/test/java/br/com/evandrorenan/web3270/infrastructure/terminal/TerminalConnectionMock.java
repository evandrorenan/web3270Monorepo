package br.com.evandrorenan.web3270.infrastructure.terminal;

import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import br.com.evandrorenan.web3270.domain.session.SessionProperties;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class TerminalConnectionMock implements TerminalConnection {
    private final SimpMessagingTemplate messagingTemplate;
    private final IScreenService screenService;

    @Setter
    private String currentSessionId;

    private boolean connected = false;
    private String screenContent = "WELCOME TO IBM 3270 MOCK TERMINAL\nENTER USERID:";

    @Override
    public void connect(SessionProperties props) {
        log.info("Mock connecting to {}:{}", props.host(), props.port());
        this.connected = true;
    }

    @Override
    public void disconnect() {
        log.info("Mock disconnecting");
        this.connected = false;
    }

    @Override
    public boolean isConnected() {
        log.debug("Mock isConnected called, returning: {}", connected);
        return connected;
    }

    @Override
    public String getScreen() {
        log.debug("Mock getScreen called, returning content length: {}", 
            screenContent != null ? screenContent.length() : "null");
        return connected ? screenContent : "";
    }

    @Override
    public void sendKey(String key) {
        log.info("Mock sending key: {}", key);
        if (connected) {
            if ("ENTER".equalsIgnoreCase(key)) {
                this.screenContent = "LOGGED IN TO MOCK SYSTEM\nREADY FOR COMMANDS";
            } else {
                this.screenContent += "\nReceived: " + key;
            }
            broadcastUpdate();
        }
    }

    private void broadcastUpdate() {
        if (currentSessionId != null) {
            log.info("Mock broadcasting update for session: {} with content: {}", currentSessionId, screenContent);
            // Simulate the ScreenResponse that the real service would return
            ScreenResponse response = new ScreenResponse(
                screenContent,
                0, // cursorPosition
                java.util.Collections.emptyList() // fields
            );
            messagingTemplate.convertAndSend("/topic/session/" + currentSessionId + "/screen", response);
        } else {
            log.warn("Mock broadcast skipped: currentSessionId is null for content: {}", screenContent);
        }
    }
}
