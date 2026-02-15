package br.com.evandrorenan.web3270.domain.session.exception;

import br.com.evandrorenan.web3270.domain.shared.exception.DomainException;

public class InvalidSessionPropertiesException extends DomainException {
    public InvalidSessionPropertiesException(String message) {
        super(message);
    }
}
