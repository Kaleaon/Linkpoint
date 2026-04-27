package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public class ExecutionError extends Error {
    private static final long serialVersionUID = 0;

    protected ExecutionError() {
    }

    public ExecutionError(@Nullable Error error) {
        super(error);
    }

    protected ExecutionError(@Nullable String str) {
        super(str);
    }

    public ExecutionError(@Nullable String str, @Nullable Error error) {
        super(str, error);
    }
}
