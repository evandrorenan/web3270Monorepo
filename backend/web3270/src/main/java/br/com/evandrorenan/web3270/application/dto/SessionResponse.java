package br.com.evandrorenan.web3270.application.dto;

import br.com.evandrorenan.web3270.domain.session.Session;
import java.time.LocalDateTime;

public record SessionResponse(
    String sessionId,
    boolean connected,
    LocalDateTime createdAt
) {
    public static SessionResponse from(Session session) {
        return new SessionResponse(
            session.getId().value(),
            session.isConnected(),
            session.getCreatedAt()
        );
    }
}
