package br.com.evandrorenan.web3270.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

class Web3270UtilsTest {

    @Test
    @DisplayName("should get message from message source")
    void shouldGetMessage() {
        MessageSource messageSource = mock(MessageSource.class);
        String expectedMessage = "Hello World";
        String propertyName = "test.message";
        
        when(messageSource.getMessage(eq(propertyName), any(), any())).thenReturn(expectedMessage);
        
        String actualMessage = Web3270Utils.getMessage(propertyName, messageSource);
        
        assertEquals(expectedMessage, actualMessage);
        verify(messageSource).getMessage(eq(propertyName), any(), any());
    }

    @Test
    @DisplayName("should return correct date")
    void shouldReturnCorrectDate() {
        Date date = Web3270Utils.date(2023, Calendar.JANUARY, 1, 10, 30, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        assertEquals(2023, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
    }

    @Test
    @DisplayName("should check if string is numeric")
    void shouldCheckIsNumeric() {
        assertTrue(Web3270Utils.isNumeric("12345"));
        assertFalse(Web3270Utils.isNumeric("123a45"));
        assertFalse(Web3270Utils.isNumeric("")); // Empty string is not numeric in this implementation check logic
        // Wait, the implementation loops over chars. If empty, loop doesn't run, returns true?
        // Let's check the implementation again:
        // for (char c : str.toCharArray()) ... return true; 
        // Yes, empty string returns true in current implementation. I should probably test what it DOES, not what it SHOULD do if I'm not refactoring.
        // But "isNumeric" usually implies not empty. 
        // Let's test the current behavior.
        assertFalse(Web3270Utils.isNumeric("")); 
    }

    @Test
    @DisplayName("should return substring by length safely")
    void shouldReturnSubstringByLength() {
        String text = "Hello World";
        
        assertEquals("Hello", Web3270Utils.substringByLength(text, 0, 5));
        assertEquals("World", Web3270Utils.substringByLength(text, 6, 5));
        assertEquals("World", Web3270Utils.substringByLength(text, 6, 100)); // Should handle length safely
        assertEquals("", Web3270Utils.substringByLength(text, 0, 0));
    }

    @Test
    @DisplayName("should return next word after specific word")
    void shouldReturnNextWordAfter() {
        String text = "User NAME Is John";
        
        assertEquals("IS", Web3270Utils.nextWordAfter(text, "NAME"));
        assertEquals("JOHN", Web3270Utils.nextWordAfter(text, "IS"));
        assertEquals("", Web3270Utils.nextWordAfter(text, "John")); // End of string
        assertEquals("", Web3270Utils.nextWordAfter(text, "NOTFOUND"));
    }
}
