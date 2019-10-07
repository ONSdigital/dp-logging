package com.github.onsdigital.logging.v2.event;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ErrorsTest {

    @Test
    public void testErrorsAsExpected() {
        Throwable inner = new RuntimeException("inner");
        Throwable outter = new RuntimeException("outter", inner);

        Errors err = new Errors(outter);

        assertThat(err.getErrors().size(), equalTo(2));

        Error actual = err.getErrors().get(0);
        assertError(err.getErrors().get(0), new Error(outter));
        assertError(err.getErrors().get(1), new Error(inner));
    }

    private void assertError(Error actual, Error expected) {
        assertThat(actual, equalTo(expected));
        assertThat(actual.getMessage(), equalTo(expected.getMessage()));
        assertThat(actual.getStackTraces(), equalTo(expected.getStackTraces()));
        assertThat(actual.getData(), equalTo(expected.getData()));
    }
}
