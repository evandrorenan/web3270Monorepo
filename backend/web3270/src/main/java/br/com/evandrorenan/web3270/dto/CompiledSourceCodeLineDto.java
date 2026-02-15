package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompiledSourceCodeLineDto {

	private Integer lineId;
	private String pL;
	private String sL;
	private String text;
	private String mapReference;
	
	public CompiledSourceCodeLineDto(String line) {
		if (! this.isNumeric(line.substring(2, 8))
		||  ! line.substring(1, 2).equals(" ")) {
			return;
		}

		this.lineId = Integer.parseInt(line.substring(2, 8).trim());
		this.pL = line.substring(10, 12);
		this.sL = line.substring(13, 15);
		this.text = line.substring(17, 89);
		this.mapReference = line.substring(89, line.length());
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