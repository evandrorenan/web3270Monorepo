package br.com.evandrorenan.web3270.presentation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    private RateLimitingFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new RateLimitingFilter();
        ReflectionTestUtils.setField(filter, "enabled", true);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void shouldAllowRequestWhenEnabledAndWithinLimit() throws Exception {
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldBlockRequestWhenLimitExceeded() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Consume all tokens
        for (int i = 0; i < 101; i++) {
            filter.doFilter(request, response, chain);
        }

        verify(response).setStatus(429);
        verify(chain, times(100)).doFilter(request, response);
    }

    @Test
    void shouldAllowRequestWhenDisabled() throws Exception {
        ReflectionTestUtils.setField(filter, "enabled", false);
        
        // Consume theoretically more than limit
        for (int i = 0; i < 150; i++) {
            filter.doFilter(request, response, chain);
        }

        verify(chain, times(150)).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }
}
