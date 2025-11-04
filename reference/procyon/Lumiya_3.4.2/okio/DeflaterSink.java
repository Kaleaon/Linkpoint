// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import java.io.IOException;
import java.util.zip.Deflater;

public final class DeflaterSink implements Sink
{
    private boolean closed;
    private final Deflater deflater;
    private final BufferedSink sink;
    
    DeflaterSink(final BufferedSink sink, final Deflater deflater) {
        if (sink == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (deflater != null) {
            this.sink = sink;
            this.deflater = deflater;
            return;
        }
        throw new IllegalArgumentException("inflater == null");
    }
    
    public DeflaterSink(final Sink sink, final Deflater deflater) {
        this(Okio.buffer(sink), deflater);
    }
    
    @IgnoreJRERequirement
    private void deflate(final boolean b) throws IOException {
        final Buffer buffer = this.sink.buffer();
        Segment writableSegment;
        while (true) {
            writableSegment = buffer.writableSegment(1);
            int n;
            if (!b) {
                n = this.deflater.deflate(writableSegment.data, writableSegment.limit, 8192 - writableSegment.limit);
            }
            else {
                n = this.deflater.deflate(writableSegment.data, writableSegment.limit, 8192 - writableSegment.limit, 2);
            }
            if (n <= 0) {
                if (this.deflater.needsInput()) {
                    break;
                }
                continue;
            }
            else {
                writableSegment.limit += n;
                buffer.size += n;
                this.sink.emitCompleteSegments();
            }
        }
        if (writableSegment.pos == writableSegment.limit) {
            buffer.head = writableSegment.pop();
            SegmentPool.recycle(writableSegment);
        }
    }
    
    @Override
    public void close() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_1       
        //     2: aload_0        
        //     3: getfield        okio/DeflaterSink.closed:Z
        //     6: ifne            43
        //     9: aload_0        
        //    10: invokevirtual   okio/DeflaterSink.finishDeflate:()V
        //    13: aload_0        
        //    14: getfield        okio/DeflaterSink.deflater:Ljava/util/zip/Deflater;
        //    17: invokevirtual   java/util/zip/Deflater.end:()V
        //    20: aload_1        
        //    21: astore_2       
        //    22: aload_0        
        //    23: getfield        okio/DeflaterSink.sink:Lokio/BufferedSink;
        //    26: invokeinterface okio/BufferedSink.close:()V
        //    31: aload_2        
        //    32: astore_1       
        //    33: aload_0        
        //    34: iconst_1       
        //    35: putfield        okio/DeflaterSink.closed:Z
        //    38: aload_1        
        //    39: ifnonnull       70
        //    42: return         
        //    43: return         
        //    44: astore_1       
        //    45: goto            13
        //    48: astore_3       
        //    49: aload_1        
        //    50: astore_2       
        //    51: aload_1        
        //    52: ifnonnull       22
        //    55: aload_3        
        //    56: astore_2       
        //    57: goto            22
        //    60: astore_1       
        //    61: aload_2        
        //    62: ifnull          33
        //    65: aload_2        
        //    66: astore_1       
        //    67: goto            33
        //    70: aload_1        
        //    71: invokestatic    okio/Util.sneakyRethrow:(Ljava/lang/Throwable;)V
        //    74: goto            42
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  9      13     44     48     Ljava/lang/Throwable;
        //  13     20     48     60     Ljava/lang/Throwable;
        //  22     31     60     70     Ljava/lang/Throwable;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 43 out of bounds for length 43
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
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
    
    void finishDeflate() throws IOException {
        this.deflater.finish();
        this.deflate(false);
    }
    
    @Override
    public void flush() throws IOException {
        this.deflate(true);
        this.sink.flush();
    }
    
    @Override
    public Timeout timeout() {
        return this.sink.timeout();
    }
    
    @Override
    public String toString() {
        return "DeflaterSink(" + this.sink + ")";
    }
    
    @Override
    public void write(final Buffer buffer, long a) throws IOException {
        Util.checkOffsetAndCount(buffer.size, 0L, a);
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
            final Segment head = buffer.head;
            final int len = (int)Math.min(a, head.limit - head.pos);
            this.deflater.setInput(head.data, head.pos, len);
            this.deflate(false);
            buffer.size -= len;
            head.pos += len;
            if (head.pos == head.limit) {
                buffer.head = head.pop();
                SegmentPool.recycle(head);
            }
            a -= len;
        }
    }
}
