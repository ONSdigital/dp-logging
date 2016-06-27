package com.github.onsdigital.logging.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.github.onsdigital.logging.builder.LogParameters;
import com.github.onsdigital.logging.layouts.style.Beautifier;
import org.apache.commons.lang3.StringUtils;

import static com.github.onsdigital.logging.layouts.style.Beautifier.tertiaryColour;
import static com.github.onsdigital.logging.layouts.style.Beautifier.styleDate;
import static com.github.onsdigital.logging.layouts.style.Beautifier.styleKeyValue;
import static com.github.onsdigital.logging.layouts.style.Beautifier.styleLogLevel;
import static com.github.onsdigital.logging.layouts.style.Beautifier.styleLoggerName;
import static com.github.onsdigital.logging.layouts.style.Beautifier.styleMessage;
import static com.github.onsdigital.logging.layouts.style.Beautifier.styleThreadName;
import static com.github.onsdigital.logging.util.RequestLogUtil.REMOTE_HOST_KEY;
import static com.github.onsdigital.logging.util.RequestLogUtil.REQUEST_ID_KEY;

/**
 * Created by dave on 5/10/16.
 */
public class TextLayout extends LayoutBase<ILoggingEvent> {

    private static final String SINGLE_SPACE = " ";
    private static final String DIVIDER = " | ";

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(tertiaryColour(event, "[dp-logging]"))
                .append(SINGLE_SPACE)
                .append(styleLogLevel(event))
                .append(SINGLE_SPACE)
                .append(styleDate(event))
                .append(SINGLE_SPACE)
                .append(styleThreadName(event))
                .append(SINGLE_SPACE)
                .append(styleLoggerName(event))
                .append(SINGLE_SPACE)
                .append(styleMessage(event));

        String requestId = event.getMDCPropertyMap().get(REQUEST_ID_KEY);
        String remoteHost = event.getMDCPropertyMap().get(REMOTE_HOST_KEY);

        if (StringUtils.isNotEmpty(requestId)) {
            sb.append(tertiaryColour(event, DIVIDER)).append(styleKeyValue(event, REQUEST_ID_KEY, requestId));
        }

        if (StringUtils.isNotEmpty(remoteHost)) {
            sb.append(tertiaryColour(event, DIVIDER)).append(styleKeyValue(event, REMOTE_HOST_KEY, remoteHost));
        }

        appendParameters(sb, event);
        sb.append(CoreConstants.LINE_SEPARATOR);
        return sb.toString();
    }

    private void appendParameters(StringBuilder sb, ILoggingEvent event) {
        if (event.getArgumentArray().length > 0) {
            for (Object arg : event.getArgumentArray()) {
                if (arg instanceof LogParameters) {
                    sb.append(SINGLE_SPACE);
                    sb.append(Beautifier.beautifyParameters(event, (LogParameters) arg));
                    break;
                }
            }
        }
    }
}
