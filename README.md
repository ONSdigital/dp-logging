#dp-logging

Uses logback and provides extendable log message builder for structured logging.

## Example
Extend the `LogMessageBuilder`:
```java
public class LogBuilderExample extends LogMessageBuilder {

    /**
     * static convenience method for info level logging.
     */
    public static LogBuilderExample info(String message) {
        return new LogBuilderExample(message);
    }

    private LogBuilderExample(String eventDescription) {
        super(eventDescription);
    }

    private LogBuilderExample(String description, Level logLevel) {
        super(description, logLevel);
    }

    private LogBuilderExample(Throwable t, Level level, String description) {
        super(t, level, description);
    }

    private LogBuilderExample(Throwable t, String description) {
        super(t, description);
    }

    @Override
    public String getLoggerName() {
        return "com.test";
    }
}
```

And add a logback.xml file to your classpath

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="DP_LOGGER" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="com.github.onsdigital.logging.layouts.ConfigurableLayout"/>
        </encoder>
    </appender>

    <logger name="com.test" level="debug" additivity="false">
        <appender-ref ref="DP_LOGGER"/>
    </logger>

    <root level="info">
        <appender-ref ref="DP_LOGGER"/>
    </root>
</configuration>
```

See [logback config](https://logback.qos.ch/manual/configuration.html) for more.

## Logging something

```
info("Rick Sanchez").addParameter("catchPhrase", "Wubba lubba dub dub!").log();
```



## Configuration
### Format
The default is standard plain text logging:

```
[dp-logging] INFO 2018-02-02T13:25:48.813+0000 [pool-1-thread-1] com.test : Rick Sanchez parameters={catchPhrase=Wubba lubba dub dub!}
```

You can also enable json logging:
 
 ```json
 {"time":"2018-02-02T13:26:15.115+0000","level":"INFO","name":"com.test","thread":"pool-1-thread-1","description":"Rick Sanchez","parameters":{"catchPhrase":"Wubba lubba dub dub!"}}
 ```
 
 or pretty json logging
 
 ```json
{
  "time" : "2018-02-02T13:26:59.270+0000",
  "level" : "INFO",
  "name" : "com.test",
  "thread" : "pool-1-thread-1",
  "description" : "Rick Sanchez",
  "parameters" : {
    "catchPhrase" : "Wubba lubba dub dub!"
  }
}
 ```

To change the logging format simply set an env var `DP_LOGGING_FORMAT` to `text`, `json` or `pretty_json`.

###Colour logging
By default coloured logging is not enabled but if you would like to enabled it simply setan env var 
`DP_COLOURED_LOGGING` to `true`.

