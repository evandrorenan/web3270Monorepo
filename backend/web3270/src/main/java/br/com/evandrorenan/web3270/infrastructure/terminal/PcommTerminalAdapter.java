package br.com.evandrorenan.web3270.infrastructure.terminal;

import br.com.evandrorenan.web3270.domain.session.SessionProperties;
import br.com.evandrorenan.web3270.domain.session.port.TerminalConnection;
import br.com.evandrorenan.web3270.pcomm.MyPcommSession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
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
        log.info("Pcomm adapter connecting to {}:{}", props.host(), props.port());
        Properties pcommProps = new Properties();
        pcommProps.setProperty("SESSION_HOST", props.host());
        pcommProps.setProperty("SESSION_HOST_PORT", props.port());
        pcommProps.setProperty("SESSION_TYPE", props.type());
        pcommProps.setProperty("SESSION_CODE_PAGE", props.codePage());
        
        try {
            this.session = new MyPcommSession(pcommProps, messagingTemplate, screenService);
            this.session.connect();
            log.info("Pcomm session connected successfully");
        } catch (Exception e) {
            log.error("Pcomm connection failed for {}:{}", props.host(), props.port(), e);
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
    public void sendKey(String key) {
        if (session != null) {
            try {
                log.debug("Pcomm adapter sending key: {}", key);
                session.sendKey(key);
            } catch (Exception e) {
                log.error("Failed to send key to Pcomm: {}", e.getMessage(), e);
            }
        } else {
            log.warn("Attempted to send key to a null Pcomm session");
        }
    }

    @Override
    public void setCurrentSessionId(String sessionId) {
        // No-op for production adapter
    }
}
