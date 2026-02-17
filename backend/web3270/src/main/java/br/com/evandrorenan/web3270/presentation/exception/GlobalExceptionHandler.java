package br.com.evandrorenan.web3270.presentation.exception;

import br.com.evandrorenan.web3270.application.service.ApplicationException;
import br.com.evandrorenan.web3270.domain.shared.exception.DomainException;
import br.com.evandrorenan.web3270.domain.session.exception.SessionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler following RFC 9457 (Problem Details for HTTP APIs).
 * Provides standardized error responses across the application.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private static final String PROBLEM_BASE_URL = "https://web3270.com/problems/";

    /**
     * Handle SessionNotFoundException - 404 Not Found
     */
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(
            SessionNotFoundException ex, 
            HttpServletRequest request) {
        
        log.warn("Session not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type(PROBLEM_BASE_URL + "session-not-found")
                .title("Session Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle DomainException - 400 Bad Request
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, 
            HttpServletRequest request) {
        
        log.warn("Domain exception: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type(PROBLEM_BASE_URL + "domain-violation")
                .title("Business Rule Violation")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle validation errors - 400 Bad Request with field details
     * Override from ResponseEntityExceptionHandler
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            org.springframework.http.HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request) {
        
        log.warn("Validation failed: {} errors", ex.getBindingResult().getErrorCount());
        
        List<ValidationErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());

        String requestUri = request.getDescription(false).replace("uri=", "");
        
        ValidationErrorResponse error = ValidationErrorResponse.builder()
                .type(PROBLEM_BASE_URL + "validation-failed")
                .title("Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(String.format("Validation failed for %d field(s)", fieldErrors.size()))
                .instance(requestUri)
                .timestamp(Instant.now())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle ApplicationException - 500 Internal Server Error
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex,
            HttpServletRequest request) {
        
        log.error("Application exception occurred", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .type(PROBLEM_BASE_URL + "application-error")
                .title("Application Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(isProduction() ? "An internal error occurred" : ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .properties(isProduction() ? null : createDebugProperties(ex))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle unexpected exceptions - 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unhandled exception occurred", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .type(PROBLEM_BASE_URL + "internal-error")
                .title("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("An unexpected error occurred")
                .instance(request.getRequestURI())
                .timestamp(Instant.now())
                .properties(isProduction() ? null : createDebugProperties(ex))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Check if running in production environment
     */
    private boolean isProduction() {
        return "prod".equalsIgnoreCase(activeProfile);
    }

    /**
     * Create debug properties for non-production environments
     */
    private Map<String, Object> createDebugProperties(Exception ex) {
        Map<String, Object> debug = new HashMap<>();
        debug.put("exception", ex.getClass().getName());
        debug.put("message", ex.getMessage());
        
        // Include stack trace only in dev/test
        if (!isProduction() && ex.getStackTrace().length > 0) {
            debug.put("stackTrace", 
                java.util.Arrays.stream(ex.getStackTrace())
                    .limit(5)  // Limit to first 5 frames
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList())
            );
        }
        
        return debug;
    }
}
