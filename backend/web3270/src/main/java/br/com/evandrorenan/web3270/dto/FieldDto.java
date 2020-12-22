package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
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
			return (int) Math.ceil(this.start / 80.0);
		}
		return this.row;
	}
	
	public Integer getCol() {
		if ( this.col == null ) {
			int col = this.start - (int) Math.floor(this.start / 80.0) * 80;
			return col == 0 ? 80 : col;
		}
		return this.col;
	}
	
	public Integer getLength() {
		return this.end - this.start + 1;
	}
}