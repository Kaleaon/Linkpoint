package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public class ComputationException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public ComputationException(@Nullable Throwable th) {
        super(th);
    }
}
