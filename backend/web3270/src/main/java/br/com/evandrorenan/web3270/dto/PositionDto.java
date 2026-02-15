package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionDto {
	private int positionId;
	private char text;
	private boolean isProtected;
	private boolean isHidden;
	private boolean isHighLight;
}