package br.com.evandrorenan.web3270.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
import br.com.evandrorenan.web3270.session._interface.ISessionService;
import lombok.Data;

@Component
@Data
public class SessionService implements ISessionService {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
	
	private Boolean isMonitoring;

	private Map<String, IMySession> sessionMap;
	private SimpMessagingTemplate messageTemplate;

	
	@Autowired
	public SessionService(SimpMessagingTemplate messageTemplate) {
		this.messageTemplate = messageTemplate;
		this.sessionMap = new HashMap<>();
		System.out.println("SessionService constructed;");
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
		Properties props = new Properties();
		props.setProperty("SESSION_HOST"		, host);
		props.setProperty("SESSION_HOST_PORT"	, port);
		props.setProperty("SESSION_TYPE"		, "1");
		props.setProperty("codePage"			, "037");
		props.setProperty("3D"					, "false");
		props.setProperty("SESSION_WIN_STATE"	, "false");
		props.setProperty("AutoConnect"			, "N");

		try {
			MyPcommSession pcomm = new MyPcommSession(props, messageTemplate);
			TimeLimiter limiter = new SimpleTimeLimiter();
			IMySession proxyPcomm = limiter.newProxy(
					pcomm, IMySession.class, 1000, TimeUnit.MILLISECONDS);

			proxyPcomm.connect();
			
			Thread.sleep(2000);
			if (pcomm.GetCommStatus() != ECLConnection.CONNECTION_READY
			&&  pcomm.GetCommStatus() != ECLConnection.CONNECTION_ACTIVE) {
				return null;
			}
			
			if (mapSession) {
				this.sessionMap.put(pcomm.getSessionId(), pcomm); 
			}
			
			return pcomm;

		} catch (UncheckedTimeoutException e) {
			System.out.println("Connection timeout");
			return null;
		} catch (ECLErr e) {
			throw new ExceptionWeb3270(
				"Error creating new Session.",
					props.getProperty("SESSION_HOST"		) + ", " +
					props.getProperty("SESSION_HOST_PORT"	) + ", " +
					props.getProperty("SESSION_TYPE"		) + ", " +
					props.getProperty("codePage"			) + ", " +
					props.getProperty("3D"					) + ", " +
					props.getProperty("SESSION_WIN_STATE"	) + ", " , 
				e);			
		} catch (InterruptedException e) {
			throw new ExceptionWeb3270(
				"Thread Sleep error when creating new Session.",
					props.getProperty("SESSION_HOST"		) + ", " +
					props.getProperty("SESSION_HOST_PORT"	) + ", " +
					props.getProperty("SESSION_TYPE"		) + ", " +
					props.getProperty("codePage"			) + ", " +
					props.getProperty("3D"					) + ", " +
					props.getProperty("SESSION_WIN_STATE"	) + ", " , 
				e);		
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
		System.out.println("end async Call");
	}
}