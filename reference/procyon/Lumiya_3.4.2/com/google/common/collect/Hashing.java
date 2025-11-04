// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class Hashing
{
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static int MAX_TABLE_SIZE;
    
    static {
        Hashing.MAX_TABLE_SIZE = 1073741824;
    }
    
    private Hashing() {
    }
    
    static int closedTableSize(int n, final double n2) {
        n = Math.max(n, 2);
        final int highestOneBit = Integer.highestOneBit(n);
        if (n <= (int)(highestOneBit * n2)) {
            return highestOneBit;
        }
        if ((n = highestOneBit << 1) <= 0) {
            n = Hashing.MAX_TABLE_SIZE;
        }
        return n;
    }
    
    static boolean needsResizing(final int n, final int n2, final double n3) {
        return n > n2 * n3 && n2 < Hashing.MAX_TABLE_SIZE;
    }
    
    static int smear(final int n) {
        return Integer.rotateLeft(-862048943 * n, 15) * 461845907;
    }
    
    static int smearedHash(@Nullable final Object o) {
        int hashCode;
        if (o != null) {
            hashCode = o.hashCode();
        }
        else {
            hashCode = 0;
        }
        return smear(hashCode);
    }
}
