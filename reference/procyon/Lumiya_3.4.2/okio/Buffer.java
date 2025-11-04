// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public final class Buffer implements BufferedSource, BufferedSink, Cloneable
{
    private static final byte[] DIGITS;
    static final int REPLACEMENT_CHARACTER = 65533;
    Segment head;
    long size;
    
    static {
        DIGITS = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
    }
    
    private ByteString digest(final String algorithm) {
        try {
            final MessageDigest instance = MessageDigest.getInstance(algorithm);
            if (this.head != null) {
                instance.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment segment = this.head.next; segment != this.head; segment = segment.next) {
                    instance.update(segment.data, segment.pos, segment.limit - segment.pos);
                }
            }
            return ByteString.of(instance.digest());
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new AssertionError();
        }
    }
    
    private ByteString hmac(final String s, final ByteString byteString) {
        try {
            final Mac instance = Mac.getInstance(s);
            instance.init(new SecretKeySpec(byteString.toByteArray(), s));
            if (this.head != null) {
                instance.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment segment = this.head.next; segment != this.head; segment = segment.next) {
                    instance.update(segment.data, segment.pos, segment.limit - segment.pos);
                }
            }
            return ByteString.of(instance.doFinal());
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new AssertionError();
        }
        catch (final InvalidKeyException cause) {
            throw new IllegalArgumentException(cause);
        }
    }
    
    private boolean rangeEquals(final Segment segment, int limit, final ByteString byteString, int i, final int n) {
        int limit2 = segment.limit;
        final byte[] data = segment.data;
        Segment next = segment;
        byte[] data2 = data;
        while (i < n) {
            int pos;
            if (limit != limit2) {
                pos = limit;
                limit = limit2;
            }
            else {
                next = next.next;
                data2 = next.data;
                pos = next.pos;
                limit = next.limit;
            }
            if (data2[pos] != byteString.getByte(i)) {
                return false;
            }
            ++pos;
            ++i;
            limit2 = limit;
            limit = pos;
        }
        return true;
    }
    
    private void readFrom(final InputStream inputStream, long a, final boolean b) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("in == null");
        }
        while (true) {
            boolean b2;
            if (a > 0L) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            if (!b2 && !b) {
                return;
            }
            final Segment writableSegment = this.writableSegment(1);
            final int read = inputStream.read(writableSegment.data, writableSegment.limit, (int)Math.min(a, 8192 - writableSegment.limit));
            if (read != -1) {
                writableSegment.limit += read;
                this.size += read;
                a -= read;
            }
            else if (!b) {
                throw new EOFException();
            }
        }
    }
    
    @Override
    public Buffer buffer() {
        return this;
    }
    
    public void clear() {
        try {
            this.skip(this.size);
        }
        catch (final EOFException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    public Buffer clone() {
        final Buffer buffer = new Buffer();
        if (this.size == 0L) {
            return buffer;
        }
        buffer.head = new Segment(this.head);
        final Segment head = buffer.head;
        final Segment head2 = buffer.head;
        final Segment head3 = buffer.head;
        head2.prev = head3;
        head.next = head3;
        for (Segment segment = this.head.next; segment != this.head; segment = segment.next) {
            buffer.head.prev.push(new Segment(segment));
        }
        buffer.size = this.size;
        return buffer;
    }
    
    @Override
    public void close() {
    }
    
    public long completeSegmentByteCount() {
        final long size = this.size;
        if (size == 0L) {
            return 0L;
        }
        final Segment prev = this.head.prev;
        long n;
        if (prev.limit >= 8192) {
            n = size;
        }
        else {
            n = size;
            if (prev.owner) {
                n = size - (prev.limit - prev.pos);
            }
        }
        return n;
    }
    
    public Buffer copyTo(final OutputStream outputStream) throws IOException {
        return this.copyTo(outputStream, 0L, this.size);
    }
    
    public Buffer copyTo(final OutputStream outputStream, long n, long b) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, n, b);
        if (b == 0L) {
            return this;
        }
        Segment segment = this.head;
        while (true) {
            int n2;
            if (n < segment.limit - segment.pos) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                break;
            }
            n -= segment.limit - segment.pos;
            segment = segment.next;
        }
        while (true) {
            int n3;
            if (b <= 0L) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 != 0) {
                break;
            }
            final int off = (int)(segment.pos + n);
            final int len = (int)Math.min(segment.limit - off, b);
            outputStream.write(segment.data, off, len);
            b -= len;
            segment = segment.next;
            n = 0L;
        }
        return this;
    }
    
    public Buffer copyTo(final Buffer buffer, long n, long n2) {
        if (buffer == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, n, n2);
        if (n2 == 0L) {
            return this;
        }
        buffer.size += n2;
        Segment segment = this.head;
        while (true) {
            int n3;
            if (n < segment.limit - segment.pos) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 != 0) {
                break;
            }
            n -= segment.limit - segment.pos;
            segment = segment.next;
        }
        while (true) {
            int n4;
            if (n2 <= 0L) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            if (n4 != 0) {
                break;
            }
            final Segment head = new Segment(segment);
            head.pos += (int)n;
            head.limit = Math.min(head.pos + (int)n2, head.limit);
            if (buffer.head != null) {
                buffer.head.prev.push(head);
            }
            else {
                head.prev = head;
                head.next = head;
                buffer.head = head;
            }
            n2 -= head.limit - head.pos;
            segment = segment.next;
            n = 0L;
        }
        return this;
    }
    
    @Override
    public BufferedSink emit() {
        return this;
    }
    
    @Override
    public Buffer emitCompleteSegments() {
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        long n = 0L;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Buffer)) {
            return false;
        }
        final Buffer buffer = (Buffer)o;
        if (this.size != buffer.size) {
            return false;
        }
        if (this.size == 0L) {
            return true;
        }
        Segment segment = this.head;
        Segment segment2 = buffer.head;
        int n2 = segment.pos;
        int n3 = segment2.pos;
        while (true) {
            int n4;
            if (n >= this.size) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            if (n4 != 0) {
                return true;
            }
            final long n5 = Math.min(segment.limit - n2, segment2.limit - n3);
            int n6 = 0;
            while (true) {
                int n7;
                if (n6 >= n5) {
                    n7 = 1;
                }
                else {
                    n7 = 0;
                }
                if (n7 != 0) {
                    if (n2 == segment.limit) {
                        segment = segment.next;
                        n2 = segment.pos;
                    }
                    if (n3 == segment2.limit) {
                        segment2 = segment2.next;
                        n3 = segment2.pos;
                    }
                    n += n5;
                    break;
                }
                if (segment.data[n2] != segment2.data[n3]) {
                    return false;
                }
                ++n6;
                ++n3;
                ++n2;
            }
        }
    }
    
    @Override
    public boolean exhausted() {
        return this.size == 0L;
    }
    
    @Override
    public void flush() {
    }
    
    public byte getByte(long n) {
        Util.checkOffsetAndCount(this.size, n, 1L);
        Segment segment = this.head;
        while (true) {
            final int n2 = segment.limit - segment.pos;
            int n3;
            if (n >= n2) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                break;
            }
            n -= n2;
            segment = segment.next;
        }
        return segment.data[segment.pos + (int)n];
    }
    
    @Override
    public int hashCode() {
        Segment head = this.head;
        if (head != null) {
            int n = 1;
            int n2;
            Segment next;
            do {
                int i;
                int limit;
                byte b;
                for (i = head.pos, limit = head.limit, n2 = n; i < limit; ++i, n2 = b + n2 * 31) {
                    b = head.data[i];
                }
                next = head.next;
                n = n2;
            } while ((head = next) != this.head);
            return n2;
        }
        return 0;
    }
    
    public ByteString hmacSha1(final ByteString byteString) {
        return this.hmac("HmacSHA1", byteString);
    }
    
    public ByteString hmacSha256(final ByteString byteString) {
        return this.hmac("HmacSHA256", byteString);
    }
    
    @Override
    public long indexOf(final byte b) {
        return this.indexOf(b, 0L);
    }
    
    @Override
    public long indexOf(final byte b, final long n) {
        long n2 = 0L;
        int n3;
        if (n >= 0L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        final Segment head = this.head;
        if (head == null) {
            return -1L;
        }
        int n4;
        if (this.size - n >= n) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        Segment next;
        long n6;
        if (n4 == 0) {
            long size = this.size;
            Segment prev = head;
            while (true) {
                int n5;
                if (size <= n) {
                    n5 = 1;
                }
                else {
                    n5 = 0;
                }
                n2 = size;
                next = prev;
                n6 = n;
                if (n5 != 0) {
                    break;
                }
                prev = prev.prev;
                size -= prev.limit - prev.pos;
            }
        }
        else {
            Segment next2 = head;
            while (true) {
                final long n7 = next2.limit - next2.pos + n2;
                int n8;
                if (n7 >= n) {
                    n8 = 1;
                }
                else {
                    n8 = 0;
                }
                next = next2;
                n6 = n;
                if (n8 != 0) {
                    break;
                }
                next2 = next2.next;
                n2 = n7;
            }
        }
        while (true) {
            int n9;
            if (n2 >= this.size) {
                n9 = 1;
            }
            else {
                n9 = 0;
            }
            if (n9 != 0) {
                return -1L;
            }
            final byte[] data = next.data;
            for (int i = (int)(next.pos + n6 - n2); i < next.limit; ++i) {
                if (data[i] == b) {
                    return n2 + (i - next.pos);
                }
            }
            n2 += next.limit - next.pos;
            next = next.next;
            n6 = n2;
        }
    }
    
    @Override
    public long indexOf(final ByteString byteString) throws IOException {
        return this.indexOf(byteString, 0L);
    }
    
    @Override
    public long indexOf(final ByteString byteString, long n) throws IOException {
        if (byteString.size() == 0) {
            throw new IllegalArgumentException("bytes is empty");
        }
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        final Segment head = this.head;
        if (head == null) {
            return -1L;
        }
        int n3;
        if (this.size - n >= n) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        long n5;
        Segment next;
        if (n3 == 0) {
            long size = this.size;
            Segment prev = head;
            while (true) {
                int n4;
                if (size <= n) {
                    n4 = 1;
                }
                else {
                    n4 = 0;
                }
                n5 = size;
                next = prev;
                if (n4 != 0) {
                    break;
                }
                prev = prev.prev;
                size -= prev.limit - prev.pos;
            }
        }
        else {
            n5 = 0L;
            Segment next2 = head;
            while (true) {
                final long n6 = next2.limit - next2.pos + n5;
                int n7;
                if (n6 >= n) {
                    n7 = 1;
                }
                else {
                    n7 = 0;
                }
                next = next2;
                if (n7 != 0) {
                    break;
                }
                next2 = next2.next;
                n5 = n6;
            }
        }
        final byte byte1 = byteString.getByte(0);
        final int size2 = byteString.size();
        final long n8 = this.size - size2 + 1L;
        while (true) {
            int n9;
            if (n5 >= n8) {
                n9 = 1;
            }
            else {
                n9 = 0;
            }
            if (n9 != 0) {
                return -1L;
            }
            final byte[] data = next.data;
            for (int n10 = (int)Math.min(next.limit, next.pos + n8 - n5), i = (int)(next.pos + n - n5); i < n10; ++i) {
                if (data[i] == byte1 && this.rangeEquals(next, i + 1, byteString, 1, size2)) {
                    return i - next.pos + n5;
                }
            }
            n = next.limit - next.pos + n5;
            next = next.next;
            n5 = n;
        }
    }
    
    @Override
    public long indexOfElement(final ByteString byteString) {
        return this.indexOfElement(byteString, 0L);
    }
    
    @Override
    public long indexOfElement(final ByteString byteString, long n) {
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        Segment segment = this.head;
        if (segment != null) {
            int n3;
            if (this.size - n >= n) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            long n5;
            Segment segment2;
            if (n3 == 0) {
                long size = this.size;
                while (true) {
                    int n4;
                    if (size <= n) {
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    n5 = size;
                    segment2 = segment;
                    if (n4 != 0) {
                        break;
                    }
                    segment = segment.prev;
                    size -= segment.limit - segment.pos;
                }
            }
            else {
                n5 = 0L;
                while (true) {
                    final long n6 = segment.limit - segment.pos + n5;
                    int n7;
                    if (n6 >= n) {
                        n7 = 1;
                    }
                    else {
                        n7 = 0;
                    }
                    segment2 = segment;
                    if (n7 != 0) {
                        break;
                    }
                    segment = segment.next;
                    n5 = n6;
                }
            }
            if (byteString.size() != 2) {
                final byte[] internalArray = byteString.internalArray();
                while (true) {
                    int n8;
                    if (n5 >= this.size) {
                        n8 = 1;
                    }
                    else {
                        n8 = 0;
                    }
                    if (n8 != 0) {
                        break;
                    }
                    final byte[] data = segment2.data;
                    for (int i = (int)(segment2.pos + n - n5); i < segment2.limit; ++i) {
                        final byte b = data[i];
                        for (int length = internalArray.length, j = 0; j < length; ++j) {
                            if (b == internalArray[j]) {
                                return n5 + (i - segment2.pos);
                            }
                        }
                    }
                    n5 += segment2.limit - segment2.pos;
                    segment2 = segment2.next;
                    n = n5;
                }
            }
            else {
                final byte byte1 = byteString.getByte(0);
                final byte byte2 = byteString.getByte(1);
                while (true) {
                    int n9;
                    if (n5 >= this.size) {
                        n9 = 1;
                    }
                    else {
                        n9 = 0;
                    }
                    if (n9 != 0) {
                        break;
                    }
                    final byte[] data2 = segment2.data;
                    for (int k = (int)(segment2.pos + n - n5); k < segment2.limit; ++k) {
                        final byte b2 = data2[k];
                        if (b2 == byte1 || b2 == byte2) {
                            return n5 + (k - segment2.pos);
                        }
                    }
                    n5 += segment2.limit - segment2.pos;
                    segment2 = segment2.next;
                    n = n5;
                }
            }
            return -1L;
        }
        return -1L;
    }
    
    @Override
    public InputStream inputStream() {
        return new InputStream() {
            @Override
            public int available() {
                return (int)Math.min(Buffer.this.size, 2147483647L);
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public int read() {
                int n;
                if (Buffer.this.size <= 0L) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n == 0) {
                    return Buffer.this.readByte() & 0xFF;
                }
                return -1;
            }
            
            @Override
            public int read(final byte[] array, final int n, final int n2) {
                return Buffer.this.read(array, n, n2);
            }
            
            @Override
            public String toString() {
                return Buffer.this + ".inputStream()";
            }
        };
    }
    
    public ByteString md5() {
        return this.digest("MD5");
    }
    
    @Override
    public OutputStream outputStream() {
        return new OutputStream() {
            @Override
            public void close() {
            }
            
            @Override
            public void flush() {
            }
            
            @Override
            public String toString() {
                return Buffer.this + ".outputStream()";
            }
            
            @Override
            public void write(final int n) {
                Buffer.this.writeByte((int)(byte)n);
            }
            
            @Override
            public void write(final byte[] array, final int n, final int n2) {
                Buffer.this.write(array, n, n2);
            }
        };
    }
    
    @Override
    public boolean rangeEquals(final long n, final ByteString byteString) {
        return this.rangeEquals(n, byteString, 0, byteString.size());
    }
    
    @Override
    public boolean rangeEquals(final long n, final ByteString byteString, final int n2, final int n3) {
        boolean b;
        if (n < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        if (!b && n2 >= 0 && n3 >= 0) {
            boolean b2;
            if (this.size - n < n3) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            if (!b2 && byteString.size() - n2 >= n3) {
                for (int i = 0; i < n3; ++i) {
                    if (this.getByte(i + n) != byteString.getByte(n2 + i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int read(final byte[] array) {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, final int n, int min) {
        Util.checkOffsetAndCount(array.length, n, min);
        final Segment head = this.head;
        if (head != null) {
            min = Math.min(min, head.limit - head.pos);
            System.arraycopy(head.data, head.pos, array, n, min);
            head.pos += min;
            this.size -= min;
            if (head.pos == head.limit) {
                this.head = head.pop();
                SegmentPool.recycle(head);
            }
            return min;
        }
        return -1;
    }
    
    @Override
    public long read(final Buffer buffer, long size) {
        final int n = 1;
        if (buffer == null) {
            throw new IllegalArgumentException("sink == null");
        }
        int n2;
        if (size >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("byteCount < 0: " + size);
        }
        if (this.size == 0L) {
            return -1L;
        }
        int n3;
        if (size <= this.size) {
            n3 = n;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            size = this.size;
        }
        buffer.write(this, size);
        return size;
    }
    
    @Override
    public long readAll(final Sink sink) throws IOException {
        final long size = this.size;
        int n;
        if (size <= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            sink.write(this, size);
        }
        return size;
    }
    
    @Override
    public byte readByte() {
        if (this.size == 0L) {
            throw new IllegalStateException("size == 0");
        }
        final Segment head = this.head;
        final int pos = head.pos;
        final int limit = head.limit;
        final byte[] data = head.data;
        final int pos2 = pos + 1;
        final byte b = data[pos];
        --this.size;
        if (pos2 != limit) {
            head.pos = pos2;
        }
        else {
            this.head = head.pop();
            SegmentPool.recycle(head);
        }
        return b;
    }
    
    @Override
    public byte[] readByteArray() {
        try {
            return this.readByteArray(this.size);
        }
        catch (final EOFException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    @Override
    public byte[] readByteArray(final long lng) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0L, lng);
        int n;
        if (lng <= 2147483647L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + lng);
        }
        final byte[] array = new byte[(int)lng];
        this.readFully(array);
        return array;
    }
    
    @Override
    public ByteString readByteString() {
        return new ByteString(this.readByteArray());
    }
    
    @Override
    public ByteString readByteString(final long n) throws EOFException {
        return new ByteString(this.readByteArray(n));
    }
    
    @Override
    public long readDecimalLong() {
        if (this.size == 0L) {
            throw new IllegalStateException("size == 0");
        }
        long n = 0L;
        int n2 = 0;
        boolean b = false;
        boolean b2 = false;
        long n3 = -7L;
        byte j = 0;
    Label_0200:
        while (true) {
            final Segment head = this.head;
            final byte[] data = head.data;
            int i;
            int limit;
            for (i = head.pos, limit = head.limit; i < limit; ++i) {
                j = data[i];
                if (j >= 48 && j <= 57) {
                    final int n4 = 48 - j;
                    int n5;
                    if (n < -922337203685477580L) {
                        n5 = 1;
                    }
                    else {
                        n5 = 0;
                    }
                    if (n5 != 0) {
                        break Label_0200;
                    }
                    if (n == -922337203685477580L) {
                        int n6;
                        if (n4 >= n3) {
                            n6 = 1;
                        }
                        else {
                            n6 = 0;
                        }
                        if (n6 == 0) {
                            break Label_0200;
                        }
                    }
                    n = n * 10L + n4;
                }
                else if (j == 45 && n2 == 0) {
                    b = true;
                    --n3;
                }
                else {
                    if (n2 != 0) {
                        b2 = true;
                        break;
                    }
                    throw new NumberFormatException("Expected leading [0-9] or '-' character but was 0x" + Integer.toHexString(j));
                }
                ++n2;
            }
            if (i != limit) {
                head.pos = i;
            }
            else {
                this.head = head.pop();
                SegmentPool.recycle(head);
            }
            if (!b2 && this.head != null) {
                continue;
            }
            this.size -= n2;
            long n7 = n;
            if (!b) {
                n7 = -n;
            }
            return n7;
        }
        final Buffer writeByte = new Buffer().writeDecimalLong(n).writeByte((int)j);
        if (!b) {
            writeByte.readByte();
        }
        throw new NumberFormatException("Number too large: " + writeByte.readUtf8());
    }
    
    public Buffer readFrom(final InputStream inputStream) throws IOException {
        this.readFrom(inputStream, Long.MAX_VALUE, true);
        return this;
    }
    
    public Buffer readFrom(final InputStream inputStream, final long lng) throws IOException {
        int n;
        if (lng >= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException("byteCount < 0: " + lng);
        }
        this.readFrom(inputStream, lng, false);
        return this;
    }
    
    @Override
    public void readFully(final Buffer buffer, final long n) throws EOFException {
        int n2;
        if (this.size >= n) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            buffer.write(this, this.size);
            throw new EOFException();
        }
        buffer.write(this, n);
    }
    
    @Override
    public void readFully(final byte[] array) throws EOFException {
        int read;
        for (int i = 0; i < array.length; i += read) {
            read = this.read(array, i, array.length - i);
            if (read == -1) {
                throw new EOFException();
            }
        }
    }
    
    @Override
    public long readHexadecimalUnsignedLong() {
        if (this.size == 0L) {
            throw new IllegalStateException("size == 0");
        }
        long n = 0L;
        int n2 = 0;
        boolean b = false;
        while (true) {
            final Segment head = this.head;
            final byte[] data = head.data;
            int i;
            int limit;
            long n4;
            for (i = head.pos, limit = head.limit; i < limit; ++i, n = (n4 | n << 4)) {
                final byte j = data[i];
                int n3;
                if (j >= 48 && j <= 57) {
                    n3 = j - 48;
                }
                else if (j >= 97 && j <= 102) {
                    n3 = j - 97 + 10;
                }
                else if (j >= 65 && j <= 70) {
                    n3 = j - 65 + 10;
                }
                else {
                    if (n2 != 0) {
                        b = true;
                        break;
                    }
                    throw new NumberFormatException("Expected leading [0-9a-fA-F] character but was 0x" + Integer.toHexString(j));
                }
                if ((0xF000000000000000L & n) != 0x0L) {
                    throw new NumberFormatException("Number too large: " + new Buffer().writeHexadecimalUnsignedLong(n).writeByte((int)j).readUtf8());
                }
                n4 = n3;
                ++n2;
            }
            if (i != limit) {
                head.pos = i;
            }
            else {
                this.head = head.pop();
                SegmentPool.recycle(head);
            }
            if (!b && this.head != null) {
                continue;
            }
            this.size -= n2;
            return n;
        }
    }
    
    @Override
    public int readInt() {
        int n;
        if (this.size >= 4L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalStateException("size < 4: " + this.size);
        }
        final Segment head = this.head;
        final int pos = head.pos;
        final int limit = head.limit;
        if (limit - pos >= 4) {
            final byte[] data = head.data;
            final int n2 = pos + 1;
            final byte b = data[pos];
            final int n3 = n2 + 1;
            final byte b2 = data[n2];
            final int n4 = n3 + 1;
            final byte b3 = data[n3];
            final int pos2 = n4 + 1;
            final byte b4 = data[n4];
            this.size -= 4L;
            if (pos2 != limit) {
                head.pos = pos2;
            }
            else {
                this.head = head.pop();
                SegmentPool.recycle(head);
            }
            return (b & 0xFF) << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
        }
        return (this.readByte() & 0xFF) << 24 | (this.readByte() & 0xFF) << 16 | (this.readByte() & 0xFF) << 8 | (this.readByte() & 0xFF);
    }
    
    @Override
    public int readIntLe() {
        return Util.reverseBytesInt(this.readInt());
    }
    
    @Override
    public long readLong() {
        int n;
        if (this.size >= 8L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalStateException("size < 8: " + this.size);
        }
        final Segment head = this.head;
        final int pos = head.pos;
        final int limit = head.limit;
        if (limit - pos >= 8) {
            final byte[] data = head.data;
            final int n2 = pos + 1;
            final long n3 = data[pos];
            final int n4 = n2 + 1;
            final long n5 = data[n2];
            final int n6 = n4 + 1;
            final long n7 = data[n4];
            final int n8 = n6 + 1;
            final long n9 = data[n6];
            final int n10 = n8 + 1;
            final long n11 = data[n8];
            final int n12 = n10 + 1;
            final long n13 = data[n10];
            final int n14 = n12 + 1;
            final long n15 = data[n12];
            final int pos2 = n14 + 1;
            final long n16 = data[n14];
            this.size -= 8L;
            if (pos2 != limit) {
                head.pos = pos2;
            }
            else {
                this.head = head.pop();
                SegmentPool.recycle(head);
            }
            return (n5 & 0xFFL) << 48 | (n3 & 0xFFL) << 56 | (n7 & 0xFFL) << 40 | (n9 & 0xFFL) << 32 | (n11 & 0xFFL) << 24 | (n13 & 0xFFL) << 16 | (n15 & 0xFFL) << 8 | (n16 & 0xFFL);
        }
        return ((long)this.readInt() & 0xFFFFFFFFL) << 32 | ((long)this.readInt() & 0xFFFFFFFFL);
    }
    
    @Override
    public long readLongLe() {
        return Util.reverseBytesLong(this.readLong());
    }
    
    @Override
    public short readShort() {
        int n;
        if (this.size >= 2L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalStateException("size < 2: " + this.size);
        }
        final Segment head = this.head;
        final int pos = head.pos;
        final int limit = head.limit;
        if (limit - pos >= 2) {
            final byte[] data = head.data;
            final int n2 = pos + 1;
            final byte b = data[pos];
            final int pos2 = n2 + 1;
            final byte b2 = data[n2];
            this.size -= 2L;
            if (pos2 != limit) {
                head.pos = pos2;
            }
            else {
                this.head = head.pop();
                SegmentPool.recycle(head);
            }
            return (short)((b & 0xFF) << 8 | (b2 & 0xFF));
        }
        return (short)((this.readByte() & 0xFF) << 8 | (this.readByte() & 0xFF));
    }
    
    @Override
    public short readShortLe() {
        return Util.reverseBytesShort(this.readShort());
    }
    
    @Override
    public String readString(final long lng, final Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0L, lng);
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        }
        int n;
        if (lng <= 2147483647L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + lng);
        }
        if (lng == 0L) {
            return "";
        }
        final Segment head = this.head;
        int n2;
        if (head.pos + lng <= head.limit) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            return new String(this.readByteArray(lng), charset);
        }
        final String s = new String(head.data, head.pos, (int)lng, charset);
        head.pos += (int)lng;
        this.size -= lng;
        if (head.pos == head.limit) {
            this.head = head.pop();
            SegmentPool.recycle(head);
        }
        return s;
    }
    
    @Override
    public String readString(final Charset charset) {
        try {
            return this.readString(this.size, charset);
        }
        catch (final EOFException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    @Override
    public String readUtf8() {
        try {
            return this.readString(this.size, Util.UTF_8);
        }
        catch (final EOFException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    @Override
    public String readUtf8(final long n) throws EOFException {
        return this.readString(n, Util.UTF_8);
    }
    
    @Override
    public int readUtf8CodePoint() throws EOFException {
        int i = 1;
        boolean b = false;
        if (this.size == 0L) {
            throw new EOFException();
        }
        final byte byte1 = this.getByte(0L);
        int n;
        int j;
        int n2;
        if ((byte1 & 0x80) != 0x0) {
            if ((byte1 & 0xE0) != 0xC0) {
                if ((byte1 & 0xF0) != 0xE0) {
                    if ((byte1 & 0xF8) != 0xF0) {
                        this.skip(1L);
                        return 65533;
                    }
                    n = (byte1 & 0x7);
                    j = 4;
                    n2 = 65536;
                }
                else {
                    n = (byte1 & 0xF);
                    j = 3;
                    n2 = 2048;
                }
            }
            else {
                n = (byte1 & 0x1F);
                j = 2;
                n2 = 128;
            }
        }
        else {
            j = 1;
            n = (byte1 & 0x7F);
            n2 = 0;
        }
        if (this.size >= j) {
            b = true;
        }
        if (!b) {
            throw new EOFException("size < " + j + ": " + this.size + " (to read code point prefixed 0x" + Integer.toHexString(byte1) + ")");
        }
        while (i < j) {
            final byte byte2 = this.getByte(i);
            if ((byte2 & 0xC0) != 0x80) {
                this.skip(i);
                return 65533;
            }
            n = (n << 6 | (byte2 & 0x3F));
            ++i;
        }
        this.skip(j);
        if (n > 1114111) {
            return 65533;
        }
        if (n >= 55296 && n <= 57343) {
            return 65533;
        }
        if (n >= n2) {
            return n;
        }
        return 65533;
    }
    
    @Override
    public String readUtf8Line() throws EOFException {
        final long index = this.indexOf((byte)10);
        if (index == -1L) {
            String utf8;
            if (this.size != 0L) {
                utf8 = this.readUtf8(this.size);
            }
            else {
                utf8 = null;
            }
            return utf8;
        }
        return this.readUtf8Line(index);
    }
    
    String readUtf8Line(final long n) throws EOFException {
        boolean b;
        if (n <= 0L) {
            b = true;
        }
        else {
            b = false;
        }
        if (b || this.getByte(n - 1L) != 13) {
            final String utf8 = this.readUtf8(n);
            this.skip(1L);
            return utf8;
        }
        final String utf9 = this.readUtf8(n - 1L);
        this.skip(2L);
        return utf9;
    }
    
    @Override
    public String readUtf8LineStrict() throws EOFException {
        final long index = this.indexOf((byte)10);
        if (index == -1L) {
            final Buffer buffer = new Buffer();
            this.copyTo(buffer, 0L, Math.min(32L, this.size));
            throw new EOFException("\\n not found: size=" + this.size() + " content=" + buffer.readByteString().hex() + "\u2026");
        }
        return this.readUtf8Line(index);
    }
    
    @Override
    public boolean request(final long n) {
        boolean b = true;
        int n2;
        if (this.size < n) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 != 0) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void require(final long n) throws EOFException {
        int n2;
        if (this.size >= n) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new EOFException();
        }
    }
    
    List<Integer> segmentSizes() {
        if (this.head != null) {
            final ArrayList list = new ArrayList();
            list.add(this.head.limit - this.head.pos);
            for (Segment segment = this.head.next; segment != this.head; segment = segment.next) {
                list.add(segment.limit - segment.pos);
            }
            return list;
        }
        return Collections.emptyList();
    }
    
    @Override
    public int select(final Options options) {
        final Segment head = this.head;
        if (head != null) {
            final ByteString[] byteStrings = options.byteStrings;
            final int length = byteStrings.length;
            int i = 0;
            while (i < length) {
                final ByteString byteString = byteStrings[i];
                boolean b;
                if (this.size < byteString.size()) {
                    b = true;
                }
                else {
                    b = false;
                }
                if (b || !this.rangeEquals(head, head.pos, byteString, 0, byteString.size())) {
                    ++i;
                }
                else {
                    try {
                        this.skip(byteString.size());
                        return i;
                    }
                    catch (final EOFException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
            }
            return -1;
        }
        return options.indexOf(ByteString.EMPTY);
    }
    
    int selectPrefix(final Options options) {
        final Segment head = this.head;
        final ByteString[] byteStrings = options.byteStrings;
        for (int length = byteStrings.length, i = 0; i < length; ++i) {
            final ByteString byteString = byteStrings[i];
            final int n = (int)Math.min(this.size, byteString.size());
            if (n == 0 || this.rangeEquals(head, head.pos, byteString, 0, n)) {
                return i;
            }
        }
        return -1;
    }
    
    public ByteString sha1() {
        return this.digest("SHA-1");
    }
    
    public ByteString sha256() {
        return this.digest("SHA-256");
    }
    
    public long size() {
        return this.size;
    }
    
    @Override
    public void skip(long a) throws EOFException {
        while (true) {
            int n;
            if (a <= 0L) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n != 0) {
                return;
            }
            if (this.head == null) {
                throw new EOFException();
            }
            final int n2 = (int)Math.min(a, this.head.limit - this.head.pos);
            this.size -= n2;
            final long n3 = a - n2;
            final Segment head = this.head;
            head.pos += n2;
            a = n3;
            if (this.head.pos != this.head.limit) {
                continue;
            }
            final Segment head2 = this.head;
            this.head = head2.pop();
            SegmentPool.recycle(head2);
            a = n3;
        }
    }
    
    public ByteString snapshot() {
        int n;
        if (this.size <= 2147483647L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.size);
        }
        return this.snapshot((int)this.size);
    }
    
    public ByteString snapshot(final int n) {
        if (n != 0) {
            return new SegmentedByteString(this, n);
        }
        return ByteString.EMPTY;
    }
    
    @Override
    public Timeout timeout() {
        return Timeout.NONE;
    }
    
    @Override
    public String toString() {
        return this.snapshot().toString();
    }
    
    Segment writableSegment(final int n) {
        if (n < 1 || n > 8192) {
            throw new IllegalArgumentException();
        }
        if (this.head != null) {
            Segment segment = this.head.prev;
            if (segment.limit + n > 8192 || !segment.owner) {
                segment = segment.push(SegmentPool.take());
            }
            return segment;
        }
        this.head = SegmentPool.take();
        final Segment head = this.head;
        final Segment head2 = this.head;
        final Segment head3 = this.head;
        head2.prev = head3;
        return head.next = head3;
    }
    
    @Override
    public Buffer write(final ByteString byteString) {
        if (byteString != null) {
            byteString.write(this);
            return this;
        }
        throw new IllegalArgumentException("byteString == null");
    }
    
    @Override
    public Buffer write(final byte[] array) {
        if (array != null) {
            return this.write(array, 0, array.length);
        }
        throw new IllegalArgumentException("source == null");
    }
    
    @Override
    public Buffer write(final byte[] array, int i, final int n) {
        if (array != null) {
            Util.checkOffsetAndCount(array.length, i, n);
            Segment writableSegment;
            int min;
            for (int n2 = i + n; i < n2; i += min, writableSegment.limit += min) {
                writableSegment = this.writableSegment(1);
                min = Math.min(n2 - i, 8192 - writableSegment.limit);
                System.arraycopy(array, i, writableSegment.data, writableSegment.limit, min);
            }
            this.size += n;
            return this;
        }
        throw new IllegalArgumentException("source == null");
    }
    
    @Override
    public BufferedSink write(final Source source, long n) throws IOException {
        while (true) {
            int n2;
            if (n <= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                return this;
            }
            final long read = source.read(this, n);
            if (read == -1L) {
                throw new EOFException();
            }
            n -= read;
        }
    }
    
    @Override
    public void write(final Buffer buffer, long n) {
        if (buffer == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (buffer == this) {
            throw new IllegalArgumentException("source == this");
        }
        Util.checkOffsetAndCount(buffer.size, 0L, n);
        while (true) {
            int n2;
            if (n <= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                return;
            }
            int n3;
            if (n >= buffer.head.limit - buffer.head.pos) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                Segment prev;
                if (this.head == null) {
                    prev = null;
                }
                else {
                    prev = this.head.prev;
                }
                if (prev != null && prev.owner) {
                    final long n4 = prev.limit;
                    int pos;
                    if (!prev.shared) {
                        pos = prev.pos;
                    }
                    else {
                        pos = 0;
                    }
                    int n5;
                    if (n4 + n - pos > 8192L) {
                        n5 = 1;
                    }
                    else {
                        n5 = 0;
                    }
                    if (n5 == 0) {
                        buffer.head.writeTo(prev, (int)n);
                        buffer.size -= n;
                        this.size += n;
                        return;
                    }
                }
                buffer.head = buffer.head.split((int)n);
            }
            final Segment head = buffer.head;
            final long n6 = head.limit - head.pos;
            buffer.head = head.pop();
            if (this.head != null) {
                this.head.prev.push(head).compact();
            }
            else {
                this.head = head;
                final Segment head2 = this.head;
                final Segment head3 = this.head;
                final Segment head4 = this.head;
                head3.prev = head4;
                head2.next = head4;
            }
            buffer.size -= n6;
            this.size += n6;
            n -= n6;
        }
    }
    
    @Override
    public long writeAll(final Source source) throws IOException {
        if (source != null) {
            long n = 0L;
            while (true) {
                final long read = source.read(this, 8192L);
                if (read == -1L) {
                    break;
                }
                n += read;
            }
            return n;
        }
        throw new IllegalArgumentException("source == null");
    }
    
    @Override
    public Buffer writeByte(final int n) {
        final Segment writableSegment = this.writableSegment(1);
        writableSegment.data[writableSegment.limit++] = (byte)n;
        ++this.size;
        return this;
    }
    
    @Override
    public Buffer writeDecimalLong(long size) {
        if (size == 0L) {
            return this.writeByte(48);
        }
        int n;
        if (size >= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        boolean b;
        if (n == 0) {
            size = -size;
            int n2;
            if (size >= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                return this.writeUtf8("-9223372036854775808");
            }
            b = true;
        }
        else {
            b = false;
        }
        int n3;
        if (size >= 100000000L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        int n7;
        if (n3 == 0) {
            int n4;
            if (size >= 10000L) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            if (n4 == 0) {
                int n5;
                if (size >= 100L) {
                    n5 = 1;
                }
                else {
                    n5 = 0;
                }
                if (n5 == 0) {
                    int n6;
                    if (size >= 10L) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    if (n6 == 0) {
                        n7 = 1;
                    }
                    else {
                        n7 = 2;
                    }
                }
                else {
                    int n8;
                    if (size >= 1000L) {
                        n8 = 1;
                    }
                    else {
                        n8 = 0;
                    }
                    if (n8 == 0) {
                        n7 = 3;
                    }
                    else {
                        n7 = 4;
                    }
                }
            }
            else {
                int n9;
                if (size >= 1000000L) {
                    n9 = 1;
                }
                else {
                    n9 = 0;
                }
                if (n9 == 0) {
                    int n10;
                    if (size >= 100000L) {
                        n10 = 1;
                    }
                    else {
                        n10 = 0;
                    }
                    if (n10 == 0) {
                        n7 = 5;
                    }
                    else {
                        n7 = 6;
                    }
                }
                else {
                    int n11;
                    if (size >= 10000000L) {
                        n11 = 1;
                    }
                    else {
                        n11 = 0;
                    }
                    if (n11 == 0) {
                        n7 = 7;
                    }
                    else {
                        n7 = 8;
                    }
                }
            }
        }
        else {
            int n12;
            if (size >= 1000000000000L) {
                n12 = 1;
            }
            else {
                n12 = 0;
            }
            if (n12 == 0) {
                int n13;
                if (size >= 10000000000L) {
                    n13 = 1;
                }
                else {
                    n13 = 0;
                }
                if (n13 == 0) {
                    int n14;
                    if (size >= 1000000000L) {
                        n14 = 1;
                    }
                    else {
                        n14 = 0;
                    }
                    if (n14 == 0) {
                        n7 = 9;
                    }
                    else {
                        n7 = 10;
                    }
                }
                else {
                    int n15;
                    if (size >= 100000000000L) {
                        n15 = 1;
                    }
                    else {
                        n15 = 0;
                    }
                    if (n15 == 0) {
                        n7 = 11;
                    }
                    else {
                        n7 = 12;
                    }
                }
            }
            else {
                int n16;
                if (size >= 1000000000000000L) {
                    n16 = 1;
                }
                else {
                    n16 = 0;
                }
                if (n16 == 0) {
                    int n17;
                    if (size >= 10000000000000L) {
                        n17 = 1;
                    }
                    else {
                        n17 = 0;
                    }
                    if (n17 == 0) {
                        n7 = 13;
                    }
                    else {
                        int n18;
                        if (size >= 100000000000000L) {
                            n18 = 1;
                        }
                        else {
                            n18 = 0;
                        }
                        if (n18 == 0) {
                            n7 = 14;
                        }
                        else {
                            n7 = 15;
                        }
                    }
                }
                else {
                    int n19;
                    if (size >= 100000000000000000L) {
                        n19 = 1;
                    }
                    else {
                        n19 = 0;
                    }
                    if (n19 == 0) {
                        int n20;
                        if (size >= 10000000000000000L) {
                            n20 = 1;
                        }
                        else {
                            n20 = 0;
                        }
                        if (n20 == 0) {
                            n7 = 16;
                        }
                        else {
                            n7 = 17;
                        }
                    }
                    else {
                        int n21;
                        if (size >= 1000000000000000000L) {
                            n21 = 1;
                        }
                        else {
                            n21 = 0;
                        }
                        if (n21 == 0) {
                            n7 = 18;
                        }
                        else {
                            n7 = 19;
                        }
                    }
                }
            }
        }
        if (b) {
            ++n7;
        }
        final Segment writableSegment = this.writableSegment(n7);
        final byte[] data = writableSegment.data;
        int n22 = writableSegment.limit + n7;
        while (size != 0L) {
            final int n23 = (int)(size % 10L);
            --n22;
            data[n22] = Buffer.DIGITS[n23];
            size /= 10L;
        }
        if (b) {
            data[n22 - 1] = 45;
        }
        writableSegment.limit += n7;
        size = this.size;
        this.size = n7 + size;
        return this;
    }
    
    @Override
    public Buffer writeHexadecimalUnsignedLong(long size) {
        if (size == 0L) {
            return this.writeByte(48);
        }
        final int n = Long.numberOfTrailingZeros(Long.highestOneBit(size)) / 4 + 1;
        final Segment writableSegment = this.writableSegment(n);
        final byte[] data = writableSegment.data;
        for (int i = writableSegment.limit + n - 1; i >= writableSegment.limit; --i) {
            data[i] = Buffer.DIGITS[(int)(0xFL & size)];
            size >>>= 4;
        }
        writableSegment.limit += n;
        size = this.size;
        this.size = n + size;
        return this;
    }
    
    @Override
    public Buffer writeInt(final int n) {
        final Segment writableSegment = this.writableSegment(4);
        final byte[] data = writableSegment.data;
        final int limit = writableSegment.limit;
        final int n2 = limit + 1;
        data[limit] = (byte)(n >>> 24 & 0xFF);
        final int n3 = n2 + 1;
        data[n2] = (byte)(n >>> 16 & 0xFF);
        final int n4 = n3 + 1;
        data[n3] = (byte)(n >>> 8 & 0xFF);
        data[n4] = (byte)(n & 0xFF);
        writableSegment.limit = n4 + 1;
        this.size += 4L;
        return this;
    }
    
    @Override
    public Buffer writeIntLe(final int n) {
        return this.writeInt(Util.reverseBytesInt(n));
    }
    
    @Override
    public Buffer writeLong(final long n) {
        final Segment writableSegment = this.writableSegment(8);
        final byte[] data = writableSegment.data;
        final int limit = writableSegment.limit;
        final int n2 = limit + 1;
        data[limit] = (byte)(n >>> 56 & 0xFFL);
        final int n3 = n2 + 1;
        data[n2] = (byte)(n >>> 48 & 0xFFL);
        final int n4 = n3 + 1;
        data[n3] = (byte)(n >>> 40 & 0xFFL);
        final int n5 = n4 + 1;
        data[n4] = (byte)(n >>> 32 & 0xFFL);
        final int n6 = n5 + 1;
        data[n5] = (byte)(n >>> 24 & 0xFFL);
        final int n7 = n6 + 1;
        data[n6] = (byte)(n >>> 16 & 0xFFL);
        final int n8 = n7 + 1;
        data[n7] = (byte)(n >>> 8 & 0xFFL);
        data[n8] = (byte)(n & 0xFFL);
        writableSegment.limit = n8 + 1;
        this.size += 8L;
        return this;
    }
    
    @Override
    public Buffer writeLongLe(final long n) {
        return this.writeLong(Util.reverseBytesLong(n));
    }
    
    @Override
    public Buffer writeShort(final int n) {
        final Segment writableSegment = this.writableSegment(2);
        final byte[] data = writableSegment.data;
        final int limit = writableSegment.limit;
        final int n2 = limit + 1;
        data[limit] = (byte)(n >>> 8 & 0xFF);
        data[n2] = (byte)(n & 0xFF);
        writableSegment.limit = n2 + 1;
        this.size += 2L;
        return this;
    }
    
    @Override
    public Buffer writeShortLe(final int n) {
        return this.writeShort((int)Util.reverseBytesShort((short)n));
    }
    
    @Override
    public Buffer writeString(final String s, final int beginIndex, final int endIndex, final Charset charset) {
        if (s == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (beginIndex < 0) {
            throw new IllegalAccessError("beginIndex < 0: " + beginIndex);
        }
        if (endIndex < beginIndex) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
        }
        if (endIndex > s.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + s.length());
        }
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        }
        if (!charset.equals(Util.UTF_8)) {
            final byte[] bytes = s.substring(beginIndex, endIndex).getBytes(charset);
            return this.write(bytes, 0, bytes.length);
        }
        return this.writeUtf8(s, beginIndex, endIndex);
    }
    
    @Override
    public Buffer writeString(final String s, final Charset charset) {
        return this.writeString(s, 0, s.length(), charset);
    }
    
    public Buffer writeTo(final OutputStream outputStream) throws IOException {
        return this.writeTo(outputStream, this.size);
    }
    
    public Buffer writeTo(final OutputStream outputStream, long a) throws IOException {
        if (outputStream != null) {
            Util.checkOffsetAndCount(this.size, 0L, a);
            Segment head = this.head;
            while (true) {
                int n;
                if (a <= 0L) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    break;
                }
                final int len = (int)Math.min(a, head.limit - head.pos);
                outputStream.write(head.data, head.pos, len);
                head.pos += len;
                this.size -= len;
                final long n2 = a -= len;
                if (head.pos != head.limit) {
                    continue;
                }
                final Segment pop = head.pop();
                this.head = pop;
                SegmentPool.recycle(head);
                head = pop;
                a = n2;
            }
            return this;
        }
        throw new IllegalArgumentException("out == null");
    }
    
    @Override
    public Buffer writeUtf8(final String s) {
        return this.writeUtf8(s, 0, s.length());
    }
    
    @Override
    public Buffer writeUtf8(final String s, int i, final int j) {
        if (s == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (i < 0) {
            throw new IllegalAccessError("beginIndex < 0: " + i);
        }
        if (j < i) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + j + " < " + i);
        }
        if (j <= s.length()) {
            while (i < j) {
                final char char1 = s.charAt(i);
                if (char1 >= '\u0080') {
                    if (char1 >= '\u0800') {
                        if (char1 >= '\ud800' && char1 <= '\udfff') {
                            int char2;
                            if (i + 1 >= j) {
                                char2 = 0;
                            }
                            else {
                                char2 = s.charAt(i + 1);
                            }
                            if (char1 <= '\udbff' && char2 >= 56320 && char2 <= 57343) {
                                final int n = ((char2 & 0xFFFF23FF) | (char1 & 0xFFFF27FF) << 10) + 65536;
                                this.writeByte(n >> 18 | 0xF0);
                                this.writeByte((n >> 12 & 0x3F) | 0x80);
                                this.writeByte((n >> 6 & 0x3F) | 0x80);
                                this.writeByte((n & 0x3F) | 0x80);
                                i += 2;
                            }
                            else {
                                this.writeByte(63);
                                ++i;
                            }
                        }
                        else {
                            this.writeByte(char1 >> 12 | 0xE0);
                            this.writeByte((char1 >> 6 & 0x3F) | 0x80);
                            this.writeByte((char1 & '?') | 0x80);
                            ++i;
                        }
                    }
                    else {
                        this.writeByte(char1 >> 6 | 0xC0);
                        this.writeByte((char1 & '?') | 0x80);
                        ++i;
                    }
                }
                else {
                    final Segment writableSegment = this.writableSegment(1);
                    final byte[] data = writableSegment.data;
                    final int n2 = writableSegment.limit - i;
                    final int min = Math.min(j, 8192 - n2);
                    final int n3 = i + 1;
                    data[n2 + i] = (byte)char1;
                    char char3;
                    for (i = n3; i < min; ++i) {
                        char3 = s.charAt(i);
                        if (char3 >= '\u0080') {
                            break;
                        }
                        data[i + n2] = (byte)char3;
                    }
                    final int n4 = i + n2 - writableSegment.limit;
                    writableSegment.limit += n4;
                    this.size += n4;
                }
            }
            return this;
        }
        throw new IllegalArgumentException("endIndex > string.length: " + j + " > " + s.length());
    }
    
    @Override
    public Buffer writeUtf8CodePoint(final int n) {
        if (n >= 128) {
            if (n >= 2048) {
                if (n >= 65536) {
                    if (n > 1114111) {
                        throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(n));
                    }
                    this.writeByte(n >> 18 | 0xF0);
                    this.writeByte((n >> 12 & 0x3F) | 0x80);
                    this.writeByte((n >> 6 & 0x3F) | 0x80);
                    this.writeByte((n & 0x3F) | 0x80);
                }
                else {
                    if (n >= 55296 && n <= 57343) {
                        throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(n));
                    }
                    this.writeByte(n >> 12 | 0xE0);
                    this.writeByte((n >> 6 & 0x3F) | 0x80);
                    this.writeByte((n & 0x3F) | 0x80);
                }
            }
            else {
                this.writeByte(n >> 6 | 0xC0);
                this.writeByte((n & 0x3F) | 0x80);
            }
        }
        else {
            this.writeByte(n);
        }
        return this;
    }
}
