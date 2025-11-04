// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class CharEscaper extends Escaper
{
    private static final int DEST_PAD_MULTIPLIER = 2;
    
    protected CharEscaper() {
    }
    
    private static char[] growBuffer(final char[] array, final int n, final int n2) {
        final char[] array2 = new char[n2];
        if (n > 0) {
            System.arraycopy(array, 0, array2, 0, n);
        }
        return array2;
    }
    
    @Override
    public String escape(final String s) {
        Preconditions.checkNotNull(s);
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (this.escape(s.charAt(i)) != null) {
                return this.escapeSlow(s, i);
            }
        }
        return s;
    }
    
    protected abstract char[] escape(final char p0);
    
    protected final String escapeSlow(final String s, int count) {
        final int length = s.length();
        char[] value = Platform.charBufferFromThreadLocal();
        int length2 = value.length;
        int n = 0;
        final int n2 = 0;
        int i = count;
        count = n2;
        while (i < length) {
            final char[] escape = this.escape(s.charAt(i));
            int n3 = n;
            int n4 = count;
            int n5 = length2;
            char[] array = value;
            if (escape != null) {
                final int length3 = escape.length;
                final int n6 = i - n;
                final int n7 = count + n6 + length3;
                if (length2 < n7) {
                    length2 = (length - i) * 2 + n7;
                    value = growBuffer(value, count, length2);
                }
                if (n6 > 0) {
                    s.getChars(n, i, value, count);
                    count += n6;
                }
                if (length3 > 0) {
                    System.arraycopy(escape, 0, value, count, length3);
                    count += length3;
                }
                n3 = i + 1;
                array = value;
                n5 = length2;
                n4 = count;
            }
            ++i;
            n = n3;
            count = n4;
            length2 = n5;
            value = array;
        }
        final int n8 = length - n;
        if (n8 > 0) {
            final int n9 = n8 + count;
            if (length2 < n9) {
                value = growBuffer(value, count, n9);
            }
            s.getChars(n, length, value, count);
            count = n9;
        }
        return new String(value, 0, count);
    }
}
