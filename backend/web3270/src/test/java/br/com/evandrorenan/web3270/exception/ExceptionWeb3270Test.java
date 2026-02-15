package br.com.evandrorenan.web3270.exception;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionWeb3270Test {

    @Test
    @DisplayName("should construct with null exception")
    void shouldConstructWithNullException() {
        assertDoesNotThrow(() -> new ExceptionWeb3270("message", "details", null));
    }

    @Test
    @DisplayName("should construct with valid exception and log stack trace")
    void shouldConstructWithValidException() {
        Exception cause = new RuntimeException("Original error");
        assertDoesNotThrow(() -> new ExceptionWeb3270("message", "details", cause));
    }

    @Test
    @DisplayName("should implement toString correctly")
    void shouldImplementToString() {
        ExceptionWeb3270 exception = new ExceptionWeb3270("Error occurred", "Invalid input", null);
        
        String toString = exception.toString();
        
        assertTrue(toString.contains("Message: Error occurred"));
        assertTrue(toString.contains("details: Invalid input"));
        assertTrue(toString.contains("timestamp:"));
    }
}
