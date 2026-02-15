package br.com.evandrorenan.web3270.domain.session;

import br.com.evandrorenan.web3270.domain.shared.value.Identifier;
import java.util.UUID;

public record SessionId(String value) implements Identifier<String> {
    public static SessionId random() {
        return new SessionId(UUID.randomUUID().toString());
    }
}
