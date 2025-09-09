package com.google.common.util.concurrent;

import javax.annotation.Nullable;

public class UncheckedTimeoutException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public UncheckedTimeoutException() {
    }

    public UncheckedTimeoutException(@Nullable String str) {
        super(str);
    }

    public UncheckedTimeoutException(@Nullable String str, @Nullable Throwable th) {
        super(str, th);
    }

    public UncheckedTimeoutException(@Nullable Throwable th) {
        super(th);
    }
}
