package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserInputDtoTest {

    @Test
    @DisplayName("should set and get values correctly")
    void shouldSetAndGetValues() {
        UserInputDto userInputDto = new UserInputDto();
        userInputDto.setSessionId("session-123");
        
        SendKeysDto sendKeysDto = new SendKeysDto();
        sendKeysDto.setText("test");
        List<SendKeysDto> sendKeysList = Collections.singletonList(sendKeysDto);
        userInputDto.setSendKeys(sendKeysList);

        assertEquals("session-123", userInputDto.getSessionId());
        assertEquals(sendKeysList, userInputDto.getSendKeys());
    }

    @Test
    @DisplayName("should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCode() {
        UserInputDto dto1 = new UserInputDto();
        dto1.setSessionId("123");
        
        UserInputDto dto2 = new UserInputDto();
        dto2.setSessionId("123");
        
        UserInputDto dto3 = new UserInputDto();
        dto3.setSessionId("456");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }
    
    @Test
    @DisplayName("should implement toString correctly")
    void shouldImplementToString() {
        UserInputDto dto = new UserInputDto();
        dto.setSessionId("123");
        
        String toString = dto.toString();
        assertTrue(toString.contains("sessionId=123"));
    }
}
