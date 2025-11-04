// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.OutputStream;
import java.util.Arrays;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.Serializable;

public class ByteString implements Serializable, Comparable<ByteString>
{
    public static final ByteString EMPTY;
    static final char[] HEX_DIGITS;
    private static final long serialVersionUID = 1L;
    final byte[] data;
    transient int hashCode;
    transient String utf8;
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        EMPTY = of(new byte[0]);
    }
    
    ByteString(final byte[] data) {
        this.data = data;
    }
    
    static int codePointIndexToCharIndex(final String s, final int n) {
        int n2 = 0;
        int codePoint;
        for (int length = s.length(), i = 0; i < length; i += Character.charCount(codePoint)) {
            if (n2 == n) {
                return i;
            }
            codePoint = s.codePointAt(i);
            if ((Character.isISOControl(codePoint) && codePoint != 10 && codePoint != 13) || codePoint == 65533) {
                return -1;
            }
            ++n2;
        }
        return s.length();
    }
    
    public static ByteString decodeBase64(final String s) {
        final ByteString byteString = null;
        if (s != null) {
            final byte[] decode = Base64.decode(s);
            ByteString byteString2;
            if (decode == null) {
                byteString2 = byteString;
            }
            else {
                byteString2 = new ByteString(decode);
            }
            return byteString2;
        }
        throw new IllegalArgumentException("base64 == null");
    }
    
    public static ByteString decodeHex(final String str) {
        int i = 0;
        if (str == null) {
            throw new IllegalArgumentException("hex == null");
        }
        if (str.length() % 2 == 0) {
            byte[] array;
            for (array = new byte[str.length() / 2]; i < array.length; ++i) {
                array[i] = (byte)((decodeHexDigit(str.charAt(i * 2)) << 4) + decodeHexDigit(str.charAt(i * 2 + 1)));
            }
            return of(array);
        }
        throw new IllegalArgumentException("Unexpected hex string: " + str);
    }
    
    private static int decodeHexDigit(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        throw new IllegalArgumentException("Unexpected hex digit: " + c);
    }
    
    private ByteString digest(final String algorithm) {
        try {
            return of(MessageDigest.getInstance(algorithm).digest(this.data));
        }
        catch (final NoSuchAlgorithmException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    public static ByteString encodeString(final String s, final Charset charset) {
        if (s == null) {
            throw new IllegalArgumentException("s == null");
        }
        if (charset != null) {
            return new ByteString(s.getBytes(charset));
        }
        throw new IllegalArgumentException("charset == null");
    }
    
    public static ByteString encodeUtf8(final String utf8) {
        if (utf8 != null) {
            final ByteString byteString = new ByteString(utf8.getBytes(Util.UTF_8));
            byteString.utf8 = utf8;
            return byteString;
        }
        throw new IllegalArgumentException("s == null");
    }
    
    private ByteString hmac(final String s, final ByteString byteString) {
        try {
            final Mac instance = Mac.getInstance(s);
            instance.init(new SecretKeySpec(byteString.toByteArray(), s));
            return of(instance.doFinal(this.data));
        }
        catch (final NoSuchAlgorithmException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
        catch (final InvalidKeyException cause) {
            throw new IllegalArgumentException(cause);
        }
    }
    
    public static ByteString of(final ByteBuffer byteBuffer) {
        if (byteBuffer != null) {
            final byte[] dst = new byte[byteBuffer.remaining()];
            byteBuffer.get(dst);
            return new ByteString(dst);
        }
        throw new IllegalArgumentException("data == null");
    }
    
    public static ByteString of(final byte... array) {
        if (array != null) {
            return new ByteString(array.clone());
        }
        throw new IllegalArgumentException("data == null");
    }
    
    public static ByteString of(final byte[] array, final int n, final int n2) {
        if (array != null) {
            Util.checkOffsetAndCount(array.length, n, n2);
            final byte[] array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
            return new ByteString(array2);
        }
        throw new IllegalArgumentException("data == null");
    }
    
    public static ByteString read(final InputStream inputStream, final int i) throws IOException {
        int j = 0;
        if (inputStream == null) {
            throw new IllegalArgumentException("in == null");
        }
        if (i >= 0) {
            final byte[] b = new byte[i];
            while (j < i) {
                final int read = inputStream.read(b, j, i - j);
                if (read == -1) {
                    throw new EOFException();
                }
                j += read;
            }
            return new ByteString(b);
        }
        throw new IllegalArgumentException("byteCount < 0: " + i);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException {
        final ByteString read = read(objectInputStream, objectInputStream.readInt());
        try {
            final Field declaredField = ByteString.class.getDeclaredField("data");
            declaredField.setAccessible(true);
            declaredField.set(this, read.data);
        }
        catch (final NoSuchFieldException ex) {
            throw new AssertionError();
        }
        catch (final IllegalAccessException ex2) {
            throw new AssertionError();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.data.length);
        objectOutputStream.write(this.data);
    }
    
    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.data).asReadOnlyBuffer();
    }
    
    public String base64() {
        return Base64.encode(this.data);
    }
    
    public String base64Url() {
        return Base64.encodeUrl(this.data);
    }
    
    @Override
    public int compareTo(final ByteString byteString) {
        final int n = 1;
        final int n2 = 1;
        final int size = this.size();
        final int size2 = byteString.size();
        for (int min = Math.min(size, size2), i = 0; i < min; ++i) {
            final int n3 = this.getByte(i) & 0xFF;
            final int n4 = byteString.getByte(i) & 0xFF;
            if (n3 != n4) {
                int n5;
                if (n3 >= n4) {
                    n5 = n;
                }
                else {
                    n5 = -1;
                }
                return n5;
            }
        }
        if (size != size2) {
            int n6;
            if (size >= size2) {
                n6 = n2;
            }
            else {
                n6 = -1;
            }
            return n6;
        }
        return 0;
    }
    
    public final boolean endsWith(final ByteString byteString) {
        return this.rangeEquals(this.size() - byteString.size(), byteString, 0, byteString.size());
    }
    
    public final boolean endsWith(final byte[] array) {
        return this.rangeEquals(this.size() - array.length, array, 0, array.length);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof ByteString && ((ByteString)o).size() == this.data.length && ((ByteString)o).rangeEquals(0, this.data, 0, this.data.length));
    }
    
    public byte getByte(final int n) {
        return this.data[n];
    }
    
    @Override
    public int hashCode() {
        int hashCode;
        if ((hashCode = this.hashCode) == 0) {
            hashCode = Arrays.hashCode(this.data);
            this.hashCode = hashCode;
        }
        return hashCode;
    }
    
    public String hex() {
        int i = 0;
        final char[] value = new char[this.data.length * 2];
        final byte[] data = this.data;
        final int length = data.length;
        int n = 0;
        while (i < length) {
            final byte b = data[i];
            final int n2 = n + 1;
            value[n] = ByteString.HEX_DIGITS[b >> 4 & 0xF];
            n = n2 + 1;
            value[n2] = ByteString.HEX_DIGITS[b & 0xF];
            ++i;
        }
        return new String(value);
    }
    
    public ByteString hmacSha1(final ByteString byteString) {
        return this.hmac("HmacSHA1", byteString);
    }
    
    public ByteString hmacSha256(final ByteString byteString) {
        return this.hmac("HmacSHA256", byteString);
    }
    
    public final int indexOf(final ByteString byteString) {
        return this.indexOf(byteString.internalArray(), 0);
    }
    
    public final int indexOf(final ByteString byteString, final int n) {
        return this.indexOf(byteString.internalArray(), n);
    }
    
    public final int indexOf(final byte[] array) {
        return this.indexOf(array, 0);
    }
    
    public int indexOf(final byte[] array, int i) {
        for (i = Math.max(i, 0); i <= this.data.length - array.length; ++i) {
            if (Util.arrayRangeEquals(this.data, i, array, 0, array.length)) {
                return i;
            }
        }
        return -1;
    }
    
    byte[] internalArray() {
        return this.data;
    }
    
    public final int lastIndexOf(final ByteString byteString) {
        return this.lastIndexOf(byteString.internalArray(), this.size());
    }
    
    public final int lastIndexOf(final ByteString byteString, final int n) {
        return this.lastIndexOf(byteString.internalArray(), n);
    }
    
    public final int lastIndexOf(final byte[] array) {
        return this.lastIndexOf(array, this.size());
    }
    
    public int lastIndexOf(final byte[] array, int i) {
        for (i = Math.min(i, this.data.length - array.length); i >= 0; --i) {
            if (Util.arrayRangeEquals(this.data, i, array, 0, array.length)) {
                return i;
            }
        }
        return -1;
    }
    
    public ByteString md5() {
        return this.digest("MD5");
    }
    
    public boolean rangeEquals(final int n, final ByteString byteString, final int n2, final int n3) {
        return byteString.rangeEquals(n2, this.data, n, n3);
    }
    
    public boolean rangeEquals(final int n, final byte[] array, final int n2, final int n3) {
        final boolean b = false;
        boolean b2;
        if (n < 0) {
            b2 = b;
        }
        else {
            b2 = b;
            if (n <= this.data.length - n3) {
                b2 = b;
                if (n2 >= 0) {
                    b2 = b;
                    if (n2 <= array.length - n3) {
                        b2 = b;
                        if (Util.arrayRangeEquals(this.data, n, array, n2, n3)) {
                            b2 = true;
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    public ByteString sha1() {
        return this.digest("SHA-1");
    }
    
    public ByteString sha256() {
        return this.digest("SHA-256");
    }
    
    public int size() {
        return this.data.length;
    }
    
    public final boolean startsWith(final ByteString byteString) {
        return this.rangeEquals(0, byteString, 0, byteString.size());
    }
    
    public final boolean startsWith(final byte[] array) {
        return this.rangeEquals(0, array, 0, array.length);
    }
    
    public String string(final Charset charset) {
        if (charset != null) {
            return new String(this.data, charset);
        }
        throw new IllegalArgumentException("charset == null");
    }
    
    public ByteString substring(final int n) {
        return this.substring(n, this.data.length);
    }
    
    public ByteString substring(final int n, final int n2) {
        if (n < 0) {
            throw new IllegalArgumentException("beginIndex < 0");
        }
        if (n2 > this.data.length) {
            throw new IllegalArgumentException("endIndex > length(" + this.data.length + ")");
        }
        final int n3 = n2 - n;
        if (n3 < 0) {
            throw new IllegalArgumentException("endIndex < beginIndex");
        }
        if (n == 0 && n2 == this.data.length) {
            return this;
        }
        final byte[] array = new byte[n3];
        System.arraycopy(this.data, n, array, 0, n3);
        return new ByteString(array);
    }
    
    public ByteString toAsciiLowercase() {
        for (int i = 0; i < this.data.length; ++i) {
            final byte b = this.data[i];
            if (b >= 65 && b <= 90) {
                final byte[] array = this.data.clone();
                array[i] = (byte)(b + 32);
                ++i;
                while (i < array.length) {
                    final byte b2 = array[i];
                    if (b2 >= 65 && b2 <= 90) {
                        array[i] = (byte)(b2 + 32);
                    }
                    ++i;
                }
                return new ByteString(array);
            }
        }
        return this;
    }
    
    public ByteString toAsciiUppercase() {
        for (int i = 0; i < this.data.length; ++i) {
            final byte b = this.data[i];
            if (b >= 97 && b <= 122) {
                final byte[] array = this.data.clone();
                array[i] = (byte)(b - 32);
                ++i;
                while (i < array.length) {
                    final byte b2 = array[i];
                    if (b2 >= 97 && b2 <= 122) {
                        array[i] = (byte)(b2 - 32);
                    }
                    ++i;
                }
                return new ByteString(array);
            }
        }
        return this;
    }
    
    public byte[] toByteArray() {
        return this.data.clone();
    }
    
    @Override
    public String toString() {
        if (this.data.length == 0) {
            return "[size=0]";
        }
        final String utf8 = this.utf8();
        final int codePointIndexToCharIndex = codePointIndexToCharIndex(utf8, 64);
        if (codePointIndexToCharIndex != -1) {
            final String replace = utf8.substring(0, codePointIndexToCharIndex).replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r");
            String s;
            if (codePointIndexToCharIndex >= utf8.length()) {
                s = "[text=" + replace + "]";
            }
            else {
                s = "[size=" + this.data.length + " text=" + replace + "\u2026]";
            }
            return s;
        }
        String s2;
        if (this.data.length > 64) {
            s2 = "[size=" + this.data.length + " hex=" + this.substring(0, 64).hex() + "\u2026]";
        }
        else {
            s2 = "[hex=" + this.hex() + "]";
        }
        return s2;
    }
    
    public String utf8() {
        String utf8;
        if ((utf8 = this.utf8) == null) {
            utf8 = new String(this.data, Util.UTF_8);
            this.utf8 = utf8;
        }
        return utf8;
    }
    
    public void write(final OutputStream outputStream) throws IOException {
        if (outputStream != null) {
            outputStream.write(this.data);
            return;
        }
        throw new IllegalArgumentException("out == null");
    }
    
    void write(final Buffer buffer) {
        buffer.write(this.data, 0, this.data.length);
    }
}
