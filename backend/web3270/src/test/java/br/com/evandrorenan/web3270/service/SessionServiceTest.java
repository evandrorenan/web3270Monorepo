package br.com.evandrorenan.web3270.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SimpMessagingTemplate messageTemplate;

    @Mock
    private br.com.evandrorenan.web3270.session.MySessionFactory sessionFactory;

    @InjectMocks
    private SessionService sessionService;

    private Map<String, IMySession> sessionMap;
    private IMySession sessionMock;

    @BeforeEach
    void setUp() {
        sessionMap = new HashMap<>();
        sessionMock = mock(IMySession.class);
        sessionService.setSessionMap(sessionMap);
    }

    @Test
    @DisplayName("should create new session correctly")
    void shouldCreateNewSession() throws ExceptionWeb3270 {
        when(sessionFactory.getNewSession("host", "port")).thenReturn(sessionMock);
        when(sessionMock.getSessionId()).thenReturn("session-1");
        when(sessionMock.isConnected()).thenReturn(true);

        SessionDto result = sessionService.createNewSessionDto("host", "port");

        assertNotNull(result);
        assertEquals("session-1", result.getSessionId());
        assertTrue(result.isConnected());
        assertTrue(sessionMap.containsKey("session-1"));
    }

    @Test
    @DisplayName("should handle exception when creating session")
    void shouldHandleExceptionOnCreateSession() throws ExceptionWeb3270 {
        when(sessionFactory.getNewSession("host", "port")).thenThrow(new ExceptionWeb3270("Error", "Detail", null));

        SessionDto result = sessionService.createNewSessionDto("host", "port");

        assertNull(result);
    }

    @Test
    @DisplayName("should get session by id when exists")
    void shouldGetSessionById() {
        String sessionId = "session-1";
        sessionMap.put(sessionId, sessionMock);

        IMySession result = sessionService.getSession(sessionId);

        assertNotNull(result);
        assertEquals(sessionMock, result);
    }
    
    @Test
    @DisplayName("should return null when session not found")
    void shouldReturnNullWhenSessionNotFound() {
        IMySession result = sessionService.getSession("unknown");
        assertNull(result);
    }

    @Test
    @DisplayName("should disconnect session correctly")
    void shouldDisconnectSession() {
        String sessionId = "session-1";
        sessionMap.put(sessionId, sessionMock);

        SessionDto result = sessionService.disconnect(sessionId);

        assertFalse(sessionMap.containsKey(sessionId));
        assertEquals(sessionId, result.getSessionId());
        assertFalse(result.isConnected());
        verify(sessionMock).dispose();
    }
    
    @Test
    @DisplayName("should get session dto from session")
    void shouldGetSessionDto() {
        when(sessionMock.getSessionId()).thenReturn("session-1");
        when(sessionMock.isConnected()).thenReturn(true);

        SessionDto result = sessionService.getSessionDto(sessionMock);

        assertNotNull(result);
        assertEquals("session-1", result.getSessionId());
        assertTrue(result.isConnected());
    }

    @Test
    @DisplayName("should return null dto when session is null")
    void shouldReturnNullDtoWhenSessionIsNull() {
        SessionDto result = sessionService.getSessionDto(null);
        assertNull(result);
    }

    @Test
    @DisplayName("should monitor connection status")
    void testMonitorConnectionStatus() throws ExceptionWeb3270 {
        sessionMap.put("session-1", sessionMock);
        when(sessionMock.getHost()).thenReturn("host");
        when(sessionMock.getHostPort()).thenReturn("port");
        when(sessionMock.getSessionId()).thenReturn("session-1");
        
        // Mock newSession to return null (simulating failure/disconnection)
        when(sessionFactory.getNewSession("host", "port")).thenReturn(null);

        sessionService.monitorConnectionStatus();

        assertFalse(sessionMap.containsKey("session-1"));
        verify(sessionMock).dispose();
    }
}
