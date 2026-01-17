package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
@GwtCompatible
@CheckReturnValue
/* loaded from: classes.dex */
public final class Bytes {

    @GwtCompatible
    /* loaded from: classes.dex */
    private static class ByteArrayAsList extends AbstractList<Byte> implements RandomAccess, Serializable {
        private static final long serialVersionUID = 0;
        final byte[] array;
        final int end;
        final int start;

        ByteArrayAsList(byte[] bArr) {
            this(bArr, 0, bArr.length);
        }

        ByteArrayAsList(byte[] bArr, int i, int i2) {
            this.array = bArr;
            this.start = i;
            this.end = i2;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object obj) {
            return (obj instanceof Byte) && Bytes.indexOf(this.array, ((Byte) obj).byteValue(), this.start, this.end) != -1;
        }

        @Override // java.util.AbstractList, java.util.Collection, java.util.List
        public boolean equals(@Nullable Object obj) {
            if (obj != this) {
                if (obj instanceof ByteArrayAsList) {
                    ByteArrayAsList byteArrayAsList = (ByteArrayAsList) obj;
                    int size = size();
                    if (byteArrayAsList.size() == size) {
                        for (int i = 0; i < size; i++) {
                            if (this.array[this.start + i] != byteArrayAsList.array[byteArrayAsList.start + i]) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                }
                return super.equals(obj);
            }
            return true;
        }

        @Override // java.util.AbstractList, java.util.List
        public Byte get(int i) {
            Preconditions.checkElementIndex(i, size());
            return Byte.valueOf(this.array[this.start + i]);
        }

        @Override // java.util.AbstractList, java.util.Collection, java.util.List
        public int hashCode() {
            int i = 1;
            for (int i2 = this.start; i2 < this.end; i2++) {
                i = (i * 31) + Bytes.hashCode(this.array[i2]);
            }
            return i;
        }

        @Override // java.util.AbstractList, java.util.List
        public int indexOf(Object obj) {
            int indexOf;
            if ((obj instanceof Byte) && (indexOf = Bytes.indexOf(this.array, ((Byte) obj).byteValue(), this.start, this.end)) >= 0) {
                return indexOf - this.start;
            }
            return -1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean isEmpty() {
            return false;
        }

        @Override // java.util.AbstractList, java.util.List
        public int lastIndexOf(Object obj) {
            int lastIndexOf;
            if ((obj instanceof Byte) && (lastIndexOf = Bytes.lastIndexOf(this.array, ((Byte) obj).byteValue(), this.start, this.end)) >= 0) {
                return lastIndexOf - this.start;
            }
            return -1;
        }

        @Override // java.util.AbstractList, java.util.List
        public Byte set(int i, Byte b) {
            Preconditions.checkElementIndex(i, size());
            byte b2 = this.array[this.start + i];
            this.array[this.start + i] = ((Byte) Preconditions.checkNotNull(b)).byteValue();
            return Byte.valueOf(b2);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.end - this.start;
        }

        @Override // java.util.AbstractList, java.util.List
        public List<Byte> subList(int i, int i2) {
            Preconditions.checkPositionIndexes(i, i2, size());
            return i != i2 ? new ByteArrayAsList(this.array, this.start + i, this.start + i2) : Collections.emptyList();
        }

        byte[] toByteArray() {
            int size = size();
            byte[] bArr = new byte[size];
            System.arraycopy(this.array, this.start, bArr, 0, size);
            return bArr;
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            StringBuilder sb = new StringBuilder(size() * 5);
            sb.append('[').append((int) this.array[this.start]);
            int i = this.start;
            while (true) {
                i++;
                if (i >= this.end) {
                    return sb.append(']').toString();
                }
                sb.append(", ").append((int) this.array[i]);
            }
        }
    }

    private Bytes() {
    }

    public static List<Byte> asList(byte... bArr) {
        return bArr.length != 0 ? new ByteArrayAsList(bArr) : Collections.emptyList();
    }

    public static byte[] concat(byte[]... bArr) {
        int i = 0;
        for (byte[] bArr2 : bArr) {
            i += bArr2.length;
        }
        byte[] bArr3 = new byte[i];
        int i2 = 0;
        for (byte[] bArr4 : bArr) {
            System.arraycopy(bArr4, 0, bArr3, i2, bArr4.length);
            i2 += bArr4.length;
        }
        return bArr3;
    }

    public static boolean contains(byte[] bArr, byte b) {
        for (byte b2 : bArr) {
            if (b2 == b) {
                return true;
            }
        }
        return false;
    }

    private static byte[] copyOf(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, Math.min(bArr.length, i));
        return bArr2;
    }

    public static byte[] ensureCapacity(byte[] bArr, int i, int i2) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", Integer.valueOf(i));
        Preconditions.checkArgument(i2 >= 0, "Invalid padding: %s", Integer.valueOf(i2));
        return bArr.length >= i ? bArr : copyOf(bArr, i + i2);
    }

    public static int hashCode(byte b) {
        return b;
    }

    public static int indexOf(byte[] bArr, byte b) {
        return indexOf(bArr, b, 0, bArr.length);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int indexOf(byte[] bArr, byte b, int i, int i2) {
        while (i < i2) {
            if (bArr[i] == b) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x002b, code lost:
        r0 = r0 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static int indexOf(byte[] r5, byte[] r6) {
        /*
            r1 = 0
            java.lang.String r0 = "array"
            com.google.common.base.Preconditions.checkNotNull(r5, r0)
            java.lang.String r0 = "target"
            com.google.common.base.Preconditions.checkNotNull(r6, r0)
            int r0 = r6.length
            if (r0 == 0) goto L1a
            r0 = r1
        L11:
            int r2 = r5.length
            int r3 = r6.length
            int r2 = r2 - r3
            int r2 = r2 + 1
            if (r0 < r2) goto L1b
            r0 = -1
            return r0
        L1a:
            return r1
        L1b:
            r2 = r1
        L1c:
            int r3 = r6.length
            if (r2 < r3) goto L20
            return r0
        L20:
            int r3 = r0 + r2
            r3 = r5[r3]
            r4 = r6[r2]
            if (r3 != r4) goto L2b
            int r2 = r2 + 1
            goto L1c
        L2b:
            int r0 = r0 + 1
            goto L11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.primitives.Bytes.indexOf(byte[], byte[]):int");
    }

    public static int lastIndexOf(byte[] bArr, byte b) {
        return lastIndexOf(bArr, b, 0, bArr.length);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int lastIndexOf(byte[] bArr, byte b, int i, int i2) {
        for (int i3 = i2 - 1; i3 >= i; i3--) {
            if (bArr[i3] == b) {
                return i3;
            }
        }
        return -1;
    }

    public static byte[] toArray(Collection<? extends Number> collection) {
        Object[] array;
        if (collection instanceof ByteArrayAsList) {
            return ((ByteArrayAsList) collection).toByteArray();
        }
        int length = collection.toArray().length;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr[i] = ((Number) Preconditions.checkNotNull(array[i])).byteValue();
        }
        return bArr;
    }
}
