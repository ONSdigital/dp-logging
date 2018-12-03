package com.github.onsdigital.logging;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LogMessageBuilderTest {

    @Test
    public void shouldNotAddParamIfKeyNull() {
        LogBuilder lb = new LogBuilder("test");
        lb.addParameter(null, "Some value");

        assertTrue(lb.getParams().isEmpty());
    }

    @Test
    public void shouldNotAddParamIfKeyEmpty() {
        LogBuilder lb = new LogBuilder("test");
        lb.addParameter("", "Some value");

        assertTrue(lb.getParams().isEmpty());
    }

    @Test
    public void shouldNotAddParamIfNull() {
        LogBuilder lb = new LogBuilder("test");
        lb.addParameter("key", null);
        Map<String, Object> params = lb.getParams();

        assertFalse(params.containsKey("key"));
        assertTrue(params.isEmpty());
    }

    @Test
    public void shouldAddPathAsString() {
        LogBuilder lb = new LogBuilder("test");
        Path p = Paths.get("/test/path/1");

        lb.addParameter("key", p);
        Map<String, Object> params = lb.getParams();

        assertTrue(params.containsKey("key"));
        Object target = params.get("key");

        if (!(target instanceof String)) {
            fail("expected parameter value to be type string");
        }
        assertThat((String) target, equalTo(p.toString()));
    }
}
