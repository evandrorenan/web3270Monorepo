package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SendKeysDtoTest {

    @Test
    @DisplayName("should set and get values correctly")
    void shouldSetAndGetValues() {
        SendKeysDto dto = new SendKeysDto();
        dto.setRow(1);
        dto.setCol(2);
        dto.setText("text");
        dto.setFunctionKey("ENTER");

        assertEquals(1, dto.getRow());
        assertEquals(2, dto.getCol());
        assertEquals("text", dto.getText());
        assertEquals("ENTER", dto.getFunctionKey());
    }

    @Test
    @DisplayName("should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCode() {
        SendKeysDto dto1 = new SendKeysDto();
        dto1.setText("text");
        
        SendKeysDto dto2 = new SendKeysDto();
        dto2.setText("text");
        
        SendKeysDto dto3 = new SendKeysDto();
        dto3.setText("other");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }
    
    @Test
    @DisplayName("should implement toString correctly")
    void shouldImplementToString() {
        SendKeysDto dto = new SendKeysDto();
        dto.setText("text");
        
        String toString = dto.toString();
        assertTrue(toString.contains("text=text"));
    }
}
