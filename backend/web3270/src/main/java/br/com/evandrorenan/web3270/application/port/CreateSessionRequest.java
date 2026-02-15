package br.com.evandrorenan.web3270.application.port;

public record CreateSessionRequest(
    String host,
    String port,
    String type,
    String codePage
) {}
