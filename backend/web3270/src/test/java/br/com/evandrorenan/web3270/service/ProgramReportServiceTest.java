package br.com.evandrorenan.web3270.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.evandrorenan.web3270.dto.BaseLocatorDto;
import br.com.evandrorenan.web3270.dto.CompilationReportDto;
import br.com.evandrorenan.web3270.dto.CompiledSourceCodeLineDto;
import br.com.evandrorenan.web3270.dto.DataDivisionMapItemDto;
import br.com.evandrorenan.web3270.dto.ProgramReportDto;
import br.com.evandrorenan.web3270.session._interface.IBaseLocatorExtractParams;
import br.com.evandrorenan.web3270.session._interface.IBaseLocatorService;
import br.com.evandrorenan.web3270.session._interface.IEvtService;
import br.com.evandrorenan.web3270.session._interface.ISessionService;

@ExtendWith(MockitoExtension.class)
public class ProgramReportServiceTest {

    @Mock
    private ISessionService sessionService;
    @Mock
    private IEvtService evtService;
    @Mock
    private IBaseLocatorService abendService;

    @InjectMocks
    private ProgramReportService programReportService;

    @Test
    @DisplayName("should generate report correctly")
    void shouldGenerateReport() throws Exception {
        CompilationReportDto compilationReportDto = new CompilationReportDto();
        compilationReportDto.setProgramName("PROG1");
        compilationReportDto.setSourceCodeLines(new ArrayList<>());
        compilationReportDto.setDataDivisionMap(new ArrayList<>());
        
        String coLine = "  000100  01 10   MOVE A TO B.                                                          1";
        CompiledSourceCodeLineDto line = new CompiledSourceCodeLineDto(coLine);
        compilationReportDto.getSourceCodeLines().add(line);
        
        DataDivisionMapItemDto item = new DataDivisionMapItemDto();
        item.setLineId(1);
        item.setDataName("A");
        item.setBaseLocatorType("BL1");
        item.setBaseLocatorId("1");
        item.setBaseLocatorShift("0");
        item.setAssemblerDataDefinition("4");
        compilationReportDto.getDataDivisionMap().add(item);

        List<BaseLocatorDto> baseLocators = new ArrayList<>();
        BaseLocatorDto bl = new BaseLocatorDto();
        bl.setBaseLocatorType("1");
        bl.setBaseLocatorId("1");
        bl.setWorkAreaEbcdic(new StringBuilder("1234"));
        bl.setWorkAreaHex(new StringBuilder("F1F2F3F4"));
        baseLocators.add(bl);

        when(evtService.getCompilationReport(anyString())).thenReturn(compilationReportDto);
        when(abendService.getBaseLocators(any(IBaseLocatorExtractParams.class))).thenReturn(baseLocators);

        ProgramReportDto result = programReportService.generateReport("job1", mock(IBaseLocatorExtractParams.class));

        assertNotNull(result);
        assertEquals(1, result.getSourceCode().size());
        assertEquals(1, result.getDataDivisionMap().size());
    }
}
