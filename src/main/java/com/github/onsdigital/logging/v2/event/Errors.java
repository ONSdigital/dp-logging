package com.github.onsdigital.logging.v2.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public List<Error> getErrors() {
        return this.errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Errors errors1 = (Errors) o;

        return new EqualsBuilder()
                .append(getErrors(), errors1.getErrors())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getErrors())
                .toHashCode();
    }
}
