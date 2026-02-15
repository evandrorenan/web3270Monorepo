package br.com.evandrorenan.web3270.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.BaseLocatorDto;
import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.dto.CompiledSourceCodeLineDto;
import br.com.evandrorenan.web3270.dto.DataDivisionMapItemContentDto;
import br.com.evandrorenan.web3270.dto.DataDivisionMapItemDto;
import br.com.evandrorenan.web3270.dto.ProgramReportDto;
import br.com.evandrorenan.web3270.dto.SourceCodeLineDto;
import br.com.evandrorenan.web3270.dto.TokenDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session._interface.COBOL_RESERVED_WORDS;
import br.com.evandrorenan.web3270.session._interface.IBaseLocatorService;
import br.com.evandrorenan.web3270.session._interface.IBaseLocatorExtractParams;
import br.com.evandrorenan.web3270.session._interface.IEvtService;
import br.com.evandrorenan.web3270.session._interface.IProgramReportService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;
import lombok.Data;

@Component
@Data
@Slf4j
public class ProgramReportService implements IProgramReportService {

	public static final String SPACE_TAB_ENTER_FORMFEED = " \\t\\n\\r\\f.";
	private IEvtService evtService;
	
	private IBaseLocatorService abendService;
	
	private ISessionService sessionService;
	
	@Autowired
	public ProgramReportService(ISessionService sessionService, IEvtService evtService, IBaseLocatorService abendService) {
		log.info("ProgramReportService constructed;");
		this.sessionService = sessionService;
		this.evtService = evtService;
		this.abendService = abendService;
	}
	
	@Override
	public CompilationReportDto getCompilationReport(
			String compilationJobId) throws ExceptionWeb3270 {
		
		return this.evtService.getCompilationReport(compilationJobId);
	}
	
	@Override
	public List<BaseLocatorDto> getBaseLocators(
			IBaseLocatorExtractParams blExtractParams) throws ExceptionWeb3270 {
		
		return this.abendService.getBaseLocators(blExtractParams);
	}	

	@Override
	public ProgramReportDto generateReport(
			String compilationJobid,
			IBaseLocatorExtractParams blExtractParams) throws ExceptionWeb3270 {
		
		CompilationReportDto compilationReport = this.evtService.getCompilationReport(compilationJobid);
		
		List<BaseLocatorDto> baseLocators = this.abendService.getBaseLocators(blExtractParams);
		
		return this.generateReport(compilationReport, baseLocators); 
		
	}
	
	@Override
	public ProgramReportDto generateReport(
			CompilationReportDto compilationReportDto,
			IBaseLocatorExtractParams blExtractParams) throws ExceptionWeb3270 {
			
		List<BaseLocatorDto> baseLocators = this.abendService.getBaseLocators(blExtractParams);
		
		return this.generateReport(compilationReportDto, baseLocators);
		
	}

	@Override
	public ProgramReportDto generateReport(String compilationJobid, List<BaseLocatorDto> baseLocators) throws ExceptionWeb3270 {

		CompilationReportDto compilationReport = this.evtService.getCompilationReport(compilationJobid);
		
		return this.generateReport(compilationReport, baseLocators);
		
	}
	
	@Override
	public ProgramReportDto generateReport(
			CompilationReportDto compilationReportDto, 
			List<BaseLocatorDto> baseLocators) {

		List<DataDivisionMapItemContentDto> dataDivisionMap = 
				this.loadcontentOnDataDivisionMap(
						compilationReportDto.getDataDivisionMap(), 
						baseLocators);
		
		ProgramReportDto programReportDto = this.attachRefContentToSourceCode(
				compilationReportDto.getSourceCodeLines(), 
				dataDivisionMap);

		if (programReportDto != null) programReportDto.setDataDivisionMap(dataDivisionMap);
		
		return programReportDto;
	}

	/* TODO: Refactor for more clarity, handle possible dup. key 
	 		 (lineId) dataDivision*/
	private ProgramReportDto attachRefContentToSourceCode(
				List<CompiledSourceCodeLineDto> sourceCodeLines,
				List<DataDivisionMapItemContentDto> dataDivision) {
		
		try { 
		Map<Integer, DataDivisionMapItemContentDto> dataDivisionMap = 
				dataDivision.stream()
							.collect(Collectors.toMap(
									DataDivisionMapItemContentDto :: getLineId, 
									item -> item));
		ProgramReportDto report = new ProgramReportDto();
		
		for (CompiledSourceCodeLineDto scLine : sourceCodeLines) {
			SourceCodeLineDto repLineDto = new SourceCodeLineDto();

			repLineDto.setLineId(scLine.getLineId());
			repLineDto.setText(scLine.getText());

			StringTokenizer tokens = new StringTokenizer(scLine.getText());
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();				
				TokenDto tokenDto = createNewTokenDto(dataDivisionMap, scLine, token);
				repLineDto.getTokens().add(tokenDto);
			}

			report.getSourceCode().add(repLineDto);
		}
		

		return report;
	}

	private TokenDto createNewTokenDto(Map<Integer, DataDivisionMapItemContentDto> dataDivisionMap,
			CompiledSourceCodeLineDto scLine, String token) {
		TokenDto tokenDto = new TokenDto();
		
		if (COBOL_RESERVED_WORDS.isReservedWord(token)) {
			tokenDto.setReservedWord(true);
		} else {
			tokenDto.setDataReference(
					this.searchDataReference(
							scLine.getMapReference(), 
							token,
							dataDivisionMap));
		}
		return tokenDto;
	}
	
	private Integer searchDataReference(
			String mapReference, 
			String token,
			Map<Integer, DataDivisionMapItemContentDto> dataDivisionMap) {
		
		StringTokenizer refs = new StringTokenizer(mapReference, SPACE_TAB_ENTER_FORMFEED);
		
		while (refs.hasMoreTokens()) {
			String ref = refs.nextToken();
			if (this.isNumeric(ref)) {
				DataDivisionMapItemContentDto ddRef = dataDivisionMap.get(Integer.parseInt(ref));
				if (ddRef != null 
				&&  ddRef.getDataName().equals(token)) {
					return Integer.parseInt(ref);					
				}
			}
		}

		return null;
	}

	/* TODO: Performance improvement. This method is taking too long. */ 
	private List<DataDivisionMapItemContentDto> loadcontentOnDataDivisionMap(
			List<DataDivisionMapItemDto> dataDivisionMap, 
			List<BaseLocatorDto> baseLocators) {
		
		List<DataDivisionMapItemContentDto> dataDivisionMapContent = new ArrayList<>();
		
		if (baseLocators.isEmpty()) {
			return dataDivisionMapContent;
		}		
		
		log.info("Start loading data division Content: ");
		Instant antes = java.time.Instant.now();
		
		
		for (int i = 0; i < dataDivisionMap.size(); i++ ) {
			antes = this.sysout(i, String.valueOf(i), antes);
			
			DataDivisionMapItemDto item = dataDivisionMap.get(i);
			BaseLocatorDto currentBaseLocator = this.findBaseLocator(item.getBaseLocatorType(), item.getBaseLocatorId(), baseLocators);

			if (currentBaseLocator != null ) {				
				DataDivisionMapItemContentDto ddContent = mapContentFromBaseLocator(item, baseLocators, baseLocators.indexOf(currentBaseLocator));
				dataDivisionMapContent.add(ddContent);
			}
		}
		log.info("End loading data division Content: " + java.time.Instant.now());
		
		return dataDivisionMapContent;
	}
	
	private Instant sysout(int i, String a, Instant antes) {
//		if (i < 5) {
			Instant depois = java.time.Instant.now();
			log.info(a + " - " + depois.toString() + " +" + (depois.toEpochMilli() - antes.toEpochMilli())); 
			return depois;
//		}		
//		return null;
	}
	
	private DataDivisionMapItemContentDto mapContentFromBaseLocator(
			DataDivisionMapItemDto item,
			List<BaseLocatorDto> baseLocators,
			Integer idxBaseLocator) {
		
		DataDivisionMapItemContentDto ddContent = new DataDivisionMapItemContentDto(item);

		StringBuilder ebcdicContent = new StringBuilder();
		StringBuilder hexContent = new StringBuilder();
		
		Integer ini = ddContent.getNumericOffset();
		Integer end = ini + ddContent.getNumericDataLength();
		
		while (idxBaseLocator < baseLocators.size()) {
		
			if ((ini + end) <= baseLocators.get(idxBaseLocator).getWorkAreaEbcdic().length()) {
				ebcdicContent.append(baseLocators.get(idxBaseLocator).getWorkAreaEbcdic().substring(ini, ini + end));
				hexContent.append(baseLocators.get(idxBaseLocator).getWorkAreaHex().substring(ini, ini + end));
				break;
			} else {
				ebcdicContent.append(baseLocators.get(idxBaseLocator).getWorkAreaEbcdic().substring(ini));
				hexContent.append(baseLocators.get(idxBaseLocator).getWorkAreaHex().substring(ini * 2));
				ini = 0;
				end = baseLocators.get(idxBaseLocator).getWorkAreaEbcdic().length() - ini;
				idxBaseLocator++;
			}
		}
		
		ddContent.getEbcdicContent().add(ebcdicContent.toString());
		ddContent.getHexContent().add(hexContent.toString());
		return ddContent;
	}

	private BaseLocatorDto findBaseLocator(
			String baseLocatorType, 
			String baseLocatorId,
			List<BaseLocatorDto> baseLocators) {

		for (BaseLocatorDto baseLocator : baseLocators ) {
			if (("BL" + baseLocator.getBaseLocatorType()).equals(baseLocatorType)
			&&  Integer.parseInt(baseLocator.getBaseLocatorId()) == Integer.parseInt(baseLocatorId)) {
				return baseLocator;
			}
		}
		return null;
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
