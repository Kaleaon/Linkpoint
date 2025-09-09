package okio;

import java.io.IOException;
import java.util.zip.Deflater;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

public final class DeflaterSink implements Sink {
    private boolean closed;
    private final Deflater deflater;
    private final BufferedSink sink;

    DeflaterSink(BufferedSink bufferedSink, Deflater deflater2) {
        if (bufferedSink == null) {
            throw new IllegalArgumentException("source == null");
        } else if (deflater2 != null) {
            this.sink = bufferedSink;
            this.deflater = deflater2;
        } else {
            throw new IllegalArgumentException("inflater == null");
        }
    }

    public DeflaterSink(Sink sink2, Deflater deflater2) {
        this(Okio.buffer(sink2), deflater2);
    }

    @IgnoreJRERequirement
    private void deflate(boolean z) throws IOException {
        Segment writableSegment;
        Buffer buffer = this.sink.buffer();
        while (true) {
            writableSegment = buffer.writableSegment(1);
            int deflate = !z ? this.deflater.deflate(writableSegment.data, writableSegment.limit, 8192 - writableSegment.limit) : this.deflater.deflate(writableSegment.data, writableSegment.limit, 8192 - writableSegment.limit, 2);
            if (deflate > 0) {
                writableSegment.limit += deflate;
                buffer.size += (long) deflate;
                this.sink.emitCompleteSegments();
            } else if (this.deflater.needsInput()) {
                break;
            }
        }
        if (writableSegment.pos == writableSegment.limit) {
            buffer.head = writableSegment.pop();
            SegmentPool.recycle(writableSegment);
        }
    }

    public void close() throws IOException {
        Throwable th = null;
        if (!this.closed) {
            try {
                finishDeflate();
            } catch (Throwable th2) {
                th = th2;
            }
            try {
                this.deflater.end();
            } catch (Throwable th3) {
                if (th == null) {
                    th = th3;
                }
            }
            try {
                this.sink.close();
                th = th;
            } catch (Throwable th4) {
                th = th4;
                if (th != null) {
                    th = th;
                }
            }
            this.closed = true;
            if (th != null) {
                Util.sneakyRethrow(th);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void finishDeflate() throws IOException {
        this.deflater.finish();
        deflate(false);
    }

    public void flush() throws IOException {
        deflate(true);
        this.sink.flush();
    }

    public Timeout timeout() {
        return this.sink.timeout();
    }

    public String toString() {
        return "DeflaterSink(" + this.sink + ")";
    }

    public void write(Buffer buffer, long j) throws IOException {
        Util.checkOffsetAndCount(buffer.size, 0, j);
        while (true) {
            if (!(j <= 0)) {
                Segment segment = buffer.head;
                int min = (int) Math.min(j, (long) (segment.limit - segment.pos));
                this.deflater.setInput(segment.data, segment.pos, min);
                deflate(false);
                buffer.size -= (long) min;
                segment.pos += min;
                if (segment.pos == segment.limit) {
                    buffer.head = segment.pop();
                    SegmentPool.recycle(segment);
                }
                j -= (long) min;
            } else {
                return;
            }
        }
    }
}
