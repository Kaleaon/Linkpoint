package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import java.util.BitSet;

@GwtIncompatible("no precomputation is done in GWT")
final class SmallCharMatcher extends CharMatcher.NamedFastMatcher {
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static final double DESIRED_LOAD_FACTOR = 0.5d;
    static final int MAX_SIZE = 1023;
    private final boolean containsZero;
    private final long filter;
    private final char[] table;

    private SmallCharMatcher(char[] cArr, long j, boolean z, String str) {
        super(str);
        this.table = cArr;
        this.filter = j;
        this.containsZero = z;
    }

    private boolean checkFilter(int i) {
        return 1 == ((this.filter >> i) & 1);
    }

    @VisibleForTesting
    static int chooseTableSize(int i) {
        if (i == 1) {
            return 2;
        }
        int highestOneBit = Integer.highestOneBit(i - 1);
        while (true) {
            highestOneBit <<= 1;
            if (((double) highestOneBit) * DESIRED_LOAD_FACTOR >= ((double) i)) {
                return highestOneBit;
            }
        }
    }

    static CharMatcher from(BitSet bitSet, String str) {
        int i;
        long j = 0;
        int cardinality = bitSet.cardinality();
        boolean z = bitSet.get(0);
        char[] cArr = new char[chooseTableSize(cardinality)];
        int length = cArr.length - 1;
        int nextSetBit = bitSet.nextSetBit(0);
        while (true) {
            int i2 = nextSetBit;
            if (i2 == -1) {
                return new SmallCharMatcher(cArr, j, z, str);
            }
            j |= 1 << i2;
            int smear = smear(i2);
            while (true) {
                i = smear & length;
                if (cArr[i] == 0) {
                    break;
                }
                smear = i + 1;
            }
            cArr[i] = (char) ((char) i2);
            nextSetBit = bitSet.nextSetBit(i2 + 1);
        }
    }

    static int smear(int i) {
        return Integer.rotateLeft(C1 * i, 15) * C2;
    }

    public boolean matches(char c) {
        if (c == 0) {
            return this.containsZero;
        }
        if (!checkFilter(c)) {
            return false;
        }
        int length = this.table.length - 1;
        int smear = smear(c) & length;
        int i = smear;
        while (this.table[i] != 0) {
            if (this.table[i] == c) {
                return true;
            }
            i = (i + 1) & length;
            if (i == smear) {
                return false;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setBits(BitSet bitSet) {
        if (this.containsZero) {
            bitSet.set(0);
        }
        for (char c : this.table) {
            if (c != 0) {
                bitSet.set(c);
            }
        }
    }
}
