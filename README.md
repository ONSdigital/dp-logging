# dp-logging

## V2

Logging library for DP Java apps. Library adds a wrapper around Logback that formats all logs events into a JSON 
string that conforms to DP logging standards specification. 

### Getting started

Add the maven dependency to your `pom.xml`.
```
<dependency>
    <groupId>com.github.ONSdigital</groupId>
    <artifactId>dp-logging</artifactId>
    <version>${VERSION_HERE}</version>
</dependency>
```

### Logback configuration

As previously stated dp-logging is a wrapper around logback so we need to create a logback.xml to set our 
configuration. Create a `logback.xml` file on your class path - typically it lives under: 
```
/src/main/resources
```
You can use the example logback.xml as starting point.

The example logback.xml defines 2 `appenders`:

```xml
<appender name="DP_LOGGER" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%msg%n</pattern>
    </encoder>
</appender>
```

The `DP_LOGGER` appender is the appender to be used by your application. Internally dp-logging will convert each log 
event into a JSON string following to the DP logging standards specification. In this case the event has already been 
formatted so the appender simply writes it to the console.


```xml
<appender name="THIRD_PARTY" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
        <layout class="com.github.onsdigital.logging.v2.layout.ThirdPartyEventLayout">
            <Pattern>%n%msg</Pattern>
        </layout>
    </encoder>
</appender>
```

The `THIRD_PARTY` appender is used to capture any log event that has a different namespace from your 
application and covert it into DP logging format. For example if your app uses the 
Spark Java REST framework any event logged from within the framwork  will be captured 
and formatted to be consistent with our logging output.

Finally the example logback defines:
 - A root logger which uses the `THIRD_PARTY` appender
 - A logger for our application using the `DP_LOGGER` appender.
 
### Programatic configuration

First we need to create a `LogConfig`. Its recommended you do this as part of your application initalisation. Below is 
a typical example using the `Builder` to generate our config:
 
```java
    static LogConfig getLogConfig() throws Exception {
        LogSerialiser serialiser = new JacksonLogSerialiser(true);
        Logger logger = new LoggerImpl("logging-example");
        LogStore logStore = new MDCLogStore(serialiser);
        ShutdownHook shutdownHook = new NopShutdownHook();

        return new Builder()
                .serialiser(serialiser)
                .logger(logger)
                .logStore(logStore)
                .shutdownHook(shutdownHook)
                .dataNamespace("logging-example-data")
                .create();
    }
```
 - `LogSerialiser` Responsible for marshalling our log event Object to a JSON string.
 - `Logger` - The logging implementation to use - sets the namespace for our log messages.
 - `LogStore` - An object for storing values in ThreadLocal so they do not have to be passed into every log message.
 - `ShutdownHook` - A hook allowing you to do any clean up on shutdown - closing resources etc. Here we are using a 
 Nop implmentation
 
 Finally we init `DPLogger` with `LogConfig` instance we created:
 
 ```java
DPLogger.init(config);
```

Unless explicitly configured DPLogger will use ```java com.github.onsdigital.logging.v2.nop.NopConfig``` by default. 
As the name suggests is a nop impl essentially the same as sending log events to `>/dev/null`.

### Using the logger

We create and log events by creating a new instance of `com.github.onsdigital.logging.v2.event.SimpleEvent` then 
use the appropriate helper setter methods to add additional details to the event. `SimpleEvent` implements a builder 
pattern allowing you to chain method calls together to build up your event. Once all details have been added we invoke 
`log(...)` providing a event message and the the event is sent to our logger.

`SimpleEvent` comes with several static methods for creating new log events for each logging level. It also provides 
several setters for common fields, additionally you can use `data(K, V)` to add a key value pair if you need to add 
any details that do not have specific setter methods.

The following is an example of logging an info and error event:   
````java
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
...
...
    info()
        .traceID("1234")
        .serviceIdentity("batman")
        .data("key", "value")
        .log("info logging");

    error().traceID("1234")
            .serviceIdentity("batman")
            .data("key", "value")
            .exception(new RuntimeException("example exception"))
            .log("error logging with exception");
````

Which create log output:
````json
{
  "created_at" : "2019-08-09T11:49:41.365Z",
  "namespace" : "logging-example",
  "severity" : 3,
  "event" : "info logging",
  "trace_id" : "1234",
  "data" : {
    "key" : "value"
  },
  "auth" : {
    "identity" : "batman",
    "identity_type" : "service"
  }
}
{
  "created_at" : "2019-08-09T11:55:08.369Z",
  "namespace" : "logging-example",
  "severity" : 1,
  "event" : "error logging with exception",
  "trace_id" : "1234",
  "data" : {
    "key" : "value"
  },
  "auth" : {
    "identity" : "batman",
    "identity_type" : "service"
  },
  "error" : {
    "message" : "java.lang.RuntimeException: example exception",
    "stack_trace" : [ {
      "file" : "com.github.onsdigital.logging.example.Example",
      "function" : "main",
      "line" : 56
    } ]
  }
}
````