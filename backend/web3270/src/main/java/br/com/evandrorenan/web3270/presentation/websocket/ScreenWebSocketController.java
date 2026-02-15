package br.com.evandrorenan.web3270.presentation.websocket;

import br.com.evandrorenan.web3270.application.usecase.GetSessionScreenUseCase;
import br.com.evandrorenan.web3270.application.usecase.SendKeysUseCase;
import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ScreenWebSocketController {
    private final GetSessionScreenUseCase getScreen;
    private final SendKeysUseCase sendKeys;

    public ScreenWebSocketController(GetSessionScreenUseCase getScreen, SendKeysUseCase sendKeys) {
        this.getScreen = getScreen;
        this.sendKeys = sendKeys;
    }

    @MessageMapping("/session/{sessionId}/screen")
    @SendTo("/topic/session/{sessionId}/screen")
    public ScreenResponse getScreen(@DestinationVariable String sessionId) {
        return getScreen.execute(sessionId);
    }

    @MessageMapping("/sendkeys")
    public void sendKeys(br.com.evandrorenan.web3270.dto.UserInputDto payload) {
        // Adapt DTO if needed, or use the legacy DTO for now as the WebSocket API seems to use it
        // For simplicity and to match the test, we'll use a wrapper or adapt it
        br.com.evandrorenan.web3270.application.port.SendKeysRequest request = 
            new br.com.evandrorenan.web3270.application.port.SendKeysRequest(payload.getSendKeys().get(0).getText());
        sendKeys.execute(payload.getSessionId(), request);
    }
}
