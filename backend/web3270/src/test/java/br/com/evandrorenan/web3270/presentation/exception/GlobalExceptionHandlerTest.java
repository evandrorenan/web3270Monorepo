package br.com.evandrorenan.web3270.presentation.exception;

import br.com.evandrorenan.web3270.domain.session.exception.SessionNotFoundException;
import br.com.evandrorenan.web3270.domain.shared.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleSessionNotFound() {
        SessionNotFoundException ex = new SessionNotFoundException(new br.com.evandrorenan.web3270.domain.session.SessionId("123"));
        ResponseEntity<String> response = handler.handleNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Session 123 not found");
    }

    @Test
    void shouldHandleDomainException() {
        DomainException ex = new DomainException("Domain error") {};
        ResponseEntity<String> response = handler.handleDomain(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Domain error");
    }

    @Test
    void shouldHandleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "field", "message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<String> response = handler.handleValidation(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation failed", "field: message");
    }

    @Test
    void shouldHandleGeneralException() {
        Exception ex = new Exception("General error");
        ResponseEntity<String> response = handler.handleGeneral(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("An unexpected error occurred");
    }
}
