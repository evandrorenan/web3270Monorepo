package websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import br.com.evandrorenan.web3270.controller.ScreenController;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {br.com.evandrorenan.web3270.Web3270Application.class})
public class WebSocketScreenTest {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

	private SockJsClient sockJsClient;

	private WebSocketStompClient stompClient;

	private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @org.springframework.test.context.bean.override.mockito.MockitoBean
	private br.com.evandrorenan.web3270.session._interface.IScreenService screenService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
	private br.com.evandrorenan.web3270.session._interface.ISessionService sessionService;

	@BeforeEach
	public void setup() {
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		this.sockJsClient = new SockJsClient(transports);

		this.stompClient = new WebSocketStompClient(sockJsClient);
		this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}

	@Test
	public void testSendKeys() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				// We don't subscribe because ScreenController doesn't reply directly.
                // We verify the side effect (service call).
				try {
                    br.com.evandrorenan.web3270.dto.UserInputDto payload = new br.com.evandrorenan.web3270.dto.UserInputDto();
                    payload.setSessionId("123");
                    payload.setSendKeys(new java.util.ArrayList<>());
                    br.com.evandrorenan.web3270.dto.SendKeysDto sendKey = new br.com.evandrorenan.web3270.dto.SendKeysDto();
                    sendKey.setRow(1);
                    sendKey.setCol(1);
                    sendKey.setText("test");
                    payload.getSendKeys().add(sendKey);

					session.send("/ws/sendkeys", payload);
                    latch.countDown();
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/web3270-websocket", this.headers, handler, port);

		if (latch.await(3, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
            
            // Allow some time for the message to be processed
            Thread.sleep(1000);

            verify(sessionService).getSession("123");
            // We can also verify sendKeysAsync if we mock the session return
            // But since getSession returns null by default mock, sendKeysAsync receives null session.
            // ScreenController passes whatever it gets.
            verify(screenService).sendKeysAsync(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
		}
		else {
			fail("Timeout waiting for connection/send");
		}
	}

	private class TestSessionHandler extends StompSessionHandlerAdapter {

		private final AtomicReference<Throwable> failure;

		public TestSessionHandler(AtomicReference<Throwable> failure) {
			this.failure = failure;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			this.failure.set(new Exception(headers.toString()));
		}

		@Override
		public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
			this.failure.set(ex);
		}

		@Override
		public void handleTransportError(StompSession session, Throwable ex) {
			this.failure.set(ex);
		}
	}
}
