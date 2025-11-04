// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.ws;

import okio.Timeout;
import okio.Sink;
import java.io.IOException;
import okio.ByteString;
import okio.BufferedSink;
import java.util.Random;
import okio.Buffer;

final class WebSocketWriter
{
    boolean activeWriter;
    final Buffer buffer;
    final FrameSink frameSink;
    final boolean isClient;
    final byte[] maskBuffer;
    final byte[] maskKey;
    final Random random;
    final BufferedSink sink;
    boolean writerClosed;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!WebSocketWriter.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
    }
    
    WebSocketWriter(final boolean isClient, final BufferedSink sink, final Random random) {
        final byte[] array = null;
        this.buffer = new Buffer();
        this.frameSink = new FrameSink();
        if (sink == null) {
            throw new NullPointerException("sink == null");
        }
        if (random != null) {
            this.isClient = isClient;
            this.sink = sink;
            this.random = random;
            byte[] maskKey;
            if (!isClient) {
                maskKey = null;
            }
            else {
                maskKey = new byte[4];
            }
            this.maskKey = maskKey;
            byte[] maskBuffer;
            if (!isClient) {
                maskBuffer = array;
            }
            else {
                maskBuffer = new byte[8192];
            }
            this.maskBuffer = maskBuffer;
            return;
        }
        throw new NullPointerException("random == null");
    }
    
    private void writeControlFrameSynchronized(final int n, final ByteString byteString) throws IOException {
        boolean b = false;
        assert Thread.holdsLock(this);
        if (this.writerClosed) {
            throw new IOException("closed");
        }
        final int size = byteString.size();
        if (size <= 125L) {
            b = true;
        }
        if (!b) {
            throw new IllegalArgumentException("Payload size must be less than or equal to 125");
        }
        this.sink.writeByte(n | 0x80);
        if (!this.isClient) {
            this.sink.writeByte(size);
            this.sink.write(byteString);
        }
        else {
            this.sink.writeByte(size | 0x80);
            this.random.nextBytes(this.maskKey);
            this.sink.write(this.maskKey);
            final byte[] byteArray = byteString.toByteArray();
            WebSocketProtocol.toggleMask(byteArray, byteArray.length, this.maskKey, 0L);
            this.sink.write(byteArray);
        }
        this.sink.flush();
    }
    
    Sink newMessageSink(final int formatOpcode, final long contentLength) {
        if (!this.activeWriter) {
            this.activeWriter = true;
            this.frameSink.formatOpcode = formatOpcode;
            this.frameSink.contentLength = contentLength;
            this.frameSink.isFirstFrame = true;
            this.frameSink.closed = false;
            return this.frameSink;
        }
        throw new IllegalStateException("Another message writer is active. Did you call close()?");
    }
    
    void writeClose(final int p0, final ByteString p1) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     4: iload_1        
        //     5: ifeq            52
        //     8: iload_1        
        //     9: ifne            61
        //    12: new             Lokio/Buffer;
        //    15: dup            
        //    16: invokespecial   okio/Buffer.<init>:()V
        //    19: astore_3       
        //    20: aload_3        
        //    21: iload_1        
        //    22: invokevirtual   okio/Buffer.writeShort:(I)Lokio/Buffer;
        //    25: pop            
        //    26: aload_2        
        //    27: ifnonnull       68
        //    30: aload_3        
        //    31: invokevirtual   okio/Buffer.readByteString:()Lokio/ByteString;
        //    34: astore_2       
        //    35: aload_0        
        //    36: monitorenter   
        //    37: aload_0        
        //    38: bipush          8
        //    40: aload_2        
        //    41: invokespecial   okhttp3/internal/ws/WebSocketWriter.writeControlFrameSynchronized:(ILokio/ByteString;)V
        //    44: aload_0        
        //    45: iconst_1       
        //    46: putfield        okhttp3/internal/ws/WebSocketWriter.writerClosed:Z
        //    49: aload_0        
        //    50: monitorexit    
        //    51: return         
        //    52: aload_2        
        //    53: ifnonnull       8
        //    56: aload_3        
        //    57: astore_2       
        //    58: goto            35
        //    61: iload_1        
        //    62: invokestatic    okhttp3/internal/ws/WebSocketProtocol.validateCloseCode:(I)V
        //    65: goto            12
        //    68: aload_3        
        //    69: aload_2        
        //    70: invokevirtual   okio/Buffer.write:(Lokio/ByteString;)Lokio/Buffer;
        //    73: pop            
        //    74: goto            30
        //    77: astore_2       
        //    78: aload_0        
        //    79: iconst_1       
        //    80: putfield        okhttp3/internal/ws/WebSocketWriter.writerClosed:Z
        //    83: aload_2        
        //    84: athrow         
        //    85: astore_2       
        //    86: aload_0        
        //    87: monitorexit    
        //    88: aload_2        
        //    89: athrow         
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  37     44     77     85     Any
        //  44     51     85     90     Any
        //  78     85     85     90     Any
        //  86     88     85     90     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0052:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    void writeMessageFrameSynchronized(int read, final long a, final boolean b, final boolean b2) throws IOException {
        assert Thread.holdsLock(this);
        if (!this.writerClosed) {
            if (!b) {
                read = 0;
            }
            if (b2) {
                read |= 0x80;
            }
            this.sink.writeByte(read);
            if (!this.isClient) {
                read = 0;
            }
            else {
                read = 128;
            }
            int n;
            if (a > 125L) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                this.sink.writeByte((int)a | read);
            }
            else {
                int n2;
                if (a > 65535L) {
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                if (n2 == 0) {
                    this.sink.writeByte(read | 0x7E);
                    this.sink.writeShort((int)a);
                }
                else {
                    this.sink.writeByte(read | 0x7F);
                    this.sink.writeLong(a);
                }
            }
            Label_0100: {
                if (this.isClient) {
                    this.random.nextBytes(this.maskKey);
                    this.sink.write(this.maskKey);
                    long n3 = 0L;
                    while (true) {
                        if (n3 >= a) {
                            read = 1;
                        }
                        else {
                            read = 0;
                        }
                        if (read != 0) {
                            break Label_0100;
                        }
                        read = (int)Math.min(a, this.maskBuffer.length);
                        read = this.buffer.read(this.maskBuffer, 0, read);
                        if (read == -1) {
                            break;
                        }
                        WebSocketProtocol.toggleMask(this.maskBuffer, read, this.maskKey, n3);
                        this.sink.write(this.maskBuffer, 0, read);
                        n3 += read;
                    }
                    throw new AssertionError();
                }
                this.sink.write(this.buffer, a);
            }
            this.sink.emit();
            return;
        }
        throw new IOException("closed");
    }
    
    void writePing(final ByteString byteString) throws IOException {
        synchronized (this) {
            this.writeControlFrameSynchronized(9, byteString);
        }
    }
    
    void writePong(final ByteString byteString) throws IOException {
        synchronized (this) {
            this.writeControlFrameSynchronized(10, byteString);
        }
    }
    
    final class FrameSink implements Sink
    {
        boolean closed;
        long contentLength;
        int formatOpcode;
        boolean isFirstFrame;
        
        @Override
        public void close() throws IOException {
            Label_0056: {
                if (this.closed) {
                    break Label_0056;
                }
                synchronized (WebSocketWriter.this) {
                    WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, true);
                    monitorexit(WebSocketWriter.this);
                    this.closed = true;
                    WebSocketWriter.this.activeWriter = false;
                    return;
                    throw new IOException("closed");
                }
            }
        }
        
        @Override
        public void flush() throws IOException {
            Label_0048: {
                if (this.closed) {
                    break Label_0048;
                }
                synchronized (WebSocketWriter.this) {
                    WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, false);
                    monitorexit(WebSocketWriter.this);
                    this.isFirstFrame = false;
                    return;
                    throw new IOException("closed");
                }
            }
        }
        
        @Override
        public Timeout timeout() {
            return WebSocketWriter.this.sink.timeout();
        }
        
        @Override
        public void write(final Buffer buffer, long completeSegmentByteCount) throws IOException {
            boolean b = true;
            if (!this.closed) {
                WebSocketWriter.this.buffer.write(buffer, completeSegmentByteCount);
                boolean b2 = false;
                Label_0032: {
                    if (this.isFirstFrame && this.contentLength != -1L) {
                        int n;
                        if (WebSocketWriter.this.buffer.size() <= this.contentLength - 8192L) {
                            n = 1;
                        }
                        else {
                            n = 0;
                        }
                        if (n == 0) {
                            b2 = true;
                            break Label_0032;
                        }
                    }
                    b2 = false;
                }
                completeSegmentByteCount = WebSocketWriter.this.buffer.completeSegmentByteCount();
                if (completeSegmentByteCount > 0L) {
                    b = false;
                }
                if (!b && !b2) {
                    synchronized (WebSocketWriter.this) {
                        WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, completeSegmentByteCount, this.isFirstFrame, false);
                        monitorexit(WebSocketWriter.this);
                        this.isFirstFrame = false;
                    }
                }
                return;
            }
            throw new IOException("closed");
        }
    }
}
