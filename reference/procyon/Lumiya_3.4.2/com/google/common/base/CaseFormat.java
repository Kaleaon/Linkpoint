// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.Nullable;
import java.io.Serializable;
import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public enum CaseFormat
{
    LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        @Override
        String normalizeWord(final String s) {
            return firstCharOnlyToUpper(s);
        }
    }, 
    LOWER_HYPHEN(CharMatcher.is('-'), "-") {
        @Override
        String convert(final CaseFormat caseFormat, final String s) {
            if (caseFormat == CaseFormat$1.LOWER_UNDERSCORE) {
                return s.replace('-', '_');
            }
            if (caseFormat != CaseFormat$1.UPPER_UNDERSCORE) {
                return super.convert(caseFormat, s);
            }
            return Ascii.toUpperCase(s.replace('-', '_'));
        }
        
        @Override
        String normalizeWord(final String s) {
            return Ascii.toLowerCase(s);
        }
    }, 
    LOWER_UNDERSCORE(CharMatcher.is('_'), "_") {
        @Override
        String convert(final CaseFormat caseFormat, final String s) {
            if (caseFormat == CaseFormat$2.LOWER_HYPHEN) {
                return s.replace('_', '-');
            }
            if (caseFormat != CaseFormat$2.UPPER_UNDERSCORE) {
                return super.convert(caseFormat, s);
            }
            return Ascii.toUpperCase(s);
        }
        
        @Override
        String normalizeWord(final String s) {
            return Ascii.toLowerCase(s);
        }
    }, 
    UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        @Override
        String normalizeWord(final String s) {
            return firstCharOnlyToUpper(s);
        }
    }, 
    UPPER_UNDERSCORE(CharMatcher.is('_'), "_") {
        @Override
        String convert(final CaseFormat caseFormat, final String s) {
            if (caseFormat == CaseFormat$5.LOWER_HYPHEN) {
                return Ascii.toLowerCase(s.replace('_', '-'));
            }
            if (caseFormat != CaseFormat$5.LOWER_UNDERSCORE) {
                return super.convert(caseFormat, s);
            }
            return Ascii.toLowerCase(s);
        }
        
        @Override
        String normalizeWord(final String s) {
            return Ascii.toUpperCase(s);
        }
    };
    
    private final CharMatcher wordBoundary;
    private final String wordSeparator;
    
    private CaseFormat(final CharMatcher wordBoundary, final String wordSeparator) {
        this.wordBoundary = wordBoundary;
        this.wordSeparator = wordSeparator;
    }
    
    private static String firstCharOnlyToUpper(final String s) {
        String string = s;
        if (!s.isEmpty()) {
            string = new StringBuilder(s.length()).append(Ascii.toUpperCase(s.charAt(0))).append(Ascii.toLowerCase(s.substring(1))).toString();
        }
        return string;
    }
    
    private String normalizeFirstWord(String s) {
        if (this != CaseFormat.LOWER_CAMEL) {
            s = this.normalizeWord(s);
        }
        else {
            s = Ascii.toLowerCase(s);
        }
        return s;
    }
    
    String convert(final CaseFormat caseFormat, final String s) {
        StringBuilder sb = null;
        int beginIndex = 0;
        int indexIn = -1;
        while (true) {
            indexIn = this.wordBoundary.indexIn(s, indexIn + 1);
            if (indexIn == -1) {
                break;
            }
            if (beginIndex != 0) {
                sb.append(caseFormat.normalizeWord(s.substring(beginIndex, indexIn)));
            }
            else {
                sb = new StringBuilder(s.length() + this.wordSeparator.length() * 4);
                sb.append(caseFormat.normalizeFirstWord(s.substring(beginIndex, indexIn)));
            }
            sb.append(caseFormat.wordSeparator);
            beginIndex = this.wordSeparator.length() + indexIn;
        }
        String s2;
        if (beginIndex != 0) {
            s2 = sb.append(caseFormat.normalizeWord(s.substring(beginIndex))).toString();
        }
        else {
            s2 = caseFormat.normalizeFirstWord(s);
        }
        return s2;
    }
    
    @Beta
    public Converter<String, String> converterTo(final CaseFormat caseFormat) {
        return new StringConverter(this, caseFormat);
    }
    
    abstract String normalizeWord(final String p0);
    
    public final String to(final CaseFormat caseFormat, final String s) {
        Preconditions.checkNotNull(caseFormat);
        Preconditions.checkNotNull(s);
        String convert = s;
        if (caseFormat != this) {
            convert = this.convert(caseFormat, s);
        }
        return convert;
    }
    
    private static final class StringConverter extends Converter<String, String> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final CaseFormat sourceFormat;
        private final CaseFormat targetFormat;
        
        StringConverter(final CaseFormat caseFormat, final CaseFormat caseFormat2) {
            this.sourceFormat = Preconditions.checkNotNull(caseFormat);
            this.targetFormat = Preconditions.checkNotNull(caseFormat2);
        }
        
        @Override
        protected String doBackward(final String s) {
            return this.targetFormat.to(this.sourceFormat, s);
        }
        
        @Override
        protected String doForward(final String s) {
            return this.sourceFormat.to(this.targetFormat, s);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof StringConverter)) {
                return false;
            }
            final StringConverter stringConverter = (StringConverter)o;
            if (this.sourceFormat.equals(stringConverter.sourceFormat) && this.targetFormat.equals(stringConverter.targetFormat)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
        }
        
        @Override
        public String toString() {
            return this.sourceFormat + ".converterTo(" + this.targetFormat + ")";
        }
    }
}
