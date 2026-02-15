package br.com.evandrorenan.web3270.dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import lombok.Data;

@Data
public class BaseLocatorDto {
	
	private String baseLocatorType; 
	private String baseLocatorId;
	private Long address;
	private Long offset;
	private StringBuilder workAreaEbcdic;
	private StringBuilder workAreaHex;
	
	public BaseLocatorDto() {
		this.baseLocatorType = "";
		this.baseLocatorId = "";
		this.address = 0L;
		this.offset = 0L;
		this.workAreaEbcdic = new StringBuilder();
		this.workAreaHex = new StringBuilder();
	}
	
	public String getWorkAreaEbcdic() {
		if (this.workAreaEbcdic.length() < 4096) {
			this.workAreaEbcdic.append(
					new String(new char[4096 - this.workAreaEbcdic.length()]).replace("\0", "."));
		}
		String strEbcdic = this.workAreaEbcdic.toString();
		return strEbcdic.substring(0, Math.min(4096, strEbcdic.length()));
	}

	public String getWorkAreaHex() {
		if (this.workAreaHex.length() < 4096 * 2) {
			this.workAreaHex.append(
					new String(new char[4096 * 2 - this.workAreaHex.length()]).replace("\0", "00"));
		}
		String strHex = workAreaHex.toString();
		return strHex.substring(0, Math.min(4096 * 2, strHex.length()));
	}

	public void extractWorkAreas(IMySession mySession, String programName) throws ExceptionWeb3270 {
		
		while (true) {
			boolean hasMore = this.scanCurrentPage(IMySession.getTextScreenAsArray(mySession), programName);
			if ( !hasMore || this.workAreaEbcdic.length() >= 4096 ) {
				return;
			}
			mySession.sendKeys("[pf8]", 8, 15);
		}
	}

	private boolean scanCurrentPage(List<String> lines, String programName) {
		
		for (int i = 7; i < lines.size(); i++ ) {			
			if ( isBaseLocatorEnd(lines.get(i), programName)) {
				return false;
			}
			
			if (BaseLocatorDto.isValidWorkArea(lines.get(i))) {
				this.buildWorkAreas(lines.get(i));
			} else {
				handleRepetitions(lines.get(i));
			}
		}
		return true;
	}
	
	private boolean isBaseLocatorEnd(String line, String programName) {
		if ( this.workAreaEbcdic.length() >= 4096 ) {
			return true;
		}
		
		if ( line.matches("\\s{5,10}Event.*Program.*" + programName + ".*BL.=[0-9]{1}.*")) {
			return ! line.contains(" BL" + this.baseLocatorType + "=" + this.baseLocatorId);
		}
		return false;
	}

	private void buildWorkAreas(String line) {
		if (!isValidWorkArea(line) ) {
			return;
		}
		
		this.address = Long.parseLong(line.substring(0,14).replace(" ", ""), 16);
		
		int lastAst = line.lastIndexOf('*');
		
		String hexWord = line.substring(
				lastAst - 17 - 36 , 
				lastAst - 17).replace(" ", "");		
		
		this.workAreaHex.append(hexWord);
		
		if (hexWord.length() == 16) {
			this.workAreaEbcdic.append(line.substring(
					lastAst - 8,
					lastAst));
		} else {
			this.workAreaEbcdic.append(line.substring(
					lastAst - 16,
					lastAst));
		}
	}
	
	public static boolean isValidWorkArea(String str) {
		if ( str.length() < 14 || str.lastIndexOf('*') < 16 ) {
			return false;
		}
		return str.substring(0,14).replace(" ", "").matches("[0-9A-F]{8,}");
	}	

	private void handleRepetitions(String line) {
		if (this.workAreaEbcdic == null
		||  this.workAreaEbcdic.length() < 16 
		||	! line.matches(".*Lines.*same as above.*")) {					
			return;
		}
		
		int reps = Integer.parseInt(
				line.replaceAll("' bytes.*same as above.*", "")
							.replaceAll(".*Lines.*X'", ""), 16) / 16;

		String lastWorkEbcdicArea = this.workAreaEbcdic.substring(
				this.workAreaEbcdic.length() - 16,
				this.workAreaEbcdic.length());
		
		String lastWorkHexArea = this.workAreaHex.substring(
				this.workAreaHex.length() - 16 * 2,
				this.workAreaHex.length());

		for (int j = 0; j < reps; j++ ) {
			
			this.workAreaEbcdic.append(lastWorkEbcdicArea);
			this.workAreaHex.append(lastWorkHexArea);
		}
		
		if (this.workAreaEbcdic.length() > 4096 ) {
			this.workAreaEbcdic = new StringBuilder(this.workAreaEbcdic.substring(0, 4096));
			this.workAreaHex = new StringBuilder(this.workAreaHex.substring(0, 4096 * 2));
		}
	}	
}