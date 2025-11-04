// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.IOException;

public final class Pipe
{
    final Buffer buffer;
    final long maxBufferSize;
    private final Sink sink;
    boolean sinkClosed;
    private final Source source;
    boolean sourceClosed;
    
    public Pipe(final long n) {
        this.buffer = new Buffer();
        this.sink = new PipeSink();
        this.source = new PipeSource();
        int n2;
        if (n >= 1L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("maxBufferSize < 1: " + n);
        }
        this.maxBufferSize = n;
    }
    
    public Sink sink() {
        return this.sink;
    }
    
    public Source source() {
        return this.source;
    }
    
    final class PipeSink implements Sink
    {
        final Timeout timeout;
        
        PipeSink() {
            this.timeout = new Timeout();
        }
        
        @Override
        public void close() throws IOException {
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sinkClosed) {
                    return;
                }
                try {
                    this.flush();
                }
                finally {
                    Pipe.this.sinkClosed = true;
                    Pipe.this.buffer.notifyAll();
                }
            }
        }
        
        @Override
        public void flush() throws IOException {
            while (true) {
            Label_0102:
                while (true) {
                    Label_0085: {
                        synchronized (Pipe.this.buffer) {
                            if (!Pipe.this.sinkClosed) {
                                while (Pipe.this.buffer.size() <= 0L) {
                                    final int n = 1;
                                    if (n != 0) {
                                        break Label_0102;
                                    }
                                    if (Pipe.this.sourceClosed) {
                                        throw new IOException("source is closed");
                                    }
                                    this.timeout.waitUntilNotified(Pipe.this.buffer);
                                }
                                break Label_0085;
                            }
                        }
                        throw new IllegalStateException("closed");
                    }
                    final int n = 0;
                    continue;
                }
                final Buffer buffer;
                monitorexit(buffer);
            }
        }
        
        @Override
        public Timeout timeout() {
            return this.timeout;
        }
        
        @Override
        public void write(final Buffer buffer, long b) throws IOException {
            while (true) {
            Label_0031_Outer:
                while (true) {
                    long min = 0L;
                Label_0126:
                    while (true) {
                        Label_0108: {
                            synchronized (Pipe.this.buffer) {
                                if (!Pipe.this.sinkClosed) {
                                    while (b <= 0L) {
                                        final int n = 1;
                                        if (n != 0) {
                                            break Label_0031_Outer;
                                        }
                                        if (Pipe.this.sourceClosed) {
                                            throw new IOException("source is closed");
                                        }
                                        min = Pipe.this.maxBufferSize - Pipe.this.buffer.size();
                                        if (min != 0L) {
                                            break Label_0126;
                                        }
                                        this.timeout.waitUntilNotified(Pipe.this.buffer);
                                    }
                                    break Label_0108;
                                }
                            }
                            throw new IllegalStateException("closed");
                        }
                        final int n = 0;
                        continue;
                    }
                    min = Math.min(min, b);
                    final Buffer buffer2;
                    Pipe.this.buffer.write(buffer2, min);
                    b -= min;
                    Pipe.this.buffer.notifyAll();
                    continue Label_0031_Outer;
                }
                final Buffer buffer3;
                monitorexit(buffer3);
            }
        }
    }
    
    final class PipeSource implements Source
    {
        final Timeout timeout;
        
        PipeSource() {
            this.timeout = new Timeout();
        }
        
        @Override
        public void close() throws IOException {
            synchronized (Pipe.this.buffer) {
                Pipe.this.sourceClosed = true;
                Pipe.this.buffer.notifyAll();
            }
        }
        
        @Override
        public long read(final Buffer buffer, long read) throws IOException {
            final Buffer buffer2;
            Label_0089: {
                Label_0082: {
                    synchronized (Pipe.this.buffer) {
                        if (!Pipe.this.sourceClosed) {
                            while (Pipe.this.buffer.size() == 0L) {
                                if (Pipe.this.sinkClosed) {
                                    break Label_0082;
                                }
                                this.timeout.waitUntilNotified(Pipe.this.buffer);
                            }
                            break Label_0089;
                        }
                    }
                    throw new IllegalStateException("closed");
                }
                monitorexit(buffer2);
                return -1L;
            }
            final Buffer buffer3;
            read = Pipe.this.buffer.read(buffer3, read);
            Pipe.this.buffer.notifyAll();
            monitorexit(buffer2);
            return read;
        }
        
        @Override
        public Timeout timeout() {
            return this.timeout;
        }
    }
}
