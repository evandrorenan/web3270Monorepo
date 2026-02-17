package br.com.evandrorenan.web3270.presentation.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * RFC 9457 compliant validation error response.
 * Extends ErrorResponse with field-level validation details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationErrorResponse(
    URI type,
    String title,
    int status,
    String detail,
    URI instance,
    Instant timestamp,
    List<FieldError> errors
) {
    public record FieldError(
        String field,
        String message,
        Object rejectedValue
    ) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URI type;
        private String title;
        private int status;
        private String detail;
        private URI instance;
        private Instant timestamp = Instant.now();
        private List<FieldError> errors;

        public Builder type(String type) {
            this.type = URI.create(type);
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = URI.create(instance);
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder errors(List<FieldError> errors) {
            this.errors = errors;
            return this;
        }

        public ValidationErrorResponse build() {
            return new ValidationErrorResponse(type, title, status, detail, instance, timestamp, errors);
        }
    }
}
