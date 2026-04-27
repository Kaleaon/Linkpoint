// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class CharEscaperBuilder
{
    private final Map<Character, String> map;
    private int max;
    
    public CharEscaperBuilder() {
        this.max = -1;
        this.map = new HashMap<Character, String>();
    }
    
    public CharEscaperBuilder addEscape(final char c, final String s) {
        this.map.put(c, Preconditions.checkNotNull(s));
        if (c > this.max) {
            this.max = c;
        }
        return this;
    }
    
    public CharEscaperBuilder addEscapes(final char[] array, final String s) {
        Preconditions.checkNotNull(s);
        for (int length = array.length, i = 0; i < length; ++i) {
            this.addEscape(array[i], s);
        }
        return this;
    }
    
    public char[][] toArray() {
        final char[][] array = new char[this.max + 1][];
        for (final Map.Entry<Character, V> entry : this.map.entrySet()) {
            array[(char)entry.getKey()] = ((String)entry.getValue()).toCharArray();
        }
        return array;
    }
    
    public Escaper toEscaper() {
        return new CharArrayDecorator(this.toArray());
    }
    
    private static class CharArrayDecorator extends CharEscaper
    {
        private final int replaceLength;
        private final char[][] replacements;
        
        CharArrayDecorator(final char[][] replacements) {
            this.replacements = replacements;
            this.replaceLength = replacements.length;
        }
        
        @Override
        public String escape(final String s) {
            for (int length = s.length(), i = 0; i < length; ++i) {
                final char char1 = s.charAt(i);
                if (char1 < this.replacements.length && this.replacements[char1] != null) {
                    return this.escapeSlow(s, i);
                }
            }
            return s;
        }
        
        @Override
        protected char[] escape(final char c) {
            char[] array;
            if (c >= this.replaceLength) {
                array = null;
            }
            else {
                array = this.replacements[c];
            }
            return array;
        }
    }
}
