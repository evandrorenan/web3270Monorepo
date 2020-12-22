package br.com.evandrorenan.web3270.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ScreenDto {
	private String sessionId;
	private String screendId;
	private List<PositionDto> positions = new ArrayList<>();
	private List<Integer> fieldPos = new ArrayList<>();
	private List<FieldDto> fields = new ArrayList<>();
	private int cursorPos;
	
	public ScreenDto() {
		this.screendId = new Timestamp(System.currentTimeMillis()).toString()
				.replace(" ", "")
				.replace("-", "")
				.replace(".", "")
				.replace(":", "");
	}
}