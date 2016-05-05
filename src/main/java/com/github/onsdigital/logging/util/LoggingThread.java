package com.github.onsdigital.logging.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a {@link ThreadLocal} {@link Map} for convenient storage of logging information.
 */
public final class LoggingThread {

    public static final String REQUEST_ID_KEY = "request-id";
    public static final String REMOTE_HOST = "host";

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    public static Map<String, Object> get() {
        return threadLocal.get();
    }

    public static void put(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    public static String get(String key) {
        return (String)threadLocal.get().get(key);
    }

}
