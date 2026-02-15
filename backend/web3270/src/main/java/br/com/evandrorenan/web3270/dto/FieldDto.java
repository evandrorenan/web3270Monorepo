package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FieldDto {
	private String fieldId;
	private Integer start;
	private Integer end;
	private String text;
	private boolean isProtected;
	private boolean isHidden;
	private boolean isHighIntensity;
	private String color;
	private Integer row;
	private Integer col;
	private boolean underline;
	
	public FieldDto(String fieldId) {
		this.fieldId = fieldId;
	}
	
	public Integer getRow() {
		if ( this.row == null) {
			return (this.start / 80) + 1;
		}
		return this.row;
	}
	
	public Integer getCol() {
		if ( this.col == null ) {
			return (this.start % 80) + 1;
		}
		return this.col;
	}
	
	public Integer getLength() {
		return this.end - this.start + 1;
	}
}