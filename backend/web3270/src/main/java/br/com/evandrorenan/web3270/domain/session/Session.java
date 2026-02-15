package br.com.evandrorenan.web3270.domain.session;

import java.time.LocalDateTime;

public class Session {
    private final SessionId id;
    private final SessionProperties properties;
    private SessionStatus status;
    private final LocalDateTime createdAt;

    public Session(SessionId id, SessionProperties properties) {
        this.id = id;
        this.properties = properties;
        this.status = SessionStatus.DISCONNECTED;
        this.createdAt = LocalDateTime.now();
    }

    public void connect() {
        this.status = SessionStatus.CONNECTED;
    }

    public void disconnect() {
        this.status = SessionStatus.DISCONNECTED;
    }

    public boolean isConnected() {
        return this.status == SessionStatus.CONNECTED;
    }

    public SessionId getId() {
        return id;
    }

    public SessionProperties getProperties() {
        return properties;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
