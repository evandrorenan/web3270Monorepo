package br.com.evandrorenan.web3270.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class CompilationReportDto {

	private String programName;
	private String jobId;
	private List<CompiledSourceCodeLineDto> sourceCodeLines;
	private List<DataDivisionMapItemDto> dataDivisionMap;
	
}