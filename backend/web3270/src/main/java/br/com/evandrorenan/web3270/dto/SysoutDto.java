package br.com.evandrorenan.web3270.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SysoutDto {
	private int id;
	private String evt; 
	private String opcao;
	private String jobId;
	private String content;
}