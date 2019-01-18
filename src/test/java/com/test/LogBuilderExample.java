package com.test;

import ch.qos.logback.classic.Level;
import com.github.onsdigital.logging.builder.LogMessageBuilder;

import java.time.LocalDateTime;

/**
 * Created by dave on 01/02/2018.
 */
public class LogBuilderExample extends LogMessageBuilder {

    /**
     * static convenience method for info level logging.
     */
    public static LogBuilderExample info(String message) {
        return new LogBuilderExample(message, Level.INFO);
    }

    public static LogBuilderExample debug(String message) {
        return new LogBuilderExample(message, Level.DEBUG);
    }

    public static LogBuilderExample warn(String message) {
        return new LogBuilderExample(message, Level.WARN);
    }

    public static LogBuilderExample error(String message) {
        return new LogBuilderExample(message, Level.ERROR);
    }

    private LogBuilderExample(String eventDescription) {
        super(eventDescription);
    }

    private LogBuilderExample(String description, Level logLevel) {
        super(description, logLevel);
        setNamespace("mylogger"); // your namespace value
    }

    private LogBuilderExample(Throwable t, Level level, String description) {
        super(t, level, description);
        setNamespace("mylogger"); // your namespace value
    }

    private LogBuilderExample(Throwable t, String description) {
        super(t, description);
        setNamespace("mylogger"); // your namespace value
    }

    @Override
    public String getLoggerName() {
        return "com.test.logger.example";
    }

    public static void main(String[] args) throws InterruptedException {

        warn("Rick Sanchez").addParameter("catchPhrase", "Wubba lubba dub dub!").log();

        info("Rick Sanchez").addParameter("catchPhrase", "Wubba lubba dub dub!").log();

        debug("Rick Sanchez").addParameter("catchPhrase", "Wubba lubba dub dub!").log();

        error("Rick Sanchez").log();

        LocalDateTime s = LocalDateTime.now();
        Thread.sleep(1000 * 3);
    }
}
