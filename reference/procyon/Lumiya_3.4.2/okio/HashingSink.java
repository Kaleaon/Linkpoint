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

public final class HashingSink extends ForwardingSink
{
    private final Mac mac;
    private final MessageDigest messageDigest;
    
    private HashingSink(final Sink sink, final String algorithm) {
        super(sink);
        try {
            this.messageDigest = MessageDigest.getInstance(algorithm);
            this.mac = null;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new AssertionError();
        }
    }
    
    private HashingSink(final Sink sink, final ByteString byteString, final String s) {
        super(sink);
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
    
    public static HashingSink hmacSha1(final Sink sink, final ByteString byteString) {
        return new HashingSink(sink, byteString, "HmacSHA1");
    }
    
    public static HashingSink hmacSha256(final Sink sink, final ByteString byteString) {
        return new HashingSink(sink, byteString, "HmacSHA256");
    }
    
    public static HashingSink md5(final Sink sink) {
        return new HashingSink(sink, "MD5");
    }
    
    public static HashingSink sha1(final Sink sink) {
        return new HashingSink(sink, "SHA-1");
    }
    
    public static HashingSink sha256(final Sink sink) {
        return new HashingSink(sink, "SHA-256");
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
    public void write(final Buffer buffer, final long n) throws IOException {
        long n2 = 0L;
        Util.checkOffsetAndCount(buffer.size, 0L, n);
        Segment segment = buffer.head;
        while (true) {
            int n3;
            if (n2 >= n) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 != 0) {
                break;
            }
            final int n4 = (int)Math.min(n - n2, segment.limit - segment.pos);
            if (this.messageDigest == null) {
                this.mac.update(segment.data, segment.pos, n4);
            }
            else {
                this.messageDigest.update(segment.data, segment.pos, n4);
            }
            n2 += n4;
            segment = segment.next;
        }
        super.write(buffer, n);
    }
}
