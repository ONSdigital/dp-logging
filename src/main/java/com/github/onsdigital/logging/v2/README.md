# Version 2.

## Example 1
Hello world spark API example that logs "request receieved" from a pre handler fileter and "request compeleted" from 
a post handler filter.

```java
public class ExampleApp {

    public static void main(String[] args) throws Exception {
        // Initialise the logger
        DPLogger.init(LoggerFactory.getLogger("com.test.app"), new JacksonEventSerialiser());

        // Set up a pre handler filter
        before((req, resp) -> {
            // capture the trace_id so it can be automatically added to any logEvents created on this thread
            LogEventUtil.setTraceID(req.raw()); 
            // log an event.
            logInfo().beginHTTP(req.raw()).log("request received");
        });

        // Set up a post handler filter which logs a request has compeletd.
        after((req, resp) -> logInfo().endHTTP(req.raw(), resp.raw()).log("request completed"));

        // very basic handler that waits 5 seconds before completing.
        get("/hello", (req, resp) -> {
            Thread.sleep(5000);
            resp.status(200);
            return "Hello!";
        });
    }
}
``` 
