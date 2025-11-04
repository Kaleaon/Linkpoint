// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import javax.annotation.Nullable;
import java.io.Serializable;
import com.google.common.base.Ascii;
import java.util.Arrays;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import javax.annotation.CheckReturnValue;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Writer;
import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import com.google.common.base.Preconditions;
import com.google.common.base.CharMatcher;
import java.io.Reader;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public abstract class BaseEncoding
{
    private static final BaseEncoding BASE16;
    private static final BaseEncoding BASE32;
    private static final BaseEncoding BASE32_HEX;
    private static final BaseEncoding BASE64;
    private static final BaseEncoding BASE64_URL;
    
    static {
        BASE64 = new Base64Encoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", '=');
        BASE64_URL = new Base64Encoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", '=');
        BASE32 = new StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", '=');
        BASE32_HEX = new StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", '=');
        BASE16 = new Base16Encoding("base16()", "0123456789ABCDEF");
    }
    
    BaseEncoding() {
    }
    
    public static BaseEncoding base16() {
        return BaseEncoding.BASE16;
    }
    
    public static BaseEncoding base32() {
        return BaseEncoding.BASE32;
    }
    
    public static BaseEncoding base32Hex() {
        return BaseEncoding.BASE32_HEX;
    }
    
    public static BaseEncoding base64() {
        return BaseEncoding.BASE64;
    }
    
    public static BaseEncoding base64Url() {
        return BaseEncoding.BASE64_URL;
    }
    
    private static byte[] extract(final byte[] array, final int n) {
        if (n != array.length) {
            final byte[] array2 = new byte[n];
            System.arraycopy(array, 0, array2, 0, n);
            return array2;
        }
        return array;
    }
    
    @GwtIncompatible("Reader")
    static Reader ignoringReader(final Reader reader, final CharMatcher charMatcher) {
        Preconditions.checkNotNull(reader);
        Preconditions.checkNotNull(charMatcher);
        return new Reader() {
            @Override
            public void close() throws IOException {
                reader.close();
            }
            
            @Override
            public int read() throws IOException {
                int read;
                do {
                    read = reader.read();
                } while (read != -1 && charMatcher.matches((char)read));
                return read;
            }
            
            @Override
            public int read(final char[] array, final int n, final int n2) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    static Appendable separatingAppendable(final Appendable appendable, final String s, final int n) {
        boolean b = false;
        Preconditions.checkNotNull(appendable);
        Preconditions.checkNotNull(s);
        if (n > 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        return new Appendable() {
            int charsUntilSeparator = n;
            
            @Override
            public Appendable append(final char c) throws IOException {
                if (this.charsUntilSeparator == 0) {
                    appendable.append(s);
                    this.charsUntilSeparator = n;
                }
                appendable.append(c);
                --this.charsUntilSeparator;
                return this;
            }
            
            @Override
            public Appendable append(final CharSequence charSequence) throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Appendable append(final CharSequence charSequence, final int n, final int n2) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @GwtIncompatible("Writer")
    static Writer separatingWriter(final Writer writer, final String s, final int n) {
        return new Writer() {
            final /* synthetic */ Appendable val$seperatingAppendable = separatingAppendable(writer, s, n);
            
            @Override
            public void close() throws IOException {
                writer.close();
            }
            
            @Override
            public void flush() throws IOException {
                writer.flush();
            }
            
            @Override
            public void write(final int n) throws IOException {
                this.val$seperatingAppendable.append((char)n);
            }
            
            @Override
            public void write(final char[] array, final int n, final int n2) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public final byte[] decode(final CharSequence charSequence) {
        try {
            return this.decodeChecked(charSequence);
        }
        catch (final DecodingException cause) {
            throw new IllegalArgumentException(cause);
        }
    }
    
    final byte[] decodeChecked(final CharSequence charSequence) throws DecodingException {
        final String trimTrailing = this.padding().trimTrailingFrom(charSequence);
        final byte[] array = new byte[this.maxDecodedSize(trimTrailing.length())];
        return extract(array, this.decodeTo(array, trimTrailing));
    }
    
    abstract int decodeTo(final byte[] p0, final CharSequence p1) throws DecodingException;
    
    @GwtIncompatible("ByteSource,CharSource")
    public final ByteSource decodingSource(final CharSource charSource) {
        Preconditions.checkNotNull(charSource);
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return BaseEncoding.this.decodingStream(charSource.openStream());
            }
        };
    }
    
    @GwtIncompatible("Reader,InputStream")
    public abstract InputStream decodingStream(final Reader p0);
    
    public String encode(final byte[] array) {
        return this.encode(array, 0, array.length);
    }
    
    public final String encode(final byte[] array, final int n, final int n2) {
        Preconditions.checkPositionIndexes(n, n + n2, array.length);
        final StringBuilder sb = new StringBuilder(this.maxEncodedSize(n2));
        try {
            this.encodeTo(sb, array, n, n2);
            return sb.toString();
        }
        catch (final IOException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    abstract void encodeTo(final Appendable p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    @GwtIncompatible("ByteSink,CharSink")
    public final ByteSink encodingSink(final CharSink charSink) {
        Preconditions.checkNotNull(charSink);
        return new ByteSink() {
            @Override
            public OutputStream openStream() throws IOException {
                return BaseEncoding.this.encodingStream(charSink.openStream());
            }
        };
    }
    
    @GwtIncompatible("Writer,OutputStream")
    public abstract OutputStream encodingStream(final Writer p0);
    
    @CheckReturnValue
    public abstract BaseEncoding lowerCase();
    
    abstract int maxDecodedSize(final int p0);
    
    abstract int maxEncodedSize(final int p0);
    
    @CheckReturnValue
    public abstract BaseEncoding omitPadding();
    
    abstract CharMatcher padding();
    
    @CheckReturnValue
    public abstract BaseEncoding upperCase();
    
    @CheckReturnValue
    public abstract BaseEncoding withPadChar(final char p0);
    
    @CheckReturnValue
    public abstract BaseEncoding withSeparator(final String p0, final int p1);
    
    private static final class Alphabet extends CharMatcher
    {
        final int bitsPerChar;
        final int bytesPerChunk;
        private final char[] chars;
        final int charsPerChunk;
        private final byte[] decodabet;
        final int mask;
        private final String name;
        private final boolean[] validPadding;
        
        Alphabet(final String s, final char[] array) {
        Label_0118_Outer:
            while (true) {
                final int n = 0;
                this.name = Preconditions.checkNotNull(s);
                this.chars = Preconditions.checkNotNull(array);
                while (true) {
                    boolean[] validPadding = null;
                    int n3 = 0;
                Label_0245:
                    while (true) {
                        byte[] array2;
                        int n2;
                        try {
                            this.bitsPerChar = IntMath.log2(array.length, RoundingMode.UNNECESSARY);
                            final int min = Math.min(8, Integer.lowestOneBit(this.bitsPerChar));
                            this.charsPerChunk = 8 / min;
                            this.bytesPerChunk = this.bitsPerChar / min;
                            this.mask = array.length - 1;
                            array2 = new byte[128];
                            Arrays.fill(array2, (byte)(-1));
                            n2 = 0;
                            if (n2 >= array.length) {
                                this.decodabet = array2;
                                validPadding = new boolean[this.charsPerChunk];
                                n3 = n;
                                if (n3 >= this.bytesPerChunk) {
                                    this.validPadding = validPadding;
                                    return;
                                }
                                break Label_0245;
                            }
                        }
                        catch (final ArithmeticException cause) {
                            throw new IllegalArgumentException("Illegal alphabet length " + array.length, cause);
                        }
                        final char c = array[n2];
                        Preconditions.checkArgument(CharMatcher.ASCII.matches(c), "Non-ASCII character: %s", c);
                        Preconditions.checkArgument(array2[c] == -1, "Duplicate character: %s", c);
                        array2[c] = (byte)n2;
                        ++n2;
                        continue Label_0118_Outer;
                    }
                    validPadding[IntMath.divide(n3 * 8, this.bitsPerChar, RoundingMode.CEILING)] = true;
                    ++n3;
                    continue;
                }
            }
        }
        
        private boolean hasLowerCase() {
            final char[] chars = this.chars;
            for (int length = chars.length, i = 0; i < length; ++i) {
                if (com.google.common.base.Ascii.isLowerCase(chars[i])) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean hasUpperCase() {
            final char[] chars = this.chars;
            for (int length = chars.length, i = 0; i < length; ++i) {
                if (com.google.common.base.Ascii.isUpperCase(chars[i])) {
                    return true;
                }
            }
            return false;
        }
        
        int decode(final char c) throws DecodingException {
            if (c <= '\u007f' && this.decodabet[c] != -1) {
                return this.decodabet[c];
            }
            final StringBuilder append = new StringBuilder().append("Unrecognized character: ");
            Serializable obj;
            if (!CharMatcher.INVISIBLE.matches(c)) {
                obj = c;
            }
            else {
                obj = "0x" + Integer.toHexString(c);
            }
            throw new DecodingException(append.append(obj).toString());
        }
        
        char encode(final int n) {
            return this.chars[n];
        }
        
        boolean isValidPaddingStartPosition(final int n) {
            return this.validPadding[n % this.charsPerChunk];
        }
        
        Alphabet lowerCase() {
            int i = 0;
            if (this.hasUpperCase()) {
                Preconditions.checkState(!this.hasLowerCase(), (Object)"Cannot call lowerCase() on a mixed-case alphabet");
                final char[] array = new char[this.chars.length];
                while (i < this.chars.length) {
                    array[i] = com.google.common.base.Ascii.toLowerCase(this.chars[i]);
                    ++i;
                }
                return new Alphabet(this.name + ".lowerCase()", array);
            }
            return this;
        }
        
        @Override
        public boolean matches(final char c) {
            boolean b = false;
            if (CharMatcher.ASCII.matches(c) && this.decodabet[c] != -1) {
                b = true;
            }
            return b;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        Alphabet upperCase() {
            int i = 0;
            if (this.hasLowerCase()) {
                Preconditions.checkState(!this.hasUpperCase(), (Object)"Cannot call upperCase() on a mixed-case alphabet");
                final char[] array = new char[this.chars.length];
                while (i < this.chars.length) {
                    array[i] = com.google.common.base.Ascii.toUpperCase(this.chars[i]);
                    ++i;
                }
                return new Alphabet(this.name + ".upperCase()", array);
            }
            return this;
        }
    }
    
    static final class Base16Encoding extends StandardBaseEncoding
    {
        final char[] encoding;
        
        private Base16Encoding(final Alphabet alphabet) {
            int i = 0;
            super(alphabet, null);
            this.encoding = new char[512];
            Preconditions.checkArgument(alphabet.chars.length == 16);
            while (i < 256) {
                this.encoding[i] = alphabet.encode(i >>> 4);
                this.encoding[i | 0x100] = alphabet.encode(i & 0xF);
                ++i;
            }
        }
        
        Base16Encoding(final String s, final String s2) {
            this(new Alphabet(s, s2.toCharArray()));
        }
        
        @Override
        int decodeTo(final byte[] array, final CharSequence charSequence) throws DecodingException {
            int i = 0;
            Preconditions.checkNotNull(array);
            if (charSequence.length() % 2 != 1) {
                int n;
                for (n = 0; i < charSequence.length(); i += 2, ++n) {
                    array[n] = (byte)(this.alphabet.decode(charSequence.charAt(i + 1)) | this.alphabet.decode(charSequence.charAt(i)) << 4);
                }
                return n;
            }
            throw new DecodingException("Invalid input length " + charSequence.length());
        }
        
        @Override
        void encodeTo(final Appendable appendable, final byte[] array, final int n, final int n2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(n, n + n2, array.length);
            for (int i = 0; i < n2; ++i) {
                final int n3 = array[n + i] & 0xFF;
                appendable.append(this.encoding[n3]);
                appendable.append(this.encoding[n3 | 0x100]);
            }
        }
        
        @Override
        BaseEncoding newInstance(final Alphabet alphabet, @Nullable final Character c) {
            return new Base16Encoding(alphabet);
        }
    }
    
    static final class Base64Encoding extends StandardBaseEncoding
    {
        private Base64Encoding(final Alphabet alphabet, @Nullable final Character c) {
            super(alphabet, c);
            Preconditions.checkArgument(alphabet.chars.length == 64);
        }
        
        Base64Encoding(final String s, final String s2, @Nullable final Character c) {
            this(new Alphabet(s, s2.toCharArray()), c);
        }
        
        @Override
        int decodeTo(final byte[] array, final CharSequence charSequence) throws DecodingException {
            int i = 0;
            Preconditions.checkNotNull(array);
            final String trimTrailing = ((StandardBaseEncoding)this).padding().trimTrailingFrom(charSequence);
            if (this.alphabet.isValidPaddingStartPosition(trimTrailing.length())) {
                int n = 0;
                while (i < trimTrailing.length()) {
                    final Alphabet alphabet = this.alphabet;
                    final int n2 = i + 1;
                    final int decode = alphabet.decode(trimTrailing.charAt(i));
                    final Alphabet alphabet2 = this.alphabet;
                    i = n2 + 1;
                    final int n3 = alphabet2.decode(trimTrailing.charAt(n2)) << 12 | decode << 18;
                    final int n4 = n + 1;
                    array[n] = (byte)(n3 >>> 16);
                    if (i >= trimTrailing.length()) {
                        n = n4;
                    }
                    else {
                        final Alphabet alphabet3 = this.alphabet;
                        final int n5 = i + 1;
                        final int n6 = n3 | alphabet3.decode(trimTrailing.charAt(i)) << 6;
                        n = n4 + 1;
                        array[n4] = (byte)(n6 >>> 8 & 0xFF);
                        if (n5 >= trimTrailing.length()) {
                            i = n5;
                        }
                        else {
                            final Alphabet alphabet4 = this.alphabet;
                            i = n5 + 1;
                            final int decode2 = alphabet4.decode(trimTrailing.charAt(n5));
                            final int n7 = n + 1;
                            array[n] = (byte)((n6 | decode2) & 0xFF);
                            n = n7;
                        }
                    }
                }
                return n;
            }
            throw new DecodingException("Invalid input length " + trimTrailing.length());
        }
        
        @Override
        void encodeTo(final Appendable appendable, final byte[] array, final int n, final int n2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(n, n + n2, array.length);
            int i = n2;
            int n3 = n;
            while (i >= 3) {
                final int n4 = n3 + 1;
                final byte b = array[n3];
                final int n5 = n4 + 1;
                final byte b2 = array[n4];
                n3 = n5 + 1;
                final int n6 = (b2 & 0xFF) << 8 | (b & 0xFF) << 16 | (array[n5] & 0xFF);
                appendable.append(this.alphabet.encode(n6 >>> 18));
                appendable.append(this.alphabet.encode(n6 >>> 12 & 0x3F));
                appendable.append(this.alphabet.encode(n6 >>> 6 & 0x3F));
                appendable.append(this.alphabet.encode(n6 & 0x3F));
                i -= 3;
            }
            if (n3 < n + n2) {
                ((StandardBaseEncoding)this).encodeChunkTo(appendable, array, n3, n + n2 - n3);
            }
        }
        
        @Override
        BaseEncoding newInstance(final Alphabet alphabet, @Nullable final Character c) {
            return new Base64Encoding(alphabet, c);
        }
    }
    
    public static final class DecodingException extends IOException
    {
        DecodingException(final String message) {
            super(message);
        }
        
        DecodingException(final Throwable cause) {
            super(cause);
        }
    }
    
    static final class SeparatedBaseEncoding extends BaseEncoding
    {
        private final int afterEveryChars;
        private final BaseEncoding delegate;
        private final String separator;
        private final CharMatcher separatorChars;
        
        SeparatedBaseEncoding(final BaseEncoding baseEncoding, final String s, final int n) {
            this.delegate = Preconditions.checkNotNull(baseEncoding);
            this.separator = Preconditions.checkNotNull(s);
            this.afterEveryChars = n;
            Preconditions.checkArgument(n > 0, "Cannot add a separator after every %s chars", n);
            this.separatorChars = CharMatcher.anyOf(s).precomputed();
        }
        
        @Override
        int decodeTo(final byte[] array, final CharSequence charSequence) throws DecodingException {
            return this.delegate.decodeTo(array, this.separatorChars.removeFrom(charSequence));
        }
        
        @GwtIncompatible("Reader,InputStream")
        @Override
        public InputStream decodingStream(final Reader reader) {
            return this.delegate.decodingStream(BaseEncoding.ignoringReader(reader, this.separatorChars));
        }
        
        @Override
        void encodeTo(final Appendable appendable, final byte[] array, final int n, final int n2) throws IOException {
            this.delegate.encodeTo(BaseEncoding.separatingAppendable(appendable, this.separator, this.afterEveryChars), array, n, n2);
        }
        
        @GwtIncompatible("Writer,OutputStream")
        @Override
        public OutputStream encodingStream(final Writer writer) {
            return this.delegate.encodingStream(BaseEncoding.separatingWriter(writer, this.separator, this.afterEveryChars));
        }
        
        @Override
        public BaseEncoding lowerCase() {
            return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
        }
        
        @Override
        int maxDecodedSize(final int n) {
            return this.delegate.maxDecodedSize(n);
        }
        
        @Override
        int maxEncodedSize(int maxEncodedSize) {
            maxEncodedSize = this.delegate.maxEncodedSize(maxEncodedSize);
            return maxEncodedSize + this.separator.length() * IntMath.divide(Math.max(0, maxEncodedSize - 1), this.afterEveryChars, RoundingMode.FLOOR);
        }
        
        @Override
        public BaseEncoding omitPadding() {
            return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
        }
        
        @Override
        CharMatcher padding() {
            return this.delegate.padding();
        }
        
        @Override
        public String toString() {
            return this.delegate.toString() + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
        }
        
        @Override
        public BaseEncoding upperCase() {
            return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
        }
        
        @Override
        public BaseEncoding withPadChar(final char c) {
            return this.delegate.withPadChar(c).withSeparator(this.separator, this.afterEveryChars);
        }
        
        @Override
        public BaseEncoding withSeparator(final String s, final int n) {
            throw new UnsupportedOperationException("Already have a separator");
        }
    }
    
    static class StandardBaseEncoding extends BaseEncoding
    {
        final Alphabet alphabet;
        private transient BaseEncoding lowerCase;
        @Nullable
        final Character paddingChar;
        private transient BaseEncoding upperCase;
        
        StandardBaseEncoding(final Alphabet alphabet, @Nullable final Character paddingChar) {
            this.alphabet = Preconditions.checkNotNull(alphabet);
            Preconditions.checkArgument(paddingChar == null || !alphabet.matches(paddingChar), "Padding character %s was already in alphabet", paddingChar);
            this.paddingChar = paddingChar;
        }
        
        StandardBaseEncoding(final String s, final String s2, @Nullable final Character c) {
            this(new Alphabet(s, s2.toCharArray()), c);
        }
        
        @Override
        int decodeTo(final byte[] array, final CharSequence charSequence) throws DecodingException {
            Preconditions.checkNotNull(array);
            final String trimTrailing = this.padding().trimTrailingFrom(charSequence);
            if (this.alphabet.isValidPaddingStartPosition(trimTrailing.length())) {
                int n = 0;
                for (int i = 0; i < trimTrailing.length(); i += this.alphabet.charsPerChunk) {
                    long n2 = 0L;
                    int n3 = 0;
                    for (int j = 0; j < this.alphabet.charsPerChunk; ++j) {
                        n2 <<= this.alphabet.bitsPerChar;
                        if (i + j < trimTrailing.length()) {
                            final long n4 = this.alphabet.decode(trimTrailing.charAt(n3 + i));
                            ++n3;
                            n2 |= n4;
                        }
                    }
                    for (int bytesPerChunk = this.alphabet.bytesPerChunk, bitsPerChar = this.alphabet.bitsPerChar, k = (this.alphabet.bytesPerChunk - 1) * 8; k >= bytesPerChunk * 8 - n3 * bitsPerChar; k -= 8, ++n) {
                        array[n] = (byte)(n2 >>> k & 0xFFL);
                    }
                }
                return n;
            }
            throw new DecodingException("Invalid input length " + trimTrailing.length());
        }
        
        @GwtIncompatible("Reader,InputStream")
        @Override
        public InputStream decodingStream(final Reader reader) {
            Preconditions.checkNotNull(reader);
            return new InputStream() {
                int bitBuffer = 0;
                int bitBufferLength = 0;
                boolean hitPadding = false;
                final CharMatcher paddingMatcher = StandardBaseEncoding.this.padding();
                int readChars = 0;
                
                @Override
                public void close() throws IOException {
                    reader.close();
                }
                
                @Override
                public int read() throws IOException {
                    while (true) {
                        final int read = reader.read();
                        if (read != -1) {
                            ++this.readChars;
                            final char c = (char)read;
                            if (!this.paddingMatcher.matches(c)) {
                                if (this.hitPadding) {
                                    throw new DecodingException("Expected padding character but found '" + c + "' at index " + this.readChars);
                                }
                                this.bitBuffer <<= StandardBaseEncoding.this.alphabet.bitsPerChar;
                                this.bitBuffer |= StandardBaseEncoding.this.alphabet.decode(c);
                                this.bitBufferLength += StandardBaseEncoding.this.alphabet.bitsPerChar;
                                if (this.bitBufferLength >= 8) {
                                    this.bitBufferLength -= 8;
                                    return this.bitBuffer >> this.bitBufferLength & 0xFF;
                                }
                                continue;
                            }
                            else {
                                if (!this.hitPadding && (this.readChars == 1 || !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1))) {
                                    throw new DecodingException("Padding cannot start at index " + this.readChars);
                                }
                                this.hitPadding = true;
                            }
                        }
                        else {
                            if (!this.hitPadding && !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars)) {
                                throw new DecodingException("Invalid input length " + this.readChars);
                            }
                            return -1;
                        }
                    }
                }
            };
        }
        
        void encodeChunkTo(final Appendable appendable, final byte[] array, int i, final int n) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + n, array.length);
            Preconditions.checkArgument(n <= this.alphabet.bytesPerChunk);
            long n2 = 0L;
            for (int j = 0; j < n; ++j) {
                n2 = (n2 | (long)(array[i + j] & 0xFF)) << 8;
            }
            final int bitsPerChar = this.alphabet.bitsPerChar;
            for (i = 0; i < n * 8; i += this.alphabet.bitsPerChar) {
                appendable.append(this.alphabet.encode((int)(n2 >>> (n + 1) * 8 - bitsPerChar - i) & this.alphabet.mask));
            }
            if (this.paddingChar != null) {
                while (i < this.alphabet.bytesPerChunk * 8) {
                    appendable.append(this.paddingChar);
                    i += this.alphabet.bitsPerChar;
                }
            }
        }
        
        @Override
        void encodeTo(final Appendable appendable, final byte[] array, final int n, final int n2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(n, n + n2, array.length);
            for (int i = 0; i < n2; i += this.alphabet.bytesPerChunk) {
                this.encodeChunkTo(appendable, array, n + i, Math.min(this.alphabet.bytesPerChunk, n2 - i));
            }
        }
        
        @GwtIncompatible("Writer,OutputStream")
        @Override
        public OutputStream encodingStream(final Writer writer) {
            Preconditions.checkNotNull(writer);
            return new OutputStream() {
                int bitBuffer = 0;
                int bitBufferLength = 0;
                int writtenChars = 0;
                
                @Override
                public void close() throws IOException {
                    if (this.bitBufferLength > 0) {
                        writer.write(StandardBaseEncoding.this.alphabet.encode(this.bitBuffer << StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength & StandardBaseEncoding.this.alphabet.mask));
                        ++this.writtenChars;
                        if (StandardBaseEncoding.this.paddingChar != null) {
                            while (this.writtenChars % StandardBaseEncoding.this.alphabet.charsPerChunk != 0) {
                                writer.write(StandardBaseEncoding.this.paddingChar);
                                ++this.writtenChars;
                            }
                        }
                    }
                    writer.close();
                }
                
                @Override
                public void flush() throws IOException {
                    writer.flush();
                }
                
                @Override
                public void write(int bitBufferLength) throws IOException {
                    this.bitBuffer <<= 8;
                    this.bitBuffer |= (bitBufferLength & 0xFF);
                    this.bitBufferLength += 8;
                    while (this.bitBufferLength >= StandardBaseEncoding.this.alphabet.bitsPerChar) {
                        final int bitBuffer = this.bitBuffer;
                        bitBufferLength = this.bitBufferLength;
                        writer.write(StandardBaseEncoding.this.alphabet.encode(bitBuffer >> bitBufferLength - StandardBaseEncoding.this.alphabet.bitsPerChar & StandardBaseEncoding.this.alphabet.mask));
                        ++this.writtenChars;
                        this.bitBufferLength -= StandardBaseEncoding.this.alphabet.bitsPerChar;
                    }
                }
            };
        }
        
        @Override
        public BaseEncoding lowerCase() {
            BaseEncoding lowerCase = this.lowerCase;
            if (lowerCase == null) {
                final Alphabet lowerCase2 = this.alphabet.lowerCase();
                if (lowerCase2 != this.alphabet) {
                    lowerCase = this.newInstance(lowerCase2, this.paddingChar);
                }
                else {
                    lowerCase = this;
                }
                this.lowerCase = lowerCase;
            }
            return lowerCase;
        }
        
        @Override
        int maxDecodedSize(final int n) {
            return (int)((this.alphabet.bitsPerChar * (long)n + 7L) / 8L);
        }
        
        @Override
        int maxEncodedSize(final int n) {
            return this.alphabet.charsPerChunk * IntMath.divide(n, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
        }
        
        BaseEncoding newInstance(final Alphabet alphabet, @Nullable final Character c) {
            return new StandardBaseEncoding(alphabet, c);
        }
        
        @Override
        public BaseEncoding omitPadding() {
            BaseEncoding instance = this;
            if (this.paddingChar != null) {
                instance = this.newInstance(this.alphabet, null);
            }
            return instance;
        }
        
        @Override
        CharMatcher padding() {
            CharMatcher charMatcher;
            if (this.paddingChar != null) {
                charMatcher = CharMatcher.is(this.paddingChar);
            }
            else {
                charMatcher = CharMatcher.NONE;
            }
            return charMatcher;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BaseEncoding.");
            sb.append(this.alphabet.toString());
            if (8 % this.alphabet.bitsPerChar != 0) {
                if (this.paddingChar != null) {
                    sb.append(".withPadChar(").append(this.paddingChar).append(')');
                }
                else {
                    sb.append(".omitPadding()");
                }
            }
            return sb.toString();
        }
        
        @Override
        public BaseEncoding upperCase() {
            BaseEncoding upperCase = this.upperCase;
            if (upperCase == null) {
                final Alphabet upperCase2 = this.alphabet.upperCase();
                if (upperCase2 != this.alphabet) {
                    upperCase = this.newInstance(upperCase2, this.paddingChar);
                }
                else {
                    upperCase = this;
                }
                this.upperCase = upperCase;
            }
            return upperCase;
        }
        
        @Override
        public BaseEncoding withPadChar(final char c) {
            if (8 % this.alphabet.bitsPerChar != 0 && (this.paddingChar == null || this.paddingChar != c)) {
                return this.newInstance(this.alphabet, c);
            }
            return this;
        }
        
        @Override
        public BaseEncoding withSeparator(final String s, final int n) {
            Preconditions.checkArgument(this.padding().or(this.alphabet).matchesNoneOf(s), "Separator (%s) cannot contain alphabet or padding characters", s);
            return new SeparatedBaseEncoding(this, s, n);
        }
    }
}
