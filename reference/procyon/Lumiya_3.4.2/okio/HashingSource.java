// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import javax.crypto.Mac;

public final class HashingSource extends ForwardingSource
{
    private final Mac mac;
    private final MessageDigest messageDigest;
    
    private HashingSource(final Source source, final String algorithm) {
        super(source);
        try {
            this.messageDigest = MessageDigest.getInstance(algorithm);
            this.mac = null;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new AssertionError();
        }
    }
    
    private HashingSource(final Source source, final ByteString byteString, final String s) {
        super(source);
        try {
            (this.mac = Mac.getInstance(s)).init(new SecretKeySpec(byteString.toByteArray(), s));
            this.messageDigest = null;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new AssertionError();
        }
        catch (final InvalidKeyException cause) {
            throw new IllegalArgumentException(cause);
        }
    }
    
    public static HashingSource hmacSha1(final Source source, final ByteString byteString) {
        return new HashingSource(source, byteString, "HmacSHA1");
    }
    
    public static HashingSource hmacSha256(final Source source, final ByteString byteString) {
        return new HashingSource(source, byteString, "HmacSHA256");
    }
    
    public static HashingSource md5(final Source source) {
        return new HashingSource(source, "MD5");
    }
    
    public static HashingSource sha1(final Source source) {
        return new HashingSource(source, "SHA-1");
    }
    
    public static HashingSource sha256(final Source source) {
        return new HashingSource(source, "SHA-256");
    }
    
    public ByteString hash() {
        byte[] array;
        if (this.messageDigest == null) {
            array = this.mac.doFinal();
        }
        else {
            array = this.messageDigest.digest();
        }
        return ByteString.of(array);
    }
    
    @Override
    public long read(final Buffer buffer, long size) throws IOException {
        final long read = super.read(buffer, size);
        if (read != -1L) {
            final long n = buffer.size - read;
            size = buffer.size;
            Segment segment = buffer.head;
            Segment next;
            long n3;
            long n4;
            while (true) {
                int n2;
                if (size <= n) {
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                next = segment;
                n3 = size;
                n4 = n;
                if (n2 != 0) {
                    break;
                }
                segment = segment.prev;
                size -= segment.limit - segment.pos;
            }
            while (true) {
                int n5;
                if (n3 >= buffer.size) {
                    n5 = 1;
                }
                else {
                    n5 = 0;
                }
                if (n5 != 0) {
                    break;
                }
                final int n6 = (int)(n4 + next.pos - n3);
                if (this.messageDigest == null) {
                    this.mac.update(next.data, n6, next.limit - n6);
                }
                else {
                    this.messageDigest.update(next.data, n6, next.limit - n6);
                }
                n3 += next.limit - next.pos;
                next = next.next;
                n4 = n3;
            }
        }
        return read;
    }
}
