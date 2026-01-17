package okio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SegmentedByteString extends ByteString {
    final transient int[] directory;
    final transient byte[][] segments;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SegmentedByteString(Buffer buffer, int i) {
        super(null);
        int i2 = 0;
        Util.checkOffsetAndCount(buffer.size, 0L, i);
        Segment segment = buffer.head;
        int i3 = 0;
        int i4 = 0;
        while (i4 < i) {
            if (segment.limit == segment.pos) {
                throw new AssertionError("s.limit == s.pos");
            }
            i4 += segment.limit - segment.pos;
            i3++;
            segment = segment.next;
        }
        this.segments = new byte[i3];
        this.directory = new int[i3 * 2];
        Segment segment2 = buffer.head;
        int i5 = 0;
        while (i2 < i) {
            this.segments[i5] = segment2.data;
            int i6 = (segment2.limit - segment2.pos) + i2;
            if (i6 > i) {
                i6 = i;
            }
            this.directory[i5] = i6;
            this.directory[this.segments.length + i5] = segment2.pos;
            segment2.shared = true;
            i5++;
            segment2 = segment2.next;
            i2 = i6;
        }
    }

    private int segment(int i) {
        int binarySearch = Arrays.binarySearch(this.directory, 0, this.segments.length, i + 1);
        return binarySearch < 0 ? binarySearch ^ (-1) : binarySearch;
    }

    private ByteString toByteString() {
        return new ByteString(toByteArray());
    }

    private Object writeReplace() {
        return toByteString();
    }

    @Override // okio.ByteString
    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(toByteArray()).asReadOnlyBuffer();
    }

    @Override // okio.ByteString
    public String base64() {
        return toByteString().base64();
    }

    @Override // okio.ByteString
    public String base64Url() {
        return toByteString().base64Url();
    }

    @Override // okio.ByteString
    public boolean equals(Object obj) {
        if (obj != this) {
            return (obj instanceof ByteString) && ((ByteString) obj).size() == size() && rangeEquals(0, (ByteString) obj, 0, size());
        }
        return true;
    }

    @Override // okio.ByteString
    public byte getByte(int i) {
        Util.checkOffsetAndCount(this.directory[this.segments.length - 1], i, 1L);
        int segment = segment(i);
        return this.segments[segment][(i - (segment != 0 ? this.directory[segment - 1] : 0)) + this.directory[this.segments.length + segment]];
    }

    @Override // okio.ByteString
    public int hashCode() {
        int i = this.hashCode;
        if (i == 0) {
            int i2 = 1;
            int length = this.segments.length;
            int i3 = 0;
            int i4 = 0;
            while (i3 < length) {
                byte[] bArr = this.segments[i3];
                int i5 = this.directory[length + i3];
                int i6 = this.directory[i3];
                int i7 = (i6 - i4) + i5;
                while (i5 < i7) {
                    i2 = (i2 * 31) + bArr[i5];
                    i5++;
                }
                i3++;
                i4 = i6;
            }
            this.hashCode = i2;
            return i2;
        }
        return i;
    }

    @Override // okio.ByteString
    public String hex() {
        return toByteString().hex();
    }

    @Override // okio.ByteString
    public ByteString hmacSha1(ByteString byteString) {
        return toByteString().hmacSha1(byteString);
    }

    @Override // okio.ByteString
    public ByteString hmacSha256(ByteString byteString) {
        return toByteString().hmacSha256(byteString);
    }

    @Override // okio.ByteString
    public int indexOf(byte[] bArr, int i) {
        return toByteString().indexOf(bArr, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // okio.ByteString
    public byte[] internalArray() {
        return toByteArray();
    }

    @Override // okio.ByteString
    public int lastIndexOf(byte[] bArr, int i) {
        return toByteString().lastIndexOf(bArr, i);
    }

    @Override // okio.ByteString
    public ByteString md5() {
        return toByteString().md5();
    }

    @Override // okio.ByteString
    public boolean rangeEquals(int i, ByteString byteString, int i2, int i3) {
        if (i >= 0 && i <= size() - i3) {
            int segment = segment(i);
            while (i3 > 0) {
                int i4 = segment != 0 ? this.directory[segment - 1] : 0;
                int min = Math.min(i3, ((this.directory[segment] - i4) + i4) - i);
                if (!byteString.rangeEquals(i2, this.segments[segment], (i - i4) + this.directory[this.segments.length + segment], min)) {
                    return false;
                }
                i += min;
                i2 += min;
                i3 -= min;
                segment++;
            }
            return true;
        }
        return false;
    }

    @Override // okio.ByteString
    public boolean rangeEquals(int i, byte[] bArr, int i2, int i3) {
        if (i >= 0 && i <= size() - i3 && i2 >= 0 && i2 <= bArr.length - i3) {
            int segment = segment(i);
            while (i3 > 0) {
                int i4 = segment != 0 ? this.directory[segment - 1] : 0;
                int min = Math.min(i3, ((this.directory[segment] - i4) + i4) - i);
                if (!Util.arrayRangeEquals(this.segments[segment], (i - i4) + this.directory[this.segments.length + segment], bArr, i2, min)) {
                    return false;
                }
                i += min;
                i2 += min;
                i3 -= min;
                segment++;
            }
            return true;
        }
        return false;
    }

    @Override // okio.ByteString
    public ByteString sha1() {
        return toByteString().sha1();
    }

    @Override // okio.ByteString
    public ByteString sha256() {
        return toByteString().sha256();
    }

    @Override // okio.ByteString
    public int size() {
        return this.directory[this.segments.length - 1];
    }

    @Override // okio.ByteString
    public String string(Charset charset) {
        return toByteString().string(charset);
    }

    @Override // okio.ByteString
    public ByteString substring(int i) {
        return toByteString().substring(i);
    }

    @Override // okio.ByteString
    public ByteString substring(int i, int i2) {
        return toByteString().substring(i, i2);
    }

    @Override // okio.ByteString
    public ByteString toAsciiLowercase() {
        return toByteString().toAsciiLowercase();
    }

    @Override // okio.ByteString
    public ByteString toAsciiUppercase() {
        return toByteString().toAsciiUppercase();
    }

    @Override // okio.ByteString
    public byte[] toByteArray() {
        int i = 0;
        byte[] bArr = new byte[this.directory[this.segments.length - 1]];
        int length = this.segments.length;
        int i2 = 0;
        while (i < length) {
            int i3 = this.directory[length + i];
            int i4 = this.directory[i];
            System.arraycopy(this.segments[i], i3, bArr, i2, i4 - i2);
            i++;
            i2 = i4;
        }
        return bArr;
    }

    @Override // okio.ByteString
    public String toString() {
        return toByteString().toString();
    }

    @Override // okio.ByteString
    public String utf8() {
        return toByteString().utf8();
    }

    @Override // okio.ByteString
    public void write(OutputStream outputStream) throws IOException {
        int i = 0;
        if (outputStream == null) {
            throw new IllegalArgumentException("out == null");
        }
        int length = this.segments.length;
        int i2 = 0;
        while (i < length) {
            int i3 = this.directory[length + i];
            int i4 = this.directory[i];
            outputStream.write(this.segments[i], i3, i4 - i2);
            i++;
            i2 = i4;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // okio.ByteString
    public void write(Buffer buffer) {
        int i = 0;
        int length = this.segments.length;
        int i2 = 0;
        while (i < length) {
            int i3 = this.directory[length + i];
            int i4 = this.directory[i];
            Segment segment = new Segment(this.segments[i], i3, (i3 + i4) - i2);
            if (buffer.head != null) {
                buffer.head.prev.push(segment);
            } else {
                segment.prev = segment;
                segment.next = segment;
                buffer.head = segment;
            }
            i++;
            i2 = i4;
        }
        buffer.size = i2 + buffer.size;
    }
}
