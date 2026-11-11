package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
@Beta
@GwtCompatible
/* loaded from: classes.dex */
public final class Escapers {
    private static final Escaper NULL_ESCAPER = new CharEscaper() { // from class: com.google.common.escape.Escapers.1
        @Override // com.google.common.escape.CharEscaper, com.google.common.escape.Escaper
        public String escape(String str) {
            return (String) Preconditions.checkNotNull(str);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.common.escape.CharEscaper
        public char[] escape(char c) {
            return null;
        }
    };

    @Beta
    /* loaded from: classes.dex */
    public static final class Builder {
        private final Map<Character, String> replacementMap;
        private char safeMax;
        private char safeMin;
        private String unsafeReplacement;

        private Builder() {
            this.replacementMap = new HashMap();
            this.safeMin = (char) 0;
            this.safeMax = (char) 65535;
            this.unsafeReplacement = null;
        }

        public Builder addEscape(char c, String str) {
            Preconditions.checkNotNull(str);
            this.replacementMap.put(Character.valueOf(c), str);
            return this;
        }

        public Escaper build() {
            return new ArrayBasedCharEscaper(this.replacementMap, this.safeMin, this.safeMax) { // from class: com.google.common.escape.Escapers.Builder.1
                private final char[] replacementChars;

                {
                    this.replacementChars = Builder.this.unsafeReplacement != null ? Builder.this.unsafeReplacement.toCharArray() : null;
                }

                @Override // com.google.common.escape.ArrayBasedCharEscaper
                protected char[] escapeUnsafe(char c) {
                    return this.replacementChars;
                }
            };
        }

        public Builder setSafeRange(char c, char c2) {
            this.safeMin = c;
            this.safeMax = c2;
            return this;
        }

        public Builder setUnsafeReplacement(@Nullable String str) {
            this.unsafeReplacement = str;
            return this;
        }
    }

    private Escapers() {
    }

    static UnicodeEscaper asUnicodeEscaper(Escaper escaper) {
        Preconditions.checkNotNull(escaper);
        if (escaper instanceof UnicodeEscaper) {
            return (UnicodeEscaper) escaper;
        }
        if (escaper instanceof CharEscaper) {
            return wrap((CharEscaper) escaper);
        }
        throw new IllegalArgumentException("Cannot create a UnicodeEscaper from: " + escaper.getClass().getName());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String computeReplacement(CharEscaper charEscaper, char c) {
        return stringOrNull(charEscaper.escape(c));
    }

    public static String computeReplacement(UnicodeEscaper unicodeEscaper, int i) {
        return stringOrNull(unicodeEscaper.escape(i));
    }

    public static Escaper nullEscaper() {
        return NULL_ESCAPER;
    }

    private static String stringOrNull(char[] cArr) {
        if (cArr != null) {
            return new String(cArr);
        }
        return null;
    }

    private static UnicodeEscaper wrap(final CharEscaper charEscaper) {
        return new UnicodeEscaper() { // from class: com.google.common.escape.Escapers.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.common.escape.UnicodeEscaper
            public char[] escape(int i) {
                if (i >= 65536) {
                    char[] cArr = new char[2];
                    Character.toChars(i, cArr, 0);
                    char[] escape = CharEscaper.this.escape(cArr[0]);
                    char[] escape2 = CharEscaper.this.escape(cArr[1]);
                    if (escape == null && escape2 == null) {
                        return null;
                    }
                    int length = escape == null ? 1 : escape.length;
                    char[] cArr2 = new char[(escape2 == null ? 1 : escape2.length) + length];
                    if (escape == null) {
                        cArr2[0] = cArr[0];
                    } else {
                        for (int i2 = 0; i2 < escape.length; i2++) {
                            cArr2[i2] = escape[i2];
                        }
                    }
                    if (escape2 == null) {
                        cArr2[length] = cArr[1];
                    } else {
                        for (int i3 = 0; i3 < escape2.length; i3++) {
                            cArr2[length + i3] = escape2[i3];
                        }
                    }
                    return cArr2;
                }
                return CharEscaper.this.escape((char) i);
            }
        };
    }
}
