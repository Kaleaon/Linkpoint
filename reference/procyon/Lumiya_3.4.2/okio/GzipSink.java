// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.CRC32;

public final class GzipSink implements Sink
{
    private boolean closed;
    private final CRC32 crc;
    private final Deflater deflater;
    private final DeflaterSink deflaterSink;
    private final BufferedSink sink;
    
    public GzipSink(final Sink sink) {
        this.crc = new CRC32();
        if (sink != null) {
            this.deflater = new Deflater(-1, true);
            this.sink = Okio.buffer(sink);
            this.deflaterSink = new DeflaterSink(this.sink, this.deflater);
            this.writeHeader();
            return;
        }
        throw new IllegalArgumentException("sink == null");
    }
    
    private void updateCrc(final Buffer buffer, long a) {
        Segment segment = buffer.head;
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
            final int len = (int)Math.min(a, segment.limit - segment.pos);
            this.crc.update(segment.data, segment.pos, len);
            a -= len;
            segment = segment.next;
        }
    }
    
    private void writeFooter() throws IOException {
        this.sink.writeIntLe((int)this.crc.getValue());
        this.sink.writeIntLe((int)this.deflater.getBytesRead());
    }
    
    private void writeHeader() {
        final Buffer buffer = this.sink.buffer();
        buffer.writeShort(8075);
        buffer.writeByte(8);
        buffer.writeByte(0);
        buffer.writeInt(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
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
        //     3: getfield        okio/GzipSink.closed:Z
        //     6: ifne            50
        //     9: aload_0        
        //    10: getfield        okio/GzipSink.deflaterSink:Lokio/DeflaterSink;
        //    13: invokevirtual   okio/DeflaterSink.finishDeflate:()V
        //    16: aload_0        
        //    17: invokespecial   okio/GzipSink.writeFooter:()V
        //    20: aload_0        
        //    21: getfield        okio/GzipSink.deflater:Ljava/util/zip/Deflater;
        //    24: invokevirtual   java/util/zip/Deflater.end:()V
        //    27: aload_1        
        //    28: astore_2       
        //    29: aload_0        
        //    30: getfield        okio/GzipSink.sink:Lokio/BufferedSink;
        //    33: invokeinterface okio/BufferedSink.close:()V
        //    38: aload_2        
        //    39: astore_1       
        //    40: aload_0        
        //    41: iconst_1       
        //    42: putfield        okio/GzipSink.closed:Z
        //    45: aload_1        
        //    46: ifnonnull       77
        //    49: return         
        //    50: return         
        //    51: astore_1       
        //    52: goto            20
        //    55: astore_3       
        //    56: aload_1        
        //    57: astore_2       
        //    58: aload_1        
        //    59: ifnonnull       29
        //    62: aload_3        
        //    63: astore_2       
        //    64: goto            29
        //    67: astore_1       
        //    68: aload_2        
        //    69: ifnull          40
        //    72: aload_2        
        //    73: astore_1       
        //    74: goto            40
        //    77: aload_1        
        //    78: invokestatic    okio/Util.sneakyRethrow:(Ljava/lang/Throwable;)V
        //    81: goto            49
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  9      20     51     55     Ljava/lang/Throwable;
        //  20     27     55     67     Ljava/lang/Throwable;
        //  29     38     67     77     Ljava/lang/Throwable;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 46 out of bounds for length 46
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
    
    public Deflater deflater() {
        return this.deflater;
    }
    
    @Override
    public void flush() throws IOException {
        this.deflaterSink.flush();
    }
    
    @Override
    public Timeout timeout() {
        return this.sink.timeout();
    }
    
    @Override
    public void write(final Buffer buffer, final long lng) throws IOException {
        int n;
        if (lng >= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new IllegalArgumentException("byteCount < 0: " + lng);
        }
        if (lng == 0L) {
            return;
        }
        this.updateCrc(buffer, lng);
        this.deflaterSink.write(buffer, lng);
    }
}
