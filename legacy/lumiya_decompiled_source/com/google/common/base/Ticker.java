package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;
@Beta
@GwtCompatible
/* loaded from: classes.dex */
public abstract class Ticker {
    private static final Ticker SYSTEM_TICKER = new Ticker() { // from class: com.google.common.base.Ticker.1
        @Override // com.google.common.base.Ticker
        public long read() {
            return Platform.systemNanoTime();
        }
    };

    @CheckReturnValue
    public static Ticker systemTicker() {
        return SYSTEM_TICKER;
    }

    public abstract long read();
}
