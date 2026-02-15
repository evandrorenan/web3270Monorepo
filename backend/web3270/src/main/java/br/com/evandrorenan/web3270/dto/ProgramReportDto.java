package br.com.evandrorenan.web3270.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProgramReportDto {

	private List<SourceCodeLineDto> sourceCode = new ArrayList<>();
	private List<DataDivisionMapItemContentDto> dataDivisionMap;
}