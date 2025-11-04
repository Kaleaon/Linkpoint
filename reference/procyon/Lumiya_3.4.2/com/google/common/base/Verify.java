// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class Verify
{
    private Verify() {
    }
    
    public static void verify(final boolean b) {
        if (b) {
            return;
        }
        throw new VerifyException();
    }
    
    public static void verify(final boolean b, @Nullable final String s, @Nullable final Object... array) {
        if (b) {
            return;
        }
        throw new VerifyException(Preconditions.format(s, array));
    }
    
    public static <T> T verifyNotNull(@Nullable final T t) {
        return verifyNotNull(t, "expected a non-null reference", new Object[0]);
    }
    
    public static <T> T verifyNotNull(@Nullable final T t, @Nullable final String s, @Nullable final Object... array) {
        verify(t != null, s, array);
        return t;
    }
}
