package br.com.evandrorenan.web3270.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import br.com.evandrorenan.web3270.dto.ScreenDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@RestController
/**
 * Provide access to a Terminal 3270 session, making possible to connect 
 * with Custom Connection Properties, get screen's content and send input keys. 
 * 
 * @author  evandrorenan
 */
public class ScreenController {
	
	private IScreenService screenService;
	private ISessionService sessionService;
	
	@Autowired	
	public ScreenController(IScreenService screenService, ISessionService sessionService) {
		this.screenService = screenService;
		this.sessionService = sessionService;
	}
	
    /**
     * Create a new session and send it to be subscribed in the Websocket
     *
     * @param   data     {@code SessionPropertiesDto} object.
     * @return 
     * @return  a {@code SessionDto} that contains the Session Id and Connection Status.
     * @throws ExceptionWeb3270 
     */	
	@MessageMapping("/sendkeys")
    public void getScreen(UserInputDto payload) throws ExceptionWeb3270 {
		IMySession mySession = this.sessionService.getSession(payload.getSessionId());
		this.screenService.sendKeysAsync(mySession, payload);
    }
}