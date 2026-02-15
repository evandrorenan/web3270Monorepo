package br.com.evandrorenan.web3270.infrastructure.persistence;

import br.com.evandrorenan.web3270.domain.session.Session;
import br.com.evandrorenan.web3270.domain.session.SessionId;
import br.com.evandrorenan.web3270.domain.session.port.SessionRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

public class CachedSessionRepository implements SessionRepository {
    private final Cache<SessionId, Session> cache;

    public CachedSessionRepository() {
        this.cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    }

    @Override
    public void save(Session session) {
        cache.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findById(SessionId id) {
        return Optional.ofNullable(cache.getIfPresent(id));
    }

    @Override
    public void delete(SessionId id) {
        cache.invalidate(id);
    }

    @Override
    public List<Session> findAll() {
        return new ArrayList<>(cache.asMap().values());
    }
}
