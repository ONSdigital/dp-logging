package com.github.onsdigital.logging.v2.serializer;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class CustomPrettyPrinter extends DefaultPrettyPrinter {

    public CustomPrettyPrinter() {
        _arrayIndenter = Lf2SpacesIndenter.instance;
    }
}
