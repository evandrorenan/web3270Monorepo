package br.com.evandrorenan.web3270.domain.session.exception;

import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.shared.exception.DomainException;

public class SessionNotFoundException extends DomainException {
    public SessionNotFoundException(SessionId id) {
        super(String.format("Session %s not found", id.value()));
    }
}
