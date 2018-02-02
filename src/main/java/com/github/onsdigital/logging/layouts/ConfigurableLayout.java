package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.LayoutBase;

import static java.lang.System.getenv;


/**
 * Layout which uses environment variable to determined which Layout to use. Simply set an the env var
 * <b>DP_LOGGING_FORMAT</b> to one for the following:
 * formats:
 * <ul>
 * <li><b>json</b></li>
 * <li><b>pretty_json</b></li>
 * <li><b>text</b></li>
 * </ul>
 * <p>
 * Simply create an env var <i>DP_LOGGING_FORMAT=</i> to one of the above. The default is text if no var is set or the
 * value is not valid.
 */
public class ConfigurableLayout extends LayoutBase<ILoggingEvent> {

    private static final String LOGGING_FORMAT_ENV_KEY = "DP_LOGGING_FORMAT";
    private static final String TEXT_FORMAT = "text";
    private static final String JSON_FORMAT = "json";
    private static final String PRETTY_JSON_FORMAT = "pretty_json";

    private Layout layout;

    public ConfigurableLayout() {
        String format = getenv(LOGGING_FORMAT_ENV_KEY);
        if (JSON_FORMAT.equals(format)) {
            this.layout = new JsonLayout();
        } else if (PRETTY_JSON_FORMAT.equals(format)) {
            this.layout = new PrettyJsonLayout();
        } else {
            this.layout = new TextLayout();
        }
    }

    @Override
    public String doLayout(ILoggingEvent iLoggingEvent) {
        return layout.doLayout(iLoggingEvent);
    }
}
