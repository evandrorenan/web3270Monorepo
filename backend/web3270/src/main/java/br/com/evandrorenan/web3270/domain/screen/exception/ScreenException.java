package br.com.evandrorenan.web3270.domain.screen.exception;

import br.com.evandrorenan.web3270.domain.shared.exception.DomainException;

public class ScreenException extends DomainException {
    public ScreenException(String message) {
        super(message);
    }

    public ScreenException(String message, Throwable cause) {
        super(message, cause);
    }
}
