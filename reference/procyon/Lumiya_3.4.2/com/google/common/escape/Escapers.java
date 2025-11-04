// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class Escapers
{
    private static final Escaper NULL_ESCAPER;
    
    static {
        NULL_ESCAPER = new CharEscaper() {
            @Override
            public String escape(final String s) {
                return Preconditions.checkNotNull(s);
            }
            
            @Override
            protected char[] escape(final char c) {
                return null;
            }
        };
    }
    
    private Escapers() {
    }
    
    static UnicodeEscaper asUnicodeEscaper(final Escaper escaper) {
        Preconditions.checkNotNull(escaper);
        if (escaper instanceof UnicodeEscaper) {
            return (UnicodeEscaper)escaper;
        }
        if (!(escaper instanceof CharEscaper)) {
            throw new IllegalArgumentException("Cannot create a UnicodeEscaper from: " + escaper.getClass().getName());
        }
        return wrap((CharEscaper)escaper);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static String computeReplacement(final CharEscaper charEscaper, final char c) {
        return stringOrNull(charEscaper.escape(c));
    }
    
    public static String computeReplacement(final UnicodeEscaper unicodeEscaper, final int n) {
        return stringOrNull(unicodeEscaper.escape(n));
    }
    
    public static Escaper nullEscaper() {
        return Escapers.NULL_ESCAPER;
    }
    
    private static String stringOrNull(final char[] value) {
        String s = null;
        if (value != null) {
            s = new String(value);
        }
        return s;
    }
    
    private static UnicodeEscaper wrap(final CharEscaper charEscaper) {
        return new UnicodeEscaper() {
            @Override
            protected char[] escape(int length) {
                final int n = 0;
                if (length < 65536) {
                    return charEscaper.escape((char)length);
                }
                final char[] dst = new char[2];
                Character.toChars(length, dst, 0);
                final char[] escape = charEscaper.escape(dst[0]);
                final char[] escape2 = charEscaper.escape(dst[1]);
                if (escape == null && escape2 == null) {
                    return null;
                }
                if (escape == null) {
                    length = 1;
                }
                else {
                    length = escape.length;
                }
                int length2;
                if (escape2 == null) {
                    length2 = 1;
                }
                else {
                    length2 = escape2.length;
                }
                final char[] array = new char[length2 + length];
                if (escape == null) {
                    array[0] = dst[0];
                }
                else {
                    for (int i = 0; i < escape.length; ++i) {
                        array[i] = escape[i];
                    }
                }
                int j = n;
                if (escape2 == null) {
                    array[length] = dst[1];
                }
                else {
                    while (j < escape2.length) {
                        array[length + j] = escape2[j];
                        ++j;
                    }
                }
                return array;
            }
        };
    }
    
    @Beta
    public static final class Builder
    {
        private final Map<Character, String> replacementMap;
        private char safeMax;
        private char safeMin;
        private String unsafeReplacement;
        
        private Builder() {
            this.replacementMap = new HashMap<Character, String>();
            this.safeMin = 0;
            this.safeMax = 65535;
            this.unsafeReplacement = null;
        }
        
        public Builder addEscape(final char c, final String s) {
            Preconditions.checkNotNull(s);
            this.replacementMap.put(c, s);
            return this;
        }
        
        public Escaper build() {
            return new ArrayBasedCharEscaper(this.replacementMap, this.safeMin, this.safeMax) {
                private final char[] replacementChars;
                
                {
                    char[] charArray;
                    if (Builder.this.unsafeReplacement == null) {
                        charArray = array;
                    }
                    else {
                        charArray = Builder.this.unsafeReplacement.toCharArray();
                    }
                    this.replacementChars = charArray;
                }
                
                @Override
                protected char[] escapeUnsafe(final char c) {
                    return this.replacementChars;
                }
            };
        }
        
        public Builder setSafeRange(final char c, final char c2) {
            this.safeMin = c;
            this.safeMax = c2;
            return this;
        }
        
        public Builder setUnsafeReplacement(@Nullable final String unsafeReplacement) {
            this.unsafeReplacement = unsafeReplacement;
            return this;
        }
    }
}
