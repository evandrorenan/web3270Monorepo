package br.com.evandrorenan.web3270.session._interface;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.FieldDto;
import br.com.evandrorenan.web3270.dto.PositionDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;

@Component
public interface IMySession {
	
	public int getCursorPosition();

	public List<Integer> getFieldsIniPosition() throws ExceptionWeb3270;

	public List<PositionDto> getPositions() throws ExceptionWeb3270;

	public boolean isConnected();
	
	public void connect();
	
	public void disconnect();
	
	public void dispose();
	
	public void sendKey(String key) throws ExceptionWeb3270;
	
	public void sendKeys(String text, int row, int col) throws ExceptionWeb3270;
	
	public String getSessionId();
	
	public void setSessionId(String sessionId);

	public List<FieldDto> getFields() throws ExceptionWeb3270;
	
	public String getTextScreen();

	public String getTextScreen(int row, int col, int length);

	public void setText(int row, int col, String text) throws ExceptionWeb3270;
	
	public static List<String> getTextScreenAsArray(IMySession mySession) {		
		
		String textScreen = mySession.getTextScreen();		
		List<String> arrStrScreen = new ArrayList<>();
		
		for (int i = 0; i < 24; i++) {
			arrStrScreen.add(textScreen.substring(i * 80, i * 80 + 79));
		}
		
		return arrStrScreen;
	}

	public String getHost();

	public String getHostPort();
}