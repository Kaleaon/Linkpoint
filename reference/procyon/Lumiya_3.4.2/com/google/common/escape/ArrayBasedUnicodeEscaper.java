// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import java.util.Map;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class ArrayBasedUnicodeEscaper extends UnicodeEscaper
{
    private final char[][] replacements;
    private final int replacementsLength;
    private final int safeMax;
    private final char safeMaxChar;
    private final int safeMin;
    private final char safeMinChar;
    
    protected ArrayBasedUnicodeEscaper(final ArrayBasedEscaperMap arrayBasedEscaperMap, int safeMin, int n, @Nullable final String s) {
        Preconditions.checkNotNull(arrayBasedEscaperMap);
        this.replacements = arrayBasedEscaperMap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (n < safeMin) {
            n = -1;
            safeMin = Integer.MAX_VALUE;
        }
        this.safeMin = safeMin;
        this.safeMax = n;
        if (safeMin < 55296) {
            this.safeMinChar = (char)safeMin;
            this.safeMaxChar = (char)Math.min(n, 55295);
        }
        else {
            this.safeMinChar = 65535;
            this.safeMaxChar = 0;
        }
    }
    
    protected ArrayBasedUnicodeEscaper(final Map<Character, String> map, final int n, final int n2, @Nullable final String s) {
        this(ArrayBasedEscaperMap.create(map), n, n2, s);
    }
    
    @Override
    public final String escape(final String s) {
        Preconditions.checkNotNull(s);
        int i = 0;
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (char1 >= this.replacementsLength || this.replacements[char1] == null) {
                if (char1 <= this.safeMaxChar && char1 >= this.safeMinChar) {
                    ++i;
                    continue;
                }
            }
            return this.escapeSlow(s, i);
        }
        return s;
    }
    
    @Override
    protected final char[] escape(final int n) {
        if (n < this.replacementsLength) {
            final char[] array = this.replacements[n];
            if (array != null) {
                return array;
            }
        }
        if (n >= this.safeMin && n <= this.safeMax) {
            return null;
        }
        return this.escapeUnsafe(n);
    }
    
    protected abstract char[] escapeUnsafe(final int p0);
    
    @Override
    protected final int nextEscapeIndex(final CharSequence charSequence, int i, final int n) {
        while (i < n) {
            final char char1 = charSequence.charAt(i);
            if (char1 < this.replacementsLength && this.replacements[char1] != null) {
                break;
            }
            if (char1 > this.safeMaxChar || char1 < this.safeMinChar) {
                break;
            }
            ++i;
        }
        return i;
    }
}
