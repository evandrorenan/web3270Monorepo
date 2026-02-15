package br.com.evandrorenan.web3270.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.ibm.eNetwork.ECL.ECLConnection;
import com.ibm.eNetwork.ECL.ECLErr;

import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.pcomm.MyPcommSession;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;
import lombok.Data;

@Slf4j
@Component
@Data
public class SessionService implements ISessionService {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
	
	private Boolean isMonitoring;

	private Map<String, IMySession> sessionMap;
	private SimpMessagingTemplate messageTemplate;
	private IScreenService screenService;
	private br.com.evandrorenan.web3270.session.MySessionFactory sessionFactory;
	
	@Autowired
	public SessionService(SimpMessagingTemplate messageTemplate, IScreenService screenService, br.com.evandrorenan.web3270.session.MySessionFactory sessionFactory) {
		this.messageTemplate = messageTemplate;
		this.sessionMap = new HashMap<>();
		this.screenService = screenService;
		this.sessionFactory = sessionFactory;
		logger.info("SessionService constructed;");
	}
	
	public SessionDto createNewSessionDto(String host, String port) {
		//TODO: Replace host and port as input variables by connection properties file
		IMySession mySession;
		try {
			mySession = this.newSession(host, port);
		} catch (ExceptionWeb3270 e) {
			logger.error(e.getMessage());
			logger.error(e.getLocalizedMessage());
			return null;
		}
		
		if (mySession == null ) {
			return null;
		}
		
		SessionDto sessionDto = new SessionDto();
		sessionDto.setSessionId(mySession.getSessionId());
		sessionDto.setConnected(mySession.isConnected());
//		try {
//			this.monitorConnectionStatus();
//		} catch (ExceptionWeb3270 e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return sessionDto;
	}
	
	public SessionDto disconnect(String sessionId) {
		
		if (this.sessionMap.containsKey(sessionId)) {
			this.sessionMap.get(sessionId).dispose();
			this.sessionMap.remove(sessionId);
		} 
		SessionDto discSession = new SessionDto();
		discSession.setSessionId(sessionId);
		discSession.setConnected(false);
		return discSession;
	}
	
	public IMySession getSession(String sessionId) {
		if (sessionMap.containsKey(sessionId)) {
			return sessionMap.get(sessionId);
		} 
		return null;
	}

	public SessionDto getSessionDto(IMySession mySession) {
		if (mySession == null ) {
			return null;
		}
		SessionDto sessionDto = new SessionDto();
		sessionDto.setConnected(mySession.isConnected());
		sessionDto.setSessionId(mySession.getSessionId());
		return sessionDto;
	}
	
	private IMySession newSession(String host, String port) throws ExceptionWeb3270 {
		return this.newSession(host, port, true);
	}

	private IMySession newSession(String host, String port, boolean mapSession) throws ExceptionWeb3270 {		
		IMySession pcomm = this.sessionFactory.getNewSession(host, port);
		
		if (mapSession) {
			this.sessionMap.put(pcomm.getSessionId(), pcomm); 
		}
		
		return pcomm;
	}	
	
	/**
	 * Monitor pcomm connection status and disconnect when 
	 * it loses connection.
	 * 
	 * @throws ExceptionWeb3270
	 */
	@Async
	public void monitorConnectionStatus() throws ExceptionWeb3270 {
		
		if (Boolean.TRUE.equals(isMonitoring)) {
			return;
		}
		
		long sleepTime = 5000L;
		IMySession tempSession = null;
		
		while (true) {
			Map<String, IMySession> localSessionMap = new HashMap<>(this.sessionMap);

			this.isMonitoring = true;
			
			for (Entry<String, IMySession> entry : localSessionMap.entrySet()) {				
				
				tempSession = this.newSession(entry.getValue().getHost(),
								entry.getValue().getHostPort(),
								false);

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
				sleepTime = 5000L;
			} catch (InterruptedException e) {
				logger.error("InterruptedException occurred when monitoring session connection Status: %s", 
						e.getMessage());
			}
			
			if (this.sessionMap.isEmpty() ) {
				break;
			}
			break; //test
		}
		this.isMonitoring = false;
		log.info("end async Call");
	}
}