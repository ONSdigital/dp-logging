package com.github.onsdigital.logging;

import com.github.onsdigital.logging.LogBuilder;

/**
 * Created by dave on 5/4/16.
 */
public interface Loggable {

    LogBuilder getBuilder();

    String getName();
}
