package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;
@GwtCompatible
/* loaded from: classes.dex */
final class Hashing {
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static int MAX_TABLE_SIZE = 1073741824;

    private Hashing() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int closedTableSize(int i, double d) {
        int max = Math.max(i, 2);
        int highestOneBit = Integer.highestOneBit(max);
        if (max <= ((int) (highestOneBit * d))) {
            return highestOneBit;
        }
        int i2 = highestOneBit << 1;
        return i2 <= 0 ? MAX_TABLE_SIZE : i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean needsResizing(int i, int i2, double d) {
        return ((double) i) > ((double) i2) * d && i2 < MAX_TABLE_SIZE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int smear(int i) {
        return Integer.rotateLeft(C1 * i, 15) * C2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int smearedHash(@Nullable Object obj) {
        return smear(obj != null ? obj.hashCode() : 0);
    }
}
