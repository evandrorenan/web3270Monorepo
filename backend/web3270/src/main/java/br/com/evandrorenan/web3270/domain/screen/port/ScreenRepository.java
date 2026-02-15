package br.com.evandrorenan.web3270.domain.screen.port;

import br.com.evandrorenan.web3270.domain.screen.Screen;

public interface ScreenRepository {
    void save(String sessionId, Screen screen);
    Screen findBySessionId(String sessionId);
}
