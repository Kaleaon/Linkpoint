package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum BoundType {
    OPEN {
        /* access modifiers changed from: package-private */
        public BoundType flip() {
            return CLOSED;
        }
    },
    CLOSED {
        /* access modifiers changed from: package-private */
        public BoundType flip() {
            return OPEN;
        }
    };

    static BoundType forBoolean(boolean z) {
        return !z ? OPEN : CLOSED;
    }

    /* access modifiers changed from: package-private */
    public abstract BoundType flip();
}
