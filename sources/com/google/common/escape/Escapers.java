package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
@Beta
public final class Escapers {
    private static final Escaper NULL_ESCAPER = new CharEscaper() {
        public String escape(String str) {
            return (String) Preconditions.checkNotNull(str);
        }

        /* access modifiers changed from: protected */
        public char[] escape(char c) {
            return null;
        }
    };

    @Beta
    public static final class Builder {
        private final Map<Character, String> replacementMap;
        private char safeMax;
        private char safeMin;
        /* access modifiers changed from: private */
        public String unsafeReplacement;

        private Builder() {
            this.replacementMap = new HashMap();
            this.safeMin = 0;
            this.safeMax = 65535;
            this.unsafeReplacement = null;
        }

        public Builder addEscape(char c, String str) {
            Preconditions.checkNotNull(str);
            this.replacementMap.put(Character.valueOf(c), str);
            return this;
        }

        public Escaper build() {
            return new ArrayBasedCharEscaper(this, this.replacementMap, this.safeMin, this.safeMax) {
                private final char[] replacementChars;
                final /* synthetic */ Builder this$0;

                {
                    char[] cArr = null;
                    this.this$0 = r3;
                    this.replacementChars = this.this$0.unsafeReplacement != null ? this.this$0.unsafeReplacement.toCharArray() : cArr;
                }

                /* access modifiers changed from: protected */
                public char[] escapeUnsafe(char c) {
                    return this.replacementChars;
                }
            };
        }

        public Builder setSafeRange(char c, char c2) {
            this.safeMin = (char) c;
            this.safeMax = (char) c2;
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
        return new UnicodeEscaper() {
            /* access modifiers changed from: protected */
            public char[] escape(int i) {
                if (i < 65536) {
                    return charEscaper.escape((char) i);
                }
                char[] cArr = new char[2];
                Character.toChars(i, cArr, 0);
                char[] escape = charEscaper.escape(cArr[0]);
                char[] escape2 = charEscaper.escape(cArr[1]);
                if (escape == null && escape2 == null) {
                    return null;
                }
                int length = escape == null ? 1 : escape.length;
                char[] cArr2 = new char[((escape2 == null ? 1 : escape2.length) + length)];
                if (escape == null) {
                    cArr2[0] = (char) cArr[0];
                } else {
                    for (int i2 = 0; i2 < escape.length; i2++) {
                        cArr2[i2] = (char) escape[i2];
                    }
                }
                if (escape2 == null) {
                    cArr2[length] = (char) cArr[1];
                } else {
                    for (int i3 = 0; i3 < escape2.length; i3++) {
                        cArr2[length + i3] = (char) escape2[i3];
                    }
                }
                return cArr2;
            }
        };
    }
}
