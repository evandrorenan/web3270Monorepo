package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionDtoTest {

    @Test
    @DisplayName("should set and get values correctly")
    void shouldSetAndGetValues() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("123");
        sessionDto.setConnected(true);

        assertEquals("123", sessionDto.getSessionId());
        assertTrue(sessionDto.isConnected());
    }

    @Test
    @DisplayName("should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCode() {
        SessionDto dto1 = new SessionDto();
        dto1.setSessionId("123");
        dto1.setConnected(true);

        SessionDto dto2 = new SessionDto();
        dto2.setSessionId("123");
        dto2.setConnected(true);

        SessionDto dto3 = new SessionDto();
        dto3.setSessionId("456");
        dto3.setConnected(false);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("should implement toString correctly")
    void shouldImplementToString() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setSessionId("123");
        sessionDto.setConnected(true);

        String toString = sessionDto.toString();
        assertTrue(toString.contains("sessionId=123"));
        assertTrue(toString.contains("connected=true"));
    }
}
