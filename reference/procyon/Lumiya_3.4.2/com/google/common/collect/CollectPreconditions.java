// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class CollectPreconditions
{
    static void checkEntryNotNull(final Object obj, final Object obj2) {
        if (obj == null) {
            throw new NullPointerException("null key in entry: null=" + obj2);
        }
        if (obj2 != null) {
            return;
        }
        throw new NullPointerException("null value in entry: " + obj + "=null");
    }
    
    static int checkNonnegative(final int i, final String str) {
        if (i >= 0) {
            return i;
        }
        throw new IllegalArgumentException(str + " cannot be negative but was: " + i);
    }
    
    static void checkPositive(final int i, final String str) {
        if (i > 0) {
            return;
        }
        throw new IllegalArgumentException(str + " must be positive but was: " + i);
    }
    
    static void checkRemove(final boolean b) {
        Preconditions.checkState(b, (Object)"no calls to next() since the last call to remove()");
    }
}
