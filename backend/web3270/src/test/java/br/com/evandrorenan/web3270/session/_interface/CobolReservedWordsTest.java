package br.com.evandrorenan.web3270.session._interface;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CobolReservedWordsTest {

    @Test
    @DisplayName("should identify reserved words correctly")
    void shouldIdentifyReservedWords() {
        assertTrue(COBOL_RESERVED_WORDS.isReservedWord("ACCEPT"));
        assertTrue(COBOL_RESERVED_WORDS.isReservedWord("MOVE"));
        assertFalse(COBOL_RESERVED_WORDS.isReservedWord("MY_VARIABLE"));
        
        assertEquals("ACCEPT", COBOL_RESERVED_WORDS.ACCEPT.getReservedWord());
        assertTrue(COBOL_RESERVED_WORDS.isNotReservedWord("NOT_A_WORD"));
    }
}
