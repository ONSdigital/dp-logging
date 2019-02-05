package com.github.onsdigital.logging.v2.event;

        import java.util.HashMap;

        import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SafeMap extends HashMap<String, Object> {

    @Override
    public Object put(String key, Object value) {
        if (isNotEmpty(key) && value != null) {
            super.put(key, value);
        }
        return value;
    }

    public <T> T get(String key, Class<T> tClass) {
        if (containsKey(key)) {
            Object o = get(key);

            if (tClass.isAssignableFrom(o.getClass())) {
                return (T) o;
            }
        }
        return null;
    }
}
