package com.github.onsdigital.logging.v2.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.logging.v2.event.Severity;
import com.github.onsdigital.logging.v2.event.SimpleEvent;
import com.github.onsdigital.logging.v2.serializer.JacksonLogSerialiser;
import com.github.onsdigital.logging.v2.serializer.LogSerialiser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class JacksonLogSerialiserTest {

    @Mock
    private ObjectMapper objectMapper;

    private LogSerialiser serialiser;

    @Before
    public void setUp() throws Exception {
        this.serialiser = new JacksonLogSerialiser(objectMapper);
    }

    @Test
    public void testLogEventToJsonSuccess() throws Exception {
        SimpleEvent expected = new SimpleEvent("test.test", Severity.INFO);
        ArgumentCaptor<SimpleEvent> argumentCaptor = ArgumentCaptor.forClass(SimpleEvent.class);

        serialiser.toJson(expected);

        verify(objectMapper).writeValueAsString(argumentCaptor.capture());

        SimpleEvent actual = argumentCaptor.getValue();
        assertThat(expected.getNamespace(), equalTo(actual.getNamespace()));
        assertThat(expected.getSeverity(), equalTo(actual.getSeverity()));
        assertThat(expected.getCreateAt(), equalTo(actual.getCreateAt()));
        assertThat(expected.getData(), equalTo(actual.getData()));
    }
}
