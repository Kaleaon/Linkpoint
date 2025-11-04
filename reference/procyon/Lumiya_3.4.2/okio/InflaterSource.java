// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.util.zip.DataFormatException;
import java.io.EOFException;
import java.io.IOException;
import java.util.zip.Inflater;

public final class InflaterSource implements Source
{
    private int bufferBytesHeldByInflater;
    private boolean closed;
    private final Inflater inflater;
    private final BufferedSource source;
    
    InflaterSource(final BufferedSource source, final Inflater inflater) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (inflater != null) {
            this.source = source;
            this.inflater = inflater;
            return;
        }
        throw new IllegalArgumentException("inflater == null");
    }
    
    public InflaterSource(final Source source, final Inflater inflater) {
        this(Okio.buffer(source), inflater);
    }
    
    private void releaseInflatedBytes() throws IOException {
        if (this.bufferBytesHeldByInflater != 0) {
            final int n = this.bufferBytesHeldByInflater - this.inflater.getRemaining();
            this.bufferBytesHeldByInflater -= n;
            this.source.skip(n);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.inflater.end();
            this.closed = true;
            this.source.close();
        }
    }
    
    @Override
    public long read(final Buffer buffer, final long lng) throws IOException {
        int n = 1;
        if (lng < 0L) {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException("byteCount < 0: " + lng);
        }
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (lng == 0L) {
            return 0L;
        }
        Label_0091: {
            break Label_0091;
            try {
                Segment writableSegment = null;
                while (!this.inflater.needsDictionary()) {
                    final boolean refill;
                    if (refill) {
                        throw new EOFException("source exhausted prematurely");
                    }
                    refill = this.refill();
                    writableSegment = buffer.writableSegment(1);
                    final int inflate = this.inflater.inflate(writableSegment.data, writableSegment.limit, 8192 - writableSegment.limit);
                    if (inflate > 0) {
                        writableSegment.limit += inflate;
                        buffer.size += inflate;
                        return inflate;
                    }
                    if (this.inflater.finished()) {
                        break;
                    }
                }
                this.releaseInflatedBytes();
                if (writableSegment.pos == writableSegment.limit) {
                    buffer.head = writableSegment.pop();
                    SegmentPool.recycle(writableSegment);
                }
                return -1L;
            }
            catch (final DataFormatException cause) {
                throw new IOException(cause);
            }
        }
        throw new EOFException("source exhausted prematurely");
    }
    
    public boolean refill() throws IOException {
        if (!this.inflater.needsInput()) {
            return false;
        }
        this.releaseInflatedBytes();
        if (this.inflater.getRemaining() != 0) {
            throw new IllegalStateException("?");
        }
        if (!this.source.exhausted()) {
            final Segment head = this.source.buffer().head;
            this.bufferBytesHeldByInflater = head.limit - head.pos;
            this.inflater.setInput(head.data, head.pos, this.bufferBytesHeldByInflater);
            return false;
        }
        return true;
    }
    
    @Override
    public Timeout timeout() {
        return this.source.timeout();
    }
}
