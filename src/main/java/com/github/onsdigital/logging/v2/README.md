# Version 2.

## Example 1
Hello world spark API example that logs "request receieved" from a pre handler fileter and "request compeleted" from 
a post handler filter.

```java
public class ExampleApp {

    public static void main(String[] args) throws Exception {
        System.setProperty("logback.configurationFile", "example-logback.xml");

        Logger logger = LoggerFactory.getLogger("com.test.app");
        LoggerConfig loggerConfig = new LoggerConfig(logger, new JacksonLogSerialiser(), "simple_app_data");
        DPLogger.init(loggerConfig);

        before((req, resp) -> info().beginHTTP(req.raw()).log("request received"));

        after((req, resp) -> info().endHTTP(resp.raw()).log("request completed"));

        get("/hello", (req, resp) -> {
            info().data("key1", "value1").data("key2", "value2").log("doing something....");
            resp.status(200);
            return "Hello!";
        });
    }
}
``` 
