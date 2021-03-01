package br.com.evandrorenan.web3270.pcomm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.ibm.eNetwork.ECL.ECLErr;
import com.ibm.eNetwork.ECL.ECLField;
import com.ibm.eNetwork.ECL.ECLSession;
import com.ibm.eNetwork.ECL.event.ECLPSListener;

import br.com.evandrorenan.web3270.dto.FieldDto;
import br.com.evandrorenan.web3270.dto.PositionDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.IScreenService;
import lombok.ToString;

@Component
@ToString(callSuper = true)
public class MyPcommSession extends ECLSession implements IMySession {
	
	private static final String ERROR_ON_WAIT_SCREEN_UPDATE = "Error on waitScreenUpdate.";

	private static final String ERROR_ON_GET_POSITIONS_METHOD = "Error on getPositions method.";

	private static final String ECLERR_WHEN_TRYING_TO_GET_SCREEND_FIELDS_INITIAL_POSITION = "ECLErr when trying to get screend fields initial position.";

	private static final Logger logger = LoggerFactory.getLogger(MyPcommSession.class);
	
	private String sessionId;

	public MyPcommSession(Properties props, SimpMessagingTemplate messageTemplate, IScreenService screenService) throws ECLErr {
		super(props);
		
		this.sessionId = new Timestamp(System.currentTimeMillis()).toString()
				.replace(" ", "")
				.replace("-", "")
				.replace(".", "")
				.replace(":", "");
		
		this.autoReconnect = false;
		
		ECLPSListener pcommEventListener = new PcommEventListener(this, messageTemplate, screenService);
		this.GetPS().RegisterPSEvent(pcommEventListener);
		
	}
	
	@Override 
	public int getCursorPosition() {
		return this.GetPS().GetCursorPos();
	}

	@Override
	public List<Integer> getFieldsIniPosition() throws ExceptionWeb3270 {
		
		List<Integer> fieldsPositions = new ArrayList<>();
		
		try {
			for (int i = 0; i < this.GetPS().GetFieldList().size(); i++) {
				ECLField field = (ECLField) this.GetPS().GetFieldList().get(i);
				fieldsPositions.add(field.GetStart() - 1);
			}		
		} catch (ECLErr e) {
			throw new ExceptionWeb3270(
				ECLERR_WHEN_TRYING_TO_GET_SCREEND_FIELDS_INITIAL_POSITION, 
				e.GetMsgNumber() + " - " + e.GetMsgText(),
				e);
		}
		return fieldsPositions;
	}
	
	@Override
	public List<PositionDto> getPositions() throws ExceptionWeb3270 {
		List<PositionDto> positions = new ArrayList<>();
		
		int posId = 0;
		for (char posText : this.GetPS().getString().toCharArray() ) {
			PositionDto pos = new PositionDto();
			pos.setText(posText);
			
			pos.setPositionId(posId);
			pos.setProtected(false);
			pos.setHidden(false);
			pos.setHighLight(false);			
			positions.add(pos);
			posId++;
		}
		
		try {
			for (int i = 0; i < this.GetPS().GetFieldList().size(); i++) {
				ECLField field = (ECLField) this.GetPS().GetFieldList().get(i);
				// reserved byte for attribute. Should be protected on front-end
				if (field.GetStart() < 2) {
					positions.get(0).setProtected(true);
					positions.get(0).setHidden(true);
					positions.get(0).setHighLight(false);
				} else {
					positions.get(field.GetStart()  - 2).setProtected(true);
					positions.get(field.GetStart()  - 2).setHidden(true);
					positions.get(field.GetStart()  - 2).setHighLight(false);
				}
				
				for (int j = field.GetStart() - 1; j <= field.GetEnd() - 1; j++) {
					positions.get(j).setProtected	(field.isProtected());
					positions.get(j).setHidden		(field.isHidden());
					positions.get(j).setHighLight	(field.isHighIntensity());
				}
			}
		} catch (ECLErr e) {
			throw new ExceptionWeb3270(
					ERROR_ON_GET_POSITIONS_METHOD, 
					e.GetMsgNumber() + " - " + e.GetMsgText(),
					e);
		}
		
		return positions;
	}
	
	private void waitScreenUpdate() throws ExceptionWeb3270 {
		for (int i = 0;i < 1000; i+= 50) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new ExceptionWeb3270(
						ERROR_ON_WAIT_SCREEN_UPDATE, 
						"Received key: " + e.getMessage(),
						e);
			}
		}
	}
	
	@Override
	public boolean isConnected() {
		return ! this.isDisconnected();
	}

	@Override
	public void sendKey(String key) throws ExceptionWeb3270 {	
		try {
			this.GetPS().SendKeys(key);
			this.waitScreenUpdate();
		} catch (ECLErr e) {
			logger.error("Error on sendKey method. Received key: %s", key);
		}
	}
	
	private void customWaitScreenUpdate(String oldScreen) {
		printScreenOnConsole();
		try {
			this.waitScreenUpdate();
			for (int i = 7000 ; i > 10; i -= 50) {
				String newScreen = this.getTextScreen();
				if (!oldScreen.equals(newScreen)) {
//					System.out.println(i + ", ");
					break;
				}
				System.out.print(i + ", ");
				Thread.sleep(50);
			}
			
		} catch (ExceptionWeb3270 | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printScreenOnConsole();
	}
	
	@Override
	public void sendKeys(String text, int row, int col) throws ExceptionWeb3270 {	
		System.out.println("Send keys: " + row + ", " + col + ", " + text);
		try {
			
			this.GetPS().SendKeys(text, row, col);
			for (String mnemonic : this.GetPS().GetSendKeyMnemonics()) {
				if (text.contains(mnemonic)) {
					this.customWaitScreenUpdate(this.getTextScreen());
					return;
				}
			}			
		} catch (ECLErr e) {
			logger.error("Error on sendKey method. Received key: %d, %d, %s", row, col, text);
		}
	}
	
	@Override
	public String getSessionId() {
		return this.sessionId;
	}
	
	@Override
	public String getHost() {
		return this.getProperties().getProperty("SESSION_HOST");
	}
	
	@Override
	public String getHostPort() {
		return this.getProperties().getProperty("SESSION_HOST_PORT");
	}
	
	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Override
	public List<FieldDto> getFields() throws ExceptionWeb3270 {
		List<FieldDto> fields = new ArrayList<>();
		int lastPos = 1;
		
		try {
			String baseFieldName = new Timestamp(System.currentTimeMillis()).toString()
					.replace(" ", "")
					.replace("-", "")
					.replace(".", "")
					.replace(":", "");			
			
			for (int i = 0; i < this.GetPS().GetFieldList().size(); i++) {
				FieldDto field = new FieldDto(
						baseFieldName + '_' + 
						String.valueOf(((ECLField) this.GetPS().GetFieldList().get(i)).GetStart()));
				field.setStart        (((ECLField) this.GetPS().GetFieldList().get(i)).GetStart());
				field.setEnd          (((ECLField) this.GetPS().GetFieldList().get(i)).GetEnd());
				field.setProtected    (((ECLField) this.GetPS().GetFieldList().get(i)).isProtected());
				field.setHidden       (((ECLField) this.GetPS().GetFieldList().get(i)).isHidden());
				field.setHighIntensity(((ECLField) this.GetPS().GetFieldList().get(i)).isHighIntensity());
				field.setText         (this.GetPS().getString().substring(field.getStart() - 1, field.getEnd()));
				field.setColor		  (this.getColor((this.GetPS().ColorPlane[field.getStart() - 1])));
				field.setUnderline    (false);
				
				fields.addAll(breakField(field, lastPos));
				lastPos = (((ECLField) this.GetPS().GetFieldList().get(i)).GetEnd()) + 1;
			}		
		} catch (ECLErr e) {
			throw new ExceptionWeb3270(
				ECLERR_WHEN_TRYING_TO_GET_SCREEND_FIELDS_INITIAL_POSITION, 
				e.GetMsgNumber() + " - " + e.GetMsgText(),
				e);
		}
		fields.sort(Comparator.comparing(FieldDto::getStart));
		return fields;
	}
	
	private String getColor(char binaryValue) {
		switch (binaryValue) {
		case 0x1:
			return "Blue";
		case 0x2:
			return "Green";
		case 0x3:
			return "Cyan";
		case 0x4:
			return "Red";
		case 0x5:
			return "Magenta";
		case 0x6:
			return "Brown"; 
		case 0x7:
			return "White";
		case 0x8:
			return "Gray";
		case 0x9:
			return "Light-blue";
		case 0xA:
			return "Light-green";
		case 0xB:
			return "Light-cyan";
		case 0xC:
			return "Light-red";
		case 0xD:
			return "Light-magenta";
		case 0xE:
			return "Yellow";
		case 0xF:
			return "White-HI";
		default:
			return "Blank";
		}
	}
	
	@Override
	public void setText(int row, int col, String text) throws ExceptionWeb3270 {
		try {
			this.GetPS().SetText(text, row, col);
		} catch (ECLErr e) {
			throw new ExceptionWeb3270(
				ECLERR_WHEN_TRYING_TO_GET_SCREEND_FIELDS_INITIAL_POSITION, 
				e.GetMsgNumber() + " - " + e.GetMsgText(),
				e);
		}
	}
	
	private List<FieldDto> breakField(FieldDto field, int lastPos) {
		List<FieldDto> returnList = new ArrayList<>();

		if (lastPos < field.getStart() ) {
			FieldDto newHiddenField = new FieldDto(field.getFieldId() + "1");
			newHiddenField.setStart(lastPos);
			newHiddenField.setEnd(field.getStart() - 1);
			newHiddenField.setText("");
			newHiddenField.setProtected(true);
			newHiddenField.setHidden(true);
			newHiddenField.setHighIntensity(false);
			newHiddenField.setColor("Blank");
			newHiddenField.setUnderline(false);

			lastPos = newHiddenField.getStart();
			returnList.addAll(breakField(newHiddenField, lastPos));
		}
		if ((int) Math.ceil(field.getStart() / 80.0) != (int) Math.ceil(field.getEnd() / 80.0)) {
			FieldDto newField = new FieldDto(field.getFieldId() + "2");
			newField.setStart		 (((int) Math.ceil(field.getStart() / 80.0)) * 80 + 1);
			newField.setEnd			 (field.getEnd());
			newField.setProtected	 (field.isProtected());
			newField.setHidden		 (field.isHidden());
			newField.setHighIntensity(field.isHidden());
			newField.setColor		 (field.getColor());
			newField.setUnderline	 (field.isUnderline());

			field.setEnd			 (newField.getStart() - 1);			
			field.setText    		 (this.GetPS().getString().substring(field.getStart()    - 1, field.getEnd()));
			
			newField.setText		 (this.GetPS().getString().substring(newField.getStart() - 1, newField.getEnd()));
			lastPos = newField.getStart();
			returnList.addAll(breakField(newField, lastPos));
		}
		returnList.add(field);
		return returnList;
	}
	
	private void printScreenOnConsole() {
		
		StringBuilder printScreen = new StringBuilder();
		for (int i = 0; i < 24; i++) {
			printScreen.append(this.GetPS().getString().substring(i * 80, i * 80 + 79) + "\n");
		}
		
		System.out.println(printScreen.toString());		
	}

	@Override
	public String getTextScreen() {
		return this.GetPS().getString();
	}
	
	@Override
	public String getTextScreen(int row, int col, int length) {
		
		if (row < 1 || row > 24) {
			return "";
		}
		
		if (col < 1 || col > 80) {
			return "";
		}
		
		int ini = ( row - 1) * 80 + col - 1;
		int end = ini + length > 1919 ? 1919 : ini + length;
		
		return getTextScreen().substring(ini, end);		
	}
}