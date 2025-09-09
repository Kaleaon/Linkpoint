package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.lumiyaviewer.lumiya.slproto.users.SLGroupInfo;
import javax.annotation.CheckReturnValue;

@GwtCompatible
@Beta
public final class Utf8 {
    private Utf8() {
    }

    @CheckReturnValue
    public static int encodedLength(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) < 128) {
            i++;
        }
        int i2 = i;
        int i3 = length;
        while (true) {
            if (i2 >= length) {
                break;
            }
            char charAt = charSequence.charAt(i2);
            if (charAt >= 2048) {
                i3 += encodedLengthGeneral(charSequence, i2);
                break;
            }
            i2++;
            i3 = ((127 - charAt) >>> 31) + i3;
        }
        if (i3 >= length) {
            return i3;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) i3) + SLGroupInfo.GP_LAND_ADMIN));
    }

    private static int encodedLengthGeneral(CharSequence charSequence, int i) {
        int length = charSequence.length();
        int i2 = 0;
        int i3 = i;
        while (i3 < length) {
            char charAt = charSequence.charAt(i3);
            if (charAt >= 2048) {
                i2 += 2;
                if (55296 <= charAt && charAt <= 57343) {
                    if (Character.codePointAt(charSequence, i3) != charAt) {
                        i3++;
                    } else {
                        throw new IllegalArgumentException(unpairedSurrogateMsg(i3));
                    }
                }
            } else {
                i2 += (127 - charAt) >>> 31;
            }
            i3++;
        }
        return i2;
    }

    @CheckReturnValue
    public static boolean isWellFormed(byte[] bArr) {
        return isWellFormed(bArr, 0, bArr.length);
    }

    @CheckReturnValue
    public static boolean isWellFormed(byte[] bArr, int i, int i2) {
        int i3 = i + i2;
        Preconditions.checkPositionIndexes(i, i3, bArr.length);
        while (i < i3) {
            if (bArr[i] < 0) {
                return isWellFormedSlowPath(bArr, i, i3);
            }
            i++;
        }
        return true;
    }

    private static boolean isWellFormedSlowPath(byte[] bArr, int i, int i2) {
        int i3;
        while (i < i2) {
            int i4 = i + 1;
            byte b = bArr[i];
            if (b >= 0) {
                i = i4;
            } else {
                if (b < -32) {
                    if (i4 != i2 && b >= -62) {
                        i3 = i4 + 1;
                        if (bArr[i4] > -65) {
                        }
                    }
                    return false;
                } else if (b >= -16) {
                    if (i4 + 2 >= i2) {
                        return false;
                    }
                    int i5 = i4 + 1;
                    byte b2 = bArr[i4];
                    if (b2 <= -65 && (((b << Ascii.FS) + (b2 + 112)) >> 30) == 0) {
                        int i6 = i5 + 1;
                        if (bArr[i5] <= -65) {
                            i3 = i6 + 1;
                            if (bArr[i6] > -65) {
                            }
                        }
                    }
                    return false;
                } else if (i4 + 1 >= i2) {
                    return false;
                } else {
                    int i7 = i4 + 1;
                    byte b3 = bArr[i4];
                    if (b3 <= -65 && ((b != -32 || b3 >= -96) && (b != -19 || -96 > b3))) {
                        i3 = i7 + 1;
                        if (bArr[i7] > -65) {
                        }
                    }
                    return false;
                }
                i = i3;
            }
        }
        return true;
    }

    private static String unpairedSurrogateMsg(int i) {
        return "Unpaired surrogate at index " + i;
    }
}
