package br.com.evandrorenan.web3270.infrastructure.persistence;

import br.com.evandrorenan.web3270.domain.session.Session;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

public class InMemorySessionRepository implements SessionRepository {
    private final Map<SessionId, Session> store = new ConcurrentHashMap<>();

    @Override
    public void save(Session session) {
        store.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findById(SessionId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void delete(SessionId id) {
        store.remove(id);
    }

    @Override
    public List<Session> findAll() {
        return new ArrayList<>(store.values());
    }
}
