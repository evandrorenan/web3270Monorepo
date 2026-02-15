package br.com.evandrorenan.web3270.infrastructure.terminal;

import br.com.evandrorenan.web3270.domain.session.SessionProperties;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import br.com.evandrorenan.web3270.pcomm.MyPcommSession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import java.util.Properties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

public class PcommTerminalAdapter implements TerminalConnection {
    private final SimpMessagingTemplate messagingTemplate;
    private final IScreenService screenService;
    private MyPcommSession session;

    public PcommTerminalAdapter(SimpMessagingTemplate messagingTemplate, IScreenService screenService) {
        this.messagingTemplate = messagingTemplate;
        this.screenService = screenService;
    }

    @Override
    public void connect(SessionProperties props) {
        Properties pcommProps = new Properties();
        pcommProps.setProperty("SESSION_HOST", props.host());
        pcommProps.setProperty("SESSION_HOST_PORT", props.port());
        pcommProps.setProperty("SESSION_TYPE", props.type());
        pcommProps.setProperty("SESSION_CODE_PAGE", props.codePage());
        
        try {
            this.session = new MyPcommSession(pcommProps, messagingTemplate, screenService);
            this.session.connect();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Pcomm", e);
        }
    }

    @Override
    public void disconnect() {
        if (session != null) {
            session.disconnect();
            session.dispose();
        }
    }

    @Override
    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    @Override
    public String getScreen() {
        return session != null ? session.getTextScreen() : "";
    }

    @Override
    public void sendKeys(String keys) {
        if (session != null) {
            try {
                session.sendKey(keys);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send keys", e);
            }
        }
    }
}
