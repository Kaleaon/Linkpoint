package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

final class MessageDigestHashFunction extends AbstractStreamingHashFunction implements Serializable {
    private final int bytes;
    private final MessageDigest prototype;
    private final boolean supportsClone;
    private final String toString;

    private static final class MessageDigestHasher extends AbstractByteHasher {
        private final int bytes;
        private final MessageDigest digest;
        private boolean done;

        private MessageDigestHasher(MessageDigest messageDigest, int i) {
            this.digest = messageDigest;
            this.bytes = i;
        }

        private void checkNotDone() {
            boolean z = false;
            if (!this.done) {
                z = true;
            }
            Preconditions.checkState(z, "Cannot re-use a Hasher after calling hash() on it");
        }

        public HashCode hash() {
            checkNotDone();
            this.done = true;
            return this.bytes != this.digest.getDigestLength() ? HashCode.fromBytesNoCopy(Arrays.copyOf(this.digest.digest(), this.bytes)) : HashCode.fromBytesNoCopy(this.digest.digest());
        }

        /* access modifiers changed from: protected */
        public void update(byte b) {
            checkNotDone();
            this.digest.update(b);
        }

        /* access modifiers changed from: protected */
        public void update(byte[] bArr) {
            checkNotDone();
            this.digest.update(bArr);
        }

        /* access modifiers changed from: protected */
        public void update(byte[] bArr, int i, int i2) {
            checkNotDone();
            this.digest.update(bArr, i, i2);
        }
    }

    private static final class SerializedForm implements Serializable {
        private static final long serialVersionUID = 0;
        private final String algorithmName;
        private final int bytes;
        private final String toString;

        private SerializedForm(String str, int i, String str2) {
            this.algorithmName = str;
            this.bytes = i;
            this.toString = str2;
        }

        private Object readResolve() {
            return new MessageDigestHashFunction(this.algorithmName, this.bytes, this.toString);
        }
    }

    MessageDigestHashFunction(String str, int i, String str2) {
        this.toString = (String) Preconditions.checkNotNull(str2);
        this.prototype = getMessageDigest(str);
        int digestLength = this.prototype.getDigestLength();
        Preconditions.checkArgument(i >= 4 && i <= digestLength, "bytes (%s) must be >= 4 and < %s", Integer.valueOf(i), Integer.valueOf(digestLength));
        this.bytes = i;
        this.supportsClone = supportsClone();
    }

    MessageDigestHashFunction(String str, String str2) {
        this.prototype = getMessageDigest(str);
        this.bytes = this.prototype.getDigestLength();
        this.toString = (String) Preconditions.checkNotNull(str2);
        this.supportsClone = supportsClone();
    }

    private static MessageDigest getMessageDigest(String str) {
        try {
            return MessageDigest.getInstance(str);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private boolean supportsClone() {
        try {
            this.prototype.clone();
            return true;
        } catch (CloneNotSupportedException e) {
            return false;
        }
    }

    public int bits() {
        return this.bytes * 8;
    }

    public Hasher newHasher() {
        if (this.supportsClone) {
            try {
                return new MessageDigestHasher((MessageDigest) this.prototype.clone(), this.bytes);
            } catch (CloneNotSupportedException e) {
            }
        }
        return new MessageDigestHasher(getMessageDigest(this.prototype.getAlgorithm()), this.bytes);
    }

    public String toString() {
        return this.toString;
    }

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return new SerializedForm(this.prototype.getAlgorithm(), this.bytes, this.toString);
    }
}
