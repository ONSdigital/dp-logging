package com.github.onsdigital.logging.v2.config.nop;

import com.github.onsdigital.logging.v2.config.ShutdownHook;

public class NopShutdownHook implements ShutdownHook {
    @Override
    public void shutdown() {
    }
}
