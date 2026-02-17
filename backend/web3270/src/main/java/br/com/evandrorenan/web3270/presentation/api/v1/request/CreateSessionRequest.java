package br.com.evandrorenan.web3270.presentation.api.v1.request;

import br.com.evandrorenan.web3270.presentation.validation.ValidIPAddress;
import br.com.evandrorenan.web3270.presentation.validation.ValidPort;
import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(
    @NotBlank(message = "Host is required")
    @ValidIPAddress(message = "Invalid IP address")
    String host,

    @NotBlank(message = "Port is required")
    @ValidPort(message = "Port must be between 1 and 65535")
    String port,

    String type,
    String codePage
) {}
