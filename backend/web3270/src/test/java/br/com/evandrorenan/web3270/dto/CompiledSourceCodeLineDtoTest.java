package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CompiledSourceCodeLineDtoTest {

    @Test
    @DisplayName("should parse valid COBOL source line correctly")
    void shouldParseValidLine() {
        // Index 89 is where mapReference starts.
        // Text is 17 to 89 (72 chars)
        String prefix = "  000100  01 10   "; // 18 chars (0-17)
        String text = "MOVE A TO B.".concat(" ".repeat(72 - 12)); // 72 chars
        String reference = "REF1";
        String coLine = prefix + text + reference;

        CompiledSourceCodeLineDto dto = new CompiledSourceCodeLineDto(coLine);

        assertTrue(dto.isValid());
        assertEquals(100, dto.getLineId());
        assertEquals("01", dto.getPL());
        assertEquals("10", dto.getSL());
        assertTrue(dto.getText().contains("MOVE A TO B."));
        assertEquals("REF1", dto.getMapReference().trim());
    }

    @Test
    @DisplayName("should not parse invalid source line")
    void shouldNotParseInvalidLine() {
        CompiledSourceCodeLineDto dto = new CompiledSourceCodeLineDto("invalid line");
        assertFalse(dto.isValid());
    }
}
