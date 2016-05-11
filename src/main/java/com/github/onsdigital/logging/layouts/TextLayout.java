package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.github.onsdigital.logging.builder.LogParameters;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST_KEY;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;

/**
 * Created by dave on 5/10/16.
 */
public class TextLayout extends LayoutBase<ILoggingEvent> {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ONS] ")
                .append(DATE_FORMAT.format(new Date()))
                .append(" ")
                .append(event.getLevel())
                .append(" [")
                .append(event.getThreadName())
                .append("] ")
                .append(event.getLoggerName())
                .append(" - ")
                .append(event.getFormattedMessage());

        String requestId = event.getMDCPropertyMap().get(REQUEST_ID_KEY);
        String remoteHost = event.getMDCPropertyMap().get(REMOTE_HOST_KEY);

        if (StringUtils.isNotEmpty(requestId)) {
            sb.append(" | ").append(REQUEST_ID_KEY).append("=").append(requestId);
        }

        if (StringUtils.isNotEmpty(remoteHost)) {
            sb.append(" | ").append(REMOTE_HOST_KEY).append("=").append(remoteHost);
        }

        appendParameters(sb, event);
        sb.append(CoreConstants.LINE_SEPARATOR);
        return sb.toString();
    }

    private void appendParameters(StringBuilder sb, ILoggingEvent event) {
        if (event.getArgumentArray().length > 0) {
            for (Object arg : event.getArgumentArray()) {
                if (arg instanceof LogParameters) {
                    Map<String, String> params = ((LogParameters) arg).getParameters();
                    sb.append(" parameters=[").append(params.toString()).append(" ]");
                    break;
                }
            }
        }
    }
}
