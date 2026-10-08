package okio;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
/* loaded from: classes.dex */
public final class InflaterSource implements Source {
    private int bufferBytesHeldByInflater;
    private boolean closed;
    private final Inflater inflater;
    private final BufferedSource source;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InflaterSource(BufferedSource bufferedSource, Inflater inflater) {
        if (bufferedSource == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (inflater == null) {
            throw new IllegalArgumentException("inflater == null");
        }
        this.source = bufferedSource;
        this.inflater = inflater;
    }

    public InflaterSource(Source source, Inflater inflater) {
        this(Okio.buffer(source), inflater);
    }

    private void releaseInflatedBytes() throws IOException {
        if (this.bufferBytesHeldByInflater != 0) {
            int remaining = this.bufferBytesHeldByInflater - this.inflater.getRemaining();
            this.bufferBytesHeldByInflater -= remaining;
            this.source.skip(remaining);
        }
    }

    @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.inflater.end();
        this.closed = true;
        this.source.close();
    }

    @Override // okio.Source
    public long read(Buffer buffer, long j) throws IOException {
        boolean refill;
        if (j >= 0) {
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            if (j == 0) {
                return 0L;
            }
            do {
                refill = refill();
                try {
                    Segment writableSegment = buffer.writableSegment(1);
                    int inflate = this.inflater.inflate(writableSegment.data, writableSegment.limit, 8192 - writableSegment.limit);
                    if (inflate > 0) {
                        writableSegment.limit += inflate;
                        buffer.size += inflate;
                        return inflate;
                    } else if (this.inflater.finished() || this.inflater.needsDictionary()) {
                        releaseInflatedBytes();
                        if (writableSegment.pos != writableSegment.limit) {
                            return -1L;
                        }
                        buffer.head = writableSegment.pop();
                        SegmentPool.recycle(writableSegment);
                        return -1L;
                    }
                } catch (DataFormatException e) {
                    throw new IOException(e);
                }
            } while (!refill);
            throw new EOFException("source exhausted prematurely");
        }
        throw new IllegalArgumentException("byteCount < 0: " + j);
    }

    public boolean refill() throws IOException {
        if (this.inflater.needsInput()) {
            releaseInflatedBytes();
            if (this.inflater.getRemaining() == 0) {
                if (this.source.exhausted()) {
                    return true;
                }
                Segment segment = this.source.buffer().head;
                this.bufferBytesHeldByInflater = segment.limit - segment.pos;
                this.inflater.setInput(segment.data, segment.pos, this.bufferBytesHeldByInflater);
                return false;
            }
            throw new IllegalStateException("?");
        }
        return false;
    }

    @Override // okio.Source
    public Timeout timeout() {
        return this.source.timeout();
    }
}
