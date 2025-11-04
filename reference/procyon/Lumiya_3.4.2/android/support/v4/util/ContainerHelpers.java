// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

class ContainerHelpers
{
    static final int[] EMPTY_INTS;
    static final long[] EMPTY_LONGS;
    static final Object[] EMPTY_OBJECTS;
    
    static {
        EMPTY_INTS = new int[0];
        EMPTY_LONGS = new long[0];
        EMPTY_OBJECTS = new Object[0];
    }
    
    static int binarySearch(final int[] array, int i, final int n) {
        final int n2 = 0;
        final int n3 = i - 1;
        i = n2;
        int n4 = n3;
        while (i <= n4) {
            final int n5 = i + n4 >>> 1;
            final int n6 = array[n5];
            if (n6 >= n) {
                if (n6 <= n) {
                    return n5;
                }
                n4 = n5 - 1;
            }
            else {
                i = n5 + 1;
            }
        }
        return ~i;
    }
    
    static int binarySearch(final long[] array, int i, final long n) {
        int n2 = i - 1;
        i = 0;
        while (i <= n2) {
            final int n3 = i + n2 >>> 1;
            final long n4 = array[n3];
            int n5;
            if (n4 >= n) {
                n5 = 1;
            }
            else {
                n5 = 0;
            }
            if (n5 == 0) {
                i = n3 + 1;
            }
            else {
                int n6;
                if (n4 <= n) {
                    n6 = 1;
                }
                else {
                    n6 = 0;
                }
                if (n6 != 0) {
                    return n3;
                }
                n2 = n3 - 1;
            }
        }
        return ~i;
    }
    
    public static boolean equal(final Object o, final Object obj) {
        final boolean b = false;
        if (o != obj) {
            boolean b2 = b;
            if (o == null) {
                return b2;
            }
            if (!o.equals(obj)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    public static int idealByteArraySize(final int n) {
        for (int i = 4; i < 32; ++i) {
            if (n <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return n;
    }
    
    public static int idealIntArraySize(final int n) {
        return idealByteArraySize(n * 4) / 4;
    }
    
    public static int idealLongArraySize(final int n) {
        return idealByteArraySize(n * 8) / 8;
    }
}
