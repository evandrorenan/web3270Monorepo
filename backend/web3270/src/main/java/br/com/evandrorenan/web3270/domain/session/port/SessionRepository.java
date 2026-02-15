package br.com.evandrorenan.web3270.domain.session.port;

import br.com.evandrorenan.web3270.domain.session.Session;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    void save(Session session);
    Optional<Session> findById(SessionId id);
    void delete(SessionId id);
    List<Session> findAll();
}
