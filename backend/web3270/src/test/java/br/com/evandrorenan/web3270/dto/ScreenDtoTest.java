package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScreenDtoTest {

    @Test
    @DisplayName("should initialize with default values and generate screenId")
    void shouldInitializeWithDefaults() {
        ScreenDto screenDto = new ScreenDto();
        
        assertNotNull(screenDto.getFields());
        assertTrue(screenDto.getFields().isEmpty());
        assertNotNull(screenDto.getPositions());
        assertTrue(screenDto.getPositions().isEmpty());
        assertNotNull(screenDto.getFieldPos());
        assertTrue(screenDto.getFieldPos().isEmpty());
        assertNotNull(screenDto.getScreendId());
        // ScreenId is generated in constructor based on timestamp
        assertFalse(screenDto.getScreendId().isEmpty());
    }

    @Test
    @DisplayName("should set and get values correctly")
    void shouldSetAndGetValues() {
        ScreenDto screenDto = new ScreenDto();
        screenDto.setSessionId("session-1");
        screenDto.setCursorPos(10);
        
        List<FieldDto> fields = Collections.singletonList(new FieldDto());
        screenDto.setFields(fields);

        assertEquals("session-1", screenDto.getSessionId());
        assertEquals(10, screenDto.getCursorPos());
        assertEquals(fields, screenDto.getFields());
    }
    
    @Test
    @DisplayName("should implement toString correctly")
    void shouldImplementToString() {
        ScreenDto screenDto = new ScreenDto();
        screenDto.setSessionId("session-1");
        
        String toString = screenDto.toString();
        assertTrue(toString.contains("sessionId=session-1"));
        assertTrue(toString.contains("screendId="));
    }
}
