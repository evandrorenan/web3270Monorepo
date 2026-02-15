package br.com.evandrorenan.web3270.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FieldDtoTest {

    @Test
    @DisplayName("should calculate row and col correctly from start position")
    void shouldCalculateCoordinates() {
        FieldDto f = new FieldDto("f1");
        f.setStart(80); // 80 / 80 + 1 = 2. 80 % 80 + 1 = 1.
        assertEquals(2, f.getRow());
        assertEquals(1, f.getCol());
        
        f.setStart(0); // 0 / 80 + 1 = 1. 0 % 80 + 1 = 1.
        assertEquals(1, f.getRow());
        assertEquals(1, f.getCol());
        
        f.setStart(79); // 79 / 80 + 1 = 1. 79 % 80 + 1 = 80.
        assertEquals(1, f.getRow());
        assertEquals(80, f.getCol());
    }

    @Test
    @DisplayName("should use set row and col if provided")
    void shouldPreferSetCoordinates() {
        FieldDto f = new FieldDto("f1");
        f.setRow(10);
        f.setCol(20);
        f.setStart(0);
        assertEquals(10, f.getRow());
        assertEquals(20, f.getCol());
    }
}
