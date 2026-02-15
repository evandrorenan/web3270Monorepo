package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionPropertiesDtoTest {

    @Test
    @DisplayName("should set and get values correctly")
    void shouldSetAndGetValues() {
        SessionPropertiesDto dto = new SessionPropertiesDto();
        dto.setHost("localhost");
        dto.setPort("23");

        assertEquals("localhost", dto.getHost());
        assertEquals("23", dto.getPort());
    }
    
    @Test
    @DisplayName("should start with all args constructor")
    void shouldStartWithAllArgsConstructor() {
        SessionPropertiesDto dto = new SessionPropertiesDto("localhost", "23");
        assertEquals("localhost", dto.getHost());
        assertEquals("23", dto.getPort());
    }

    @Test
    @DisplayName("should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCode() {
        SessionPropertiesDto dto1 = new SessionPropertiesDto("host", "23");
        SessionPropertiesDto dto2 = new SessionPropertiesDto("host", "23");
        SessionPropertiesDto dto3 = new SessionPropertiesDto("other", "23");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }
    
    @Test
    @DisplayName("should implement toString correctly")
    void shouldImplementToString() {
        SessionPropertiesDto dto = new SessionPropertiesDto("localhost", "23");
        
        String toString = dto.toString();
        assertTrue(toString.contains("host=localhost"));
        assertTrue(toString.contains("port=23"));
    }
}
