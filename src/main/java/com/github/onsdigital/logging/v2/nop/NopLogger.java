package com.github.onsdigital.logging.v2.nop;

import com.github.onsdigital.logging.v2.Logger;

public class NopLogger implements Logger {

    @Override
    public void error(String event) {

    }

    @Override
    public void warn(String event) {

    }

    @Override
    public void info(String event) {

    }

    @Override
    public String getName() {
        return "";
    }
}
