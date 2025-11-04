// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import java.util.Map;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class ArrayBasedCharEscaper extends CharEscaper
{
    private final char[][] replacements;
    private final int replacementsLength;
    private final char safeMax;
    private final char safeMin;
    
    protected ArrayBasedCharEscaper(final ArrayBasedEscaperMap arrayBasedEscaperMap, char c, char c2) {
        Preconditions.checkNotNull(arrayBasedEscaperMap);
        this.replacements = arrayBasedEscaperMap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (c2 < c) {
            c2 = '\0';
            c = '\uffff';
        }
        this.safeMin = c;
        this.safeMax = c2;
    }
    
    protected ArrayBasedCharEscaper(final Map<Character, String> map, final char c, final char c2) {
        this(ArrayBasedEscaperMap.create(map), c, c2);
    }
    
    @Override
    public final String escape(final String s) {
        Preconditions.checkNotNull(s);
        int i = 0;
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (char1 >= this.replacementsLength || this.replacements[char1] == null) {
                if (char1 <= this.safeMax && char1 >= this.safeMin) {
                    ++i;
                    continue;
                }
            }
            return this.escapeSlow(s, i);
        }
        return s;
    }
    
    @Override
    protected final char[] escape(final char c) {
        if (c < this.replacementsLength) {
            final char[] array = this.replacements[c];
            if (array != null) {
                return array;
            }
        }
        if (c >= this.safeMin && c <= this.safeMax) {
            return null;
        }
        return this.escapeUnsafe(c);
    }
    
    protected abstract char[] escapeUnsafe(final char p0);
}
