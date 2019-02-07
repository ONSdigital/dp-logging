package com.github.onsdigital.logging.v2.config.nop;


        import com.github.onsdigital.logging.v2.config.Logger;

public class NopLogger implements Logger {

    @Override
    public void info(String event) {

    }

    @Override
    public String getName() {
        return "";
    }
}
