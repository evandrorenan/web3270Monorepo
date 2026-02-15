package br.com.evandrorenan.web3270.presentation.api.v1.response;

import java.time.LocalDateTime;

public record SessionResponse(
    String sessionId,
    boolean connected,
    LocalDateTime createdAt
) {}
