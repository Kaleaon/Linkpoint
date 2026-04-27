// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.CRC32;

public final class GzipSource implements Source
{
    private static final byte FCOMMENT = 4;
    private static final byte FEXTRA = 2;
    private static final byte FHCRC = 1;
    private static final byte FNAME = 3;
    private static final byte SECTION_BODY = 1;
    private static final byte SECTION_DONE = 3;
    private static final byte SECTION_HEADER = 0;
    private static final byte SECTION_TRAILER = 2;
    private final CRC32 crc;
    private final Inflater inflater;
    private final InflaterSource inflaterSource;
    private int section;
    private final BufferedSource source;
    
    public GzipSource(final Source source) {
        this.section = 0;
        this.crc = new CRC32();
        if (source != null) {
            this.inflater = new Inflater(true);
            this.source = Okio.buffer(source);
            this.inflaterSource = new InflaterSource(this.source, this.inflater);
            return;
        }
        throw new IllegalArgumentException("source == null");
    }
    
    private void checkEqual(final String s, final int i, final int j) throws IOException {
        if (j == i) {
            return;
        }
        throw new IOException(String.format("%s: actual 0x%08x != expected 0x%08x", s, j, i));
    }
    
    private void consumeHeader() throws IOException {
        this.source.require(10L);
        final byte byte1 = this.source.buffer().getByte(3L);
        boolean b;
        if ((byte1 >> 1 & 0x1) != 0x1) {
            b = false;
        }
        else {
            b = true;
        }
        if (b) {
            this.updateCrc(this.source.buffer(), 0L, 10L);
        }
        this.checkEqual("ID1ID2", 8075, this.source.readShort());
        this.source.skip(8L);
        if ((byte1 >> 2 & 0x1) == 0x1) {
            this.source.require(2L);
            if (b) {
                this.updateCrc(this.source.buffer(), 0L, 2L);
            }
            final short shortLe = this.source.buffer().readShortLe();
            this.source.require(shortLe);
            if (b) {
                this.updateCrc(this.source.buffer(), 0L, shortLe);
            }
            this.source.skip(shortLe);
        }
        if ((byte1 >> 3 & 0x1) == 0x1) {
            final long index = this.source.indexOf((byte)0);
            if (index == -1L) {
                throw new EOFException();
            }
            if (b) {
                this.updateCrc(this.source.buffer(), 0L, index + 1L);
            }
            this.source.skip(1L + index);
        }
        if ((byte1 >> 4 & 0x1) == 0x1) {
            final long index2 = this.source.indexOf((byte)0);
            if (index2 == -1L) {
                throw new EOFException();
            }
            if (b) {
                this.updateCrc(this.source.buffer(), 0L, index2 + 1L);
            }
            this.source.skip(1L + index2);
        }
        if (b) {
            this.checkEqual("FHCRC", this.source.readShortLe(), (short)this.crc.getValue());
            this.crc.reset();
        }
    }
    
    private void consumeTrailer() throws IOException {
        this.checkEqual("CRC", this.source.readIntLe(), (int)this.crc.getValue());
        this.checkEqual("ISIZE", this.source.readIntLe(), (int)this.inflater.getBytesWritten());
    }
    
    private void updateCrc(final Buffer buffer, long n, long b) {
        Segment segment = buffer.head;
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
            this.crc.update(segment.data, off, len);
            b -= len;
            segment = segment.next;
            n = 0L;
        }
    }
    
    @Override
    public void close() throws IOException {
        this.inflaterSource.close();
    }
    
    @Override
    public long read(final Buffer buffer, long read) throws IOException {
        boolean b = false;
        if (read >= 0L) {
            b = true;
        }
        if (!b) {
            throw new IllegalArgumentException("byteCount < 0: " + read);
        }
        if (read == 0L) {
            return 0L;
        }
        if (this.section == 0) {
            this.consumeHeader();
            this.section = 1;
        }
        if (this.section == 1) {
            final long size = buffer.size;
            read = this.inflaterSource.read(buffer, read);
            if (read != -1L) {
                this.updateCrc(buffer, size, read);
                return read;
            }
            this.section = 2;
        }
        if (this.section == 2) {
            this.consumeTrailer();
            this.section = 3;
            if (!this.source.exhausted()) {
                throw new IOException("gzip finished without exhausting source");
            }
        }
        return -1L;
    }
    
    @Override
    public Timeout timeout() {
        return this.source.timeout();
    }
}
