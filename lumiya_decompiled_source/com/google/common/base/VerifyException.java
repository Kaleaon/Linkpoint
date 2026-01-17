package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;
@Beta
@GwtCompatible
/* loaded from: classes.dex */
public class VerifyException extends RuntimeException {
    public VerifyException() {
    }

    public VerifyException(@Nullable String str) {
        super(str);
    }

    public VerifyException(@Nullable String str, @Nullable Throwable th) {
        super(str, th);
    }

    public VerifyException(@Nullable Throwable th) {
        super(th);
    }
}
