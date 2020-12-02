package br.com.evandrorenan.web3270scripts.bean;

public enum ACTION {
	CONSTANT			("constant", "constant\\s+\\.+=.+\\s+\\.+"),
	CHECK_SKIP_NEXT		("checkSkipNext", "checkSkipNext\\s+\\d+\\s+\\d+\\s+\".*\""),
	CHECK 				("check", "check\\s+\\d+\\s+\\d+\\s+\".*\""),
	SEND_KEYS 			("sendKeys", "sendKeys\\s+\\d+\\s+\\d+\\s+\".*\""),
	WAIT_SCREEN_CHANGE 	("waitScreenChange", "waitScreenChange\\s+discardTsoNotifications|waitScreenChange"),
	WAIT_TIME 			("waitTime", "waitTime\\s*\\d+"),
	COPY 				("copy", "copy\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+\\s+.{3,4}\\s+.{3,4}(\\s+\\d+\\s+\\d+)*"),
	LOOP_TIMES 			("loopTimes", "loopTimes\\s+\\d+"),
	LOOP_UNTIL 			("loopUntil", "loopUntil\\s+\\d+\\s+\".*\""),
	END_LOOP 			("endLoop", "endLoop");

	private final String actionName;
	private final String regex;

	private ACTION(String actionName, String value) {
		this.actionName = actionName;
		this.regex = value;
	}		
	
	public String getActionName() {
		return this.actionName;
	}
	
	public String getRegex() {
		return this.regex;
	}
}