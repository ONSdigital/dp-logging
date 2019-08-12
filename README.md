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

As previously stated dp-logging is a wrapper around logback so we need to create a logback.xml to set our configuration.
 Create a `logback.xml` file on your class path - typically it lives here: 
```
/src/main/resources
```
You can use the [example-logback.xml](src/main/resources/example-logback.xml) as starting point.

The example logback.xml defines 2 `appenders`:

```xml
<appender name="DP_LOGGER" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%msg%n</pattern>
    </encoder>
</appender>
```

The `DP_LOGGER` appender will be used by your application. Internally dp-logging will convert each log event into a JSON
 string compliant with the DP logging standards. Log events created by your app will already be correctly formatted 
 so the appender simply writes them to the console.


```xml
<appender name="THIRD_PARTY" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
        <layout class="com.github.onsdigital.logging.v2.layout.ThirdPartyEventLayout">
            <Pattern>%n%msg</Pattern>
        </layout>
    </encoder>
</appender>
```

The `THIRD_PARTY` appender is used to capture any log event with a namespace different from your application. The 
appender captures the raw event and coverts it to a JSON string complying wit the DP logging spec. For example if your 
app uses a third party library any log output it generates be captured and formatted before it it written - ensuring 
all log events are consistent.

Finally the example logback.xml defines:
 - A root logger which uses the `THIRD_PARTY` appender.
 - A logger for our application using the `DP_LOGGER` appender. 
 
### Programatic configuration
_Note:_ See the [examples](src/main/java/com/github/onsdigital/logging/v2/examples) package for the full version of 
the snippets used here

Before you can use the logger in your code you have to set up a few things. Its recommended you do this as part of your 
application initalisation.

Firstly we need to create a `LogConfig` which sets which versions on the logger depencencies we want to use. 
 
```java
        LogSerialiser serialiser = new JacksonLogSerialiser(true);
        Logger logger = new LoggerImpl("logging-example");
        LogStore logStore = new MDCLogStore(serialiser);
        ShutdownHook shutdownHook = new NopShutdownHook();

        LogConfig config = new Builder()
                .serialiser(serialiser)
                .logger(logger)
                .logStore(logStore)
                .shutdownHook(shutdownHook)
                .dataNamespace("logging-example-data")
                .create();
```
 - `LogSerialiser` Responsible for marshalling our log event Object to a JSON string
 - `Logger` - The logging implementation to use - sets the namespace for our log messages
 - `LogStore` - An object for storing certain common values in `ThreadLocal`. Typcially reserved for details we want in
  every message (_e.g._ traceID) but don't want to have to explicitly pass them around everywhere within our application
 - `ShutdownHook` - A hook allowing you to do any clean up on shutdown - closing resources etc. Here we are using a 
 Nop implmentation
 
 Finally we init `DPLogger` with the `LogConfig` instance we created:
 
 ```java
DPLogger.init(config);
```

Unless explicitly configured DPLogger will use ```java com.github.onsdigital.logging.v2.nop.NopConfig``` by default - 
essentially the same as sending log events to `>/dev/null`. This handy when unit testing any classes using dp-logger - 
the nop default means you are not required to set up the logger in each test class.

### Using the logger

We create and log events by creating a new instance of `com.github.onsdigital.logging.v2.event.SimpleEvent` then 
use the appropriate helper setter methods to add additional details to the event. `SimpleEvent` implements a builder 
pattern allowing you to chain method calls together to build up your event. Once all details have been added we invoke 
`log(...)` providing a event message which triggers the event to be logged. 

**Note**: You must invoke ```.log(....)``` or the event will not be output. 

`SimpleEvent` comes with several static methods for creating new log events for each logging level to reduce the 
overhead of logging code in your application.
 
 ```java
info();

warn();

error();

fatal();
```
 
 It also provides several setters for common fields and `data(K, V)` allows you to add key value pairs if there is no
  appropriate setter for the data you want to add. 

#### Examples: Using SimpleEvent
````java
import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
...
...
    info()
        .traceID("1234")
        .serviceIdentity("batman")
        .data("key", "value")
        .log("info logging");
...
...
    error().traceID("1234")
            .serviceIdentity("batman")
            .data("key", "value")
            .exception(new RuntimeException("example exception"))
            .log("error logging with exception");
````

Which create log output:
````json
{
  "created_at" : "2019-08-09T12:36:01.278Z",
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
  "created_at" : "2019-08-09T12:36:01.415Z",
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
      "file" : "com.github.onsdigital.logging.v2.examples.Example",
      "function" : "main",
      "line" : 48
    } ]
  }
}
````

### Extending SimpleEvent
As previously mentioned you can use `SimpleEvent.data(K, V)` to add key value pairs to a log event if there is no 
appropriate setter. However, if you have bespoke events fields that are likely to be included many times throughout your
application you can extend `com.github.onsdigital.logging.v2.event.BaseEvent` and add your own convenience methods.

Example:
````java
package com.github.onsdigital.logging.example;

import com.github.onsdigital.logging.v2.event.BaseEvent;
import com.github.onsdigital.logging.v2.event.Severity;
import org.apache.commons.lang3.StringUtils;

import static com.github.onsdigital.logging.v2.DPLogger.logConfig;

public class CustomEvent extends BaseEvent<CustomEvent> {

    protected CustomEvent(Severity severity) {
        super(logConfig().getNamespace(), severity, logConfig().getLogStore());
    }

    public static CustomEvent warn() {
        return new CustomEvent(Severity.WARN);
    }

    public static CustomEvent info() {
        return new CustomEvent(Severity.INFO);
    }

    public static CustomEvent error() {
        return new CustomEvent(Severity.ERROR);
    }

    public CustomEvent myField(String value) {
        if (StringUtils.isNotEmpty(value))
            data("myField", value);
        return this;
    }
}
````

Using your custom event:
```java
    CustomEvent.info().myField("batman").traceID("1234").log("customized event");
```

Output:
```json
{
  "created_at" : "2019-08-09T12:36:01.438Z",
  "namespace" : "logging-example",
  "severity" : 3,
  "event" : "customized event",
  "trace_id" : "1234",
  "data" : {
    "myField" : "batman"
  },
  "auth" : {
    "identity" : "batman",
    "identity_type" : "service"
  }
}
```