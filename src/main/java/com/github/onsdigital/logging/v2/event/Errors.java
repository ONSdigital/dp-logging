package com.github.onsdigital.logging.v2.event;

import java.util.ArrayList;
import java.util.List;

public class Errors {

    private List<Error> errors;

    public Errors(Throwable t) {
        this.errors = new ArrayList<>();
        addRecursive(t);
    }

    private void addRecursive(Throwable t) {
        if (null == t)
            return;

        addError(t);

        if (null == t.getCause())
            return;

        addRecursive(t.getCause());
    }

    private void addError(Throwable t) {
        errors.add(new Error(t));
    }
}
