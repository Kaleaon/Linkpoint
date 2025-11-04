// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.text;

import android.text.SpannableStringBuilder;
import java.util.Locale;

public final class BidiFormatter
{
    private static final int DEFAULT_FLAGS = 2;
    private static final BidiFormatter DEFAULT_LTR_INSTANCE;
    private static final BidiFormatter DEFAULT_RTL_INSTANCE;
    private static TextDirectionHeuristicCompat DEFAULT_TEXT_DIRECTION_HEURISTIC;
    private static final int DIR_LTR = -1;
    private static final int DIR_RTL = 1;
    private static final int DIR_UNKNOWN = 0;
    private static final String EMPTY_STRING = "";
    private static final int FLAG_STEREO_RESET = 2;
    private static final char LRE = '\u202a';
    private static final char LRM = '\u200e';
    private static final String LRM_STRING;
    private static final char PDF = '\u202c';
    private static final char RLE = '\u202b';
    private static final char RLM = '\u200f';
    private static final String RLM_STRING;
    private final TextDirectionHeuristicCompat mDefaultTextDirectionHeuristicCompat;
    private final int mFlags;
    private final boolean mIsRtlContext;
    
    static {
        BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;
        LRM_STRING = Character.toString('\u200e');
        RLM_STRING = Character.toString('\u200f');
        DEFAULT_LTR_INSTANCE = new BidiFormatter(false, 2, BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC);
        DEFAULT_RTL_INSTANCE = new BidiFormatter(true, 2, BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC);
    }
    
    private BidiFormatter(final boolean mIsRtlContext, final int mFlags, final TextDirectionHeuristicCompat mDefaultTextDirectionHeuristicCompat) {
        this.mIsRtlContext = mIsRtlContext;
        this.mFlags = mFlags;
        this.mDefaultTextDirectionHeuristicCompat = mDefaultTextDirectionHeuristicCompat;
    }
    
    private static int getEntryDir(final CharSequence charSequence) {
        return new DirectionalityEstimator(charSequence, false).getEntryDir();
    }
    
    private static int getExitDir(final CharSequence charSequence) {
        return new DirectionalityEstimator(charSequence, false).getExitDir();
    }
    
    public static BidiFormatter getInstance() {
        return new Builder().build();
    }
    
    public static BidiFormatter getInstance(final Locale locale) {
        return new Builder(locale).build();
    }
    
    public static BidiFormatter getInstance(final boolean b) {
        return new Builder(b).build();
    }
    
    private static boolean isRtlLocale(final Locale locale) {
        boolean b = true;
        if (TextUtilsCompat.getLayoutDirectionFromLocale(locale) != 1) {
            b = false;
        }
        return b;
    }
    
    private String markAfter(final CharSequence charSequence, final TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        final boolean rtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        if (!this.mIsRtlContext && (rtl || getExitDir(charSequence) == 1)) {
            return BidiFormatter.LRM_STRING;
        }
        if (this.mIsRtlContext && (!rtl || getExitDir(charSequence) == -1)) {
            return BidiFormatter.RLM_STRING;
        }
        return "";
    }
    
    private String markBefore(final CharSequence charSequence, final TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        final boolean rtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
        if (!this.mIsRtlContext && (rtl || getEntryDir(charSequence) == 1)) {
            return BidiFormatter.LRM_STRING;
        }
        if (this.mIsRtlContext && (!rtl || getEntryDir(charSequence) == -1)) {
            return BidiFormatter.RLM_STRING;
        }
        return "";
    }
    
    public boolean getStereoReset() {
        boolean b = false;
        if ((this.mFlags & 0x2) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public boolean isRtl(final CharSequence charSequence) {
        return this.mDefaultTextDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
    }
    
    public boolean isRtl(final String s) {
        return this.isRtl((CharSequence)s);
    }
    
    public boolean isRtlContext() {
        return this.mIsRtlContext;
    }
    
    public CharSequence unicodeWrap(final CharSequence charSequence) {
        return this.unicodeWrap(charSequence, this.mDefaultTextDirectionHeuristicCompat, true);
    }
    
    public CharSequence unicodeWrap(final CharSequence charSequence, final TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        return this.unicodeWrap(charSequence, textDirectionHeuristicCompat, true);
    }
    
    public CharSequence unicodeWrap(final CharSequence charSequence, TextDirectionHeuristicCompat textDirectionHeuristicCompat, final boolean b) {
        if (charSequence != null) {
            final boolean rtl = textDirectionHeuristicCompat.isRtl(charSequence, 0, charSequence.length());
            final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (this.getStereoReset() && b) {
                if (!rtl) {
                    textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR;
                }
                else {
                    textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL;
                }
                spannableStringBuilder.append((CharSequence)this.markBefore(charSequence, textDirectionHeuristicCompat));
            }
            if (rtl == this.mIsRtlContext) {
                spannableStringBuilder.append(charSequence);
            }
            else {
                char c;
                if (!rtl) {
                    c = '\u202a';
                }
                else {
                    c = '\u202b';
                }
                spannableStringBuilder.append(c);
                spannableStringBuilder.append(charSequence);
                spannableStringBuilder.append('\u202c');
            }
            if (b) {
                if (!rtl) {
                    textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR;
                }
                else {
                    textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL;
                }
                spannableStringBuilder.append((CharSequence)this.markAfter(charSequence, textDirectionHeuristicCompat));
            }
            return (CharSequence)spannableStringBuilder;
        }
        return null;
    }
    
    public CharSequence unicodeWrap(final CharSequence charSequence, final boolean b) {
        return this.unicodeWrap(charSequence, this.mDefaultTextDirectionHeuristicCompat, b);
    }
    
    public String unicodeWrap(final String s) {
        return this.unicodeWrap(s, this.mDefaultTextDirectionHeuristicCompat, true);
    }
    
    public String unicodeWrap(final String s, final TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        return this.unicodeWrap(s, textDirectionHeuristicCompat, true);
    }
    
    public String unicodeWrap(final String s, final TextDirectionHeuristicCompat textDirectionHeuristicCompat, final boolean b) {
        if (s != null) {
            return this.unicodeWrap((CharSequence)s, textDirectionHeuristicCompat, b).toString();
        }
        return null;
    }
    
    public String unicodeWrap(final String s, final boolean b) {
        return this.unicodeWrap(s, this.mDefaultTextDirectionHeuristicCompat, b);
    }
    
    public static final class Builder
    {
        private int mFlags;
        private boolean mIsRtlContext;
        private TextDirectionHeuristicCompat mTextDirectionHeuristicCompat;
        
        public Builder() {
            this.initialize(isRtlLocale(Locale.getDefault()));
        }
        
        public Builder(final Locale locale) {
            this.initialize(isRtlLocale(locale));
        }
        
        public Builder(final boolean b) {
            this.initialize(b);
        }
        
        private static BidiFormatter getDefaultInstanceFromContext(final boolean b) {
            BidiFormatter bidiFormatter;
            if (!b) {
                bidiFormatter = BidiFormatter.DEFAULT_LTR_INSTANCE;
            }
            else {
                bidiFormatter = BidiFormatter.DEFAULT_RTL_INSTANCE;
            }
            return bidiFormatter;
        }
        
        private void initialize(final boolean mIsRtlContext) {
            this.mIsRtlContext = mIsRtlContext;
            this.mTextDirectionHeuristicCompat = BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC;
            this.mFlags = 2;
        }
        
        public BidiFormatter build() {
            if (this.mFlags == 2 && this.mTextDirectionHeuristicCompat == BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC) {
                return getDefaultInstanceFromContext(this.mIsRtlContext);
            }
            return new BidiFormatter(this.mIsRtlContext, this.mFlags, this.mTextDirectionHeuristicCompat, null);
        }
        
        public Builder setTextDirectionHeuristic(final TextDirectionHeuristicCompat mTextDirectionHeuristicCompat) {
            this.mTextDirectionHeuristicCompat = mTextDirectionHeuristicCompat;
            return this;
        }
        
        public Builder stereoReset(final boolean b) {
            if (!b) {
                this.mFlags &= 0xFFFFFFFD;
            }
            else {
                this.mFlags |= 0x2;
            }
            return this;
        }
    }
    
    private static class DirectionalityEstimator
    {
        private static final byte[] DIR_TYPE_CACHE;
        private static final int DIR_TYPE_CACHE_SIZE = 1792;
        private int charIndex;
        private final boolean isHtml;
        private char lastChar;
        private final int length;
        private final CharSequence text;
        
        static {
            DIR_TYPE_CACHE = new byte[1792];
            for (int i = 0; i < 1792; ++i) {
                DirectionalityEstimator.DIR_TYPE_CACHE[i] = Character.getDirectionality(i);
            }
        }
        
        DirectionalityEstimator(final CharSequence text, final boolean isHtml) {
            this.text = text;
            this.isHtml = isHtml;
            this.length = text.length();
        }
        
        private static byte getCachedDirectionality(final char ch) {
            byte directionality;
            if (ch >= '\u0700') {
                directionality = Character.getDirectionality(ch);
            }
            else {
                directionality = DirectionalityEstimator.DIR_TYPE_CACHE[ch];
            }
            return directionality;
        }
        
        private byte skipEntityBackward() {
            final int charIndex = this.charIndex;
            while (this.charIndex > 0) {
                final CharSequence text = this.text;
                final int charIndex2 = this.charIndex - 1;
                this.charIndex = charIndex2;
                this.lastChar = text.charAt(charIndex2);
                if (this.lastChar == '&') {
                    return 12;
                }
                if (this.lastChar != ';') {
                    continue;
                }
                break;
            }
            this.charIndex = charIndex;
            this.lastChar = 59;
            return 13;
        }
        
        private byte skipEntityForward() {
            while (this.charIndex < this.length) {
                final char char1 = this.text.charAt(this.charIndex++);
                this.lastChar = char1;
                if (char1 == ';') {
                    break;
                }
            }
            return 12;
        }
        
        private byte skipTagBackward() {
            final int charIndex = this.charIndex;
            while (this.charIndex > 0) {
                final CharSequence text = this.text;
                final int charIndex2 = this.charIndex - 1;
                this.charIndex = charIndex2;
                this.lastChar = text.charAt(charIndex2);
                if (this.lastChar == '<') {
                    return 12;
                }
                if (this.lastChar == '>') {
                    break;
                }
                if (this.lastChar != '\"' && this.lastChar != '\'') {
                    continue;
                }
                final char lastChar = this.lastChar;
                while (this.charIndex > 0) {
                    final CharSequence text2 = this.text;
                    final int charIndex3 = this.charIndex - 1;
                    this.charIndex = charIndex3;
                    final char char1 = text2.charAt(charIndex3);
                    this.lastChar = char1;
                    if (char1 == lastChar) {
                        break;
                    }
                }
            }
            this.charIndex = charIndex;
            this.lastChar = 62;
            return 13;
        }
        
        private byte skipTagForward() {
            final int charIndex = this.charIndex;
            while (this.charIndex < this.length) {
                this.lastChar = this.text.charAt(this.charIndex++);
                if (this.lastChar == '>') {
                    return 12;
                }
                if (this.lastChar != '\"' && this.lastChar != '\'') {
                    continue;
                }
                final char lastChar = this.lastChar;
                while (this.charIndex < this.length) {
                    final char char1 = this.text.charAt(this.charIndex++);
                    this.lastChar = char1;
                    if (char1 == lastChar) {
                        break;
                    }
                }
            }
            this.charIndex = charIndex;
            this.lastChar = 60;
            return 13;
        }
        
        byte dirTypeBackward() {
            this.lastChar = this.text.charAt(this.charIndex - 1);
            if (!Character.isLowSurrogate(this.lastChar)) {
                --this.charIndex;
                final byte cachedDirectionality = getCachedDirectionality(this.lastChar);
                byte b;
                if (!this.isHtml) {
                    b = cachedDirectionality;
                }
                else if (this.lastChar != '>') {
                    b = cachedDirectionality;
                    if (this.lastChar == ';') {
                        b = this.skipEntityBackward();
                    }
                }
                else {
                    b = this.skipTagBackward();
                }
                return b;
            }
            final int codePointBefore = Character.codePointBefore(this.text, this.charIndex);
            this.charIndex -= Character.charCount(codePointBefore);
            return Character.getDirectionality(codePointBefore);
        }
        
        byte dirTypeForward() {
            this.lastChar = this.text.charAt(this.charIndex);
            if (!Character.isHighSurrogate(this.lastChar)) {
                ++this.charIndex;
                final byte cachedDirectionality = getCachedDirectionality(this.lastChar);
                byte b;
                if (!this.isHtml) {
                    b = cachedDirectionality;
                }
                else if (this.lastChar != '<') {
                    b = cachedDirectionality;
                    if (this.lastChar == '&') {
                        b = this.skipEntityForward();
                    }
                }
                else {
                    b = this.skipTagForward();
                }
                return b;
            }
            final int codePoint = Character.codePointAt(this.text, this.charIndex);
            this.charIndex += Character.charCount(codePoint);
            return Character.getDirectionality(codePoint);
        }
        
        int getEntryDir() {
            this.charIndex = 0;
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            while (this.charIndex < this.length && n == 0) {
                switch (this.dirTypeForward()) {
                    case 18: {
                        --n3;
                        n2 = 0;
                        continue;
                    }
                    case 16:
                    case 17: {
                        ++n3;
                        n2 = 1;
                        continue;
                    }
                    case 14:
                    case 15: {
                        ++n3;
                        n2 = -1;
                    }
                    case 9: {
                        continue;
                    }
                    default: {
                        n = n3;
                        continue;
                    }
                    case 0: {
                        if (n3 != 0) {
                            n = n3;
                            continue;
                        }
                        return -1;
                    }
                    case 1:
                    case 2: {
                        if (n3 != 0) {
                            n = n3;
                            continue;
                        }
                        return 1;
                    }
                }
            }
            if (n == 0) {
                return 0;
            }
            if (n2 == 0) {
                while (this.charIndex > 0) {
                    switch (this.dirTypeBackward()) {
                        default: {
                            continue;
                        }
                        case 14:
                        case 15: {
                            if (n != n3) {
                                --n3;
                                continue;
                            }
                            return -1;
                        }
                        case 16:
                        case 17: {
                            if (n != n3) {
                                --n3;
                                continue;
                            }
                            return 1;
                        }
                        case 18: {
                            ++n3;
                            continue;
                        }
                    }
                }
                return 0;
            }
            return n2;
        }
        
        int getExitDir() {
            this.charIndex = this.length;
            int n = 0;
            int n2 = 0;
            while (this.charIndex > 0) {
                switch (this.dirTypeBackward()) {
                    case 18: {
                        ++n2;
                    }
                    case 9: {
                        continue;
                    }
                    default: {
                        if (n == 0) {
                            n = n2;
                            continue;
                        }
                        continue;
                    }
                    case 0: {
                        if (n2 == 0) {
                            return -1;
                        }
                        if (n == 0) {
                            n = n2;
                            continue;
                        }
                        continue;
                    }
                    case 14:
                    case 15: {
                        if (n != n2) {
                            --n2;
                            continue;
                        }
                        return -1;
                    }
                    case 1:
                    case 2: {
                        if (n2 == 0) {
                            return 1;
                        }
                        if (n == 0) {
                            n = n2;
                            continue;
                        }
                        continue;
                    }
                    case 16:
                    case 17: {
                        if (n != n2) {
                            --n2;
                            continue;
                        }
                        return 1;
                    }
                }
            }
            return 0;
        }
    }
}
