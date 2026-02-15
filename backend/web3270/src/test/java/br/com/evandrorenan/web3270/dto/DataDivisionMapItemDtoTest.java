package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DataDivisionMapItemDtoTest {

    @Test
    @DisplayName("should parse valid map item line correctly")
    void shouldParseValidLine() {
        char[] chars = new char[130];
        java.util.Arrays.fill(chars, ' ');
        
        // Guardians:
        // 0-6: numeric (000100)
        // 7: space
        // 56: .
        // 58-60: BL
        // 61: =
        System.arraycopy("000100".toCharArray(), 0, chars, 0, 6);
        chars[7]  = ' ';
        chars[56] = '.';
        chars[58] = 'B';
        chars[59] = 'L';
        chars[61] = '=';
        
        // Data:
        // 9-57: hierarchy and name
        System.arraycopy("01 MY-DATA".toCharArray(), 0, chars, 9, 10);
        // baseLocatorId (62-67), baseLocatorShift (69-72), etc.
        System.arraycopy("12345".toCharArray(), 0, chars, 62, 5);
        System.arraycopy("00A".toCharArray(), 0, chars, 69, 3);
        System.arraycopy("DISP-NUM".toCharArray(), 0, chars, 86, 8);
        
        String line = new String(chars);

        DataDivisionMapItemDto dto = new DataDivisionMapItemDto(line);

        assertTrue(dto.isValid());
        assertEquals(100, dto.getLineId());
        assertEquals("MY-DATA", dto.getDataName());
        assertEquals("12345", dto.getBaseLocatorId());
        assertEquals(10, dto.getNumericOffset());
    }

    @Test
    @DisplayName("should not parse invalid map item line")
    void shouldNotParseInvalidLine() {
        DataDivisionMapItemDto dto = new DataDivisionMapItemDto("invalid line");
        assertFalse(dto.isValid());
    }
}
