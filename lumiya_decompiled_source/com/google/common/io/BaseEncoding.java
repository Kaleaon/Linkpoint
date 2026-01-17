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
@Beta
@GwtCompatible(emulated = true)
/* loaded from: classes.dex */
public abstract class BaseEncoding {
    private static final BaseEncoding BASE64 = new Base64Encoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", '=');
    private static final BaseEncoding BASE64_URL = new Base64Encoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", '=');
    private static final BaseEncoding BASE32 = new StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", '=');
    private static final BaseEncoding BASE32_HEX = new StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", '=');
    private static final BaseEncoding BASE16 = new Base16Encoding("base16()", "0123456789ABCDEF");

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Alphabet extends CharMatcher {
        final int bitsPerChar;
        final int bytesPerChunk;
        private final char[] chars;
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
                    bArr[c] = (byte) i;
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
            for (char c : this.chars) {
                if (Ascii.isLowerCase(c)) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasUpperCase() {
            for (char c : this.chars) {
                if (Ascii.isUpperCase(c)) {
                    return true;
                }
            }
            return false;
        }

        int decode(char c) throws DecodingException {
            if (c <= 127 && this.decodabet[c] != -1) {
                return this.decodabet[c];
            }
            throw new DecodingException("Unrecognized character: " + (!CharMatcher.INVISIBLE.matches(c) ? Character.valueOf(c) : "0x" + Integer.toHexString(c)));
        }

        char encode(int i) {
            return this.chars[i];
        }

        boolean isValidPaddingStartPosition(int i) {
            return this.validPadding[i % this.charsPerChunk];
        }

        Alphabet lowerCase() {
            if (hasUpperCase()) {
                Preconditions.checkState(!hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
                char[] cArr = new char[this.chars.length];
                for (int i = 0; i < this.chars.length; i++) {
                    cArr[i] = Ascii.toLowerCase(this.chars[i]);
                }
                return new Alphabet(this.name + ".lowerCase()", cArr);
            }
            return this;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return CharMatcher.ASCII.matches(c) && this.decodabet[c] != -1;
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return this.name;
        }

        Alphabet upperCase() {
            if (hasLowerCase()) {
                Preconditions.checkState(!hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
                char[] cArr = new char[this.chars.length];
                for (int i = 0; i < this.chars.length; i++) {
                    cArr[i] = Ascii.toUpperCase(this.chars[i]);
                }
                return new Alphabet(this.name + ".upperCase()", cArr);
            }
            return this;
        }
    }

    /* loaded from: classes.dex */
    static final class Base16Encoding extends StandardBaseEncoding {
        final char[] encoding;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private Base16Encoding(Alphabet alphabet) {
            super(alphabet, null);
            this.encoding = new char[512];
            Preconditions.checkArgument(alphabet.chars.length == 16);
            for (int i = 0; i < 256; i++) {
                this.encoding[i] = alphabet.encode(i >>> 4);
                this.encoding[i | 256] = alphabet.encode(i & 15);
            }
        }

        Base16Encoding(String str, String str2) {
            this(new Alphabet(str, str2.toCharArray()));
        }

        @Override // com.google.common.io.BaseEncoding.StandardBaseEncoding, com.google.common.io.BaseEncoding
        int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
            int i = 0;
            Preconditions.checkNotNull(bArr);
            if (charSequence.length() % 2 != 1) {
                int i2 = 0;
                while (i < charSequence.length()) {
                    bArr[i2] = (byte) (this.alphabet.decode(charSequence.charAt(i + 1)) | (this.alphabet.decode(charSequence.charAt(i)) << 4));
                    i += 2;
                    i2++;
                }
                return i2;
            }
            throw new DecodingException("Invalid input length " + charSequence.length());
        }

        @Override // com.google.common.io.BaseEncoding.StandardBaseEncoding, com.google.common.io.BaseEncoding
        void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            for (int i3 = 0; i3 < i2; i3++) {
                int i4 = bArr[i + i3] & UnsignedBytes.MAX_VALUE;
                appendable.append(this.encoding[i4]);
                appendable.append(this.encoding[i4 | 256]);
            }
        }

        @Override // com.google.common.io.BaseEncoding.StandardBaseEncoding
        BaseEncoding newInstance(Alphabet alphabet, @Nullable Character ch) {
            return new Base16Encoding(alphabet);
        }
    }

    /* loaded from: classes.dex */
    static final class Base64Encoding extends StandardBaseEncoding {
        private Base64Encoding(Alphabet alphabet, @Nullable Character ch) {
            super(alphabet, ch);
            Preconditions.checkArgument(alphabet.chars.length == 64);
        }

        Base64Encoding(String str, String str2, @Nullable Character ch) {
            this(new Alphabet(str, str2.toCharArray()), ch);
        }

        @Override // com.google.common.io.BaseEncoding.StandardBaseEncoding, com.google.common.io.BaseEncoding
        int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
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
                    bArr[i2] = (byte) (decode2 >>> 16);
                    if (i >= trimTrailingFrom.length()) {
                        i2 = i4;
                    } else {
                        int i5 = i + 1;
                        int decode3 = decode2 | (this.alphabet.decode(trimTrailingFrom.charAt(i)) << 6);
                        int i6 = i4 + 1;
                        bArr[i4] = (byte) ((decode3 >>> 8) & 255);
                        if (i5 >= trimTrailingFrom.length()) {
                            i = i5;
                            i2 = i6;
                        } else {
                            i = i5 + 1;
                            int decode4 = decode3 | this.alphabet.decode(trimTrailingFrom.charAt(i5));
                            i2 = i6 + 1;
                            bArr[i6] = (byte) (decode4 & 255);
                        }
                    }
                }
                return i2;
            }
            throw new DecodingException("Invalid input length " + trimTrailingFrom.length());
        }

        @Override // com.google.common.io.BaseEncoding.StandardBaseEncoding, com.google.common.io.BaseEncoding
        void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            int i3 = i;
            for (int i4 = i2; i4 >= 3; i4 -= 3) {
                int i5 = i3 + 1;
                int i6 = i5 + 1;
                int i7 = ((bArr[i5] & UnsignedBytes.MAX_VALUE) << 8) | ((bArr[i3] & UnsignedBytes.MAX_VALUE) << 16);
                i3 = i6 + 1;
                int i8 = i7 | (bArr[i6] & UnsignedBytes.MAX_VALUE);
                appendable.append(this.alphabet.encode(i8 >>> 18));
                appendable.append(this.alphabet.encode((i8 >>> 12) & 63));
                appendable.append(this.alphabet.encode((i8 >>> 6) & 63));
                appendable.append(this.alphabet.encode(i8 & 63));
            }
            if (i3 >= i + i2) {
                return;
            }
            encodeChunkTo(appendable, bArr, i3, (i + i2) - i3);
        }

        @Override // com.google.common.io.BaseEncoding.StandardBaseEncoding
        BaseEncoding newInstance(Alphabet alphabet, @Nullable Character ch) {
            return new Base64Encoding(alphabet, ch);
        }
    }

    /* loaded from: classes.dex */
    public static final class DecodingException extends IOException {
        DecodingException(String str) {
            super(str);
        }

        DecodingException(Throwable th) {
            super(th);
        }
    }

    /* loaded from: classes.dex */
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

        @Override // com.google.common.io.BaseEncoding
        int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
            return this.delegate.decodeTo(bArr, this.separatorChars.removeFrom(charSequence));
        }

        @Override // com.google.common.io.BaseEncoding
        @GwtIncompatible("Reader,InputStream")
        public InputStream decodingStream(Reader reader) {
            return this.delegate.decodingStream(ignoringReader(reader, this.separatorChars));
        }

        @Override // com.google.common.io.BaseEncoding
        void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            this.delegate.encodeTo(separatingAppendable(appendable, this.separator, this.afterEveryChars), bArr, i, i2);
        }

        @Override // com.google.common.io.BaseEncoding
        @GwtIncompatible("Writer,OutputStream")
        public OutputStream encodingStream(Writer writer) {
            return this.delegate.encodingStream(separatingWriter(writer, this.separator, this.afterEveryChars));
        }

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding lowerCase() {
            return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
        }

        @Override // com.google.common.io.BaseEncoding
        int maxDecodedSize(int i) {
            return this.delegate.maxDecodedSize(i);
        }

        @Override // com.google.common.io.BaseEncoding
        int maxEncodedSize(int i) {
            int maxEncodedSize = this.delegate.maxEncodedSize(i);
            return maxEncodedSize + (this.separator.length() * IntMath.divide(Math.max(0, maxEncodedSize - 1), this.afterEveryChars, RoundingMode.FLOOR));
        }

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding omitPadding() {
            return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
        }

        @Override // com.google.common.io.BaseEncoding
        CharMatcher padding() {
            return this.delegate.padding();
        }

        public String toString() {
            return this.delegate.toString() + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
        }

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding upperCase() {
            return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
        }

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding withPadChar(char c) {
            return this.delegate.withPadChar(c).withSeparator(this.separator, this.afterEveryChars);
        }

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding withSeparator(String str, int i) {
            throw new UnsupportedOperationException("Already have a separator");
        }
    }

    /* loaded from: classes.dex */
    static class StandardBaseEncoding extends BaseEncoding {
        final Alphabet alphabet;
        private transient BaseEncoding lowerCase;
        @Nullable
        final Character paddingChar;
        private transient BaseEncoding upperCase;

        StandardBaseEncoding(Alphabet alphabet, @Nullable Character ch) {
            this.alphabet = (Alphabet) Preconditions.checkNotNull(alphabet);
            Preconditions.checkArgument(ch == null || !alphabet.matches(ch.charValue()), "Padding character %s was already in alphabet", ch);
            this.paddingChar = ch;
        }

        StandardBaseEncoding(String str, String str2, @Nullable Character ch) {
            this(new Alphabet(str, str2.toCharArray()), ch);
        }

        @Override // com.google.common.io.BaseEncoding
        int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException {
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
                            j2 |= this.alphabet.decode(trimTrailingFrom.charAt(i3 + i2));
                            i3++;
                        }
                        j = j2;
                    }
                    int i5 = (this.alphabet.bytesPerChunk * 8) - (i3 * this.alphabet.bitsPerChar);
                    int i6 = (this.alphabet.bytesPerChunk - 1) * 8;
                    while (i6 >= i5) {
                        bArr[i] = (byte) ((j >>> i6) & 255);
                        i6 -= 8;
                        i++;
                    }
                    i2 += this.alphabet.charsPerChunk;
                }
                return i;
            }
            throw new DecodingException("Invalid input length " + trimTrailingFrom.length());
        }

        @Override // com.google.common.io.BaseEncoding
        @GwtIncompatible("Reader,InputStream")
        public InputStream decodingStream(final Reader reader) {
            Preconditions.checkNotNull(reader);
            return new InputStream() { // from class: com.google.common.io.BaseEncoding.StandardBaseEncoding.2
                final CharMatcher paddingMatcher;
                int bitBuffer = 0;
                int bitBufferLength = 0;
                int readChars = 0;
                boolean hitPadding = false;

                {
                    this.paddingMatcher = StandardBaseEncoding.this.padding();
                }

                @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    reader.close();
                }

                /* JADX WARN: Code restructure failed: missing block: B:26:0x00a8, code lost:
                    throw new com.google.common.io.BaseEncoding.DecodingException("Padding cannot start at index " + r5.readChars);
                 */
                @Override // java.io.InputStream
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public int read() throws java.io.IOException {
                    /*
                        r5 = this;
                        r4 = 1
                        r3 = -1
                    L2:
                        java.io.Reader r0 = r3
                        int r0 = r0.read()
                        if (r0 == r3) goto L54
                        int r1 = r5.readChars
                        int r1 = r1 + 1
                        r5.readChars = r1
                        char r0 = (char) r0
                        com.google.common.base.CharMatcher r1 = r5.paddingMatcher
                        boolean r1 = r1.matches(r0)
                        if (r1 != 0) goto L81
                        boolean r1 = r5.hitPadding
                        if (r1 != 0) goto Lb8
                        int r1 = r5.bitBuffer
                        com.google.common.io.BaseEncoding$StandardBaseEncoding r2 = com.google.common.io.BaseEncoding.StandardBaseEncoding.this
                        com.google.common.io.BaseEncoding$Alphabet r2 = r2.alphabet
                        int r2 = r2.bitsPerChar
                        int r1 = r1 << r2
                        r5.bitBuffer = r1
                        int r1 = r5.bitBuffer
                        com.google.common.io.BaseEncoding$StandardBaseEncoding r2 = com.google.common.io.BaseEncoding.StandardBaseEncoding.this
                        com.google.common.io.BaseEncoding$Alphabet r2 = r2.alphabet
                        int r0 = r2.decode(r0)
                        r0 = r0 | r1
                        r5.bitBuffer = r0
                        int r0 = r5.bitBufferLength
                        com.google.common.io.BaseEncoding$StandardBaseEncoding r1 = com.google.common.io.BaseEncoding.StandardBaseEncoding.this
                        com.google.common.io.BaseEncoding$Alphabet r1 = r1.alphabet
                        int r1 = r1.bitsPerChar
                        int r0 = r0 + r1
                        r5.bitBufferLength = r0
                        int r0 = r5.bitBufferLength
                        r1 = 8
                        if (r0 < r1) goto L2
                        int r0 = r5.bitBufferLength
                        int r0 = r0 + (-8)
                        r5.bitBufferLength = r0
                        int r0 = r5.bitBuffer
                        int r1 = r5.bitBufferLength
                        int r0 = r0 >> r1
                        r0 = r0 & 255(0xff, float:3.57E-43)
                        return r0
                    L54:
                        boolean r0 = r5.hitPadding
                        if (r0 == 0) goto L59
                    L58:
                        return r3
                    L59:
                        com.google.common.io.BaseEncoding$StandardBaseEncoding r0 = com.google.common.io.BaseEncoding.StandardBaseEncoding.this
                        com.google.common.io.BaseEncoding$Alphabet r0 = r0.alphabet
                        int r1 = r5.readChars
                        boolean r0 = r0.isValidPaddingStartPosition(r1)
                        if (r0 != 0) goto L58
                        com.google.common.io.BaseEncoding$DecodingException r0 = new com.google.common.io.BaseEncoding$DecodingException
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r2 = "Invalid input length "
                        java.lang.StringBuilder r1 = r1.append(r2)
                        int r2 = r5.readChars
                        java.lang.StringBuilder r1 = r1.append(r2)
                        java.lang.String r1 = r1.toString()
                        r0.<init>(r1)
                        throw r0
                    L81:
                        boolean r0 = r5.hitPadding
                        if (r0 == 0) goto L89
                    L85:
                        r5.hitPadding = r4
                        goto L2
                    L89:
                        int r0 = r5.readChars
                        if (r0 != r4) goto La9
                    L8d:
                        com.google.common.io.BaseEncoding$DecodingException r0 = new com.google.common.io.BaseEncoding$DecodingException
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r2 = "Padding cannot start at index "
                        java.lang.StringBuilder r1 = r1.append(r2)
                        int r2 = r5.readChars
                        java.lang.StringBuilder r1 = r1.append(r2)
                        java.lang.String r1 = r1.toString()
                        r0.<init>(r1)
                        throw r0
                    La9:
                        com.google.common.io.BaseEncoding$StandardBaseEncoding r0 = com.google.common.io.BaseEncoding.StandardBaseEncoding.this
                        com.google.common.io.BaseEncoding$Alphabet r0 = r0.alphabet
                        int r1 = r5.readChars
                        int r1 = r1 + (-1)
                        boolean r0 = r0.isValidPaddingStartPosition(r1)
                        if (r0 == 0) goto L8d
                        goto L85
                    Lb8:
                        com.google.common.io.BaseEncoding$DecodingException r1 = new com.google.common.io.BaseEncoding$DecodingException
                        java.lang.StringBuilder r2 = new java.lang.StringBuilder
                        r2.<init>()
                        java.lang.String r3 = "Expected padding character but found '"
                        java.lang.StringBuilder r2 = r2.append(r3)
                        java.lang.StringBuilder r0 = r2.append(r0)
                        java.lang.String r2 = "' at index "
                        java.lang.StringBuilder r0 = r0.append(r2)
                        int r2 = r5.readChars
                        java.lang.StringBuilder r0 = r0.append(r2)
                        java.lang.String r0 = r0.toString()
                        r1.<init>(r0)
                        throw r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.google.common.io.BaseEncoding.StandardBaseEncoding.AnonymousClass2.read():int");
                }
            };
        }

        void encodeChunkTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            Preconditions.checkArgument(i2 <= this.alphabet.bytesPerChunk);
            long j = 0;
            for (int i3 = 0; i3 < i2; i3++) {
                j = (j | (bArr[i + i3] & UnsignedBytes.MAX_VALUE)) << 8;
            }
            int i4 = ((i2 + 1) * 8) - this.alphabet.bitsPerChar;
            int i5 = 0;
            while (i5 < i2 * 8) {
                appendable.append(this.alphabet.encode(((int) (j >>> (i4 - i5))) & this.alphabet.mask));
                i5 += this.alphabet.bitsPerChar;
            }
            if (this.paddingChar == null) {
                return;
            }
            while (i5 < this.alphabet.bytesPerChunk * 8) {
                appendable.append(this.paddingChar.charValue());
                i5 += this.alphabet.bitsPerChar;
            }
        }

        @Override // com.google.common.io.BaseEncoding
        void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException {
            Preconditions.checkNotNull(appendable);
            Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
            int i3 = 0;
            while (i3 < i2) {
                encodeChunkTo(appendable, bArr, i + i3, Math.min(this.alphabet.bytesPerChunk, i2 - i3));
                i3 += this.alphabet.bytesPerChunk;
            }
        }

        @Override // com.google.common.io.BaseEncoding
        @GwtIncompatible("Writer,OutputStream")
        public OutputStream encodingStream(final Writer writer) {
            Preconditions.checkNotNull(writer);
            return new OutputStream() { // from class: com.google.common.io.BaseEncoding.StandardBaseEncoding.1
                int bitBuffer = 0;
                int bitBufferLength = 0;
                int writtenChars = 0;

                @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
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

                @Override // java.io.OutputStream, java.io.Flushable
                public void flush() throws IOException {
                    writer.flush();
                }

                @Override // java.io.OutputStream
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

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding lowerCase() {
            BaseEncoding baseEncoding = this.lowerCase;
            if (baseEncoding == null) {
                Alphabet lowerCase = this.alphabet.lowerCase();
                baseEncoding = lowerCase != this.alphabet ? newInstance(lowerCase, this.paddingChar) : this;
                this.lowerCase = baseEncoding;
            }
            return baseEncoding;
        }

        @Override // com.google.common.io.BaseEncoding
        int maxDecodedSize(int i) {
            return (int) (((this.alphabet.bitsPerChar * i) + 7) / 8);
        }

        @Override // com.google.common.io.BaseEncoding
        int maxEncodedSize(int i) {
            return this.alphabet.charsPerChunk * IntMath.divide(i, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
        }

        BaseEncoding newInstance(Alphabet alphabet, @Nullable Character ch) {
            return new StandardBaseEncoding(alphabet, ch);
        }

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding omitPadding() {
            return this.paddingChar != null ? newInstance(this.alphabet, null) : this;
        }

        @Override // com.google.common.io.BaseEncoding
        CharMatcher padding() {
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

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding upperCase() {
            BaseEncoding baseEncoding = this.upperCase;
            if (baseEncoding == null) {
                Alphabet upperCase = this.alphabet.upperCase();
                baseEncoding = upperCase != this.alphabet ? newInstance(upperCase, this.paddingChar) : this;
                this.upperCase = baseEncoding;
            }
            return baseEncoding;
        }

        @Override // com.google.common.io.BaseEncoding
        public BaseEncoding withPadChar(char c) {
            return (8 % this.alphabet.bitsPerChar != 0 && (this.paddingChar == null || this.paddingChar.charValue() != c)) ? newInstance(this.alphabet, Character.valueOf(c)) : this;
        }

        @Override // com.google.common.io.BaseEncoding
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
        if (i != bArr.length) {
            byte[] bArr2 = new byte[i];
            System.arraycopy(bArr, 0, bArr2, 0, i);
            return bArr2;
        }
        return bArr;
    }

    @GwtIncompatible("Reader")
    static Reader ignoringReader(final Reader reader, final CharMatcher charMatcher) {
        Preconditions.checkNotNull(reader);
        Preconditions.checkNotNull(charMatcher);
        return new Reader() { // from class: com.google.common.io.BaseEncoding.3
            @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                reader.close();
            }

            @Override // java.io.Reader
            public int read() throws IOException {
                int read;
                do {
                    read = reader.read();
                    if (read == -1) {
                        break;
                    }
                } while (charMatcher.matches((char) read));
                return read;
            }

            @Override // java.io.Reader
            public int read(char[] cArr, int i, int i2) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }

    static Appendable separatingAppendable(final Appendable appendable, final String str, final int i) {
        Preconditions.checkNotNull(appendable);
        Preconditions.checkNotNull(str);
        Preconditions.checkArgument(i > 0);
        return new Appendable() { // from class: com.google.common.io.BaseEncoding.4
            int charsUntilSeparator;

            {
                this.charsUntilSeparator = i;
            }

            @Override // java.lang.Appendable
            public Appendable append(char c) throws IOException {
                if (this.charsUntilSeparator == 0) {
                    appendable.append(str);
                    this.charsUntilSeparator = i;
                }
                appendable.append(c);
                this.charsUntilSeparator--;
                return this;
            }

            @Override // java.lang.Appendable
            public Appendable append(CharSequence charSequence) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override // java.lang.Appendable
            public Appendable append(CharSequence charSequence, int i2, int i3) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }

    @GwtIncompatible("Writer")
    static Writer separatingWriter(final Writer writer, String str, int i) {
        final Appendable separatingAppendable = separatingAppendable(writer, str, i);
        return new Writer() { // from class: com.google.common.io.BaseEncoding.5
            @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                writer.close();
            }

            @Override // java.io.Writer, java.io.Flushable
            public void flush() throws IOException {
                writer.flush();
            }

            @Override // java.io.Writer
            public void write(int i2) throws IOException {
                separatingAppendable.append((char) i2);
            }

            @Override // java.io.Writer
            public void write(char[] cArr, int i2, int i3) throws IOException {
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

    final byte[] decodeChecked(CharSequence charSequence) throws DecodingException {
        String trimTrailingFrom = padding().trimTrailingFrom(charSequence);
        byte[] bArr = new byte[maxDecodedSize(trimTrailingFrom.length())];
        return extract(bArr, decodeTo(bArr, trimTrailingFrom));
    }

    abstract int decodeTo(byte[] bArr, CharSequence charSequence) throws DecodingException;

    @GwtIncompatible("ByteSource,CharSource")
    public final ByteSource decodingSource(final CharSource charSource) {
        Preconditions.checkNotNull(charSource);
        return new ByteSource() { // from class: com.google.common.io.BaseEncoding.2
            @Override // com.google.common.io.ByteSource
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

    abstract void encodeTo(Appendable appendable, byte[] bArr, int i, int i2) throws IOException;

    @GwtIncompatible("ByteSink,CharSink")
    public final ByteSink encodingSink(final CharSink charSink) {
        Preconditions.checkNotNull(charSink);
        return new ByteSink() { // from class: com.google.common.io.BaseEncoding.1
            @Override // com.google.common.io.ByteSink
            public OutputStream openStream() throws IOException {
                return BaseEncoding.this.encodingStream(charSink.openStream());
            }
        };
    }

    @GwtIncompatible("Writer,OutputStream")
    public abstract OutputStream encodingStream(Writer writer);

    @CheckReturnValue
    public abstract BaseEncoding lowerCase();

    abstract int maxDecodedSize(int i);

    abstract int maxEncodedSize(int i);

    @CheckReturnValue
    public abstract BaseEncoding omitPadding();

    abstract CharMatcher padding();

    @CheckReturnValue
    public abstract BaseEncoding upperCase();

    @CheckReturnValue
    public abstract BaseEncoding withPadChar(char c);

    @CheckReturnValue
    public abstract BaseEncoding withSeparator(String str, int i);
}
