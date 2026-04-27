// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform
{
    private Platform() {
    }
    
    static <T extends Enum<T>> Optional<T> getEnumIfPresent(final Class<T> clazz, final String s) {
        final WeakReference weakReference = Enums.getEnumConstants(clazz).get(s);
        Serializable s2;
        if (weakReference != null) {
            s2 = Optional.of(clazz.cast(weakReference.get()));
        }
        else {
            s2 = Optional.absent();
        }
        return (Optional<T>)s2;
    }
    
    static CharMatcher precomputeCharMatcher(final CharMatcher charMatcher) {
        return charMatcher.precomputedInternal();
    }
    
    static long systemNanoTime() {
        return System.nanoTime();
    }
}
