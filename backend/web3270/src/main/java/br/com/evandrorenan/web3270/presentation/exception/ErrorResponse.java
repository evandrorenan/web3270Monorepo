package br.com.evandrorenan.web3270.presentation.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.time.Instant;
import java.util.Map;

/**
 * RFC 9457 compliant error response.
 * Problem Details for HTTP APIs.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    URI type,
    String title,
    int status,
    String detail,
    URI instance,
    Instant timestamp,
    Map<String, Object> properties
) {
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
        private Map<String, Object> properties;

        public Builder type(URI type) {
            this.type = type;
            return this;
        }

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

        public Builder instance(URI instance) {
            this.instance = instance;
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

        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(type, title, status, detail, instance, timestamp, properties);
        }
    }
}
