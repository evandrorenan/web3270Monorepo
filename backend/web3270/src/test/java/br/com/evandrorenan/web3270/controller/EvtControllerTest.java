package br.com.evandrorenan.web3270.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.evandrorenan.web3270.session._interface.IEvtService;

@ExtendWith(MockitoExtension.class)
class EvtControllerTest {

    @Mock
    private IEvtService evtService;

    @InjectMocks
    private EvtController evtController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(evtController).build();
    }

    @Test
    @DisplayName("should get sysout html")
    void shouldGetSysoutHtml() throws Exception {
        when(evtService.getSysoutHtml("opcao1", "job1")).thenReturn("<html></html>");

        mockMvc.perform(get("/sysout/html/opcao1/job1"))
                .andExpect(status().isOk())
                .andExpect(content().string("<html></html>"));
    }
    
    @Test
    @DisplayName("should get sysout evt html")
    void shouldGetSysoutEvtHtml() throws Exception {
        when(evtService.getSysoutHtml("evt1", "opcao1", "job1")).thenReturn("<html>evt</html>");

        mockMvc.perform(get("/sysout/html/evt1/opcao1/job1"))
                .andExpect(status().isOk())
                .andExpect(content().string("<html>evt</html>"));
    }

    @Test
    @DisplayName("should get sysout txt")
    void shouldGetSysoutTxt() throws Exception {
        when(evtService.getSysoutTxt("evt1", "opcao1", "job1")).thenReturn("text content");

        mockMvc.perform(get("/sysout/txt/evt1/opcao1/job1"))
                .andExpect(status().isOk())
                .andExpect(content().string("text content"));
    }
}
