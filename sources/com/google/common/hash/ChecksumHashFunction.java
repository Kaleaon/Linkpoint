package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.zip.Checksum;

final class ChecksumHashFunction extends AbstractStreamingHashFunction implements Serializable {
    private static final long serialVersionUID = 0;
    /* access modifiers changed from: private */
    public final int bits;
    private final Supplier<? extends Checksum> checksumSupplier;
    private final String toString;

    private final class ChecksumHasher extends AbstractByteHasher {
        private final Checksum checksum;

        private ChecksumHasher(Checksum checksum2) {
            this.checksum = (Checksum) Preconditions.checkNotNull(checksum2);
        }

        public HashCode hash() {
            long value = this.checksum.getValue();
            return ChecksumHashFunction.this.bits != 32 ? HashCode.fromLong(value) : HashCode.fromInt((int) value);
        }

        /* access modifiers changed from: protected */
        public void update(byte b) {
            this.checksum.update(b);
        }

        /* access modifiers changed from: protected */
        public void update(byte[] bArr, int i, int i2) {
            this.checksum.update(bArr, i, i2);
        }
    }

    ChecksumHashFunction(Supplier<? extends Checksum> supplier, int i, String str) {
        this.checksumSupplier = (Supplier) Preconditions.checkNotNull(supplier);
        Preconditions.checkArgument(i == 32 || i == 64, "bits (%s) must be either 32 or 64", Integer.valueOf(i));
        this.bits = i;
        this.toString = (String) Preconditions.checkNotNull(str);
    }

    public int bits() {
        return this.bits;
    }

    public Hasher newHasher() {
        return new ChecksumHasher((Checksum) this.checksumSupplier.get());
    }

    public String toString() {
        return this.toString;
    }
}
