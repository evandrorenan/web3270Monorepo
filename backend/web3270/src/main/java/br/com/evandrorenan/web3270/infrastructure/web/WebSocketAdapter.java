package br.com.evandrorenan.web3270.infrastructure.web;

import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketAdapter {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendScreenUpdate(String sessionId, ScreenResponse screen) {
        MDC.put("sessionId", sessionId);
        try {
            log.debug("Sending screen update via WebSocket for session {}", sessionId);
            messagingTemplate.convertAndSend("/topic/session/" + sessionId + "/screen", screen);
        } finally {
            MDC.remove("sessionId");
        }
    }
}
