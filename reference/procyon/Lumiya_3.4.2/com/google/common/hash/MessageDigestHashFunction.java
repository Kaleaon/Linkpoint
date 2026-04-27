// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.util.Arrays;
import java.security.NoSuchAlgorithmException;
import com.google.common.base.Preconditions;
import java.security.MessageDigest;
import java.io.Serializable;

final class MessageDigestHashFunction extends AbstractStreamingHashFunction implements Serializable
{
    private final int bytes;
    private final MessageDigest prototype;
    private final boolean supportsClone;
    private final String toString;
    
    MessageDigestHashFunction(final String s, final int n, final String s2) {
        this.toString = Preconditions.checkNotNull(s2);
        this.prototype = getMessageDigest(s);
        final int digestLength = this.prototype.getDigestLength();
        Preconditions.checkArgument(n >= 4 && n <= digestLength, "bytes (%s) must be >= 4 and < %s", n, digestLength);
        this.bytes = n;
        this.supportsClone = this.supportsClone();
    }
    
    MessageDigestHashFunction(final String s, final String s2) {
        this.prototype = getMessageDigest(s);
        this.bytes = this.prototype.getDigestLength();
        this.toString = Preconditions.checkNotNull(s2);
        this.supportsClone = this.supportsClone();
    }
    
    private static MessageDigest getMessageDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    private boolean supportsClone() {
        try {
            this.prototype.clone();
            return true;
        }
        catch (final CloneNotSupportedException ex) {
            return false;
        }
    }
    
    @Override
    public int bits() {
        return this.bytes * 8;
    }
    
    @Override
    public Hasher newHasher() {
        if (this.supportsClone) {
            try {
                return new MessageDigestHasher((MessageDigest)this.prototype.clone(), this.bytes);
            }
            catch (final CloneNotSupportedException ex) {}
        }
        return new MessageDigestHasher(getMessageDigest(this.prototype.getAlgorithm()), this.bytes);
    }
    
    @Override
    public String toString() {
        return this.toString;
    }
    
    Object writeReplace() {
        return new SerializedForm(this.prototype.getAlgorithm(), this.bytes, this.toString);
    }
    
    private static final class MessageDigestHasher extends AbstractByteHasher
    {
        private final int bytes;
        private final MessageDigest digest;
        private boolean done;
        
        private MessageDigestHasher(final MessageDigest digest, final int bytes) {
            this.digest = digest;
            this.bytes = bytes;
        }
        
        private void checkNotDone() {
            boolean b = false;
            if (!this.done) {
                b = true;
            }
            Preconditions.checkState(b, (Object)"Cannot re-use a Hasher after calling hash() on it");
        }
        
        @Override
        public HashCode hash() {
            this.checkNotDone();
            this.done = true;
            HashCode hashCode;
            if (this.bytes != this.digest.getDigestLength()) {
                hashCode = HashCode.fromBytesNoCopy(Arrays.copyOf(this.digest.digest(), this.bytes));
            }
            else {
                hashCode = HashCode.fromBytesNoCopy(this.digest.digest());
            }
            return hashCode;
        }
        
        @Override
        protected void update(final byte input) {
            this.checkNotDone();
            this.digest.update(input);
        }
        
        @Override
        protected void update(final byte[] input) {
            this.checkNotDone();
            this.digest.update(input);
        }
        
        @Override
        protected void update(final byte[] input, final int offset, final int len) {
            this.checkNotDone();
            this.digest.update(input, offset, len);
        }
    }
    
    private static final class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final String algorithmName;
        private final int bytes;
        private final String toString;
        
        private SerializedForm(final String algorithmName, final int bytes, final String toString) {
            this.algorithmName = algorithmName;
            this.bytes = bytes;
            this.toString = toString;
        }
        
        private Object readResolve() {
            return new MessageDigestHashFunction(this.algorithmName, this.bytes, this.toString);
        }
    }
}
