package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import com.github.onsdigital.logging.builder.LogParameters;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST_KEY;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;
import static java.text.MessageFormat.format;

/**
 * Created by dave on 5/10/16.
 */
public class TextLayout extends AbstractDPLayout {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static final String SINGLE_SPACE = " ";

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(format("[{0}]", event.getLoggerName()))
                .append(SINGLE_SPACE)
                .append(addColour(event.getLevel().levelStr, event.getLevel().levelStr))
                .append(SINGLE_SPACE)
                .append(DATE_FORMAT.format(new Date()))
                .append(SINGLE_SPACE)
                .append(addColour(event.getLevel().levelStr, event.getFormattedMessage()));

        String requestId = event.getMDCPropertyMap().get(REQUEST_ID_KEY);
        String remoteHost = event.getMDCPropertyMap().get(REMOTE_HOST_KEY);

        if (StringUtils.isNotEmpty(requestId)) {
            sb.append(SINGLE_SPACE).append(format("{0}={1}", REQUEST_ID_KEY, requestId));
        }

        if (StringUtils.isNotEmpty(remoteHost)) {
            sb.append(SINGLE_SPACE).append(format("{0}={1}", REMOTE_HOST_KEY, remoteHost));
        }

        appendParameters(sb, event);
        sb.append(SINGLE_SPACE).append(format("[{0}]", event.getThreadName()));
        sb.append(CoreConstants.LINE_SEPARATOR);
        return sb.toString();
    }

    private void appendParameters(StringBuilder sb, ILoggingEvent event) {
        if (event.getArgumentArray().length > 0) {
            for (Object arg : event.getArgumentArray()) {
                if (arg instanceof LogParameters) {
                    sb.append(SINGLE_SPACE);
                    LogParameters lp = (LogParameters) arg;
                    sb.append(addColour(event.getLevel().levelStr, lp.getParameters().toString()));
                    break;
                }
            }
        }
    }
}
