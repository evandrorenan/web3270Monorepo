package br.com.evandrorenan.web3270.domain.session;

public record SessionProperties(
    String host,
    String port,
    String type,
    String codePage
) {}
