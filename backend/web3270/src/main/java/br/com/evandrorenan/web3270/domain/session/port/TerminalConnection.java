package br.com.evandrorenan.web3270.domain.session.port;

import br.com.evandrorenan.web3270.domain.session.SessionProperties;

public interface TerminalConnection {
    void connect(SessionProperties props);
    void disconnect();
    boolean isConnected();
    String getScreen();
    void sendKeys(String keys);
}
