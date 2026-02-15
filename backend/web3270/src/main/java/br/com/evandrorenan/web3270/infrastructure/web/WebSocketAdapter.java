package br.com.evandrorenan.web3270.infrastructure.web;

import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAdapter {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendScreenUpdate(String sessionId, ScreenResponse screen) {
        messagingTemplate.convertAndSend("/topic/session/" + sessionId + "/screen", screen);
    }
}
