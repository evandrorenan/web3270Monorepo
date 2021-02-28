package br.com.evandrorenan.web3270.session._interface;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;

@Component
public interface IScreenService {

	public ScreenDto getScreenDto(IMySession mySession) throws ExceptionWeb3270;
	public ScreenDto getScreenFields(IMySession mySession) throws ExceptionWeb3270;
	
	public void sendKeys(IMySession mySession, UserInputDto userInputDto) throws ExceptionWeb3270;		
	
	@Async
	public void sendKeysAsync(IMySession mySession, UserInputDto userInputDto) throws ExceptionWeb3270;
	
}
