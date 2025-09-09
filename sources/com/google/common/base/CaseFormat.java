package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
@CheckReturnValue
public enum CaseFormat {
    LOWER_HYPHEN(CharMatcher.is('-'), "-") {
        /* access modifiers changed from: package-private */
        public String convert(CaseFormat caseFormat, String str) {
            return caseFormat != LOWER_UNDERSCORE ? caseFormat != UPPER_UNDERSCORE ? CaseFormat.super.convert(caseFormat, str) : Ascii.toUpperCase(str.replace('-', '_')) : str.replace('-', '_');
        }

        /* access modifiers changed from: package-private */
        public String normalizeWord(String str) {
            return Ascii.toLowerCase(str);
        }
    },
    LOWER_UNDERSCORE(CharMatcher.is('_'), "_") {
        /* access modifiers changed from: package-private */
        public String convert(CaseFormat caseFormat, String str) {
            return caseFormat != LOWER_HYPHEN ? caseFormat != UPPER_UNDERSCORE ? CaseFormat.super.convert(caseFormat, str) : Ascii.toUpperCase(str) : str.replace('_', '-');
        }

        /* access modifiers changed from: package-private */
        public String normalizeWord(String str) {
            return Ascii.toLowerCase(str);
        }
    },
    LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        /* access modifiers changed from: package-private */
        public String normalizeWord(String str) {
            return CaseFormat.firstCharOnlyToUpper(str);
        }
    },
    UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        /* access modifiers changed from: package-private */
        public String normalizeWord(String str) {
            return CaseFormat.firstCharOnlyToUpper(str);
        }
    },
    UPPER_UNDERSCORE(CharMatcher.is('_'), "_") {
        /* access modifiers changed from: package-private */
        public String convert(CaseFormat caseFormat, String str) {
            return caseFormat != LOWER_HYPHEN ? caseFormat != LOWER_UNDERSCORE ? CaseFormat.super.convert(caseFormat, str) : Ascii.toLowerCase(str) : Ascii.toLowerCase(str.replace('_', '-'));
        }

        /* access modifiers changed from: package-private */
        public String normalizeWord(String str) {
            return Ascii.toUpperCase(str);
        }
    };
    
    private final CharMatcher wordBoundary;
    private final String wordSeparator;

    private static final class StringConverter extends Converter<String, String> implements Serializable {
        private static final long serialVersionUID = 0;
        private final CaseFormat sourceFormat;
        private final CaseFormat targetFormat;

        StringConverter(CaseFormat caseFormat, CaseFormat caseFormat2) {
            this.sourceFormat = (CaseFormat) Preconditions.checkNotNull(caseFormat);
            this.targetFormat = (CaseFormat) Preconditions.checkNotNull(caseFormat2);
        }

        /* access modifiers changed from: protected */
        public String doBackward(String str) {
            return this.targetFormat.to(this.sourceFormat, str);
        }

        /* access modifiers changed from: protected */
        public String doForward(String str) {
            return this.sourceFormat.to(this.targetFormat, str);
        }

        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof StringConverter)) {
                return false;
            }
            StringConverter stringConverter = (StringConverter) obj;
            return this.sourceFormat.equals(stringConverter.sourceFormat) && this.targetFormat.equals(stringConverter.targetFormat);
        }

        public int hashCode() {
            return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
        }

        public String toString() {
            return this.sourceFormat + ".converterTo(" + this.targetFormat + ")";
        }
    }

    private CaseFormat(CharMatcher charMatcher, String str) {
        this.wordBoundary = charMatcher;
        this.wordSeparator = str;
    }

    /* access modifiers changed from: private */
    public static String firstCharOnlyToUpper(String str) {
        return !str.isEmpty() ? new StringBuilder(str.length()).append(Ascii.toUpperCase(str.charAt(0))).append(Ascii.toLowerCase(str.substring(1))).toString() : str;
    }

    private String normalizeFirstWord(String str) {
        return this != LOWER_CAMEL ? normalizeWord(str) : Ascii.toLowerCase(str);
    }

    /* access modifiers changed from: package-private */
    public String convert(CaseFormat caseFormat, String str) {
        StringBuilder sb = null;
        int i = 0;
        int i2 = -1;
        while (true) {
            i2 = this.wordBoundary.indexIn(str, i2 + 1);
            if (i2 == -1) {
                break;
            }
            if (i != 0) {
                sb.append(caseFormat.normalizeWord(str.substring(i, i2)));
            } else {
                sb = new StringBuilder(str.length() + (this.wordSeparator.length() * 4));
                sb.append(caseFormat.normalizeFirstWord(str.substring(i, i2)));
            }
            sb.append(caseFormat.wordSeparator);
            i = this.wordSeparator.length() + i2;
        }
        return i != 0 ? sb.append(caseFormat.normalizeWord(str.substring(i))).toString() : caseFormat.normalizeFirstWord(str);
    }

    @Beta
    public Converter<String, String> converterTo(CaseFormat caseFormat) {
        return new StringConverter(this, caseFormat);
    }

    /* access modifiers changed from: package-private */
    public abstract String normalizeWord(String str);

    public final String to(CaseFormat caseFormat, String str) {
        Preconditions.checkNotNull(caseFormat);
        Preconditions.checkNotNull(str);
        return caseFormat != this ? convert(caseFormat, str) : str;
    }
}
