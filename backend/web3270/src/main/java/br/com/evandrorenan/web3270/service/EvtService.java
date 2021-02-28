package br.com.evandrorenan.web3270.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.dto.DataDivisionMapItemDto;
import br.com.evandrorenan.web3270.dto.SessionDto;
import br.com.evandrorenan.web3270.dto.CompiledSourceCodeLineDto;
import br.com.evandrorenan.web3270.exception.ExceptionWeb3270;
import br.com.evandrorenan.web3270.session.MySessionConstants;
import br.com.evandrorenan.web3270.session._interface.IEvtService;
import br.com.evandrorenan.web3270.session._interface.IMySession;
import br.com.evandrorenan.web3270.session._interface.ISessionService;
import lombok.Data;

@Component
@Data
public class EvtService implements IEvtService {
	
	@Value("${BRADESCO_HOST_IP}")
	private String bradescoHostIp;
	
	@Value("${BRADESCO_HOST_PORT}")
	private String bradescoHostPort;
	
	private ISessionService sessionService;
	
	public EvtService(ISessionService sessionService) {
		this.sessionService = sessionService;
	}
	
	@Override
	public String getSysoutHtml(String opcao, String jobId) throws ExceptionWeb3270 {
		return getSysoutHtml("EVT04", opcao, jobId);	
	}

	@Override
	public String getSysoutHtml(String evt, String opcao, String jobId) throws ExceptionWeb3270 {
		StringBuilder sysout = new StringBuilder();
		
		sysout.append("<html>");
		sysout.append("<head>");
		sysout.append("<style>p {font: 11px/1.5 Monaco, MonoSpace;white-space: pre;line-height: 0;}</style>");
		sysout.append("</head>");
		sysout.append("<body>");
		sysout.append("</head>");
		sysout.append("<body>");
		
		for (String line : this.getSysout(evt, opcao, jobId)) {
			sysout.append("<p>" + line + "</p>");
		}
		
		sysout.append("</body>");
		sysout.append("</html>");
		return sysout.toString();
	}

	@Override
	public List<String> getSysout(String evt, String opcao, String jobId) throws ExceptionWeb3270 {
		List<String> sysout = new ArrayList<>();
		
		IMySession mySession = this.navigateToSysout(evt, opcao, jobId);
		while (! mySession.getTextScreen(2, 49, 7).equals("LIHAS->")) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
 		while (true) {
			List<String> page = this.getPage(mySession).subList(4, 24);
			
			for (String line : page) {
				if (line.contains("----  FINAL DO RELATORIO  ----")) {
					return sysout;
				}
				sysout.add("<p>" + line + "</p>");
			}			
			mySession.sendKeys("[pf8]", 1, 27);			
		}
	}
	
	@Override
	public CompilationReportDto getCompilationReport(String jobId) throws ExceptionWeb3270 {
		IMySession mySession = this.navigateToSysout(jobId);
		
		CompilationReportDto compilationReportDto = new CompilationReportDto();
		compilationReportDto.setProgramName(mySession.getTextScreen().substring(90, 98).trim());
		compilationReportDto.setJobId(mySession.getTextScreen().substring(119, 127).trim());		
		
		List<CompiledSourceCodeLineDto> expandedSourceCode = getExpandedSourceCode(mySession);
		compilationReportDto.setSourceCodeLines(expandedSourceCode);

		List<DataDivisionMapItemDto> dataDivisionMap = getDataDivisionMap(mySession);
		compilationReportDto.setDataDivisionMap(dataDivisionMap);
		
		mySession.dispose();
		
		return compilationReportDto;
	}
	
	private IMySession navigateToSysout(String jobId) throws ExceptionWeb3270 {
		return navigateToSysout("EVTO4", "C", jobId);			
	}
	
	private IMySession navigateToSysout(String evt, String opcao, String jobId) throws ExceptionWeb3270 {
		SessionDto sessionDto = sessionService.createNewSessionDto(bradescoHostIp, bradescoHostPort);
		IMySession mySession = this.sessionService.getSession(sessionDto.getSessionId());
		mySession.sendKeys(evt + "[enter]", 24, 29);
		mySession.sendKeys("4253-440[enter]", 4, 25);
		mySession.sendKeys(opcao + "[enter]", 5, 38);
		mySession.sendKeys("[pf3]", 1, 27);
		mySession.sendKeys(jobId + "[enter]", 21, 38);
		return mySession;
	}

	private List<CompiledSourceCodeLineDto> getExpandedSourceCode(IMySession mySession) throws ExceptionWeb3270 {
		List <CompiledSourceCodeLineDto> sourceCodeLines = new ArrayList<>();

		mySession.sendKeys("M" + MySessionConstants.F7_STR, 1, 27);
		mySession.sendKeys("f '----+-*A-1-B--+----2'" + MySessionConstants.ENDLINE_STR + MySessionConstants.ENTER_STR, 1 , 27);
		
		if (mySession.getTextScreen(5, 2, "SELECAO OU PESQUISA".length()).equals("SELECAO OU PESQUISA")) {
			mySession.sendKey(MySessionConstants.F3_STR);
			return sourceCodeLines;
		}
		
 		while (true) {
			List<String> page = this.getPage(mySession);
			
			for (String line : page) {
				if (line.contains("----  FINAL DO RELATORIO  ----")
				||	line.substring(0, 17).equals("Data Division Map") 
				||  line.subSequence(0, 9).equals(" Defined ")) {
					return sourceCodeLines;
				}				
				
				CompiledSourceCodeLineDto scLine = new CompiledSourceCodeLineDto(line);
				if (scLine.isValid()) {
					sourceCodeLines.add(scLine);
				}				
			}			
			mySession.sendKeys("[pf8]", 1, 27);			
		}
	}
	
	private List<DataDivisionMapItemDto> getDataDivisionMap(IMySession mySession) throws ExceptionWeb3270 {
		List <DataDivisionMapItemDto> dataDivisionMapItems = new ArrayList<>();

		mySession.sendKeys("M" + MySessionConstants.F7_STR, 1, 27);
		mySession.sendKeys("f 'Data Division Map'" + MySessionConstants.ENDLINE_STR + MySessionConstants.ENTER_STR, 1 , 27);
		
		if (mySession.getTextScreen(5, 2, "SELECAO OU PESQUISA".length()).equals("SELECAO OU PESQUISA")) {
			mySession.sendKey(MySessionConstants.F3_STR);
			return dataDivisionMapItems;
		}
		
 		while (true) {
			List<String> page = this.getPage(mySession);
			
			for (String line : page) {
				if (line.contains("----  FINAL DO RELATORIO  ----")
				||	line.substring(0, 7).equals("LITERAL")) {
					return dataDivisionMapItems;
				}				
				
				DataDivisionMapItemDto ddMapItem = new DataDivisionMapItemDto(line);
				if (ddMapItem.isValid()) {
					dataDivisionMapItems.add(ddMapItem);
				}				
			}			
			mySession.sendKeys("[pf8]", 1, 27);			
		}
	}
	
	
	private List<String> getPage(IMySession mySession) throws ExceptionWeb3270 {
		List<String> pagePart1 = this.getArrStrScreen(mySession.getTextScreen()); 
		List<String> pagePart2 = null;
		
		if ( pagePart1.get(2).substring(32, 35).equals("001")) {
			mySession.sendKeys("[pf11]", 1, 27);
			pagePart2 = this.getArrStrScreen(mySession.getTextScreen());
		} else {
			pagePart2 = pagePart1;
			mySession.sendKeys("[pf10]", 1, 27);
			pagePart1 = this.getArrStrScreen(mySession.getTextScreen());
		}
		
		for (int i = 4; i < pagePart1.size(); i++ ) {
			pagePart1.set(i, pagePart1.get(i) + pagePart2.get(i));
		}
		
		return pagePart1;
	}
	
	private List<String> getArrStrScreen(String txtScreen) {
		List<String> arrStrScreen = new ArrayList<>();
		
		for (int i = 0; i < 24; i++) {
			arrStrScreen.add(txtScreen.substring(i * 80, i * 80 + 80));
		}
		
		return arrStrScreen;
	}
}