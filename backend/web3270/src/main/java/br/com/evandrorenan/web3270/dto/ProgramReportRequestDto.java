package br.com.evandrorenan.web3270.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.session._interface.IBaseLocatorExtractParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramReportRequestDto {
	private String compilationJobid; 
	private IBaseLocatorExtractParams baseLocatorExtractParams;
	private CompilationReportDto compilationReport;
	private List<BaseLocatorDto> baseLocators;
}