package br.com.evandrorenan.web3270.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ibm.eNetwork.ECL.ECLErr;

import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.pcomm.PcommSession;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import lombok.Data;

@PropertySource("classpath:pcommConnect.properties")
@Component
@Data
public class MySessionFactory {

	private static final Logger logger = LoggerFactory.getLogger(MySessionFactory.class);
	
	private Boolean isMonitoring;

	private SimpMessagingTemplate messageTemplate;
	private IScreenService screenService;
	
	@Value("${SESSION_TYPE}")
	private String sessionType;
	
	@Value("${codePage}")
	private String codePage;
	
	@Value("${3D}")
	private String screen3D;
	
	@Value("${SESSION_TYPE}")
	private String sessionWinState;
	
	private Map<String, IMySession> sessionMap;

	public enum Emulator {
		PCOMM;
	}
	
	@Autowired
	public MySessionFactory(SimpMessagingTemplate messageTemplate, IScreenService screenService) {
		this.screenService = screenService;
		this.sessionMap = new HashMap<>();
	}
	
	public IMySession getNewSession(String host, String port) throws ExceptionWeb3270 {
		return this.getNewSession(host, port, Emulator.PCOMM);
	}

	public IMySession getNewSession(String host, String port, Emulator emulator) throws ExceptionWeb3270 {
		switch (emulator) {
				
			case PCOMM :
				try {
					Properties properties = new Properties();

					properties.setProperty("SESSION_HOST", host);
					properties.setProperty("SESSION_HOST_PORT", port);
					properties.setProperty("SESSION_TYPE", this.sessionType);
					properties.setProperty("codePage", this.codePage);
					properties.setProperty("3D", this.screen3D);
					
					PcommSession pcomm = new PcommSession(messageTemplate, screenService);
					pcomm.StartCommunication();
					pcomm.connect();
					pcomm.GetOIA().WaitForInput();
					this.sessionMap.put(pcomm.getSessionId(), pcomm); 
//					this.monitorConnectionStatus();
				
				    return pcomm;
				} catch (ECLErr e) {
					System.out.println(e.getMessage() + "," + e.getLocalizedMessage());
					throw new ExceptionWeb3270("New Session creation failed.", "New Session creation failed.", e);
				}
			default :
				throw new ExceptionWeb3270("Emulator not available yet.", "Emulator not available yet.", null);
		}
	}
	
	/**
	 * Monitor pcomm connection status and disconnect when 
	 * it loses connection.
	 * 
	 * @throws ExceptionWeb3270
	 */
	@Async
	public void monitorConnectionStatus() throws ExceptionWeb3270 {
		
		System.out.println("start async method");
		if (Boolean.TRUE.equals(isMonitoring)) {
			return;
		}
		
		long sleepTime = 500L;
		IMySession tempSession = null;
		
		while (true) {
			Map<String, IMySession> localSessionMap = new HashMap<>(this.sessionMap);

			this.isMonitoring = true;
			
			for (Entry<String, IMySession> entry : localSessionMap.entrySet()) {				
				
				tempSession = this.getNewSession(
						entry.getValue().getHost(),
						entry.getValue().getHostPort(),
						Emulator.PCOMM);

				if (tempSession == null) {
					tempSession = entry.getValue();
			    	break;
				} else {
					tempSession.dispose();
				}
			}

			if (tempSession != null) {
				tempSession.dispose();
				this.sessionMap.remove(tempSession.getSessionId());
			}

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.error("InterruptedException occurred when monitoring session connection Status: %s", 
						e.getMessage());
			}
			
			if (this.sessionMap.isEmpty() ) {
				break;
			}
		}
		this.isMonitoring = false;
		System.out.println("end async method");
	}
}