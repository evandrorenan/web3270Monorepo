package br.com.evandrorenan.web3270.session._interface;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;

@Component
public interface ISessionService {

	public SessionDto createNewSessionDto(String host, String port);
	
	public SessionDto disconnect(String sessionId);
	
	public IMySession getSession(String sessionId);
	
	public SessionDto getSessionDto(IMySession mySession);

	@Async
	public void monitorConnectionStatus() throws ExceptionWeb3270;
}
