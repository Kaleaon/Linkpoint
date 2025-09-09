package okio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class HashingSink extends ForwardingSink {
    private final Mac mac;
    private final MessageDigest messageDigest;

    private HashingSink(Sink sink, String str) {
        super(sink);
        try {
            this.messageDigest = MessageDigest.getInstance(str);
            this.mac = null;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

    private HashingSink(Sink sink, ByteString byteString, String str) {
        super(sink);
        try {
            this.mac = Mac.getInstance(str);
            this.mac.init(new SecretKeySpec(byteString.toByteArray(), str));
            this.messageDigest = null;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        } catch (InvalidKeyException e2) {
            throw new IllegalArgumentException(e2);
        }
    }

    public static HashingSink hmacSha1(Sink sink, ByteString byteString) {
        return new HashingSink(sink, byteString, "HmacSHA1");
    }

    public static HashingSink hmacSha256(Sink sink, ByteString byteString) {
        return new HashingSink(sink, byteString, "HmacSHA256");
    }

    public static HashingSink md5(Sink sink) {
        return new HashingSink(sink, "MD5");
    }

    public static HashingSink sha1(Sink sink) {
        return new HashingSink(sink, "SHA-1");
    }

    public static HashingSink sha256(Sink sink) {
        return new HashingSink(sink, "SHA-256");
    }

    public ByteString hash() {
        return ByteString.of(this.messageDigest == null ? this.mac.doFinal() : this.messageDigest.digest());
    }

    public void write(Buffer buffer, long j) throws IOException {
        long j2 = 0;
        Util.checkOffsetAndCount(buffer.size, 0, j);
        Segment segment = buffer.head;
        while (true) {
            Segment segment2 = segment;
            if (!(j2 >= j)) {
                int min = (int) Math.min(j - j2, (long) (segment2.limit - segment2.pos));
                if (this.messageDigest == null) {
                    this.mac.update(segment2.data, segment2.pos, min);
                } else {
                    this.messageDigest.update(segment2.data, segment2.pos, min);
                }
                j2 += (long) min;
                segment = segment2.next;
            } else {
                super.write(buffer, j);
                return;
            }
        }
    }
}
