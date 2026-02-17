package br.com.evandrorenan.web3270.presentation.exception;

import br.com.evandrorenan.web3270.application.service.ApplicationException;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.exception.SessionNotFoundException;
import br.com.evandrorenan.web3270.domain.shared.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GlobalExceptionHandler following RFC 9457.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        ReflectionTestUtils.setField(handler, "activeProfile", "dev");
        
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions/123");
    }

    @Test
    void shouldHandleSessionNotFound() {
        SessionNotFoundException ex = new SessionNotFoundException(new SessionId("123"));
        
        ResponseEntity<ErrorResponse> response = handler.handleSessionNotFound(ex, request);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().title()).isEqualTo("Session Not Found");
        assertThat(response.getBody().detail()).contains("Session 123 not found");
        assertThat(response.getBody().type().toString()).contains("session-not-found");
    }

    @Test
    void shouldHandleDomainException() {
        DomainException ex = new DomainException("Invalid session properties");
        
        ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex, request);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().title()).isEqualTo("Business Rule Violation");
        assertThat(response.getBody().detail()).isEqualTo("Invalid session properties");
        assertThat(response.getBody().type().toString()).contains("domain-violation");
    }

    @Test
    void shouldHandleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("createSessionRequest", "host", "Host is required");
        FieldError fieldError2 = new FieldError("createSessionRequest", "port", "Port must be between 1 and 65535");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(bindingResult.getErrorCount()).thenReturn(2);

        org.springframework.web.context.request.WebRequest webRequest = mock(org.springframework.web.context.request.WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/sessions");
        
        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                ex, 
                new org.springframework.http.HttpHeaders(), 
                HttpStatus.BAD_REQUEST, 
                webRequest);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(ValidationErrorResponse.class);
        
        ValidationErrorResponse errorResponse = (ValidationErrorResponse) response.getBody();
        assertThat(errorResponse.status()).isEqualTo(400);
        assertThat(errorResponse.title()).isEqualTo("Validation Failed");
        assertThat(errorResponse.detail()).contains("Validation failed for 2 field(s)");
        assertThat(errorResponse.errors()).hasSize(2);
        assertThat(errorResponse.errors().get(0).field()).isEqualTo("host");
        assertThat(errorResponse.errors().get(1).field()).isEqualTo("port");
    }

    @Test
    void shouldHandleApplicationException() {
        ApplicationException ex = new ApplicationException("Database connection failed");
        
        ResponseEntity<ErrorResponse> response = handler.handleApplicationException(ex, request);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().title()).isEqualTo("Application Error");
        // In dev mode, should show actual message
        assertThat(response.getBody().detail()).isEqualTo("Database connection failed");
        assertThat(response.getBody().properties()).isNotNull(); // Debug info in dev mode
    }

    @Test
    void shouldHandleApplicationExceptionInProduction() {
        ReflectionTestUtils.setField(handler, "activeProfile", "prod");
        ApplicationException ex = new ApplicationException("Database connection failed");
        
        ResponseEntity<ErrorResponse> response = handler.handleApplicationException(ex, request);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        // In prod mode, should hide details
        assertThat(response.getBody().detail()).isEqualTo("An internal error occurred");
        assertThat(response.getBody().properties()).isNull(); // No debug info in prod
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().title()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().detail()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().properties()).isNotNull(); // Debug info in dev mode
    }

    @Test
    void shouldHideStackTraceInProduction() {
        ReflectionTestUtils.setField(handler, "activeProfile", "prod");
        Exception ex = new RuntimeException("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);
        
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().properties()).isNull(); // No stack trace in prod
    }

    @Test
    void shouldIncludeTimestampInErrorResponse() {
        SessionNotFoundException ex = new SessionNotFoundException(new SessionId("123"));
        
        ResponseEntity<ErrorResponse> response = handler.handleSessionNotFound(ex, request);
        
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldIncludeInstanceUriInErrorResponse() {
        SessionNotFoundException ex = new SessionNotFoundException(new SessionId("123"));
        
        ResponseEntity<ErrorResponse> response = handler.handleSessionNotFound(ex, request);
        
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().instance().toString()).isEqualTo("/api/v1/sessions/123");
    }
}
