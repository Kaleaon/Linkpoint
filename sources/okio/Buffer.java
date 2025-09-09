package okio;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.prims.PrimProfileParams;
import com.lumiyaviewer.lumiya.slproto.textures.MutableSLTextureEntryFace;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Buffer implements BufferedSource, BufferedSink, Cloneable {
    private static final byte[] DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    static final int REPLACEMENT_CHARACTER = 65533;
    Segment head;
    long size;

    private ByteString digest(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance(str);
            if (this.head != null) {
                instance.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment segment = this.head.next; segment != this.head; segment = segment.next) {
                    instance.update(segment.data, segment.pos, segment.limit - segment.pos);
                }
            }
            return ByteString.of(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

    private ByteString hmac(String str, ByteString byteString) {
        try {
            Mac instance = Mac.getInstance(str);
            instance.init(new SecretKeySpec(byteString.toByteArray(), str));
            if (this.head != null) {
                instance.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment segment = this.head.next; segment != this.head; segment = segment.next) {
                    instance.update(segment.data, segment.pos, segment.limit - segment.pos);
                }
            }
            return ByteString.of(instance.doFinal());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        } catch (InvalidKeyException e2) {
            throw new IllegalArgumentException(e2);
        }
    }

    private boolean rangeEquals(Segment segment, int i, ByteString byteString, int i2, int i3) {
        int i4 = segment.limit;
        byte[] bArr = segment.data;
        int i5 = i;
        Segment segment2 = segment;
        while (i2 < i3) {
            if (i5 == i4) {
                segment2 = segment2.next;
                bArr = segment2.data;
                i5 = segment2.pos;
                i4 = segment2.limit;
            }
            if (bArr[i5] != byteString.getByte(i2)) {
                return false;
            }
            i5++;
            i2++;
        }
        return true;
    }

    private void readFrom(InputStream inputStream, long j, boolean z) throws IOException {
        if (inputStream != null) {
            while (true) {
                if ((j > 0) || z) {
                    Segment writableSegment = writableSegment(1);
                    int read = inputStream.read(writableSegment.data, writableSegment.limit, (int) Math.min(j, (long) (8192 - writableSegment.limit)));
                    if (read != -1) {
                        writableSegment.limit += read;
                        this.size += (long) read;
                        j -= (long) read;
                    } else if (!z) {
                        throw new EOFException();
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("in == null");
        }
    }

    public Buffer buffer() {
        return this;
    }

    public void clear() {
        try {
            skip(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public Buffer clone() {
        Buffer buffer = new Buffer();
        if (this.size == 0) {
            return buffer;
        }
        buffer.head = new Segment(this.head);
        Segment segment = buffer.head;
        Segment segment2 = buffer.head;
        Segment segment3 = buffer.head;
        segment2.prev = segment3;
        segment.next = segment3;
        for (Segment segment4 = this.head.next; segment4 != this.head; segment4 = segment4.next) {
            buffer.head.prev.push(new Segment(segment4));
        }
        buffer.size = this.size;
        return buffer;
    }

    public void close() {
    }

    public long completeSegmentByteCount() {
        long j = this.size;
        if (j == 0) {
            return 0;
        }
        Segment segment = this.head.prev;
        return (segment.limit < 8192 && segment.owner) ? j - ((long) (segment.limit - segment.pos)) : j;
    }

    public Buffer copyTo(OutputStream outputStream) throws IOException {
        return copyTo(outputStream, 0, this.size);
    }

    public Buffer copyTo(OutputStream outputStream, long j, long j2) throws IOException {
        if (outputStream != null) {
            Util.checkOffsetAndCount(this.size, j, j2);
            if (j2 == 0) {
                return this;
            }
            Segment segment = this.head;
            while (true) {
                if (j < ((long) (segment.limit - segment.pos))) {
                    break;
                }
                j -= (long) (segment.limit - segment.pos);
                segment = segment.next;
            }
            Segment segment2 = segment;
            while (true) {
                if (j2 <= 0) {
                    return this;
                }
                int i = (int) (((long) segment2.pos) + j);
                int min = (int) Math.min((long) (segment2.limit - i), j2);
                outputStream.write(segment2.data, i, min);
                j2 -= (long) min;
                segment2 = segment2.next;
                j = 0;
            }
        } else {
            throw new IllegalArgumentException("out == null");
        }
    }

    public Buffer copyTo(Buffer buffer, long j, long j2) {
        if (buffer != null) {
            Util.checkOffsetAndCount(this.size, j, j2);
            if (j2 == 0) {
                return this;
            }
            buffer.size += j2;
            Segment segment = this.head;
            while (true) {
                if (j < ((long) (segment.limit - segment.pos))) {
                    break;
                }
                j -= (long) (segment.limit - segment.pos);
                segment = segment.next;
            }
            Segment segment2 = segment;
            while (true) {
                if (j2 <= 0) {
                    return this;
                }
                Segment segment3 = new Segment(segment2);
                segment3.pos = (int) (((long) segment3.pos) + j);
                segment3.limit = Math.min(segment3.pos + ((int) j2), segment3.limit);
                if (buffer.head != null) {
                    buffer.head.prev.push(segment3);
                } else {
                    segment3.prev = segment3;
                    segment3.next = segment3;
                    buffer.head = segment3;
                }
                j2 -= (long) (segment3.limit - segment3.pos);
                segment2 = segment2.next;
                j = 0;
            }
        } else {
            throw new IllegalArgumentException("out == null");
        }
    }

    public BufferedSink emit() {
        return this;
    }

    public Buffer emitCompleteSegments() {
        return this;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x005e, code lost:
        if (r3 == r5.limit) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0062, code lost:
        if (r2 == r4.limit) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0066, code lost:
        r5 = r5.next;
        r3 = r5.pos;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006b, code lost:
        r4 = r4.next;
        r2 = r4.pos;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r15) {
        /*
            r14 = this;
            r0 = 0
            r7 = 1
            r8 = 0
            if (r14 == r15) goto L_0x0015
            boolean r2 = r15 instanceof okio.Buffer
            if (r2 == 0) goto L_0x0016
            okio.Buffer r15 = (okio.Buffer) r15
            long r2 = r14.size
            long r4 = r15.size
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0017
            return r8
        L_0x0015:
            return r7
        L_0x0016:
            return r8
        L_0x0017:
            long r2 = r14.size
            int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x001e
            return r7
        L_0x001e:
            okio.Segment r5 = r14.head
            okio.Segment r4 = r15.head
            int r3 = r5.pos
            int r2 = r4.pos
        L_0x0026:
            long r10 = r14.size
            int r6 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r6 < 0) goto L_0x0057
            r6 = r7
        L_0x002d:
            if (r6 != 0) goto L_0x0070
            int r6 = r5.limit
            int r6 = r6 - r3
            int r9 = r4.limit
            int r9 = r9 - r2
            int r6 = java.lang.Math.min(r6, r9)
            long r10 = (long) r6
            r9 = r8
        L_0x003b:
            long r12 = (long) r9
            int r6 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r6 < 0) goto L_0x0059
            r6 = r7
        L_0x0041:
            if (r6 != 0) goto L_0x005c
            byte[] r12 = r5.data
            int r6 = r3 + 1
            byte r12 = r12[r3]
            byte[] r13 = r4.data
            int r3 = r2 + 1
            byte r2 = r13[r2]
            if (r12 != r2) goto L_0x005b
            int r2 = r9 + 1
            r9 = r2
            r2 = r3
            r3 = r6
            goto L_0x003b
        L_0x0057:
            r6 = r8
            goto L_0x002d
        L_0x0059:
            r6 = r8
            goto L_0x0041
        L_0x005b:
            return r8
        L_0x005c:
            int r6 = r5.limit
            if (r3 == r6) goto L_0x0066
        L_0x0060:
            int r6 = r4.limit
            if (r2 == r6) goto L_0x006b
        L_0x0064:
            long r0 = r0 + r10
            goto L_0x0026
        L_0x0066:
            okio.Segment r5 = r5.next
            int r3 = r5.pos
            goto L_0x0060
        L_0x006b:
            okio.Segment r4 = r4.next
            int r2 = r4.pos
            goto L_0x0064
        L_0x0070:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.equals(java.lang.Object):boolean");
    }

    public boolean exhausted() {
        return this.size == 0;
    }

    public void flush() {
    }

    public byte getByte(long j) {
        Util.checkOffsetAndCount(this.size, j, 1);
        Segment segment = this.head;
        while (true) {
            int i = segment.limit - segment.pos;
            if (!(j >= ((long) i))) {
                return segment.data[segment.pos + ((int) j)];
            }
            j -= (long) i;
            segment = segment.next;
        }
    }

    public int hashCode() {
        Segment segment = this.head;
        if (segment == null) {
            return 0;
        }
        int i = 1;
        do {
            int i2 = segment.pos;
            int i3 = segment.limit;
            while (i2 < i3) {
                i2++;
                i = segment.data[i2] + (i * 31);
            }
            segment = segment.next;
        } while (segment != this.head);
        return i;
    }

    public ByteString hmacSha1(ByteString byteString) {
        return hmac("HmacSHA1", byteString);
    }

    public ByteString hmacSha256(ByteString byteString) {
        return hmac("HmacSHA256", byteString);
    }

    public long indexOf(byte b) {
        return indexOf(b, 0);
    }

    public long indexOf(byte b, long j) {
        Segment segment;
        long j2;
        long j3 = 0;
        if (!(j >= 0)) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        Segment segment2 = this.head;
        if (segment2 == null) {
            return -1;
        }
        if (this.size - j >= j) {
            Segment segment3 = segment2;
            while (true) {
                long j4 = ((long) (segment.limit - segment.pos)) + j2;
                if (j4 >= j) {
                    break;
                }
                segment3 = segment.next;
                j3 = j4;
            }
        } else {
            j2 = this.size;
            segment = segment2;
            while (true) {
                if (j2 <= j) {
                    break;
                }
                segment = segment.prev;
                j2 -= (long) (segment.limit - segment.pos);
            }
        }
        while (true) {
            if (j2 >= this.size) {
                return -1;
            }
            byte[] bArr = segment.data;
            int i = segment.limit;
            for (int i2 = (int) ((((long) segment.pos) + j) - j2); i2 < i; i2++) {
                if (bArr[i2] == b) {
                    return j2 + ((long) (i2 - segment.pos));
                }
            }
            j2 += (long) (segment.limit - segment.pos);
            segment = segment.next;
            j = j2;
        }
    }

    public long indexOf(ByteString byteString) throws IOException {
        return indexOf(byteString, 0);
    }

    public long indexOf(ByteString byteString, long j) throws IOException {
        Segment segment;
        long j2;
        if (byteString.size() != 0) {
            if (!(j >= 0)) {
                throw new IllegalArgumentException("fromIndex < 0");
            }
            Segment segment2 = this.head;
            if (segment2 == null) {
                return -1;
            }
            if (this.size - j >= j) {
                long j3 = 0;
                Segment segment3 = segment2;
                while (true) {
                    long j4 = ((long) (segment.limit - segment.pos)) + j2;
                    if (j4 >= j) {
                        break;
                    }
                    segment3 = segment.next;
                    j3 = j4;
                }
            } else {
                j2 = this.size;
                segment = segment2;
                while (true) {
                    if (j2 <= j) {
                        break;
                    }
                    segment = segment.prev;
                    j2 -= (long) (segment.limit - segment.pos);
                }
            }
            byte b = byteString.getByte(0);
            int size2 = byteString.size();
            long j5 = (this.size - ((long) size2)) + 1;
            long j6 = j2;
            Segment segment4 = segment;
            while (true) {
                if (j6 >= j5) {
                    return -1;
                }
                byte[] bArr = segment4.data;
                int min = (int) Math.min((long) segment4.limit, (((long) segment4.pos) + j5) - j6);
                for (int i = (int) ((((long) segment4.pos) + j) - j6); i < min; i++) {
                    if (bArr[i] == b) {
                        if (rangeEquals(segment4, i + 1, byteString, 1, size2)) {
                            return ((long) (i - segment4.pos)) + j6;
                        }
                    }
                }
                long j7 = ((long) (segment4.limit - segment4.pos)) + j6;
                segment4 = segment4.next;
                j6 = j7;
                j = j7;
            }
        } else {
            throw new IllegalArgumentException("bytes is empty");
        }
    }

    public long indexOfElement(ByteString byteString) {
        return indexOfElement(byteString, 0);
    }

    public long indexOfElement(ByteString byteString, long j) {
        Segment segment;
        long j2;
        int i;
        if (!(j >= 0)) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        Segment segment2 = this.head;
        if (segment2 == null) {
            return -1;
        }
        if (this.size - j >= j) {
            long j3 = 0;
            Segment segment3 = segment2;
            while (true) {
                long j4 = ((long) (segment.limit - segment.pos)) + j2;
                if (j4 >= j) {
                    break;
                }
                segment3 = segment.next;
                j3 = j4;
            }
        } else {
            j2 = this.size;
            segment = segment2;
            while (true) {
                if (j2 <= j) {
                    break;
                }
                segment = segment.prev;
                j2 -= (long) (segment.limit - segment.pos);
            }
        }
        if (byteString.size() != 2) {
            byte[] internalArray = byteString.internalArray();
            while (true) {
                if (j2 >= this.size) {
                    return -1;
                }
                byte[] bArr = segment.data;
                int i2 = segment.limit;
                for (int i3 = (int) ((((long) segment.pos) + j) - j2); i3 < i2; i3++) {
                    byte b = bArr[i3];
                    for (byte b2 : internalArray) {
                        if (b == b2) {
                            return j2 + ((long) (i3 - segment.pos));
                        }
                    }
                }
                j2 += (long) (segment.limit - segment.pos);
                segment = segment.next;
                j = j2;
            }
        } else {
            byte b3 = byteString.getByte(0);
            byte b4 = byteString.getByte(1);
            loop4:
            while (true) {
                if (j2 >= this.size) {
                    return -1;
                }
                byte[] bArr2 = segment.data;
                i = (int) ((((long) segment.pos) + j) - j2);
                int i4 = segment.limit;
                while (i < i4) {
                    byte b5 = bArr2[i];
                    if (!(b5 == b3 || b5 == b4)) {
                        i++;
                    }
                }
                j2 += (long) (segment.limit - segment.pos);
                segment = segment.next;
                j = j2;
            }
            return j2 + ((long) (i - segment.pos));
        }
    }

    public InputStream inputStream() {
        return new InputStream() {
            public int available() {
                return (int) Math.min(Buffer.this.size, 2147483647L);
            }

            public void close() {
            }

            public int read() {
                if (!(Buffer.this.size <= 0)) {
                    return Buffer.this.readByte() & UnsignedBytes.MAX_VALUE;
                }
                return -1;
            }

            public int read(byte[] bArr, int i, int i2) {
                return Buffer.this.read(bArr, i, i2);
            }

            public String toString() {
                return Buffer.this + ".inputStream()";
            }
        };
    }

    public ByteString md5() {
        return digest("MD5");
    }

    public OutputStream outputStream() {
        return new OutputStream() {
            public void close() {
            }

            public void flush() {
            }

            public String toString() {
                return Buffer.this + ".outputStream()";
            }

            public void write(int i) {
                Buffer.this.writeByte((int) (byte) i);
            }

            public void write(byte[] bArr, int i, int i2) {
                Buffer.this.write(bArr, i, i2);
            }
        };
    }

    public boolean rangeEquals(long j, ByteString byteString) {
        return rangeEquals(j, byteString, 0, byteString.size());
    }

    public boolean rangeEquals(long j, ByteString byteString, int i, int i2) {
        if (!(j < 0) && i >= 0 && i2 >= 0) {
            if (!(this.size - j < ((long) i2)) && byteString.size() - i >= i2) {
                for (int i3 = 0; i3 < i2; i3++) {
                    if (getByte(((long) i3) + j) != byteString.getByte(i + i3)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int read(byte[] bArr) {
        return read(bArr, 0, bArr.length);
    }

    public int read(byte[] bArr, int i, int i2) {
        Util.checkOffsetAndCount((long) bArr.length, (long) i, (long) i2);
        Segment segment = this.head;
        if (segment == null) {
            return -1;
        }
        int min = Math.min(i2, segment.limit - segment.pos);
        System.arraycopy(segment.data, segment.pos, bArr, i, min);
        segment.pos += min;
        this.size -= (long) min;
        if (segment.pos == segment.limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return min;
    }

    public long read(Buffer buffer, long j) {
        boolean z = true;
        if (buffer != null) {
            if (!(j >= 0)) {
                throw new IllegalArgumentException("byteCount < 0: " + j);
            } else if (this.size == 0) {
                return -1;
            } else {
                if (j > this.size) {
                    z = false;
                }
                if (!z) {
                    j = this.size;
                }
                buffer.write(this, j);
                return j;
            }
        } else {
            throw new IllegalArgumentException("sink == null");
        }
    }

    public long readAll(Sink sink) throws IOException {
        long j = this.size;
        if (!(j <= 0)) {
            sink.write(this, j);
        }
        return j;
    }

    public byte readByte() {
        if (this.size == 0) {
            throw new IllegalStateException("size == 0");
        }
        Segment segment = this.head;
        int i = segment.pos;
        int i2 = segment.limit;
        int i3 = i + 1;
        byte b = segment.data[i];
        this.size--;
        if (i3 != i2) {
            segment.pos = i3;
        } else {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return b;
    }

    public byte[] readByteArray() {
        try {
            return readByteArray(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public byte[] readByteArray(long j) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, j);
        if (!(j <= 2147483647L)) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + j);
        }
        byte[] bArr = new byte[((int) j)];
        readFully(bArr);
        return bArr;
    }

    public ByteString readByteString() {
        return new ByteString(readByteArray());
    }

    public ByteString readByteString(long j) throws EOFException {
        return new ByteString(readByteArray(j));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006e, code lost:
        r2 = new okio.Buffer().writeDecimalLong(r8).writeByte((int) r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007b, code lost:
        if (r5 == false) goto L_0x009f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009a, code lost:
        throw new java.lang.NumberFormatException("Number too large: " + r2.readUtf8());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x009f, code lost:
        r2.readByte();
     */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003b  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00b7 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0027 A[EDGE_INSN: B:53:0x0027->B:7:0x0027 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readDecimalLong() {
        /*
            r18 = this;
            r0 = r18
            long r2 = r0.size
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x0013
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.String r3 = "size == 0"
            r2.<init>(r3)
            throw r2
        L_0x0013:
            r8 = 0
            r6 = 0
            r5 = 0
            r4 = 0
            r2 = -7
        L_0x001a:
            r0 = r18
            okio.Segment r11 = r0.head
            byte[] r12 = r11.data
            int r7 = r11.pos
            int r13 = r11.limit
            r10 = r7
        L_0x0025:
            if (r10 < r13) goto L_0x003b
        L_0x0027:
            if (r10 == r13) goto L_0x00d5
            r11.pos = r10
        L_0x002b:
            if (r4 == 0) goto L_0x00e2
        L_0x002d:
            r0 = r18
            long r2 = r0.size
            long r6 = (long) r6
            long r2 = r2 - r6
            r0 = r18
            r0.size = r2
            if (r5 != 0) goto L_0x003a
            long r8 = -r8
        L_0x003a:
            return r8
        L_0x003b:
            byte r14 = r12[r10]
            r7 = 48
            if (r14 >= r7) goto L_0x0049
        L_0x0041:
            r7 = 45
            if (r14 == r7) goto L_0x00b0
        L_0x0045:
            if (r6 == 0) goto L_0x00b7
            r4 = 1
            goto L_0x0027
        L_0x0049:
            r7 = 57
            if (r14 > r7) goto L_0x0041
            int r15 = 48 - r14
            r16 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            int r7 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r7 >= 0) goto L_0x009b
            r7 = 1
        L_0x0059:
            if (r7 != 0) goto L_0x006e
            r16 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            int r7 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r7 != 0) goto L_0x00a3
            long r0 = (long) r15
            r16 = r0
            int r7 = (r16 > r2 ? 1 : (r16 == r2 ? 0 : -1))
            if (r7 < 0) goto L_0x009d
            r7 = 1
        L_0x006c:
            if (r7 != 0) goto L_0x00a3
        L_0x006e:
            okio.Buffer r2 = new okio.Buffer
            r2.<init>()
            okio.Buffer r2 = r2.writeDecimalLong((long) r8)
            okio.Buffer r2 = r2.writeByte((int) r14)
            if (r5 == 0) goto L_0x009f
        L_0x007d:
            java.lang.NumberFormatException r3 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Number too large: "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r2 = r2.readUtf8()
            java.lang.StringBuilder r2 = r4.append(r2)
            java.lang.String r2 = r2.toString()
            r3.<init>(r2)
            throw r3
        L_0x009b:
            r7 = 0
            goto L_0x0059
        L_0x009d:
            r7 = 0
            goto L_0x006c
        L_0x009f:
            r2.readByte()
            goto L_0x007d
        L_0x00a3:
            r16 = 10
            long r8 = r8 * r16
            long r14 = (long) r15
            long r8 = r8 + r14
        L_0x00a9:
            int r7 = r10 + 1
            int r6 = r6 + 1
            r10 = r7
            goto L_0x0025
        L_0x00b0:
            if (r6 != 0) goto L_0x0045
            r5 = 1
            r14 = 1
            long r2 = r2 - r14
            goto L_0x00a9
        L_0x00b7:
            java.lang.NumberFormatException r2 = new java.lang.NumberFormatException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Expected leading [0-9] or '-' character but was 0x"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = java.lang.Integer.toHexString(r14)
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x00d5:
            okio.Segment r7 = r11.pop()
            r0 = r18
            r0.head = r7
            okio.SegmentPool.recycle(r11)
            goto L_0x002b
        L_0x00e2:
            r0 = r18
            okio.Segment r7 = r0.head
            if (r7 == 0) goto L_0x002d
            goto L_0x001a
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readDecimalLong():long");
    }

    public Buffer readFrom(InputStream inputStream) throws IOException {
        readFrom(inputStream, Long.MAX_VALUE, true);
        return this;
    }

    public Buffer readFrom(InputStream inputStream, long j) throws IOException {
        if (!(j >= 0)) {
            throw new IllegalArgumentException("byteCount < 0: " + j);
        }
        readFrom(inputStream, j, false);
        return this;
    }

    public void readFully(Buffer buffer, long j) throws EOFException {
        if (!(this.size >= j)) {
            buffer.write(this, this.size);
            throw new EOFException();
        } else {
            buffer.write(this, j);
        }
    }

    public void readFully(byte[] bArr) throws EOFException {
        int i = 0;
        while (i < bArr.length) {
            int read = read(bArr, i, bArr.length - i);
            if (read != -1) {
                i += read;
            } else {
                throw new EOFException();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0094 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0024 A[EDGE_INSN: B:43:0x0024->B:7:0x0024 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readHexadecimalUnsignedLong() {
        /*
            r18 = this;
            r0 = r18
            long r2 = r0.size
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x0013
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.String r3 = "size == 0"
            r2.<init>(r3)
            throw r2
        L_0x0013:
            r4 = 0
            r3 = 0
            r2 = 0
        L_0x0017:
            r0 = r18
            okio.Segment r10 = r0.head
            byte[] r11 = r10.data
            int r6 = r10.pos
            int r12 = r10.limit
            r7 = r6
        L_0x0022:
            if (r7 < r12) goto L_0x0036
        L_0x0024:
            if (r7 == r12) goto L_0x00be
            r10.pos = r7
        L_0x0028:
            if (r2 == 0) goto L_0x00cb
        L_0x002a:
            r0 = r18
            long r6 = r0.size
            long r2 = (long) r3
            long r2 = r6 - r2
            r0 = r18
            r0.size = r2
            return r4
        L_0x0036:
            byte r8 = r11[r7]
            r6 = 48
            if (r8 >= r6) goto L_0x0048
        L_0x003c:
            r6 = 97
            if (r8 >= r6) goto L_0x0082
        L_0x0040:
            r6 = 65
            if (r8 >= r6) goto L_0x008b
        L_0x0044:
            if (r3 == 0) goto L_0x0094
            r2 = 1
            goto L_0x0024
        L_0x0048:
            r6 = 57
            if (r8 > r6) goto L_0x003c
            int r6 = r8 + -48
        L_0x004e:
            r14 = -1152921504606846976(0xf000000000000000, double:-3.105036184601418E231)
            long r14 = r14 & r4
            r16 = 0
            int r9 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r9 == 0) goto L_0x00b2
            okio.Buffer r2 = new okio.Buffer
            r2.<init>()
            okio.Buffer r2 = r2.writeHexadecimalUnsignedLong((long) r4)
            okio.Buffer r2 = r2.writeByte((int) r8)
            java.lang.NumberFormatException r3 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Number too large: "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r2 = r2.readUtf8()
            java.lang.StringBuilder r2 = r4.append(r2)
            java.lang.String r2 = r2.toString()
            r3.<init>(r2)
            throw r3
        L_0x0082:
            r6 = 102(0x66, float:1.43E-43)
            if (r8 > r6) goto L_0x0040
            int r6 = r8 + -97
            int r6 = r6 + 10
            goto L_0x004e
        L_0x008b:
            r6 = 70
            if (r8 > r6) goto L_0x0044
            int r6 = r8 + -65
            int r6 = r6 + 10
            goto L_0x004e
        L_0x0094:
            java.lang.NumberFormatException r2 = new java.lang.NumberFormatException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Expected leading [0-9a-fA-F] character but was 0x"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = java.lang.Integer.toHexString(r8)
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x00b2:
            r8 = 4
            long r4 = r4 << r8
            long r8 = (long) r6
            long r8 = r8 | r4
            int r4 = r7 + 1
            int r3 = r3 + 1
            r7 = r4
            r4 = r8
            goto L_0x0022
        L_0x00be:
            okio.Segment r6 = r10.pop()
            r0 = r18
            r0.head = r6
            okio.SegmentPool.recycle(r10)
            goto L_0x0028
        L_0x00cb:
            r0 = r18
            okio.Segment r6 = r0.head
            if (r6 == 0) goto L_0x002a
            goto L_0x0017
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readHexadecimalUnsignedLong():long");
    }

    public int readInt() {
        if (!(this.size >= 4)) {
            throw new IllegalStateException("size < 4: " + this.size);
        }
        Segment segment = this.head;
        int i = segment.pos;
        int i2 = segment.limit;
        if (i2 - i < 4) {
            return ((readByte() & UnsignedBytes.MAX_VALUE) << Ascii.CAN) | ((readByte() & UnsignedBytes.MAX_VALUE) << 16) | ((readByte() & UnsignedBytes.MAX_VALUE) << 8) | (readByte() & UnsignedBytes.MAX_VALUE);
        }
        byte[] bArr = segment.data;
        int i3 = i + 1;
        int i4 = i3 + 1;
        byte b = ((bArr[i] & UnsignedBytes.MAX_VALUE) << Ascii.CAN) | ((bArr[i3] & UnsignedBytes.MAX_VALUE) << 16);
        int i5 = i4 + 1;
        byte b2 = b | ((bArr[i4] & UnsignedBytes.MAX_VALUE) << 8);
        int i6 = i5 + 1;
        byte b3 = b2 | (bArr[i5] & UnsignedBytes.MAX_VALUE);
        this.size -= 4;
        if (i6 != i2) {
            segment.pos = i6;
        } else {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return b3;
    }

    public int readIntLe() {
        return Util.reverseBytesInt(readInt());
    }

    public long readLong() {
        if (!(this.size >= 8)) {
            throw new IllegalStateException("size < 8: " + this.size);
        }
        Segment segment = this.head;
        int i = segment.pos;
        int i2 = segment.limit;
        if (i2 - i < 8) {
            return ((((long) readInt()) & 4294967295L) << 32) | (((long) readInt()) & 4294967295L);
        }
        byte[] bArr = segment.data;
        int i3 = i + 1;
        int i4 = i3 + 1;
        long j = ((((long) bArr[i3]) & 255) << 48) | ((((long) bArr[i]) & 255) << 56);
        int i5 = i4 + 1;
        int i6 = i5 + 1;
        long j2 = j | ((((long) bArr[i4]) & 255) << 40) | ((((long) bArr[i5]) & 255) << 32);
        int i7 = i6 + 1;
        int i8 = i7 + 1;
        long j3 = j2 | ((((long) bArr[i6]) & 255) << 24) | ((((long) bArr[i7]) & 255) << 16);
        int i9 = i8 + 1;
        int i10 = i9 + 1;
        long j4 = j3 | ((((long) bArr[i8]) & 255) << 8) | (((long) bArr[i9]) & 255);
        this.size -= 8;
        if (i10 != i2) {
            segment.pos = i10;
        } else {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return j4;
    }

    public long readLongLe() {
        return Util.reverseBytesLong(readLong());
    }

    public short readShort() {
        if (!(this.size >= 2)) {
            throw new IllegalStateException("size < 2: " + this.size);
        }
        Segment segment = this.head;
        int i = segment.pos;
        int i2 = segment.limit;
        if (i2 - i < 2) {
            return (short) (((readByte() & UnsignedBytes.MAX_VALUE) << 8) | (readByte() & UnsignedBytes.MAX_VALUE));
        }
        byte[] bArr = segment.data;
        int i3 = i + 1;
        int i4 = i3 + 1;
        byte b = ((bArr[i] & UnsignedBytes.MAX_VALUE) << 8) | (bArr[i3] & UnsignedBytes.MAX_VALUE);
        this.size -= 2;
        if (i4 != i2) {
            segment.pos = i4;
        } else {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return (short) b;
    }

    public short readShortLe() {
        return Util.reverseBytesShort(readShort());
    }

    public String readString(long j, Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, j);
        if (charset != null) {
            if (!(j <= 2147483647L)) {
                throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + j);
            } else if (j == 0) {
                return "";
            } else {
                Segment segment = this.head;
                if (!(((long) segment.pos) + j <= ((long) segment.limit))) {
                    return new String(readByteArray(j), charset);
                }
                String str = new String(segment.data, segment.pos, (int) j, charset);
                segment.pos = (int) (((long) segment.pos) + j);
                this.size -= j;
                if (segment.pos == segment.limit) {
                    this.head = segment.pop();
                    SegmentPool.recycle(segment);
                }
                return str;
            }
        } else {
            throw new IllegalArgumentException("charset == null");
        }
    }

    public String readString(Charset charset) {
        try {
            return readString(this.size, charset);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readUtf8() {
        try {
            return readString(this.size, Util.UTF_8);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readUtf8(long j) throws EOFException {
        return readString(j, Util.UTF_8);
    }

    public int readUtf8CodePoint() throws EOFException {
        byte b;
        int i;
        byte b2;
        boolean z = false;
        if (this.size == 0) {
            throw new EOFException();
        }
        byte b3 = getByte(0);
        if ((b3 & 128) == 0) {
            i = 1;
            b = b3 & Ascii.DEL;
            b2 = 0;
        } else if ((b3 & 224) == 192) {
            b = b3 & 31;
            i = 2;
            b2 = 128;
        } else if ((b3 & PrimProfileParams.LL_PCODE_HOLE_MASK) == 224) {
            b = b3 & 15;
            i = 3;
            b2 = 2048;
        } else if ((b3 & 248) != 240) {
            skip(1);
            return REPLACEMENT_CHARACTER;
        } else {
            b = b3 & 7;
            i = 4;
            b2 = 65536;
        }
        if (this.size >= ((long) i)) {
            z = true;
        }
        if (!z) {
            throw new EOFException("size < " + i + ": " + this.size + " (to read code point prefixed 0x" + Integer.toHexString(b3) + ")");
        }
        for (int i2 = 1; i2 < i; i2++) {
            byte b4 = getByte((long) i2);
            if ((b4 & MutableSLTextureEntryFace.SHINY_MASK) != 128) {
                skip((long) i2);
                return REPLACEMENT_CHARACTER;
            }
            b = (b << 6) | (b4 & 63);
        }
        skip((long) i);
        return b <= 1114111 ? ((b >= 55296 && b <= 57343) || b < b2) ? REPLACEMENT_CHARACTER : b : REPLACEMENT_CHARACTER;
    }

    public String readUtf8Line() throws EOFException {
        long indexOf = indexOf((byte) 10);
        if (indexOf != -1) {
            return readUtf8Line(indexOf);
        }
        if (this.size != 0) {
            return readUtf8(this.size);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public String readUtf8Line(long j) throws EOFException {
        if ((j <= 0) || getByte(j - 1) != 13) {
            String readUtf8 = readUtf8(j);
            skip(1);
            return readUtf8;
        }
        String readUtf82 = readUtf8(j - 1);
        skip(2);
        return readUtf82;
    }

    public String readUtf8LineStrict() throws EOFException {
        long indexOf = indexOf((byte) 10);
        if (indexOf != -1) {
            return readUtf8Line(indexOf);
        }
        Buffer buffer = new Buffer();
        copyTo(buffer, 0, Math.min(32, this.size));
        throw new EOFException("\\n not found: size=" + size() + " content=" + buffer.readByteString().hex() + "…");
    }

    public boolean request(long j) {
        return !((this.size > j ? 1 : (this.size == j ? 0 : -1)) < 0);
    }

    public void require(long j) throws EOFException {
        if (!(this.size >= j)) {
            throw new EOFException();
        }
    }

    /* access modifiers changed from: package-private */
    public List<Integer> segmentSizes() {
        if (this.head == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(this.head.limit - this.head.pos));
        for (Segment segment = this.head.next; segment != this.head; segment = segment.next) {
            arrayList.add(Integer.valueOf(segment.limit - segment.pos));
        }
        return arrayList;
    }

    public int select(Options options) {
        Segment segment = this.head;
        if (segment == null) {
            return options.indexOf(ByteString.EMPTY);
        }
        ByteString[] byteStringArr = options.byteStrings;
        int length = byteStringArr.length;
        for (int i = 0; i < length; i++) {
            ByteString byteString = byteStringArr[i];
            if (!(this.size < ((long) byteString.size()))) {
                if (rangeEquals(segment, segment.pos, byteString, 0, byteString.size())) {
                    try {
                        skip((long) byteString.size());
                        return i;
                    } catch (EOFException e) {
                        throw new AssertionError(e);
                    }
                }
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public int selectPrefix(Options options) {
        Segment segment = this.head;
        ByteString[] byteStringArr = options.byteStrings;
        int length = byteStringArr.length;
        int i = 0;
        while (i < length) {
            ByteString byteString = byteStringArr[i];
            int min = (int) Math.min(this.size, (long) byteString.size());
            if (min != 0) {
                if (!rangeEquals(segment, segment.pos, byteString, 0, min)) {
                    i++;
                }
            }
            return i;
        }
        return -1;
    }

    public ByteString sha1() {
        return digest("SHA-1");
    }

    public ByteString sha256() {
        return digest("SHA-256");
    }

    public long size() {
        return this.size;
    }

    public void skip(long j) throws EOFException {
        while (true) {
            if (j <= 0) {
                return;
            }
            if (this.head != null) {
                int min = (int) Math.min(j, (long) (this.head.limit - this.head.pos));
                this.size -= (long) min;
                j -= (long) min;
                Segment segment = this.head;
                segment.pos = min + segment.pos;
                if (this.head.pos == this.head.limit) {
                    Segment segment2 = this.head;
                    this.head = segment2.pop();
                    SegmentPool.recycle(segment2);
                }
            } else {
                throw new EOFException();
            }
        }
    }

    public ByteString snapshot() {
        if (this.size <= 2147483647L) {
            return snapshot((int) this.size);
        }
        throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.size);
    }

    public ByteString snapshot(int i) {
        return i != 0 ? new SegmentedByteString(this, i) : ByteString.EMPTY;
    }

    public Timeout timeout() {
        return Timeout.NONE;
    }

    public String toString() {
        return snapshot().toString();
    }

    /* access modifiers changed from: package-private */
    public Segment writableSegment(int i) {
        if (i < 1 || i > 8192) {
            throw new IllegalArgumentException();
        } else if (this.head != null) {
            Segment segment = this.head.prev;
            return (segment.limit + i <= 8192 && segment.owner) ? segment : segment.push(SegmentPool.take());
        } else {
            this.head = SegmentPool.take();
            Segment segment2 = this.head;
            Segment segment3 = this.head;
            Segment segment4 = this.head;
            segment3.prev = segment4;
            segment2.next = segment4;
            return segment4;
        }
    }

    public Buffer write(ByteString byteString) {
        if (byteString != null) {
            byteString.write(this);
            return this;
        }
        throw new IllegalArgumentException("byteString == null");
    }

    public Buffer write(byte[] bArr) {
        if (bArr != null) {
            return write(bArr, 0, bArr.length);
        }
        throw new IllegalArgumentException("source == null");
    }

    public Buffer write(byte[] bArr, int i, int i2) {
        if (bArr != null) {
            Util.checkOffsetAndCount((long) bArr.length, (long) i, (long) i2);
            int i3 = i + i2;
            while (i < i3) {
                Segment writableSegment = writableSegment(1);
                int min = Math.min(i3 - i, 8192 - writableSegment.limit);
                System.arraycopy(bArr, i, writableSegment.data, writableSegment.limit, min);
                i += min;
                writableSegment.limit = min + writableSegment.limit;
            }
            this.size += (long) i2;
            return this;
        }
        throw new IllegalArgumentException("source == null");
    }

    public BufferedSink write(Source source, long j) throws IOException {
        while (true) {
            if (j <= 0) {
                return this;
            }
            long read = source.read(this, j);
            if (read == -1) {
                throw new EOFException();
            }
            j -= read;
        }
    }

    public void write(Buffer buffer, long j) {
        if (buffer == null) {
            throw new IllegalArgumentException("source == null");
        } else if (buffer != this) {
            Util.checkOffsetAndCount(buffer.size, 0, j);
            while (true) {
                if (!(j <= 0)) {
                    if (!(j >= ((long) (buffer.head.limit - buffer.head.pos)))) {
                        Segment segment = this.head == null ? null : this.head.prev;
                        if (segment != null && segment.owner) {
                            if (!((((long) segment.limit) + j) - ((long) (!segment.shared ? segment.pos : 0)) > 8192)) {
                                buffer.head.writeTo(segment, (int) j);
                                buffer.size -= j;
                                this.size += j;
                                return;
                            }
                        }
                        buffer.head = buffer.head.split((int) j);
                    }
                    Segment segment2 = buffer.head;
                    long j2 = (long) (segment2.limit - segment2.pos);
                    buffer.head = segment2.pop();
                    if (this.head != null) {
                        this.head.prev.push(segment2).compact();
                    } else {
                        this.head = segment2;
                        Segment segment3 = this.head;
                        Segment segment4 = this.head;
                        Segment segment5 = this.head;
                        segment4.prev = segment5;
                        segment3.next = segment5;
                    }
                    buffer.size -= j2;
                    this.size += j2;
                    j -= j2;
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("source == this");
        }
    }

    public long writeAll(Source source) throws IOException {
        if (source != null) {
            long j = 0;
            while (true) {
                long read = source.read(this, 8192);
                if (read == -1) {
                    return j;
                }
                j += read;
            }
        } else {
            throw new IllegalArgumentException("source == null");
        }
    }

    public Buffer writeByte(int i) {
        Segment writableSegment = writableSegment(1);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit;
        writableSegment.limit = i2 + 1;
        bArr[i2] = (byte) ((byte) i);
        this.size++;
        return this;
    }

    public Buffer writeDecimalLong(long j) {
        boolean z;
        long j2;
        int i;
        if (j == 0) {
            return writeByte(48);
        }
        if (!(j >= 0)) {
            j2 = -j;
            if (!(j2 >= 0)) {
                return writeUtf8("-9223372036854775808");
            }
            z = true;
        } else {
            z = false;
            j2 = j;
        }
        if (!(j2 >= 100000000)) {
            if (!(j2 >= 10000)) {
                if (!(j2 >= 100)) {
                    i = !((j2 > 10 ? 1 : (j2 == 10 ? 0 : -1)) >= 0) ? 1 : 2;
                } else {
                    i = !((j2 > 1000 ? 1 : (j2 == 1000 ? 0 : -1)) >= 0) ? 3 : 4;
                }
            } else {
                if (!(j2 >= 1000000)) {
                    i = !((j2 > 100000 ? 1 : (j2 == 100000 ? 0 : -1)) >= 0) ? 5 : 6;
                } else {
                    i = !((j2 > 10000000 ? 1 : (j2 == 10000000 ? 0 : -1)) >= 0) ? 7 : 8;
                }
            }
        } else {
            if (!(j2 >= 1000000000000L)) {
                if (!(j2 >= 10000000000L)) {
                    i = !((j2 > 1000000000 ? 1 : (j2 == 1000000000 ? 0 : -1)) >= 0) ? 9 : 10;
                } else {
                    i = !((j2 > 100000000000L ? 1 : (j2 == 100000000000L ? 0 : -1)) >= 0) ? 11 : 12;
                }
            } else {
                if (!(j2 >= 1000000000000000L)) {
                    if (!(j2 >= 10000000000000L)) {
                        i = 13;
                    } else {
                        i = !((j2 > 100000000000000L ? 1 : (j2 == 100000000000000L ? 0 : -1)) >= 0) ? 14 : 15;
                    }
                } else {
                    if (!(j2 >= 100000000000000000L)) {
                        i = !((j2 > 10000000000000000L ? 1 : (j2 == 10000000000000000L ? 0 : -1)) >= 0) ? 16 : 17;
                    } else {
                        i = !((j2 > 1000000000000000000L ? 1 : (j2 == 1000000000000000000L ? 0 : -1)) >= 0) ? 18 : 19;
                    }
                }
            }
        }
        if (z) {
            i++;
        }
        Segment writableSegment = writableSegment(i);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit + i;
        while (j2 != 0) {
            i2--;
            bArr[i2] = (byte) DIGITS[(int) (j2 % 10)];
            j2 /= 10;
        }
        if (z) {
            bArr[i2 - 1] = 45;
        }
        writableSegment.limit += i;
        this.size = ((long) i) + this.size;
        return this;
    }

    public Buffer writeHexadecimalUnsignedLong(long j) {
        if (j == 0) {
            return writeByte(48);
        }
        int numberOfTrailingZeros = (Long.numberOfTrailingZeros(Long.highestOneBit(j)) / 4) + 1;
        Segment writableSegment = writableSegment(numberOfTrailingZeros);
        byte[] bArr = writableSegment.data;
        int i = writableSegment.limit;
        for (int i2 = (writableSegment.limit + numberOfTrailingZeros) - 1; i2 >= i; i2--) {
            bArr[i2] = (byte) DIGITS[(int) (15 & j)];
            j >>>= 4;
        }
        writableSegment.limit += numberOfTrailingZeros;
        this.size = ((long) numberOfTrailingZeros) + this.size;
        return this;
    }

    public Buffer writeInt(int i) {
        Segment writableSegment = writableSegment(4);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit;
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((byte) ((i >>> 24) & 255));
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((byte) ((i >>> 16) & 255));
        int i5 = i4 + 1;
        bArr[i4] = (byte) ((byte) ((i >>> 8) & 255));
        bArr[i5] = (byte) ((byte) (i & 255));
        writableSegment.limit = i5 + 1;
        this.size += 4;
        return this;
    }

    public Buffer writeIntLe(int i) {
        return writeInt(Util.reverseBytesInt(i));
    }

    public Buffer writeLong(long j) {
        Segment writableSegment = writableSegment(8);
        byte[] bArr = writableSegment.data;
        int i = writableSegment.limit;
        int i2 = i + 1;
        bArr[i] = (byte) ((byte) ((int) ((j >>> 56) & 255)));
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((byte) ((int) ((j >>> 48) & 255)));
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((byte) ((int) ((j >>> 40) & 255)));
        int i5 = i4 + 1;
        bArr[i4] = (byte) ((byte) ((int) ((j >>> 32) & 255)));
        int i6 = i5 + 1;
        bArr[i5] = (byte) ((byte) ((int) ((j >>> 24) & 255)));
        int i7 = i6 + 1;
        bArr[i6] = (byte) ((byte) ((int) ((j >>> 16) & 255)));
        int i8 = i7 + 1;
        bArr[i7] = (byte) ((byte) ((int) ((j >>> 8) & 255)));
        bArr[i8] = (byte) ((byte) ((int) (j & 255)));
        writableSegment.limit = i8 + 1;
        this.size += 8;
        return this;
    }

    public Buffer writeLongLe(long j) {
        return writeLong(Util.reverseBytesLong(j));
    }

    public Buffer writeShort(int i) {
        Segment writableSegment = writableSegment(2);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit;
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((byte) ((i >>> 8) & 255));
        bArr[i3] = (byte) ((byte) (i & 255));
        writableSegment.limit = i3 + 1;
        this.size += 2;
        return this;
    }

    public Buffer writeShortLe(int i) {
        return writeShort((int) Util.reverseBytesShort((short) i));
    }

    public Buffer writeString(String str, int i, int i2, Charset charset) {
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        } else if (i < 0) {
            throw new IllegalAccessError("beginIndex < 0: " + i);
        } else if (i2 < i) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + i2 + " < " + i);
        } else if (i2 > str.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + i2 + " > " + str.length());
        } else if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (charset.equals(Util.UTF_8)) {
            return writeUtf8(str, i, i2);
        } else {
            byte[] bytes = str.substring(i, i2).getBytes(charset);
            return write(bytes, 0, bytes.length);
        }
    }

    public Buffer writeString(String str, Charset charset) {
        return writeString(str, 0, str.length(), charset);
    }

    public Buffer writeTo(OutputStream outputStream) throws IOException {
        return writeTo(outputStream, this.size);
    }

    public Buffer writeTo(OutputStream outputStream, long j) throws IOException {
        if (outputStream != null) {
            Util.checkOffsetAndCount(this.size, 0, j);
            Segment segment = this.head;
            while (true) {
                if (j <= 0) {
                    return this;
                }
                int min = (int) Math.min(j, (long) (segment.limit - segment.pos));
                outputStream.write(segment.data, segment.pos, min);
                segment.pos += min;
                this.size -= (long) min;
                j -= (long) min;
                if (segment.pos == segment.limit) {
                    Segment pop = segment.pop();
                    this.head = pop;
                    SegmentPool.recycle(segment);
                    segment = pop;
                }
            }
        } else {
            throw new IllegalArgumentException("out == null");
        }
    }

    public Buffer writeUtf8(String str) {
        return writeUtf8(str, 0, str.length());
    }

    public Buffer writeUtf8(String str, int i, int i2) {
        int i3;
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        } else if (i < 0) {
            throw new IllegalAccessError("beginIndex < 0: " + i);
        } else if (i2 < i) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + i2 + " < " + i);
        } else if (i2 <= str.length()) {
            while (i < i2) {
                char charAt = str.charAt(i);
                if (charAt < 128) {
                    Segment writableSegment = writableSegment(1);
                    byte[] bArr = writableSegment.data;
                    int i4 = writableSegment.limit - i;
                    int min = Math.min(i2, 8192 - i4);
                    i3 = i + 1;
                    bArr[i4 + i] = (byte) ((byte) charAt);
                    while (i3 < min) {
                        char charAt2 = str.charAt(i3);
                        if (charAt2 >= 128) {
                            break;
                        }
                        bArr[i3 + i4] = (byte) ((byte) charAt2);
                        i3++;
                    }
                    int i5 = (i3 + i4) - writableSegment.limit;
                    writableSegment.limit += i5;
                    this.size = ((long) i5) + this.size;
                } else if (charAt < 2048) {
                    writeByte((charAt >> 6) | 192);
                    writeByte((int) (charAt & '?') | 128);
                    i3 = i + 1;
                } else if (charAt >= 55296 && charAt <= 57343) {
                    char charAt3 = i + 1 >= i2 ? 0 : str.charAt(i + 1);
                    if (charAt <= 56319 && charAt3 >= 56320 && charAt3 <= 57343) {
                        int i6 = ((charAt3 & 9215) | ((charAt & 10239) << 10)) + Ascii.MIN;
                        writeByte((i6 >> 18) | 240);
                        writeByte(((i6 >> 12) & 63) | 128);
                        writeByte(((i6 >> 6) & 63) | 128);
                        writeByte((i6 & 63) | 128);
                        i3 = i + 2;
                    } else {
                        writeByte(63);
                        i++;
                    }
                } else {
                    writeByte((charAt >> 12) | 224);
                    writeByte(((charAt >> 6) & 63) | 128);
                    writeByte((int) (charAt & '?') | 128);
                    i3 = i + 1;
                }
                i = i3;
            }
            return this;
        } else {
            throw new IllegalArgumentException("endIndex > string.length: " + i2 + " > " + str.length());
        }
    }

    public Buffer writeUtf8CodePoint(int i) {
        if (i < 128) {
            writeByte(i);
        } else if (i < 2048) {
            writeByte((i >> 6) | 192);
            writeByte((i & 63) | 128);
        } else if (i >= 65536) {
            if (i > 1114111) {
                throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(i));
            }
            writeByte((i >> 18) | 240);
            writeByte(((i >> 12) & 63) | 128);
            writeByte(((i >> 6) & 63) | 128);
            writeByte((i & 63) | 128);
        } else if (i >= 55296 && i <= 57343) {
            throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(i));
        } else {
            writeByte((i >> 12) | 224);
            writeByte(((i >> 6) & 63) | 128);
            writeByte((i & 63) | 128);
        }
        return this;
    }
}
