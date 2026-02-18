package br.com.evandrorenan.web3270.presentation.websocket;

import br.com.evandrorenan.web3270.application.dto.ScreenResponse;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import br.com.evandrorenan.web3270.infrastructure.configuration.TestConfig;
import br.com.evandrorenan.web3270.infrastructure.terminal.TerminalConnectionMock;
import br.com.evandrorenan.web3270.presentation.api.v1.request.CreateSessionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private WebSocketStompClient stompClient;

    @Autowired
    private TerminalConnection terminalConnection;

    @BeforeEach
    void setUp() {
        // Use a simpler client without SockJS for testing if possible, or ensure it's robust
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    @DisplayName("should receive screen update via websocket after session creation")
    void shouldReceiveScreenUpdate() throws Exception {
        // 1. Create Session via HTTP
        CreateSessionRequest createRequest = new CreateSessionRequest("127.0.0.1", "23", "3270", "037");
        MvcResult createResult = mockMvc.perform(post("/api/v1/sessions")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String sessionId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("sessionId").asText();

        terminalConnection.setCurrentSessionId(sessionId);

        // 2. Connect to WebSocket
        BlockingQueue<ScreenResponse> blockingQueue = new LinkedBlockingDeque<>();
        String url = "ws://localhost:" + port + "/web3270-websocket";
        System.out.println("Connecting to WebSocket URL: " + url);

        StompSession session = stompClient
                .connect(url, new StompSessionHandlerAdapter() {
                    @Override
                    public void handleException(StompSession session, org.springframework.messaging.simp.stomp.StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                        System.err.println("STOMP Session Error: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        System.err.println("STOMP Transport Error: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                })
                .get(10, TimeUnit.SECONDS); // Increased timeout

        // 3. Subscribe to screen updates
        session.subscribe("/topic/session/" + sessionId + "/screen", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ScreenResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((ScreenResponse) payload);
            }
        });

        // Ensure subscription is processed
        Thread.sleep(1000);

        // 4. Trigger update (Send keys)
        mockMvc.perform(post("/api/v1/sessions/" + sessionId + "/keys")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"keys\": \"ENTER\"}"))
                .andExpect(status().isOk());

        // 5. Assert update received
        ScreenResponse screen = blockingQueue.poll(10, TimeUnit.SECONDS);
        assertThat(screen).isNotNull();
        assertThat(screen.content()).contains("LOGGED IN TO MOCK SYSTEM");
    }
}
