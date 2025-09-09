package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.primitives.UnsignedBytes;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.RoundingMode;
import java.util.Arrays;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
@Beta
public abstract class BaseEncoding {
    private static final BaseEncoding BASE16 = new Base16Encoding("base16()", "0123456789ABCDEF");
    private static final BaseEncoding BASE32 = new StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", '=');
    private static final BaseEncoding BASE32_HEX = new StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", '=');
    private static final BaseEncoding BASE64 = new Base64Encoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", '=');
    private static final BaseEncoding BASE64_URL = new Base64Encoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", '=');

    private static final class Alphabet extends CharMatcher {
        final int bitsPerChar;
        final int bytesPerChunk;
        /* access modifiers changed from: private */
        public final char[] chars;
        final int charsPerChunk;
        private final byte[] decodabet;
        final int mask;
        private final String name;
        private final boolean[] validPadding;

        Alphabet(String str, char[] cArr) {
            this.name = (String) Preconditions.checkNotNull(str);
            this.chars = (char[]) Preconditions.checkNotNull(cArr);
            try {
                this.bitsPerChar = IntMath.log2(cArr.length, RoundingMode.UNNECESSARY);
                int min = Math.min(8, Integer.lowestOneBit(this.bitsPerChar));
                this.charsPerChunk = 8 / min;
                this.bytesPerChunk = this.bitsPerChar / min;
                this.mask = cArr.length - 1;
                byte[] bArr = new byte[128];
                Arrays.fill(bArr, (byte) -1);
                for (int i = 0; i < cArr.length; i++) {
                    char c = cArr[i];
                    Preconditions.checkArgument(CharMatcher.ASCII.matches(c), "Non-ASCII character: %s", Character.valueOf(c));
                    Preconditions.checkArgument(bArr[c] == -1, "Duplicate character: %s", Character.valueOf(c));
                    bArr[c] = (byte) ((byte) i);
                }
                this.decodabet = bArr;
                boolean[] zArr = new boolean[this.charsPerChunk];
                for (int i2 = 0; i2 < this.bytesPerChunk; i2++) {
                    zArr[IntMath.divide(i2 * 8, this.bitsPerChar, RoundingMode.CEILING)] = true;
                }
                this.validPadding = zArr;
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Illegal alphabet length " + cArr.length, e);
            }
        }

        private boolean hasLowerCase() {
            for (char isLowerCase : this.chars) {
                if (Ascii.isLowerCase(isLowerCase)) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasUpperCase() {
            for (char isUpperCase : this.chars) {
                if (Ascii.isUpperCase(isUpperCase)) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public int decode(char c) throws DecodingException {
            if (c <= 127 && this.decodabet[c] != -1) {
                return this.decodabet[c];
            }
            throw new DecodingException("Unrecognized character: " + (!CharMatcher.INVISIBLE.matches(c) ? Character.valueOf(c) : "0x" + Integer.toHexString(c)));
        }

        /* access modifiers changed from: package-private */
        public char encode(int i) {
            return this.chars[i];
        }

        /* access modifiers changed from: package-private */
        public boolean isValidPaddingStartPosition(int i) {
            return this.validPadding[i % this.charsPerChunk];
        }

        /* access modifiers changed from: package-private */
        public Alphabet lowerCase() {
            if (!hasUpperCase()) {
                return this;
            }
            Preconditions.checkState(!hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
            char[] cArr = new char[this.chars.length];
            for (int i = 0; i < this.chars.length; i++) {
                cArr[i] = (char) Ascii.toLowerCase(this.chars[i]);
            }
            return new Alphabet(this.name + ".lowerCase()", cArr);
        }

        public boolean matches(char c) {
            return CharMatcher.ASCII.matches(c) && this.decodabet[c] != -1;
        }

        public String toString() {
            return this.name;
        }

        /* access modifiers changed from: package-private */
        public Alphabet upperCase() {
            if (!hasLowerCase()) {
                return this;
            }
            Preconditions.checkState(!hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
            char[] cArr = new char[this.chars.length];
            for (int i = 0; i < this.chars.length; i++) {
                cArr[i] = (char) Ascii.toUpperCase(this.chars[i]);
            }
            return new Alphabet(this.name + ".upperCase()", cArr);
        }
    }

    static final class Base16Encoding extends StandardBaseEncoding {
        final char[] encoding;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        private Base16Encoding(Alphabet alphabet) {
            super(alphabet, (Character) null);
            this.encoding = new char[512];
            Preconditions.checkArgument(alphabet.chars.length == 16);
            for (int i = 0; i < 256; i++) {
                this.encoding[i] = (char) alphabet.encode(i >>> 4);
                this.encoding[i | 256] = (char) alphabet.encode(i & 15);
            }
        }

        Base16Encoding(String str, String str2) {
            this(new Alphabet(str, str2.toCharArray()));
        }

        /* access modifiers changed from: package-private */
        public int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
            int i = 0;
            Preconditions.checkNotNull(bArr);
            if (charSequence.length() % 2 != 1) {
                int i2 = 0;
                while (i < charSequence.length()) {
                    bArr[i2] = (byte) ((byte) (this.alphabet.decode(charSequence.charAt(i + 1)) | (this.alphabet.decode(charSequence.charAt(i)) << 4)));
                    i += 2;
                    i2++;
                }
                return i2;
            }
            throw new DecodingException("Invalid input length " + charSequence.length());
        }

        /* access modifiers changed from: package-private */
        public void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            for (int i3 = 0; i3 < i2; i3++) {
                byte b = bArr[i + i3] & UnsignedBytes.MAX_VALUE;
                appendable.append(this.encoding[b]);
                appendable.append(this.encoding[b | 256]);
            }
        }

        /* access modifiers changed from: package-private */
        public BaseEncoding newInstance(Alphabet alphabet, @Nullable Character ch) {
            return new Base16Encoding(alphabet);
        }
    }

    static final class Base64Encoding extends StandardBaseEncoding {
        private Base64Encoding(Alphabet alphabet, @Nullable Character ch) {
            super(alphabet, ch);
            Preconditions.checkArgument(alphabet.chars.length == 64);
        }

        Base64Encoding(String str, String str2, @Nullable Character ch) {
            this(new Alphabet(str, str2.toCharArray()), ch);
        }

        /* access modifiers changed from: package-private */
        public int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
            int i = 0;
            Preconditions.checkNotNull(bArr);
            String trimTrailingFrom = padding().trimTrailingFrom(charSequence);
            if (this.alphabet.isValidPaddingStartPosition(trimTrailingFrom.length())) {
                int i2 = 0;
                while (i < trimTrailingFrom.length()) {
                    int i3 = i + 1;
                    int decode = this.alphabet.decode(trimTrailingFrom.charAt(i)) << 18;
                    i = i3 + 1;
                    int decode2 = (this.alphabet.decode(trimTrailingFrom.charAt(i3)) << 12) | decode;
                    int i4 = i2 + 1;
                    bArr[i2] = (byte) ((byte) (decode2 >>> 16));
                    if (i >= trimTrailingFrom.length()) {
                        i2 = i4;
                    } else {
                        int i5 = i + 1;
                        int decode3 = decode2 | (this.alphabet.decode(trimTrailingFrom.charAt(i)) << 6);
                        int i6 = i4 + 1;
                        bArr[i4] = (byte) ((byte) ((decode3 >>> 8) & 255));
                        if (i5 >= trimTrailingFrom.length()) {
                            i = i5;
                            i2 = i6;
                        } else {
                            i = i5 + 1;
                            int decode4 = decode3 | this.alphabet.decode(trimTrailingFrom.charAt(i5));
                            i2 = i6 + 1;
                            bArr[i6] = (byte) ((byte) (decode4 & 255));
                        }
                    }
                }
                return i2;
            }
            throw new DecodingException("Invalid input length " + trimTrailingFrom.length());
        }

        /* access modifiers changed from: package-private */
        public void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            int i3 = i;
            for (int i4 = i2; i4 >= 3; i4 -= 3) {
                int i5 = i3 + 1;
                int i6 = i5 + 1;
                byte b = ((bArr[i5] & UnsignedBytes.MAX_VALUE) << 8) | ((bArr[i3] & UnsignedBytes.MAX_VALUE) << 16);
                i3 = i6 + 1;
                byte b2 = b | (bArr[i6] & UnsignedBytes.MAX_VALUE);
                appendable.append(this.alphabet.encode(b2 >>> Ascii.DC2));
                appendable.append(this.alphabet.encode((b2 >>> Ascii.FF) & 63));
                appendable.append(this.alphabet.encode((b2 >>> 6) & 63));
                appendable.append(this.alphabet.encode(b2 & 63));
            }
            if (i3 < i + i2) {
                encodeChunkTo(appendable, bArr, i3, (i + i2) - i3);
            }
        }

        /* access modifiers changed from: package-private */
        public BaseEncoding newInstance(Alphabet alphabet, @Nullable Character ch) {
            return new Base64Encoding(alphabet, ch);
        }
    }

    public static final class DecodingException extends IOException {
        DecodingException(String str) {
            super(str);
        }

        DecodingException(Throwable th) {
            super(th);
        }
    }

    static final class SeparatedBaseEncoding extends BaseEncoding {
        private final int afterEveryChars;
        private final BaseEncoding delegate;
        private final String separator;
        private final CharMatcher separatorChars;

        SeparatedBaseEncoding(BaseEncoding baseEncoding, String str, int i) {
            this.delegate = (BaseEncoding) Preconditions.checkNotNull(baseEncoding);
            this.separator = (String) Preconditions.checkNotNull(str);
            this.afterEveryChars = i;
            Preconditions.checkArgument(i > 0, "Cannot add a separator after every %s chars", Integer.valueOf(i));
            this.separatorChars = CharMatcher.anyOf(str).precomputed();
        }

        /* access modifiers changed from: package-private */
        public int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
            return this.delegate.decodeTo(bArr, this.separatorChars.removeFrom(charSequence));
        }

        @GwtIncompatible("Reader,InputStream")
        public InputStream decodingStream(Reader reader) {
            return this.delegate.decodingStream(ignoringReader(reader, this.separatorChars));
        }

        /* access modifiers changed from: package-private */
        public void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            this.delegate.encodeTo(separatingAppendable(appendable, this.separator, this.afterEveryChars), bArr, i, i2);
        }

        @GwtIncompatible("Writer,OutputStream")
        public OutputStream encodingStream(Writer writer) {
            return this.delegate.encodingStream(separatingWriter(writer, this.separator, this.afterEveryChars));
        }

        public BaseEncoding lowerCase() {
            return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
        }

        /* access modifiers changed from: package-private */
        public int maxDecodedSize(int i) {
            return this.delegate.maxDecodedSize(i);
        }

        /* access modifiers changed from: package-private */
        public int maxEncodedSize(int i) {
            int maxEncodedSize = this.delegate.maxEncodedSize(i);
            return maxEncodedSize + (this.separator.length() * IntMath.divide(Math.max(0, maxEncodedSize - 1), this.afterEveryChars, RoundingMode.FLOOR));
        }

        public BaseEncoding omitPadding() {
            return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
        }

        /* access modifiers changed from: package-private */
        public CharMatcher padding() {
            return this.delegate.padding();
        }

        public String toString() {
            return this.delegate.toString() + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
        }

        public BaseEncoding upperCase() {
            return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
        }

        public BaseEncoding withPadChar(char c) {
            return this.delegate.withPadChar(c).withSeparator(this.separator, this.afterEveryChars);
        }

        public BaseEncoding withSeparator(String str, int i) {
            throw new UnsupportedOperationException("Already have a separator");
        }
    }

    static class StandardBaseEncoding extends BaseEncoding {
        final Alphabet alphabet;
        private transient BaseEncoding lowerCase;
        @Nullable
        final Character paddingChar;
        private transient BaseEncoding upperCase;

        StandardBaseEncoding(Alphabet alphabet2, @Nullable Character ch) {
            this.alphabet = (Alphabet) Preconditions.checkNotNull(alphabet2);
            Preconditions.checkArgument(ch == null || !alphabet2.matches(ch.charValue()), "Padding character %s was already in alphabet", ch);
            this.paddingChar = ch;
        }

        StandardBaseEncoding(String str, String str2, @Nullable Character ch) {
            this(new Alphabet(str, str2.toCharArray()), ch);
        }

        /* access modifiers changed from: package-private */
        public int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
            Preconditions.checkNotNull(bArr);
            String trimTrailingFrom = padding().trimTrailingFrom(charSequence);
            if (this.alphabet.isValidPaddingStartPosition(trimTrailingFrom.length())) {
                int i = 0;
                int i2 = 0;
                while (i2 < trimTrailingFrom.length()) {
                    long j = 0;
                    int i3 = 0;
                    for (int i4 = 0; i4 < this.alphabet.charsPerChunk; i4++) {
                        long j2 = j << this.alphabet.bitsPerChar;
                        if (i2 + i4 < trimTrailingFrom.length()) {
                            j2 |= (long) this.alphabet.decode(trimTrailingFrom.charAt(i3 + i2));
                            i3++;
                        }
                        j = j2;
                    }
                    int i5 = (this.alphabet.bytesPerChunk * 8) - (i3 * this.alphabet.bitsPerChar);
                    int i6 = (this.alphabet.bytesPerChunk - 1) * 8;
                    while (i6 >= i5) {
                        bArr[i] = (byte) ((byte) ((int) ((j >>> i6) & 255)));
                        i6 -= 8;
                        i++;
                    }
                    i2 += this.alphabet.charsPerChunk;
                }
                return i;
            }
            throw new DecodingException("Invalid input length " + trimTrailingFrom.length());
        }

        @GwtIncompatible("Reader,InputStream")
        public InputStream decodingStream(final Reader reader) {
            Preconditions.checkNotNull(reader);
            return new InputStream() {
                int bitBuffer = 0;
                int bitBufferLength = 0;
                boolean hitPadding = false;
                final CharMatcher paddingMatcher = StandardBaseEncoding.this.padding();
                int readChars = 0;

                public void close() throws IOException {
                    reader.close();
                }

                public int read() throws IOException {
                    while (true) {
                        int read = reader.read();
                        if (read != -1) {
                            this.readChars++;
                            char c = (char) read;
                            if (!this.paddingMatcher.matches(c)) {
                                if (!this.hitPadding) {
                                    this.bitBuffer <<= StandardBaseEncoding.this.alphabet.bitsPerChar;
                                    this.bitBuffer = StandardBaseEncoding.this.alphabet.decode(c) | this.bitBuffer;
                                    this.bitBufferLength += StandardBaseEncoding.this.alphabet.bitsPerChar;
                                    if (this.bitBufferLength >= 8) {
                                        this.bitBufferLength -= 8;
                                        return (this.bitBuffer >> this.bitBufferLength) & 255;
                                    }
                                } else {
                                    throw new DecodingException("Expected padding character but found '" + c + "' at index " + this.readChars);
                                }
                            } else if (this.hitPadding || (this.readChars != 1 && StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1))) {
                                this.hitPadding = true;
                            }
                        } else if (this.hitPadding || StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars)) {
                            return -1;
                        } else {
                            throw new DecodingException("Invalid input length " + this.readChars);
                        }
                    }
                    throw new DecodingException("Padding cannot start at index " + this.readChars);
                }
            };
        }

        /* access modifiers changed from: package-private */
        public void encodeChunkTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            Preconditions.checkArgument(i2 <= this.alphabet.bytesPerChunk);
            long j = 0;
            for (int i3 = 0; i3 < i2; i3++) {
                j = (j | ((long) (bArr[i + i3] & UnsignedBytes.MAX_VALUE))) << 8;
            }
            int i4 = ((i2 + 1) * 8) - this.alphabet.bitsPerChar;
            int i5 = 0;
            while (i5 < i2 * 8) {
                appendable.append(this.alphabet.encode(((int) (j >>> (i4 - i5))) & this.alphabet.mask));
                i5 += this.alphabet.bitsPerChar;
            }
            if (this.paddingChar != null) {
                while (i5 < this.alphabet.bytesPerChunk * 8) {
                    appendable.append(this.paddingChar.charValue());
                    i5 += this.alphabet.bitsPerChar;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            int i3 = 0;
            while (i3 < i2) {
                encodeChunkTo(appendable, bArr, i + i3, Math.min(this.alphabet.bytesPerChunk, i2 - i3));
                i3 += this.alphabet.bytesPerChunk;
            }
        }

        @GwtIncompatible("Writer,OutputStream")
        public OutputStream encodingStream(final Writer writer) {
            Preconditions.checkNotNull(writer);
            return new OutputStream() {
                int bitBuffer = 0;
                int bitBufferLength = 0;
                int writtenChars = 0;

                public void close() throws IOException {
                    if (this.bitBufferLength > 0) {
                        writer.write(StandardBaseEncoding.this.alphabet.encode((this.bitBuffer << (StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength)) & StandardBaseEncoding.this.alphabet.mask));
                        this.writtenChars++;
                        if (StandardBaseEncoding.this.paddingChar != null) {
                            while (this.writtenChars % StandardBaseEncoding.this.alphabet.charsPerChunk != 0) {
                                writer.write(StandardBaseEncoding.this.paddingChar.charValue());
                                this.writtenChars++;
                            }
                        }
                    }
                    writer.close();
                }

                public void flush() throws IOException {
                    writer.flush();
                }

                public void write(int i) throws IOException {
                    this.bitBuffer <<= 8;
                    this.bitBuffer |= i & 255;
                    this.bitBufferLength += 8;
                    while (this.bitBufferLength >= StandardBaseEncoding.this.alphabet.bitsPerChar) {
                        writer.write(StandardBaseEncoding.this.alphabet.encode((this.bitBuffer >> (this.bitBufferLength - StandardBaseEncoding.this.alphabet.bitsPerChar)) & StandardBaseEncoding.this.alphabet.mask));
                        this.writtenChars++;
                        this.bitBufferLength -= StandardBaseEncoding.this.alphabet.bitsPerChar;
                    }
                }
            };
        }

        public BaseEncoding lowerCase() {
            BaseEncoding baseEncoding = this.lowerCase;
            if (baseEncoding == null) {
                Alphabet lowerCase2 = this.alphabet.lowerCase();
                baseEncoding = lowerCase2 != this.alphabet ? newInstance(lowerCase2, this.paddingChar) : this;
                this.lowerCase = baseEncoding;
            }
            return baseEncoding;
        }

        /* access modifiers changed from: package-private */
        public int maxDecodedSize(int i) {
            return (int) (((((long) this.alphabet.bitsPerChar) * ((long) i)) + 7) / 8);
        }

        /* access modifiers changed from: package-private */
        public int maxEncodedSize(int i) {
            return this.alphabet.charsPerChunk * IntMath.divide(i, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
        }

        /* access modifiers changed from: package-private */
        public BaseEncoding newInstance(Alphabet alphabet2, @Nullable Character ch) {
            return new StandardBaseEncoding(alphabet2, ch);
        }

        public BaseEncoding omitPadding() {
            return this.paddingChar != null ? newInstance(this.alphabet, (Character) null) : this;
        }

        /* access modifiers changed from: package-private */
        public CharMatcher padding() {
            return this.paddingChar != null ? CharMatcher.is(this.paddingChar.charValue()) : CharMatcher.NONE;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("BaseEncoding.");
            sb.append(this.alphabet.toString());
            if (8 % this.alphabet.bitsPerChar != 0) {
                if (this.paddingChar != null) {
                    sb.append(".withPadChar(").append(this.paddingChar).append(')');
                } else {
                    sb.append(".omitPadding()");
                }
            }
            return sb.toString();
        }

        public BaseEncoding upperCase() {
            BaseEncoding baseEncoding = this.upperCase;
            if (baseEncoding == null) {
                Alphabet upperCase2 = this.alphabet.upperCase();
                baseEncoding = upperCase2 != this.alphabet ? newInstance(upperCase2, this.paddingChar) : this;
                this.upperCase = baseEncoding;
            }
            return baseEncoding;
        }

        public BaseEncoding withPadChar(char c) {
            return (8 % this.alphabet.bitsPerChar != 0 && (this.paddingChar == null || this.paddingChar.charValue() != c)) ? newInstance(this.alphabet, Character.valueOf(c)) : this;
        }

        public BaseEncoding withSeparator(String str, int i) {
            Preconditions.checkArgument(padding().or(this.alphabet).matchesNoneOf(str), "Separator (%s) cannot contain alphabet or padding characters", str);
            return new SeparatedBaseEncoding(this, str, i);
        }
    }

    BaseEncoding() {
    }

    public static BaseEncoding base16() {
        return BASE16;
    }

    public static BaseEncoding base32() {
        return BASE32;
    }

    public static BaseEncoding base32Hex() {
        return BASE32_HEX;
    }

    public static BaseEncoding base64() {
        return BASE64;
    }

    public static BaseEncoding base64Url() {
        return BASE64_URL;
    }

    private static byte[] extract(byte[] bArr, int i) {
        if (i == bArr.length) {
            return bArr;
        }
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, i);
        return bArr2;
    }

    @GwtIncompatible("Reader")
    static Reader ignoringReader(final Reader reader, final CharMatcher charMatcher) {
        Preconditions.checkNotNull(reader);
        Preconditions.checkNotNull(charMatcher);
        return new Reader() {
            public void close() throws IOException {
                reader.close();
            }

            /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
                jadx.core.utils.exceptions.JadxOverflowException: 
                	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
                	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
                */
            public int read() throws java.io.IOException {
                /*
                    r3 = this;
                L_0x0000:
                    java.io.Reader r0 = r1
                    int r0 = r0.read()
                    r1 = -1
                    if (r0 != r1) goto L_0x000a
                L_0x0009:
                    return r0
                L_0x000a:
                    com.google.common.base.CharMatcher r1 = r2
                    char r2 = (char) r0
                    boolean r1 = r1.matches(r2)
                    if (r1 == 0) goto L_0x0009
                    goto L_0x0000
                */
                throw new UnsupportedOperationException("Method not decompiled: com.google.common.io.BaseEncoding.AnonymousClass3.read():int");
            }

            public int read(char[] cArr, int i, int i2) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }

    static Appendable separatingAppendable(final Appendable appendable, final String str, final int i) {
        boolean z = false;
        Preconditions.checkNotNull(appendable);
        Preconditions.checkNotNull(str);
        if (i > 0) {
            z = true;
        }
        Preconditions.checkArgument(z);
        return new Appendable() {
            int charsUntilSeparator = i;

            public Appendable append(char c) throws IOException {
                if (this.charsUntilSeparator == 0) {
                    appendable.append(str);
                    this.charsUntilSeparator = i;
                }
                appendable.append(c);
                this.charsUntilSeparator--;
                return this;
            }

            public Appendable append(CharSequence charSequence) throws IOException {
                throw new UnsupportedOperationException();
            }

            public Appendable append(CharSequence charSequence, int i, int i2) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }

    @GwtIncompatible("Writer")
    static Writer separatingWriter(final Writer writer, String str, int i) {
        final Appendable separatingAppendable = separatingAppendable(writer, str, i);
        return new Writer() {
            public void close() throws IOException {
                writer.close();
            }

            public void flush() throws IOException {
                writer.flush();
            }

            public void write(int i) throws IOException {
                separatingAppendable.append((char) i);
            }

            public void write(char[] cArr, int i, int i2) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }

    public final byte[] decode(CharSequence charSequence) {
        try {
            return decodeChecked(charSequence);
        } catch (DecodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /* access modifiers changed from: package-private */
    public final byte[] decodeChecked(CharSequence charSequence) throws DecodingException {
        String trimTrailingFrom = padding().trimTrailingFrom(charSequence);
        byte[] bArr = new byte[maxDecodedSize(trimTrailingFrom.length())];
        return extract(bArr, decodeTo(bArr, trimTrailingFrom));
    }

    /* access modifiers changed from: package-private */
    public abstract int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException;

    @GwtIncompatible("ByteSource,CharSource")
    public final ByteSource decodingSource(final CharSource charSource) {
        Preconditions.checkNotNull(charSource);
        return new ByteSource() {
            public InputStream openStream() throws IOException {
                return BaseEncoding.this.decodingStream(charSource.openStream());
            }
        };
    }

    @GwtIncompatible("Reader,InputStream")
    public abstract InputStream decodingStream(Reader reader);

    public String encode(byte[] bArr) {
        return encode(bArr, 0, bArr.length);
    }

    public final String encode(byte[] bArr, int i, int i2) {
        Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
        StringBuilder sb = new StringBuilder(maxEncodedSize(i2));
        try {
            encodeTo(sb, bArr, i, i2);
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /* access modifiers changed from: package-private */
    public abstract void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException;

    @GwtIncompatible("ByteSink,CharSink")
    public final ByteSink encodingSink(final CharSink charSink) {
        Preconditions.checkNotNull(charSink);
        return new ByteSink() {
            public OutputStream openStream() throws IOException {
                return BaseEncoding.this.encodingStream(charSink.openStream());
            }
        };
    }

    @GwtIncompatible("Writer,OutputStream")
    public abstract OutputStream encodingStream(Writer writer);

    @CheckReturnValue
    public abstract BaseEncoding lowerCase();

    /* access modifiers changed from: package-private */
    public abstract int maxDecodedSize(int i);

    /* access modifiers changed from: package-private */
    public abstract int maxEncodedSize(int i);

    @CheckReturnValue
    public abstract BaseEncoding omitPadding();

    /* access modifiers changed from: package-private */
    public abstract CharMatcher padding();

    @CheckReturnValue
    public abstract BaseEncoding upperCase();

    @CheckReturnValue
    public abstract BaseEncoding withPadChar(char c);

    @CheckReturnValue
    public abstract BaseEncoding withSeparator(String str, int i);
}
