package okhttp3.internal.cache2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;
/* loaded from: classes.dex */
final class Relay {
    private static final long FILE_HEADER_SIZE = 32;
    static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
    static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
    private static final int SOURCE_FILE = 2;
    private static final int SOURCE_UPSTREAM = 1;
    final long bufferMaxSize;
    boolean complete;
    RandomAccessFile file;
    private final ByteString metadata;
    int sourceCount;
    Source upstream;
    long upstreamPos;
    Thread upstreamReader;
    final Buffer upstreamBuffer = new Buffer();
    final Buffer buffer = new Buffer();

    /* loaded from: classes.dex */
    class RelaySource implements Source {
        private FileOperator fileOperator;
        private long sourcePos;
        private final Timeout timeout = new Timeout();

        RelaySource() {
            this.fileOperator = new FileOperator(Relay.this.file.getChannel());
        }

        @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            RandomAccessFile randomAccessFile = null;
            if (this.fileOperator != null) {
                this.fileOperator = null;
                synchronized (Relay.this) {
                    Relay relay = Relay.this;
                    relay.sourceCount--;
                    if (Relay.this.sourceCount == 0) {
                        randomAccessFile = Relay.this.file;
                        Relay.this.file = null;
                    }
                }
                if (randomAccessFile == null) {
                    return;
                }
                Util.closeQuietly(randomAccessFile);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0028, code lost:
            if (r0 == 2) goto L18;
         */
        /* JADX WARN: Code restructure failed: missing block: B:16:0x002a, code lost:
            r10 = r12.this$0.upstream.read(r12.this$0.upstreamBuffer, r12.this$0.bufferMaxSize);
         */
        /* JADX WARN: Code restructure failed: missing block: B:17:0x003e, code lost:
            if (r10 != (-1)) goto L35;
         */
        /* JADX WARN: Code restructure failed: missing block: B:18:0x0040, code lost:
            r12.this$0.commit(r8);
         */
        /* JADX WARN: Code restructure failed: missing block: B:19:0x0045, code lost:
            r1 = r12.this$0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:20:0x0047, code lost:
            monitor-enter(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:21:0x0048, code lost:
            r12.this$0.upstreamReader = null;
            r12.this$0.notifyAll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x0052, code lost:
            monitor-exit(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x0053, code lost:
            return -1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:45:0x00a1, code lost:
            r4 = java.lang.Math.min(r14, r8 - r12.sourcePos);
            r12.fileOperator.read(32 + r12.sourcePos, r13, r4);
            r12.sourcePos += r4;
         */
        /* JADX WARN: Code restructure failed: missing block: B:46:0x00ba, code lost:
            return r4;
         */
        /* JADX WARN: Code restructure failed: missing block: B:50:0x00be, code lost:
            r4 = java.lang.Math.min(r10, r14);
            r12.this$0.upstreamBuffer.copyTo(r13, 0, r4);
            r12.sourcePos += r4;
            r12.fileOperator.write(32 + r8, r12.this$0.upstreamBuffer.clone(), r10);
            r1 = r12.this$0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:51:0x00e4, code lost:
            monitor-enter(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:52:0x00e5, code lost:
            r12.this$0.buffer.write(r12.this$0.upstreamBuffer, r10);
         */
        /* JADX WARN: Code restructure failed: missing block: B:53:0x00fe, code lost:
            if (r12.this$0.buffer.size() > r12.this$0.bufferMaxSize) goto L54;
         */
        /* JADX WARN: Code restructure failed: missing block: B:54:0x0100, code lost:
            r0 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:55:0x0101, code lost:
            if (r0 != false) goto L42;
         */
        /* JADX WARN: Code restructure failed: missing block: B:56:0x0103, code lost:
            r12.this$0.buffer.skip(r12.this$0.buffer.size() - r12.this$0.bufferMaxSize);
         */
        /* JADX WARN: Code restructure failed: missing block: B:57:0x0117, code lost:
            r12.this$0.upstreamPos += r10;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x011e, code lost:
            monitor-exit(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:59:0x011f, code lost:
            r1 = r12.this$0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:60:0x0121, code lost:
            monitor-enter(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:61:0x0122, code lost:
            r12.this$0.upstreamReader = null;
            r12.this$0.notifyAll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:62:0x012c, code lost:
            monitor-exit(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:63:0x012d, code lost:
            return r4;
         */
        /* JADX WARN: Code restructure failed: missing block: B:64:0x012e, code lost:
            r0 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x0133, code lost:
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:70:0x0136, code lost:
            monitor-enter(r12.this$0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:71:0x0137, code lost:
            r12.this$0.upstreamReader = null;
            r12.this$0.notifyAll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:73:0x0142, code lost:
            throw r0;
         */
        @Override // okio.Source
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public long read(okio.Buffer r13, long r14) throws java.io.IOException {
            /*
                Method dump skipped, instructions count: 329
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.Relay.RelaySource.read(okio.Buffer, long):long");
        }

        @Override // okio.Source
        public Timeout timeout() {
            return this.timeout;
        }
    }

    private Relay(RandomAccessFile randomAccessFile, Source source, long j, ByteString byteString, long j2) {
        this.file = randomAccessFile;
        this.upstream = source;
        this.complete = source == null;
        this.upstreamPos = j;
        this.metadata = byteString;
        this.bufferMaxSize = j2;
    }

    public static Relay edit(File file, Source source, ByteString byteString, long j) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        Relay relay = new Relay(randomAccessFile, source, 0L, byteString, j);
        randomAccessFile.setLength(0L);
        relay.writeHeader(PREFIX_DIRTY, -1L, -1L);
        return relay;
    }

    public static Relay read(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
        Buffer buffer = new Buffer();
        fileOperator.read(0L, buffer, 32L);
        if (buffer.readByteString(PREFIX_CLEAN.size()).equals(PREFIX_CLEAN)) {
            long readLong = buffer.readLong();
            long readLong2 = buffer.readLong();
            Buffer buffer2 = new Buffer();
            fileOperator.read(32 + readLong, buffer2, readLong2);
            return new Relay(randomAccessFile, null, readLong, buffer2.readByteString(), 0L);
        }
        throw new IOException("unreadable cache file");
    }

    private void writeHeader(ByteString byteString, long j, long j2) throws IOException {
        Buffer buffer = new Buffer();
        buffer.write(byteString);
        buffer.writeLong(j);
        buffer.writeLong(j2);
        if (buffer.size() != 32) {
            throw new IllegalArgumentException();
        }
        new FileOperator(this.file.getChannel()).write(0L, buffer, 32L);
    }

    private void writeMetadata(long j) throws IOException {
        Buffer buffer = new Buffer();
        buffer.write(this.metadata);
        new FileOperator(this.file.getChannel()).write(32 + j, buffer, this.metadata.size());
    }

    void commit(long j) throws IOException {
        writeMetadata(j);
        this.file.getChannel().force(false);
        writeHeader(PREFIX_CLEAN, j, this.metadata.size());
        this.file.getChannel().force(false);
        synchronized (this) {
            this.complete = true;
        }
        Util.closeQuietly(this.upstream);
        this.upstream = null;
    }

    boolean isClosed() {
        return this.file == null;
    }

    public ByteString metadata() {
        return this.metadata;
    }

    public Source newSource() {
        synchronized (this) {
            if (this.file != null) {
                this.sourceCount++;
                return new RelaySource();
            }
            return null;
        }
    }
}
