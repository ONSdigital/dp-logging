package com.github.onsdigital.logging.v2;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.util.Date;

public class Logger implements Loggable {

    static final String TRACE_ID_KEY = "trace_id";
    static final String SPAN_ID_KEY = "span_id";

    static Gson G;
    static PrintWriter PRINT_WRITER;
    static String NAME_SPACE;

    private String event;
    private Date created;
    private String namespace;
    private String traceID;
    private String spanID;
    private HTTP http;

    /**
     * Construct a new Logger and init the base field values.
     */
    Logger() {
        this.namespace = NAME_SPACE;
        this.created = new Date();
        this.http = new HTTP();
    }

    @Override
    public String namespace() {
        return NAME_SPACE;
    }

    public Loggable event(String event) {
        this.event = event;
        return this;
    }

    public Loggable traceID(String traceID) {
        this.traceID = traceID;
        return this;
    }

    public Loggable spanID(String spanID) {
        this.spanID = spanID;
        return this;
    }

    @Override
    public Loggable httpReq(HttpServletRequest req) {
        this.http.httpReq(req);
        return this;
    }

    @Override
    public Loggable httpMethod(String method) {
        this.http.method(method);
        return this;
    }

    @Override
    public Loggable httpPath(String path) {
        this.http.path(path);
        return this;
    }

    @Override
    public void log() {
        if (StringUtils.isEmpty(traceID)) {
            this.traceID = MDC.get(TRACE_ID_KEY);
        }

        if (StringUtils.isEmpty(spanID)) {
            this.spanID = MDC.get(SPAN_ID_KEY);
        }

        PRINT_WRITER.write(G.toJson(this) + "\n");
        PRINT_WRITER.flush();
    }

    /**
     * initialize the logger - MUST be initalized before use.
     *
     * @param g
     * @param printWriter
     * @param namespace
     */
    public static void init(Gson g, PrintWriter printWriter, String namespace) {
        G = g;
        PRINT_WRITER = printWriter;
        NAME_SPACE = namespace;

        registerShutdownHook();
    }

    static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                PRINT_WRITER.close();
            }
        });
    }

    public static Loggable logger() {
        return new Logger();
    }


}
