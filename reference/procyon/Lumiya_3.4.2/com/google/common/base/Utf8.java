// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.CheckReturnValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class Utf8
{
    private Utf8() {
    }
    
    @CheckReturnValue
    public static int encodedLength(final CharSequence charSequence) {
        int length;
        int n;
        for (length = charSequence.length(), n = 0; n < length && charSequence.charAt(n) < '\u0080'; ++n) {}
        int i;
        int n2;
        char char1;
        for (i = n, n2 = length; i < length; ++i, n2 += '\u007f' - char1 >>> 31) {
            char1 = charSequence.charAt(i);
            if (char1 >= '\u0800') {
                n2 += encodedLengthGeneral(charSequence, i);
                break;
            }
        }
        if (n2 >= length) {
            return n2;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (n2 + 4294967296L));
    }
    
    private static int encodedLengthGeneral(final CharSequence seq, int i) {
        final int length = seq.length();
        int n = 0;
        while (i < length) {
            final char char1 = seq.charAt(i);
            int n3;
            if (char1 >= '\u0800') {
                final int n2 = n + 2;
                if ('\ud800' > char1) {
                    n = n2;
                    n3 = i;
                }
                else {
                    n3 = i;
                    n = n2;
                    if (char1 <= '\udfff') {
                        if (Character.codePointAt(seq, i) == char1) {
                            throw new IllegalArgumentException(unpairedSurrogateMsg(i));
                        }
                        n3 = i + 1;
                        n = n2;
                    }
                }
            }
            else {
                n += '\u007f' - char1 >>> 31;
                n3 = i;
            }
            i = n3 + 1;
        }
        return n;
    }
    
    @CheckReturnValue
    public static boolean isWellFormed(final byte[] array) {
        return isWellFormed(array, 0, array.length);
    }
    
    @CheckReturnValue
    public static boolean isWellFormed(final byte[] array, int i, int n) {
        n += i;
        Preconditions.checkPositionIndexes(i, n, array.length);
        while (i < n) {
            if (array[i] < 0) {
                return isWellFormedSlowPath(array, i, n);
            }
            ++i;
        }
        return true;
    }
    
    private static boolean isWellFormedSlowPath(final byte[] array, int i, final int n) {
        while (i < n) {
            final int n2 = i + 1;
            i = array[i];
            if (i >= 0) {
                i = n2;
            }
            else if (i >= -32) {
                if (i >= -16) {
                    if (n2 + 2 < n) {
                        final int n3 = n2 + 1;
                        final byte b = array[n2];
                        if (b <= -65 && (i << 28) + (b + 112) >> 30 == 0) {
                            final int n4 = n3 + 1;
                            if (array[n3] <= -65) {
                                i = n4 + 1;
                                if (array[n4] <= -65) {
                                    continue;
                                }
                            }
                        }
                        return false;
                    }
                    return false;
                }
                else {
                    if (n2 + 1 < n) {
                        final int n5 = n2 + 1;
                        final byte b2 = array[n2];
                        if (b2 <= -65) {
                            if (i != -32 || b2 >= -96) {
                                if (i != -19 || -96 > b2) {
                                    i = n5 + 1;
                                    if (array[n5] <= -65) {
                                        continue;
                                    }
                                }
                            }
                        }
                        return false;
                    }
                    return false;
                }
            }
            else {
                if (n2 != n) {
                    if (i >= -62) {
                        i = n2 + 1;
                        if (array[n2] <= -65) {
                            continue;
                        }
                    }
                    return false;
                }
                return false;
            }
        }
        return true;
    }
    
    private static String unpairedSurrogateMsg(final int i) {
        return "Unpaired surrogate at index " + i;
    }
}
