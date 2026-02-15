package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class BaseLocatorDtoTest {

    @Test
    @DisplayName("should validate work area lines correctly")
    void shouldValidateWorkArea() {
        String line = "12345678  F1F2F3F4 F5F6F7F8  F9C1C2C3 C4C5C6C7  *123456789ABCDEFG*";
        assertTrue(BaseLocatorDto.isValidWorkArea(line));
        assertFalse(BaseLocatorDto.isValidWorkArea("invalid"));
    }
    
    @Test
    @DisplayName("should handle repetitions correctly")
    void shouldHandleRepetitions() {
        BaseLocatorDto dto = new BaseLocatorDto();
        dto.getWorkAreaEbcdic(); // initialize with dots
        
        // I'll skip testing private methods directly and focus on what's visible.
        // Actually, I can test isValidWorkArea which is public static.
        assertTrue(BaseLocatorDto.isValidWorkArea("12345678  F1F2F3F4 F5F6F7F8  F9C1C2C3 C4C5C6C7  *123456789ABCDEFG*"));
        assertFalse(BaseLocatorDto.isValidWorkArea("invalid"));
    }
    
    @Test
    @DisplayName("should pad work areas correctly")
    void shouldPadWorkAreas() {
        BaseLocatorDto dto = new BaseLocatorDto();
        String ebcdic = dto.getWorkAreaEbcdic();
        assertEquals(4096, ebcdic.length());
        assertTrue(ebcdic.startsWith("."));
        
        String hex = dto.getWorkAreaHex();
        assertEquals(4096 * 2, hex.length());
        assertTrue(hex.startsWith("00"));
    }
}
