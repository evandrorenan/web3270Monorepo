package br.com.evandrorenan.web3270.domain.session;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void shouldCreateSessionWithDisconnectedStatus() {
        SessionId id = SessionId.random();
        SessionProperties props = new SessionProperties("localhost", "23", "3270", "UTF-8");
        Session session = new Session(id, props);

        assertEquals(id, session.getId());
        assertEquals(props, session.getProperties());
        assertEquals(SessionStatus.DISCONNECTED, session.getStatus());
        assertFalse(session.isConnected());
        assertNotNull(session.getCreatedAt());
    }

    @Test
    void shouldChangeStatusToConnected() {
        Session session = new Session(SessionId.random(), 
            new SessionProperties("localhost", "23", "3270", "UTF-8"));
        
        session.connect();
        
        assertEquals(SessionStatus.CONNECTED, session.getStatus());
        assertTrue(session.isConnected());
    }

    @Test
    void shouldChangeStatusToDisconnected() {
        Session session = new Session(SessionId.random(), 
            new SessionProperties("localhost", "23", "3270", "UTF-8"));
        
        session.connect();
        session.disconnect();
        
        assertEquals(SessionStatus.DISCONNECTED, session.getStatus());
        assertFalse(session.isConnected());
    }
}
