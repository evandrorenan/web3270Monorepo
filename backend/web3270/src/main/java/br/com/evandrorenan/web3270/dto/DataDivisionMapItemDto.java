package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataDivisionMapItemDto {
	
	private Integer lineId;
	private String dataHierarchy;
	private String dataName;
	private String baseLocatorType; 
	private String baseLocatorId;
	private String baseLocatorShift; 
	private String dataStructurePosition;
	private String assemblerDataDefinition;
	private String dataType;
	private String dataDefAttribute;
	
	public DataDivisionMapItemDto(String line) {
		if (! this.isNumeric(line.substring(0, 6).trim())
		||  ! line.substring(7, 8).equals(" ")
		||  ! line.substring(56, 57).equals(".")
		||  ! line.substring(58, 60).equals("BL")
		||  ! line.substring(61, 62).equals("=")) {
			return;
		}
		this.lineId = Integer.parseInt(line.substring(0, 6).trim());

		String dataNameAndHier = line.substring(9, 57);
		String[] splitedStr = dataNameAndHier.replace(".", "").trim().split("\\s");
		this.dataHierarchy = splitedStr[0];
		this.dataName = splitedStr[splitedStr.length - 1];
		
		this.baseLocatorType 			= line.substring(58, 61).trim();
		this.baseLocatorId				= line.substring(62, 67).trim();
		this.baseLocatorShift			= line.substring(69, 72).trim();
		this.dataStructurePosition		= line.substring(75, 84).replace(" ", "");
		this.assemblerDataDefinition	= line.substring(86, 102).trim();
		this.dataType					= line.substring(102, 117).trim();
		this.dataDefAttribute			= line.substring(118, 120).trim();
	}
	
	public Integer getNumericDataLength() {
		return Integer.parseInt(this.assemblerDataDefinition.replaceAll("[^0-9]", ""));
	}
	
	public Integer getNumericOffset() {
		return Integer.parseInt(this.baseLocatorShift.replaceAll("[^0-9A-F]", ""), 16 );
	}
	
	public boolean isValid() {
		return ! ( lineId == null || lineId == 0 );
	}
	
	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}