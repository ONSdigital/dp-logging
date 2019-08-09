package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import com.github.onsdigital.logging.builder.LogParameters;
import com.github.onsdigital.logging.util.OutputAppender;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST_KEY;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * Created by dave on 5/10/16.
 */
@Deprecated
public class TextLayout extends AbstractDPLayout {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static final String NAMESPACE_KEY = "namespace";
    private static final String PACKAGE_KEY = "package";
    private static final String LEVEL_KEY = "level";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String MESSAGE_KEY = "message";
    private static final String PARAMS_KEY = "parameters";
    private static final String THREAD_KEY = "thread";

    private ILoggingEvent event;
    private OutputAppender output;

    @Override
    public String doLayout(ILoggingEvent event) {
        this.event = event;

        List<String> messageItems = new ArrayList<>();
        this.output = (key, value) -> {
            if (StringUtils.isNotEmpty(key) && (StringUtils.isNotEmpty(value))) {
                messageItems.add(key + "=" + addColour(event.getLevel().levelStr, value));
            }
        };

        LogParameters lp = getLogParameters(event);

        output.append(NAMESPACE_KEY, lp.getNamespace());
        output.append(NAMESPACE_KEY, lp.getNamespace());
        output.append(PACKAGE_KEY, event.getLoggerName());
        output.append(LEVEL_KEY, event.getLevel().levelStr);
        output.append(TIMESTAMP_KEY, DATE_FORMAT.format(new Date()));
        output.append(REQUEST_ID_KEY, event.getMDCPropertyMap().get(REQUEST_ID_KEY));
        output.append(REMOTE_HOST_KEY, event.getMDCPropertyMap().get(REMOTE_HOST_KEY));
        output.append(THREAD_KEY, event.getThreadName());
        output.append(MESSAGE_KEY, event.getFormattedMessage());
        output.append(PARAMS_KEY, lp.getParameters().toString());

        return join(messageItems, " ") + CoreConstants.LINE_SEPARATOR;
    }

    private LogParameters getLogParameters(ILoggingEvent event) {
        if (event.getArgumentArray().length > 0) {
            for (Object arg : event.getArgumentArray()) {
                if (arg instanceof LogParameters) {
                    return (LogParameters) arg;
                }
            }
        }
        // return empty to avoid null pointers etc.
        return new LogParameters();
    }
}
