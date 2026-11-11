package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.zip.Checksum;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ChecksumHashFunction extends AbstractStreamingHashFunction implements Serializable {
    private static final long serialVersionUID = 0;
    private final int bits;
    private final Supplier<? extends Checksum> checksumSupplier;
    private final String toString;

    /* loaded from: classes.dex */
    private final class ChecksumHasher extends AbstractByteHasher {
        private final Checksum checksum;

        private ChecksumHasher(Checksum checksum) {
            this.checksum = (Checksum) Preconditions.checkNotNull(checksum);
        }

        @Override // com.google.common.hash.Hasher
        public HashCode hash() {
            long value = this.checksum.getValue();
            return ChecksumHashFunction.this.bits != 32 ? HashCode.fromLong(value) : HashCode.fromInt((int) value);
        }

        @Override // com.google.common.hash.AbstractByteHasher
        protected void update(byte b) {
            this.checksum.update(b);
        }

        @Override // com.google.common.hash.AbstractByteHasher
        protected void update(byte[] bArr, int i, int i2) {
            this.checksum.update(bArr, i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChecksumHashFunction(Supplier<? extends Checksum> supplier, int i, String str) {
        this.checksumSupplier = (Supplier) Preconditions.checkNotNull(supplier);
        Preconditions.checkArgument(i == 32 || i == 64, "bits (%s) must be either 32 or 64", Integer.valueOf(i));
        this.bits = i;
        this.toString = (String) Preconditions.checkNotNull(str);
    }

    @Override // com.google.common.hash.HashFunction
    public int bits() {
        return this.bits;
    }

    @Override // com.google.common.hash.HashFunction
    public Hasher newHasher() {
        return new ChecksumHasher(this.checksumSupplier.get());
    }

    public String toString() {
        return this.toString;
    }
}
