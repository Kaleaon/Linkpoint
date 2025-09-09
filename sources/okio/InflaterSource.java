package okio;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public final class InflaterSource implements Source {
    private int bufferBytesHeldByInflater;
    private boolean closed;
    private final Inflater inflater;
    private final BufferedSource source;

    InflaterSource(BufferedSource bufferedSource, Inflater inflater2) {
        if (bufferedSource == null) {
            throw new IllegalArgumentException("source == null");
        } else if (inflater2 != null) {
            this.source = bufferedSource;
            this.inflater = inflater2;
        } else {
            throw new IllegalArgumentException("inflater == null");
        }
    }

    public InflaterSource(Source source2, Inflater inflater2) {
        this(Okio.buffer(source2), inflater2);
    }

    private void releaseInflatedBytes() throws IOException {
        if (this.bufferBytesHeldByInflater != 0) {
            int remaining = this.bufferBytesHeldByInflater - this.inflater.getRemaining();
            this.bufferBytesHeldByInflater -= remaining;
            this.source.skip((long) remaining);
        }
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.inflater.end();
            this.closed = true;
            this.source.close();
        }
    }

    public long read(Buffer buffer, long j) throws IOException {
        boolean refill;
        boolean z = true;
        if (j < 0) {
            z = false;
        }
        if (!z) {
            throw new IllegalArgumentException("byteCount < 0: " + j);
        } else if (this.closed) {
            throw new IllegalStateException("closed");
        } else if (j == 0) {
            return 0;
        } else {
            do {
                refill = refill();
                Segment writableSegment = buffer.writableSegment(1);
                int inflate = this.inflater.inflate(writableSegment.data, writableSegment.limit, 8192 - writableSegment.limit);
                if (inflate <= 0) {
                    if (!this.inflater.finished()) {
                        try {
                            if (!this.inflater.needsDictionary()) {
                            }
                        } catch (DataFormatException e) {
                            throw new IOException(e);
                        }
                    }
                    releaseInflatedBytes();
                    if (writableSegment.pos != writableSegment.limit) {
                        return -1;
                    }
                    buffer.head = writableSegment.pop();
                    SegmentPool.recycle(writableSegment);
                    return -1;
                }
                writableSegment.limit += inflate;
                buffer.size += (long) inflate;
                return (long) inflate;
            } while (!refill);
            throw new EOFException("source exhausted prematurely");
        }
    }

    public boolean refill() throws IOException {
        if (!this.inflater.needsInput()) {
            return false;
        }
        releaseInflatedBytes();
        if (this.inflater.getRemaining() != 0) {
            throw new IllegalStateException("?");
        } else if (this.source.exhausted()) {
            return true;
        } else {
            Segment segment = this.source.buffer().head;
            this.bufferBytesHeldByInflater = segment.limit - segment.pos;
            this.inflater.setInput(segment.data, segment.pos, this.bufferBytesHeldByInflater);
            return false;
        }
    }

    public Timeout timeout() {
        return this.source.timeout();
    }
}
