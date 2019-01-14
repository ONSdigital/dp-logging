package com.github.onsdigital.logging.util;

import org.apache.commons.lang3.StringUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class RequestLogUtilTest {

    @Mock
    private HttpServletRequest request;

    private Supplier<String> idGenerator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        idGenerator = () -> "Valar Dohaeris";
    }

    @After
    public void tearDown() {
        RequestLogUtil.setRequestIdSupplier(null);
    }

    @Test
    public void shouldGenerateNewIDIfHeaderNull() {
        RequestLogUtil.setRequestIdSupplier(idGenerator);

        when(request.getHeader(REQUEST_ID_KEY)).thenReturn(null);

        RequestLogUtil.extractDiagnosticContext(request);

        assertThat(MDC.get(REQUEST_ID_KEY), equalTo("Valar Dohaeris"));
    }

    @Test
    public void shouldGenerateNewIDIfHeaderEmpty() {
        RequestLogUtil.setRequestIdSupplier(idGenerator);

        when(request.getHeader(REQUEST_ID_KEY)).thenReturn("");

        RequestLogUtil.extractDiagnosticContext(request);

        assertThat(MDC.get(REQUEST_ID_KEY), equalTo("Valar Dohaeris"));
    }

    @Test
    public void shouldExtendReqIdHeaderValueIfValid() {
        RequestLogUtil.setRequestIdSupplier(idGenerator);

        when(request.getHeader(REQUEST_ID_KEY)).thenReturn("666");

        RequestLogUtil.extractDiagnosticContext(request);

        // Length = "666".length() + ", ".length() + 8  = 13
        assertThat(MDC.get(REQUEST_ID_KEY).length(), equalTo(13));
        assertThat(StringUtils.countMatches(MDC.get(REQUEST_ID_KEY), ","), equalTo(1));
    }

    @Test
    public void shouldExtendReqIdHeaderValueIfValidAndPreviouslyExtended() {
        RequestLogUtil.setRequestIdSupplier(idGenerator);

        when(request.getHeader(REQUEST_ID_KEY)).thenReturn("666, 777");

        RequestLogUtil.extractDiagnosticContext(request);

        // Length = "666, 777".length() + ", ".length() + 8  = 18
        assertThat(MDC.get(REQUEST_ID_KEY).length(), equalTo(18));
        assertThat(StringUtils.countMatches(MDC.get(REQUEST_ID_KEY), ","), equalTo(2));
    }
}
