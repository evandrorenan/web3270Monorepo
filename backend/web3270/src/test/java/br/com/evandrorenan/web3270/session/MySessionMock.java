package br.com.evandrorenan.web3270.session;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.FieldDto;
import br.com.evandrorenan.web3270.dto.PositionDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;

public class MySessionMock implements IMySession {

	private String sessionId;
	
	public MySessionMock(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public int getCursorPosition() {
		return 1;
	}

	@Override
	public List<Integer> getFieldsIniPosition() throws ExceptionWeb3270 {
		List<Integer> fieldsIniPosition = new ArrayList<>();
		for (int i = 0; i < 1920; i = i + 80) {
			fieldsIniPosition.add(i);
		}
		return fieldsIniPosition;
	}

	@Override
	public List<PositionDto> getPositions() throws ExceptionWeb3270 {
		StringBuilder stringBuilder = new StringBuilder();		
		stringBuilder.append(" +-----------------------------------------------------------------------------+");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" | Mocked session for unit test purposes                                       |");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" +-----------------------------------------------------------------------------+");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" |   __    __     ______     ______     __  __     ______     _____            |");
		stringBuilder.append(" |  /\\ \"-./  \\   /\\  __ \\   /\\  ___\\   /\\ \\/ /    /\\  ___\\   /\\  __-.          |");
		stringBuilder.append(" |  \\ \\ \\-./\\ \\  \\ \\ \\/\\ \\  \\ \\ \\____  \\ \\  _\"-.  \\ \\  __\\   \\ \\ \\/\\ \\         |");
		stringBuilder.append(" |   \\ \\_\\ \\ \\_\\  \\ \\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\____-         |");
		stringBuilder.append(" |    \\/_/  \\/_/   \\/_____/   \\/_____/   \\/_/\\/_/   \\/_____/   \\/____/         |");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" |   ______     ______     ______     ______     __     ______     __   __     |");
		stringBuilder.append(" |  /\\  ___\\   /\\  ___\\   /\\  ___\\   /\\  ___\\   /\\ \\   /\\  __ \\   /\\ \"-.\\ \\    |");
		stringBuilder.append(" |  \\ \\___  \\  \\ \\  __\\   \\ \\___  \\  \\ \\___  \\  \\ \\ \\  \\ \\ \\/\\ \\  \\ \\ \\-.  \\   |");
		stringBuilder.append(" |   \\/\\_____\\  \\ \\_____\\  \\/\\_____\\  \\/\\_____\\  \\ \\_\\  \\ \\_____\\  \\ \\_\\\\\"\\_\\  |");
		stringBuilder.append(" |    \\/_____/   \\/_____/   \\/_____/   \\/_____/   \\/_/   \\/_____/   \\/_/ \\/_/  |");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" +-----------------------------------------------------------------------------+");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" | Mocked session for unit test purposes                                       |");
		stringBuilder.append(" |                                                                             |");
		stringBuilder.append(" +-----------------------------------------------------------------------------+");
		
		List<PositionDto> positions = new ArrayList<>();
		for (int i = 0; i < stringBuilder.length(); i++) {
			PositionDto position = new PositionDto(	i, 
													stringBuilder.charAt(i), 
													false, 
													false, 
													true);
			positions.add(position);
		}
		return positions;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void sendKey(String key) throws ExceptionWeb3270 {
	}

	@Override
	public void sendKeys(String text, int row, int col) throws ExceptionWeb3270 {
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public List<FieldDto> getFields() throws ExceptionWeb3270 {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(int row, int col, String text) throws ExceptionWeb3270 {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTextScreen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHostPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTextScreen(int row, int col, int length) {
		// TODO Auto-generated method stub
		return null;
	}

}
