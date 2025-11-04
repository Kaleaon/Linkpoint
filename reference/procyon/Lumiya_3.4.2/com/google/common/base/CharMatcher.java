// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import javax.annotation.CheckReturnValue;
import java.util.BitSet;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public abstract class CharMatcher implements Predicate<Character>
{
    public static final CharMatcher ANY;
    public static final CharMatcher ASCII;
    public static final CharMatcher BREAKING_WHITESPACE;
    public static final CharMatcher DIGIT;
    private static final int DISTINCT_CHARS = 65536;
    public static final CharMatcher INVISIBLE;
    public static final CharMatcher JAVA_DIGIT;
    public static final CharMatcher JAVA_ISO_CONTROL;
    public static final CharMatcher JAVA_LETTER;
    public static final CharMatcher JAVA_LETTER_OR_DIGIT;
    public static final CharMatcher JAVA_LOWER_CASE;
    public static final CharMatcher JAVA_UPPER_CASE;
    public static final CharMatcher NONE;
    public static final CharMatcher SINGLE_WIDTH;
    public static final CharMatcher WHITESPACE;
    
    static {
        WHITESPACE = whitespace();
        BREAKING_WHITESPACE = breakingWhitespace();
        ASCII = ascii();
        DIGIT = digit();
        JAVA_DIGIT = javaDigit();
        JAVA_LETTER = javaLetter();
        JAVA_LETTER_OR_DIGIT = javaLetterOrDigit();
        JAVA_UPPER_CASE = javaUpperCase();
        JAVA_LOWER_CASE = javaLowerCase();
        JAVA_ISO_CONTROL = javaIsoControl();
        INVISIBLE = invisible();
        SINGLE_WIDTH = singleWidth();
        ANY = any();
        NONE = none();
    }
    
    protected CharMatcher() {
    }
    
    public static CharMatcher any() {
        return Any.INSTANCE;
    }
    
    public static CharMatcher anyOf(final CharSequence charSequence) {
        switch (charSequence.length()) {
            default: {
                return new AnyOf(charSequence);
            }
            case 0: {
                return none();
            }
            case 1: {
                return is(charSequence.charAt(0));
            }
            case 2: {
                return isEither(charSequence.charAt(0), charSequence.charAt(1));
            }
        }
    }
    
    public static CharMatcher ascii() {
        return Ascii.INSTANCE;
    }
    
    public static CharMatcher breakingWhitespace() {
        return BreakingWhitespace.INSTANCE;
    }
    
    public static CharMatcher digit() {
        return Digit.INSTANCE;
    }
    
    private String finishCollapseFrom(final CharSequence charSequence, int i, final int n, final char c, final StringBuilder sb, final boolean b) {
        int n2 = b ? 1 : 0;
        while (i < n) {
            final char char1 = charSequence.charAt(i);
            int n3;
            if (!this.matches(char1)) {
                sb.append(char1);
                n3 = 0;
            }
            else if ((n3 = n2) == 0) {
                sb.append(c);
                n3 = 1;
            }
            ++i;
            n2 = n3;
        }
        return sb.toString();
    }
    
    public static CharMatcher forPredicate(final Predicate<? super Character> predicate) {
        CharMatcher charMatcher;
        if (!(predicate instanceof CharMatcher)) {
            charMatcher = new ForPredicate(predicate);
        }
        else {
            charMatcher = (CharMatcher)predicate;
        }
        return charMatcher;
    }
    
    public static CharMatcher inRange(final char c, final char c2) {
        return new InRange(c, c2);
    }
    
    public static CharMatcher invisible() {
        return Invisible.INSTANCE;
    }
    
    public static CharMatcher is(final char c) {
        return new Is(c);
    }
    
    private static IsEither isEither(final char c, final char c2) {
        return new IsEither(c, c2);
    }
    
    public static CharMatcher isNot(final char c) {
        return new IsNot(c);
    }
    
    @GwtIncompatible("SmallCharMatcher")
    private static boolean isSmall(final int n, final int n2) {
        return n <= 1023 && n2 > n * 4 * 16;
    }
    
    public static CharMatcher javaDigit() {
        return JavaDigit.INSTANCE;
    }
    
    public static CharMatcher javaIsoControl() {
        return JavaIsoControl.INSTANCE;
    }
    
    public static CharMatcher javaLetter() {
        return JavaLetter.INSTANCE;
    }
    
    public static CharMatcher javaLetterOrDigit() {
        return JavaLetterOrDigit.INSTANCE;
    }
    
    public static CharMatcher javaLowerCase() {
        return JavaLowerCase.INSTANCE;
    }
    
    public static CharMatcher javaUpperCase() {
        return JavaUpperCase.INSTANCE;
    }
    
    public static CharMatcher none() {
        return None.INSTANCE;
    }
    
    public static CharMatcher noneOf(final CharSequence charSequence) {
        return anyOf(charSequence).negate();
    }
    
    @GwtIncompatible("java.util.BitSet")
    private static CharMatcher precomputedPositive(final int n, final BitSet set, final String s) {
        switch (n) {
            default: {
                CharMatcher from;
                if (!isSmall(n, set.length())) {
                    from = new BitSetMatcher(set, s);
                }
                else {
                    from = SmallCharMatcher.from(set, s);
                }
                return from;
            }
            case 0: {
                return none();
            }
            case 1: {
                return is((char)set.nextSetBit(0));
            }
            case 2: {
                final char c = (char)set.nextSetBit(0);
                return isEither(c, (char)set.nextSetBit(c + '\u0001'));
            }
        }
    }
    
    private static String showCharacter(final char c) {
        final int n = 0;
        final char[] data = { 92, 117, 0, 0, 0, 0 };
        char c2 = c;
        for (int i = n; i < 4; ++i) {
            data[5 - i] = "0123456789ABCDEF".charAt(c2 & '\u000f');
            c2 >>= 4;
        }
        return String.copyValueOf(data);
    }
    
    public static CharMatcher singleWidth() {
        return SingleWidth.INSTANCE;
    }
    
    public static CharMatcher whitespace() {
        return Whitespace.INSTANCE;
    }
    
    public CharMatcher and(final CharMatcher charMatcher) {
        return new And(this, charMatcher);
    }
    
    @Deprecated
    @Override
    public boolean apply(final Character c) {
        return this.matches(c);
    }
    
    @CheckReturnValue
    public String collapseFrom(final CharSequence charSequence, final char c) {
        for (int length = charSequence.length(), i = 0; i < length; ++i) {
            final char char1 = charSequence.charAt(i);
            if (this.matches(char1)) {
                if (char1 != c || (i != length - 1 && this.matches(charSequence.charAt(i + 1)))) {
                    return this.finishCollapseFrom(charSequence, i + 1, length, c, new StringBuilder(length).append(charSequence.subSequence(0, i)).append(c), true);
                }
                ++i;
            }
        }
        return charSequence.toString();
    }
    
    public int countIn(final CharSequence charSequence) {
        int i = 0;
        int n = 0;
        while (i < charSequence.length()) {
            if (this.matches(charSequence.charAt(i))) {
                ++n;
            }
            ++i;
        }
        return n;
    }
    
    public int indexIn(final CharSequence charSequence) {
        return this.indexIn(charSequence, 0);
    }
    
    public int indexIn(final CharSequence charSequence, int i) {
        final int length = charSequence.length();
        Preconditions.checkPositionIndex(i, length);
        while (i < length) {
            if (this.matches(charSequence.charAt(i))) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public int lastIndexIn(final CharSequence charSequence) {
        int length = charSequence.length();
        int n;
        do {
            n = length - 1;
            if (n < 0) {
                return -1;
            }
            length = n;
        } while (!this.matches(charSequence.charAt(n)));
        return n;
    }
    
    public abstract boolean matches(final char p0);
    
    public boolean matchesAllOf(final CharSequence charSequence) {
        int length = charSequence.length();
        int n;
        do {
            n = length - 1;
            if (n < 0) {
                return true;
            }
            length = n;
        } while (this.matches(charSequence.charAt(n)));
        return false;
    }
    
    public boolean matchesAnyOf(final CharSequence charSequence) {
        boolean b = false;
        if (!this.matchesNoneOf(charSequence)) {
            b = true;
        }
        return b;
    }
    
    public boolean matchesNoneOf(final CharSequence charSequence) {
        return this.indexIn(charSequence) == -1;
    }
    
    public CharMatcher negate() {
        return new Negated(this);
    }
    
    public CharMatcher or(final CharMatcher charMatcher) {
        return new Or(this, charMatcher);
    }
    
    public CharMatcher precomputed() {
        return Platform.precomputeCharMatcher(this);
    }
    
    @GwtIncompatible("java.util.BitSet")
    CharMatcher precomputedInternal() {
        final BitSet bits = new BitSet();
        this.setBits(bits);
        final int cardinality = bits.cardinality();
        if (cardinality * 2 > 65536) {
            bits.flip(0, 65536);
            final String string = this.toString();
            String s;
            if (!string.endsWith(".negate()")) {
                s = string + ".negate()";
            }
            else {
                s = string.substring(0, string.length() - ".negate()".length());
            }
            return new NegatedFastMatcher(precomputedPositive(65536 - cardinality, bits, s)) {
                @Override
                public String toString() {
                    return string;
                }
            };
        }
        return precomputedPositive(cardinality, bits, this.toString());
    }
    
    @CheckReturnValue
    public String removeFrom(final CharSequence charSequence) {
        final String string = charSequence.toString();
        int i = this.indexIn(string);
        if (i != -1) {
            final char[] charArray = string.toCharArray();
            int n = 1;
        Label_0025:
            while (true) {
                ++i;
                while (i != charArray.length) {
                    if (this.matches(charArray[i])) {
                        ++n;
                        continue Label_0025;
                    }
                    charArray[i - n] = charArray[i];
                    ++i;
                }
                break;
            }
            return new String(charArray, 0, i - n);
        }
        return string;
    }
    
    @CheckReturnValue
    public String replaceFrom(final CharSequence charSequence, final char c) {
        final String string = charSequence.toString();
        int i = this.indexIn(string);
        if (i != -1) {
            final char[] charArray = string.toCharArray();
            charArray[i] = c;
            ++i;
            while (i < charArray.length) {
                if (this.matches(charArray[i])) {
                    charArray[i] = c;
                }
                ++i;
            }
            return new String(charArray);
        }
        return string;
    }
    
    @CheckReturnValue
    public String replaceFrom(final CharSequence charSequence, final CharSequence s) {
        int start = 0;
        final int length = s.length();
        if (length == 0) {
            return this.removeFrom(charSequence);
        }
        if (length == 1) {
            return this.replaceFrom(charSequence, s.charAt(0));
        }
        final String string = charSequence.toString();
        int indexIn = this.indexIn(string);
        if (indexIn != -1) {
            final int length2 = string.length();
            final StringBuilder sb = new StringBuilder(length2 * 3 / 2 + 16);
            int indexIn2;
            int start2;
            do {
                sb.append(string, start, indexIn);
                sb.append(s);
                start2 = indexIn + 1;
                indexIn2 = this.indexIn(string, start2);
                start = start2;
            } while ((indexIn = indexIn2) != -1);
            sb.append(string, start2, length2);
            return sb.toString();
        }
        return string;
    }
    
    @CheckReturnValue
    public String retainFrom(final CharSequence charSequence) {
        return this.negate().removeFrom(charSequence);
    }
    
    @GwtIncompatible("java.util.BitSet")
    void setBits(final BitSet set) {
        for (int i = 65535; i >= 0; --i) {
            if (this.matches((char)i)) {
                set.set(i);
            }
        }
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    @CheckReturnValue
    public String trimAndCollapseFrom(final CharSequence charSequence, final char c) {
        final int length = charSequence.length();
        final int n = length - 1;
        while (true) {
            for (int i = 0; i < length; ++i) {
                int n2 = n;
                if (!this.matches(charSequence.charAt(i))) {
                    while (n2 > i && this.matches(charSequence.charAt(n2))) {
                        --n2;
                    }
                    String s;
                    if (i == 0 && n2 == length - 1) {
                        s = this.collapseFrom(charSequence, c);
                    }
                    else {
                        s = this.finishCollapseFrom(charSequence, i, n2 + 1, c, new StringBuilder(n2 + 1 - i), false);
                    }
                    return s;
                }
            }
            int n2 = n;
            continue;
        }
    }
    
    @CheckReturnValue
    public String trimFrom(final CharSequence charSequence) {
        int length;
        int n;
        for (length = charSequence.length(), n = 0; n < length && this.matches(charSequence.charAt(n)); ++n) {}
        --length;
        while (length > n && this.matches(charSequence.charAt(length))) {
            --length;
        }
        return charSequence.subSequence(n, length + 1).toString();
    }
    
    @CheckReturnValue
    public String trimLeadingFrom(final CharSequence charSequence) {
        for (int i = 0, length = charSequence.length(); i < length; ++i) {
            if (!this.matches(charSequence.charAt(i))) {
                return charSequence.subSequence(i, length).toString();
            }
        }
        return "";
    }
    
    @CheckReturnValue
    public String trimTrailingFrom(final CharSequence charSequence) {
        int length = charSequence.length();
        int n;
        do {
            n = length - 1;
            if (n < 0) {
                return "";
            }
            length = n;
        } while (this.matches(charSequence.charAt(n)));
        return charSequence.subSequence(0, n + 1).toString();
    }
    
    private static final class And extends CharMatcher
    {
        final CharMatcher first;
        final CharMatcher second;
        
        And(final CharMatcher charMatcher, final CharMatcher charMatcher2) {
            this.first = Preconditions.checkNotNull(charMatcher);
            this.second = Preconditions.checkNotNull(charMatcher2);
        }
        
        @Override
        public boolean matches(final char c) {
            boolean b = false;
            if (this.first.matches(c) && this.second.matches(c)) {
                b = true;
            }
            return b;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            final BitSet set2 = new BitSet();
            this.first.setBits(set2);
            final BitSet set3 = new BitSet();
            this.second.setBits(set3);
            set2.and(set3);
            set.or(set2);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.and(" + this.first + ", " + this.second + ")";
        }
    }
    
    private static final class Any extends NamedFastMatcher
    {
        static final Any INSTANCE;
        
        static {
            INSTANCE = new Any();
        }
        
        private Any() {
            super("CharMatcher.any()");
        }
        
        @Override
        public CharMatcher and(final CharMatcher charMatcher) {
            return Preconditions.checkNotNull(charMatcher);
        }
        
        @Override
        public String collapseFrom(final CharSequence charSequence, final char c) {
            String value;
            if (charSequence.length() != 0) {
                value = String.valueOf(c);
            }
            else {
                value = "";
            }
            return value;
        }
        
        @Override
        public int countIn(final CharSequence charSequence) {
            return charSequence.length();
        }
        
        @Override
        public int indexIn(final CharSequence charSequence) {
            int n = 0;
            if (charSequence.length() == 0) {
                n = -1;
            }
            return n;
        }
        
        @Override
        public int indexIn(final CharSequence charSequence, int n) {
            final int length = charSequence.length();
            Preconditions.checkPositionIndex(n, length);
            if (n == length) {
                n = -1;
            }
            return n;
        }
        
        @Override
        public int lastIndexIn(final CharSequence charSequence) {
            return charSequence.length() - 1;
        }
        
        @Override
        public boolean matches(final char c) {
            return true;
        }
        
        @Override
        public boolean matchesAllOf(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return true;
        }
        
        @Override
        public boolean matchesNoneOf(final CharSequence charSequence) {
            boolean b = false;
            if (charSequence.length() == 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public CharMatcher negate() {
            return CharMatcher.none();
        }
        
        @Override
        public CharMatcher or(final CharMatcher charMatcher) {
            Preconditions.checkNotNull(charMatcher);
            return this;
        }
        
        @Override
        public String removeFrom(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return "";
        }
        
        @Override
        public String replaceFrom(final CharSequence charSequence, final char val) {
            final char[] array = new char[charSequence.length()];
            Arrays.fill(array, val);
            return new String(array);
        }
        
        @Override
        public String replaceFrom(final CharSequence charSequence, final CharSequence s) {
            final StringBuilder sb = new StringBuilder(charSequence.length() * s.length());
            for (int i = 0; i < charSequence.length(); ++i) {
                sb.append(s);
            }
            return sb.toString();
        }
        
        @Override
        public String trimFrom(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return "";
        }
    }
    
    private static final class AnyOf extends CharMatcher
    {
        private final char[] chars;
        
        public AnyOf(final CharSequence charSequence) {
            Arrays.sort(this.chars = charSequence.toString().toCharArray());
        }
        
        @Override
        public boolean matches(final char key) {
            boolean b = false;
            if (Arrays.binarySearch(this.chars, key) >= 0) {
                b = true;
            }
            return b;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            final char[] chars = this.chars;
            for (int length = chars.length, i = 0; i < length; ++i) {
                set.set(chars[i]);
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CharMatcher.anyOf(\"");
            final char[] chars = this.chars;
            for (int length = chars.length, i = 0; i < length; ++i) {
                sb.append(showCharacter(chars[i]));
            }
            sb.append("\")");
            return sb.toString();
        }
    }
    
    private static final class Ascii extends NamedFastMatcher
    {
        static final Ascii INSTANCE;
        
        static {
            INSTANCE = new Ascii();
        }
        
        Ascii() {
            super("CharMatcher.ascii()");
        }
        
        @Override
        public boolean matches(final char c) {
            return c <= '\u007f';
        }
    }
    
    @GwtIncompatible("java.util.BitSet")
    private static final class BitSetMatcher extends NamedFastMatcher
    {
        private final BitSet table;
        
        private BitSetMatcher(BitSet table, final String s) {
            super(s);
            if (table.length() + 64 < table.size()) {
                table = (BitSet)table.clone();
            }
            this.table = table;
        }
        
        @Override
        public boolean matches(final char bitIndex) {
            return this.table.get(bitIndex);
        }
        
        @Override
        void setBits(final BitSet set) {
            set.or(this.table);
        }
    }
    
    private static final class BreakingWhitespace extends CharMatcher
    {
        static final CharMatcher INSTANCE;
        
        static {
            INSTANCE = new BreakingWhitespace();
        }
        
        @Override
        public boolean matches(final char c) {
            boolean b = true;
            switch (c) {
                default: {
                    if (c < '\u2000' || c > '\u200a') {
                        b = false;
                    }
                    return b;
                }
                case '\t':
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case ' ':
                case '\u0085':
                case '\u1680':
                case '\u2028':
                case '\u2029':
                case '\u205f':
                case '\u3000': {
                    return true;
                }
                case '\u2007': {
                    return false;
                }
            }
        }
        
        @Override
        public String toString() {
            return "CharMatcher.breakingWhitespace()";
        }
    }
    
    private static final class Digit extends RangesMatcher
    {
        static final Digit INSTANCE;
        private static final String ZEROES = "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10";
        
        static {
            INSTANCE = new Digit();
        }
        
        private Digit() {
            super("CharMatcher.digit()", zeroes(), nines());
        }
        
        private static char[] nines() {
            final char[] array = new char["0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length()];
            for (int i = 0; i < "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length(); ++i) {
                array[i] = (char)("0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".charAt(i) + '\t');
            }
            return array;
        }
        
        private static char[] zeroes() {
            return "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".toCharArray();
        }
    }
    
    abstract static class FastMatcher extends CharMatcher
    {
        @Override
        public CharMatcher negate() {
            return new NegatedFastMatcher(this);
        }
        
        @Override
        public final CharMatcher precomputed() {
            return this;
        }
    }
    
    private static final class ForPredicate extends CharMatcher
    {
        private final Predicate<? super Character> predicate;
        
        ForPredicate(final Predicate<? super Character> predicate) {
            this.predicate = Preconditions.checkNotNull(predicate);
        }
        
        @Override
        public boolean apply(final Character c) {
            return this.predicate.apply(Preconditions.checkNotNull(c));
        }
        
        @Override
        public boolean matches(final char c) {
            return this.predicate.apply(c);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.forPredicate(" + this.predicate + ")";
        }
    }
    
    private static final class InRange extends FastMatcher
    {
        private final char endInclusive;
        private final char startInclusive;
        
        InRange(final char c, final char c2) {
            Preconditions.checkArgument(c2 >= c);
            this.startInclusive = c;
            this.endInclusive = c2;
        }
        
        @Override
        public boolean matches(final char c) {
            return this.startInclusive <= c && c <= this.endInclusive;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            set.set(this.startInclusive, this.endInclusive + '\u0001');
        }
        
        @Override
        public String toString() {
            return "CharMatcher.inRange('" + showCharacter(this.startInclusive) + "', '" + showCharacter(this.endInclusive) + "')";
        }
    }
    
    private static final class Invisible extends RangesMatcher
    {
        static final Invisible INSTANCE;
        private static final String RANGE_ENDS = "  \u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f\u202f\u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb";
        private static final String RANGE_STARTS = "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa";
        
        static {
            INSTANCE = new Invisible();
        }
        
        private Invisible() {
            super("CharMatcher.invisible()", "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa".toCharArray(), "  \u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f\u202f\u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb".toCharArray());
        }
    }
    
    private static final class Is extends FastMatcher
    {
        private final char match;
        
        Is(final char c) {
            this.match = c;
        }
        
        @Override
        public CharMatcher and(final CharMatcher charMatcher) {
            CharMatcher none = this;
            if (!charMatcher.matches(this.match)) {
                none = CharMatcher.none();
            }
            return none;
        }
        
        @Override
        public boolean matches(final char c) {
            return c == this.match;
        }
        
        @Override
        public CharMatcher negate() {
            return CharMatcher.isNot(this.match);
        }
        
        @Override
        public CharMatcher or(final CharMatcher charMatcher) {
            CharMatcher or = charMatcher;
            if (!charMatcher.matches(this.match)) {
                or = super.or(charMatcher);
            }
            return or;
        }
        
        @Override
        public String replaceFrom(final CharSequence charSequence, final char newChar) {
            return charSequence.toString().replace(this.match, newChar);
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            set.set(this.match);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.is('" + showCharacter(this.match) + "')";
        }
    }
    
    private static final class IsEither extends FastMatcher
    {
        private final char match1;
        private final char match2;
        
        IsEither(final char c, final char c2) {
            this.match1 = c;
            this.match2 = c2;
        }
        
        @Override
        public boolean matches(final char c) {
            return c == this.match1 || c == this.match2;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            set.set(this.match1);
            set.set(this.match2);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.anyOf(\"" + showCharacter(this.match1) + showCharacter(this.match2) + "\")";
        }
    }
    
    private static final class IsNot extends FastMatcher
    {
        private final char match;
        
        IsNot(final char c) {
            this.match = c;
        }
        
        @Override
        public CharMatcher and(CharMatcher and) {
            if (and.matches(this.match)) {
                and = super.and(and);
            }
            return and;
        }
        
        @Override
        public boolean matches(final char c) {
            return c != this.match;
        }
        
        @Override
        public CharMatcher negate() {
            return CharMatcher.is(this.match);
        }
        
        @Override
        public CharMatcher or(CharMatcher any) {
            if (!any.matches(this.match)) {
                any = this;
            }
            else {
                any = CharMatcher.any();
            }
            return any;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            set.set(0, this.match);
            set.set(this.match + '\u0001', 65536);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.isNot('" + showCharacter(this.match) + "')";
        }
    }
    
    private static final class JavaDigit extends CharMatcher
    {
        static final JavaDigit INSTANCE;
        
        static {
            INSTANCE = new JavaDigit();
        }
        
        @Override
        public boolean matches(final char ch) {
            return Character.isDigit(ch);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.javaDigit()";
        }
    }
    
    private static final class JavaIsoControl extends NamedFastMatcher
    {
        static final JavaIsoControl INSTANCE;
        
        static {
            INSTANCE = new JavaIsoControl();
        }
        
        private JavaIsoControl() {
            super("CharMatcher.javaIsoControl()");
        }
        
        @Override
        public boolean matches(final char c) {
            return c <= '\u001f' || (c >= '\u007f' && c <= '\u009f');
        }
    }
    
    private static final class JavaLetter extends CharMatcher
    {
        static final JavaLetter INSTANCE;
        
        static {
            INSTANCE = new JavaLetter();
        }
        
        @Override
        public boolean matches(final char ch) {
            return Character.isLetter(ch);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.javaLetter()";
        }
    }
    
    private static final class JavaLetterOrDigit extends CharMatcher
    {
        static final JavaLetterOrDigit INSTANCE;
        
        static {
            INSTANCE = new JavaLetterOrDigit();
        }
        
        @Override
        public boolean matches(final char ch) {
            return Character.isLetterOrDigit(ch);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.javaLetterOrDigit()";
        }
    }
    
    private static final class JavaLowerCase extends CharMatcher
    {
        static final JavaLowerCase INSTANCE;
        
        static {
            INSTANCE = new JavaLowerCase();
        }
        
        @Override
        public boolean matches(final char ch) {
            return Character.isLowerCase(ch);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.javaLowerCase()";
        }
    }
    
    private static final class JavaUpperCase extends CharMatcher
    {
        static final JavaUpperCase INSTANCE;
        
        static {
            INSTANCE = new JavaUpperCase();
        }
        
        @Override
        public boolean matches(final char ch) {
            return Character.isUpperCase(ch);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.javaUpperCase()";
        }
    }
    
    abstract static class NamedFastMatcher extends FastMatcher
    {
        private final String description;
        
        NamedFastMatcher(final String s) {
            this.description = Preconditions.checkNotNull(s);
        }
        
        @Override
        public final String toString() {
            return this.description;
        }
    }
    
    private static class Negated extends CharMatcher
    {
        final CharMatcher original;
        
        Negated(final CharMatcher charMatcher) {
            this.original = Preconditions.checkNotNull(charMatcher);
        }
        
        @Override
        public int countIn(final CharSequence charSequence) {
            return charSequence.length() - this.original.countIn(charSequence);
        }
        
        @Override
        public boolean matches(final char c) {
            boolean b = false;
            if (!this.original.matches(c)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean matchesAllOf(final CharSequence charSequence) {
            return this.original.matchesNoneOf(charSequence);
        }
        
        @Override
        public boolean matchesNoneOf(final CharSequence charSequence) {
            return this.original.matchesAllOf(charSequence);
        }
        
        @Override
        public CharMatcher negate() {
            return this.original;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            final BitSet set2 = new BitSet();
            this.original.setBits(set2);
            set2.flip(0, 65536);
            set.or(set2);
        }
        
        @Override
        public String toString() {
            return this.original + ".negate()";
        }
    }
    
    static class NegatedFastMatcher extends Negated
    {
        NegatedFastMatcher(final CharMatcher charMatcher) {
            super(charMatcher);
        }
        
        @Override
        public final CharMatcher precomputed() {
            return this;
        }
    }
    
    private static final class None extends NamedFastMatcher
    {
        static final None INSTANCE;
        
        static {
            INSTANCE = new None();
        }
        
        private None() {
            super("CharMatcher.none()");
        }
        
        @Override
        public CharMatcher and(final CharMatcher charMatcher) {
            Preconditions.checkNotNull(charMatcher);
            return this;
        }
        
        @Override
        public String collapseFrom(final CharSequence charSequence, final char c) {
            return charSequence.toString();
        }
        
        @Override
        public int countIn(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return 0;
        }
        
        @Override
        public int indexIn(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return -1;
        }
        
        @Override
        public int indexIn(final CharSequence charSequence, final int n) {
            Preconditions.checkPositionIndex(n, charSequence.length());
            return -1;
        }
        
        @Override
        public int lastIndexIn(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return -1;
        }
        
        @Override
        public boolean matches(final char c) {
            return false;
        }
        
        @Override
        public boolean matchesAllOf(final CharSequence charSequence) {
            boolean b = false;
            if (charSequence.length() == 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean matchesNoneOf(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return true;
        }
        
        @Override
        public CharMatcher negate() {
            return CharMatcher.any();
        }
        
        @Override
        public CharMatcher or(final CharMatcher charMatcher) {
            return Preconditions.checkNotNull(charMatcher);
        }
        
        @Override
        public String removeFrom(final CharSequence charSequence) {
            return charSequence.toString();
        }
        
        @Override
        public String replaceFrom(final CharSequence charSequence, final char c) {
            return charSequence.toString();
        }
        
        @Override
        public String replaceFrom(final CharSequence charSequence, final CharSequence charSequence2) {
            Preconditions.checkNotNull(charSequence2);
            return charSequence.toString();
        }
        
        @Override
        public String trimFrom(final CharSequence charSequence) {
            return charSequence.toString();
        }
        
        @Override
        public String trimLeadingFrom(final CharSequence charSequence) {
            return charSequence.toString();
        }
        
        @Override
        public String trimTrailingFrom(final CharSequence charSequence) {
            return charSequence.toString();
        }
    }
    
    private static final class Or extends CharMatcher
    {
        final CharMatcher first;
        final CharMatcher second;
        
        Or(final CharMatcher charMatcher, final CharMatcher charMatcher2) {
            this.first = Preconditions.checkNotNull(charMatcher);
            this.second = Preconditions.checkNotNull(charMatcher2);
        }
        
        @Override
        public boolean matches(final char c) {
            boolean b = false;
            if (this.first.matches(c) || this.second.matches(c)) {
                b = true;
            }
            return b;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            this.first.setBits(set);
            this.second.setBits(set);
        }
        
        @Override
        public String toString() {
            return "CharMatcher.or(" + this.first + ", " + this.second + ")";
        }
    }
    
    private static class RangesMatcher extends CharMatcher
    {
        private final String description;
        private final char[] rangeEnds;
        private final char[] rangeStarts;
        
        RangesMatcher(final String description, final char[] rangeStarts, final char[] rangeEnds) {
            this.description = description;
            this.rangeStarts = rangeStarts;
            this.rangeEnds = rangeEnds;
            Preconditions.checkArgument(rangeStarts.length == rangeEnds.length);
            for (int i = 0; i < rangeStarts.length; ++i) {
                Preconditions.checkArgument(rangeStarts[i] <= rangeEnds[i]);
                if (i + 1 < rangeStarts.length) {
                    Preconditions.checkArgument(rangeEnds[i] < rangeStarts[i + 1]);
                }
            }
        }
        
        @Override
        public boolean matches(final char key) {
            boolean b = true;
            final int binarySearch = Arrays.binarySearch(this.rangeStarts, key);
            if (binarySearch < 0) {
                final int n = ~binarySearch - 1;
                if (n < 0 || key > this.rangeEnds[n]) {
                    b = false;
                }
                return b;
            }
            return true;
        }
        
        @Override
        public String toString() {
            return this.description;
        }
    }
    
    private static final class SingleWidth extends RangesMatcher
    {
        static final SingleWidth INSTANCE;
        
        static {
            INSTANCE = new SingleWidth();
        }
        
        private SingleWidth() {
            super("CharMatcher.singleWidth()", "\u0000\u05be\u05d0\u05f3\u0600\u0750\u0e00\u1e00\u2100\ufb50\ufe70\uff61".toCharArray(), "\u04f9\u05be\u05ea\u05f4\u06ff\u077f\u0e7f\u20af\u213a\ufdff\ufeff\uffdc".toCharArray());
        }
    }
    
    @VisibleForTesting
    static final class Whitespace extends NamedFastMatcher
    {
        static final Whitespace INSTANCE;
        static final int MULTIPLIER = 1682554634;
        static final int SHIFT;
        static final String TABLE = "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f \f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000";
        
        static {
            SHIFT = Integer.numberOfLeadingZeros("\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f \f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".length() - 1);
            INSTANCE = new Whitespace();
        }
        
        Whitespace() {
            super("CharMatcher.whitespace()");
        }
        
        @Override
        public boolean matches(final char c) {
            return "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f \f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".charAt(1682554634 * c >>> Whitespace.SHIFT) == c;
        }
        
        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(final BitSet set) {
            for (int i = 0; i < "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f \f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".length(); ++i) {
                set.set("\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f \f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".charAt(i));
            }
        }
    }
}
