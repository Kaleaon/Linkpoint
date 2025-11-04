// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.nio.charset.Charset;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.IOException;

final class RealBufferedSink implements BufferedSink
{
    public final Buffer buffer;
    boolean closed;
    public final Sink sink;
    
    RealBufferedSink(final Sink sink) {
        this.buffer = new Buffer();
        if (sink != null) {
            this.sink = sink;
            return;
        }
        throw new NullPointerException("sink == null");
    }
    
    @Override
    public Buffer buffer() {
        return this.buffer;
    }
    
    @Override
    public void close() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: aconst_null    
        //     3: astore_2       
        //     4: aload_0        
        //     5: getfield        okio/RealBufferedSink.closed:Z
        //     8: ifne            74
        //    11: aload_0        
        //    12: getfield        okio/RealBufferedSink.buffer:Lokio/Buffer;
        //    15: getfield        okio/Buffer.size:J
        //    18: lconst_0       
        //    19: lcmp           
        //    20: ifgt            25
        //    23: iconst_1       
        //    24: istore_1       
        //    25: aload_2        
        //    26: astore_3       
        //    27: iload_1        
        //    28: ifne            53
        //    31: aload_0        
        //    32: getfield        okio/RealBufferedSink.sink:Lokio/Sink;
        //    35: aload_0        
        //    36: getfield        okio/RealBufferedSink.buffer:Lokio/Buffer;
        //    39: aload_0        
        //    40: getfield        okio/RealBufferedSink.buffer:Lokio/Buffer;
        //    43: getfield        okio/Buffer.size:J
        //    46: invokeinterface okio/Sink.write:(Lokio/Buffer;J)V
        //    51: aload_2        
        //    52: astore_3       
        //    53: aload_0        
        //    54: getfield        okio/RealBufferedSink.sink:Lokio/Sink;
        //    57: invokeinterface okio/Sink.close:()V
        //    62: aload_3        
        //    63: astore_2       
        //    64: aload_0        
        //    65: iconst_1       
        //    66: putfield        okio/RealBufferedSink.closed:Z
        //    69: aload_2        
        //    70: ifnonnull       89
        //    73: return         
        //    74: return         
        //    75: astore_3       
        //    76: goto            53
        //    79: astore_2       
        //    80: aload_3        
        //    81: ifnull          64
        //    84: aload_3        
        //    85: astore_2       
        //    86: goto            64
        //    89: aload_2        
        //    90: invokestatic    okio/Util.sneakyRethrow:(Ljava/lang/Throwable;)V
        //    93: goto            73
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  11     23     75     79     Ljava/lang/Throwable;
        //  31     51     75     79     Ljava/lang/Throwable;
        //  53     62     79     89     Ljava/lang/Throwable;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0053:
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
    
    @Override
    public BufferedSink emit() throws IOException {
        boolean b = false;
        if (!this.closed) {
            final long size = this.buffer.size();
            if (size <= 0L) {
                b = true;
            }
            if (!b) {
                this.sink.write(this.buffer, size);
            }
            return this;
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink emitCompleteSegments() throws IOException {
        boolean b = false;
        if (!this.closed) {
            final long completeSegmentByteCount = this.buffer.completeSegmentByteCount();
            if (completeSegmentByteCount <= 0L) {
                b = true;
            }
            if (!b) {
                this.sink.write(this.buffer, completeSegmentByteCount);
            }
            return this;
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public void flush() throws IOException {
        boolean b = false;
        if (!this.closed) {
            if (this.buffer.size <= 0L) {
                b = true;
            }
            if (!b) {
                this.sink.write(this.buffer, this.buffer.size);
            }
            this.sink.flush();
            return;
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public OutputStream outputStream() {
        return new OutputStream() {
            @Override
            public void close() throws IOException {
                RealBufferedSink.this.close();
            }
            
            @Override
            public void flush() throws IOException {
                if (!RealBufferedSink.this.closed) {
                    RealBufferedSink.this.flush();
                }
            }
            
            @Override
            public String toString() {
                return RealBufferedSink.this + ".outputStream()";
            }
            
            @Override
            public void write(final int n) throws IOException {
                if (!RealBufferedSink.this.closed) {
                    RealBufferedSink.this.buffer.writeByte((int)(byte)n);
                    RealBufferedSink.this.emitCompleteSegments();
                    return;
                }
                throw new IOException("closed");
            }
            
            @Override
            public void write(final byte[] array, final int n, final int n2) throws IOException {
                if (!RealBufferedSink.this.closed) {
                    RealBufferedSink.this.buffer.write(array, n, n2);
                    RealBufferedSink.this.emitCompleteSegments();
                    return;
                }
                throw new IOException("closed");
            }
        };
    }
    
    @Override
    public Timeout timeout() {
        return this.sink.timeout();
    }
    
    @Override
    public String toString() {
        return "buffer(" + this.sink + ")";
    }
    
    @Override
    public BufferedSink write(final ByteString byteString) throws IOException {
        if (!this.closed) {
            this.buffer.write(byteString);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
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
            final long read = source.read(this.buffer, n);
            if (read == -1L) {
                throw new EOFException();
            }
            n -= read;
            this.emitCompleteSegments();
        }
    }
    
    @Override
    public BufferedSink write(final byte[] array) throws IOException {
        if (!this.closed) {
            this.buffer.write(array);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink write(final byte[] array, final int n, final int n2) throws IOException {
        if (!this.closed) {
            this.buffer.write(array, n, n2);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public void write(final Buffer buffer, final long n) throws IOException {
        if (!this.closed) {
            this.buffer.write(buffer, n);
            this.emitCompleteSegments();
            return;
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public long writeAll(final Source source) throws IOException {
        if (source != null) {
            long n = 0L;
            while (true) {
                final long read = source.read(this.buffer, 8192L);
                if (read == -1L) {
                    break;
                }
                n += read;
                this.emitCompleteSegments();
            }
            return n;
        }
        throw new IllegalArgumentException("source == null");
    }
    
    @Override
    public BufferedSink writeByte(final int n) throws IOException {
        if (!this.closed) {
            this.buffer.writeByte(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeDecimalLong(final long n) throws IOException {
        if (!this.closed) {
            this.buffer.writeDecimalLong(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeHexadecimalUnsignedLong(final long n) throws IOException {
        if (!this.closed) {
            this.buffer.writeHexadecimalUnsignedLong(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeInt(final int n) throws IOException {
        if (!this.closed) {
            this.buffer.writeInt(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeIntLe(final int n) throws IOException {
        if (!this.closed) {
            this.buffer.writeIntLe(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeLong(final long n) throws IOException {
        if (!this.closed) {
            this.buffer.writeLong(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeLongLe(final long n) throws IOException {
        if (!this.closed) {
            this.buffer.writeLongLe(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeShort(final int n) throws IOException {
        if (!this.closed) {
            this.buffer.writeShort(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeShortLe(final int n) throws IOException {
        if (!this.closed) {
            this.buffer.writeShortLe(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeString(final String s, final int n, final int n2, final Charset charset) throws IOException {
        if (!this.closed) {
            this.buffer.writeString(s, n, n2, charset);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeString(final String s, final Charset charset) throws IOException {
        if (!this.closed) {
            this.buffer.writeString(s, charset);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeUtf8(final String s) throws IOException {
        if (!this.closed) {
            this.buffer.writeUtf8(s);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeUtf8(final String s, final int n, final int n2) throws IOException {
        if (!this.closed) {
            this.buffer.writeUtf8(s, n, n2);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
    
    @Override
    public BufferedSink writeUtf8CodePoint(final int n) throws IOException {
        if (!this.closed) {
            this.buffer.writeUtf8CodePoint(n);
            return this.emitCompleteSegments();
        }
        throw new IllegalStateException("closed");
    }
}
