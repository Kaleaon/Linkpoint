// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.ws;

import okio.ByteString;
import java.io.IOException;
import java.net.ProtocolException;
import java.io.EOFException;
import okio.Buffer;
import okio.BufferedSource;

final class WebSocketReader
{
    boolean closed;
    long frameBytesRead;
    final FrameCallback frameCallback;
    long frameLength;
    final boolean isClient;
    boolean isControlFrame;
    boolean isFinalFrame;
    boolean isMasked;
    final byte[] maskBuffer;
    final byte[] maskKey;
    int opcode;
    final BufferedSource source;
    
    WebSocketReader(final boolean isClient, final BufferedSource source, final FrameCallback frameCallback) {
        this.maskKey = new byte[4];
        this.maskBuffer = new byte[8192];
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (frameCallback != null) {
            this.isClient = isClient;
            this.source = source;
            this.frameCallback = frameCallback;
            return;
        }
        throw new NullPointerException("frameCallback == null");
    }
    
    private void readControlFrame() throws IOException {
        final Buffer buffer = new Buffer();
        int n;
        if (this.frameBytesRead >= this.frameLength) {
            n = 1;
        }
        else {
            n = 0;
        }
        Label_0153: {
            if (n == 0) {
                if (!this.isClient) {
                    while (true) {
                        int n2;
                        if (this.frameBytesRead >= this.frameLength) {
                            n2 = 1;
                        }
                        else {
                            n2 = 0;
                        }
                        if (n2 != 0) {
                            break Label_0153;
                        }
                        final int read = this.source.read(this.maskBuffer, 0, (int)Math.min(this.frameLength - this.frameBytesRead, this.maskBuffer.length));
                        if (read == -1) {
                            break;
                        }
                        WebSocketProtocol.toggleMask(this.maskBuffer, read, this.maskKey, this.frameBytesRead);
                        buffer.write(this.maskBuffer, 0, read);
                        this.frameBytesRead += read;
                    }
                    throw new EOFException();
                }
                this.source.readFully(buffer, this.frameLength);
            }
        }
        switch (this.opcode) {
            default: {
                throw new ProtocolException("Unknown control opcode: " + Integer.toHexString(this.opcode));
            }
            case 9: {
                this.frameCallback.onReadPing(buffer.readByteString());
                break;
            }
            case 10: {
                this.frameCallback.onReadPong(buffer.readByteString());
                break;
            }
            case 8: {
                int short1 = 1005;
                String utf8 = "";
                final long size = buffer.size();
                if (size == 1L) {
                    throw new ProtocolException("Malformed close payload length of 1.");
                }
                if (size != 0L) {
                    short1 = buffer.readShort();
                    utf8 = buffer.readUtf8();
                    final String closeCodeExceptionMessage = WebSocketProtocol.closeCodeExceptionMessage(short1);
                    if (closeCodeExceptionMessage != null) {
                        throw new ProtocolException(closeCodeExceptionMessage);
                    }
                }
                this.frameCallback.onReadClose(short1, utf8);
                this.closed = true;
                break;
            }
        }
    }
    
    private void readHeader() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: aload_0        
        //     3: getfield        okhttp3/internal/ws/WebSocketReader.closed:Z
        //     6: ifne            170
        //     9: aload_0        
        //    10: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //    13: invokeinterface okio/BufferedSource.timeout:()Lokio/Timeout;
        //    18: invokevirtual   okio/Timeout.timeoutNanos:()J
        //    21: lstore_2       
        //    22: aload_0        
        //    23: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //    26: invokeinterface okio/BufferedSource.timeout:()Lokio/Timeout;
        //    31: invokevirtual   okio/Timeout.clearTimeout:()Lokio/Timeout;
        //    34: pop            
        //    35: aload_0        
        //    36: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //    39: invokeinterface okio/BufferedSource.readByte:()B
        //    44: istore          4
        //    46: iload           4
        //    48: sipush          255
        //    51: iand           
        //    52: istore          5
        //    54: aload_0        
        //    55: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //    58: invokeinterface okio/BufferedSource.timeout:()Lokio/Timeout;
        //    63: lload_2        
        //    64: getstatic       java/util/concurrent/TimeUnit.NANOSECONDS:Ljava/util/concurrent/TimeUnit;
        //    67: invokevirtual   okio/Timeout.timeout:(JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
        //    70: pop            
        //    71: aload_0        
        //    72: iload           5
        //    74: bipush          15
        //    76: iand           
        //    77: putfield        okhttp3/internal/ws/WebSocketReader.opcode:I
        //    80: iload           5
        //    82: sipush          128
        //    85: iand           
        //    86: ifne            202
        //    89: iconst_0       
        //    90: istore          6
        //    92: aload_0        
        //    93: iload           6
        //    95: putfield        okhttp3/internal/ws/WebSocketReader.isFinalFrame:Z
        //    98: iload           5
        //   100: bipush          8
        //   102: iand           
        //   103: ifne            208
        //   106: iconst_0       
        //   107: istore          6
        //   109: aload_0        
        //   110: iload           6
        //   112: putfield        okhttp3/internal/ws/WebSocketReader.isControlFrame:Z
        //   115: aload_0        
        //   116: getfield        okhttp3/internal/ws/WebSocketReader.isControlFrame:Z
        //   119: ifne            214
        //   122: iload           5
        //   124: bipush          64
        //   126: iand           
        //   127: ifne            231
        //   130: iconst_0       
        //   131: istore          4
        //   133: iload           5
        //   135: bipush          32
        //   137: iand           
        //   138: ifne            237
        //   141: iconst_0       
        //   142: istore          7
        //   144: iload           5
        //   146: bipush          16
        //   148: iand           
        //   149: ifne            243
        //   152: iconst_0       
        //   153: istore          5
        //   155: iload           4
        //   157: ifeq            249
        //   160: new             Ljava/net/ProtocolException;
        //   163: dup            
        //   164: ldc             "Reserved flags are unsupported."
        //   166: invokespecial   java/net/ProtocolException.<init>:(Ljava/lang/String;)V
        //   169: athrow         
        //   170: new             Ljava/io/IOException;
        //   173: dup            
        //   174: ldc             "closed"
        //   176: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //   179: athrow         
        //   180: astore          8
        //   182: aload_0        
        //   183: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //   186: invokeinterface okio/BufferedSource.timeout:()Lokio/Timeout;
        //   191: lload_2        
        //   192: getstatic       java/util/concurrent/TimeUnit.NANOSECONDS:Ljava/util/concurrent/TimeUnit;
        //   195: invokevirtual   okio/Timeout.timeout:(JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
        //   198: pop            
        //   199: aload           8
        //   201: athrow         
        //   202: iconst_1       
        //   203: istore          6
        //   205: goto            92
        //   208: iconst_1       
        //   209: istore          6
        //   211: goto            109
        //   214: aload_0        
        //   215: getfield        okhttp3/internal/ws/WebSocketReader.isFinalFrame:Z
        //   218: ifne            122
        //   221: new             Ljava/net/ProtocolException;
        //   224: dup            
        //   225: ldc             "Control frames must be final."
        //   227: invokespecial   java/net/ProtocolException.<init>:(Ljava/lang/String;)V
        //   230: athrow         
        //   231: iconst_1       
        //   232: istore          4
        //   234: goto            133
        //   237: iconst_1       
        //   238: istore          7
        //   240: goto            144
        //   243: iconst_1       
        //   244: istore          5
        //   246: goto            155
        //   249: iload           7
        //   251: ifne            160
        //   254: iload           5
        //   256: ifne            160
        //   259: aload_0        
        //   260: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //   263: invokeinterface okio/BufferedSource.readByte:()B
        //   268: sipush          255
        //   271: iand           
        //   272: istore          4
        //   274: iload           4
        //   276: sipush          128
        //   279: iand           
        //   280: ifne            362
        //   283: iconst_0       
        //   284: istore          6
        //   286: aload_0        
        //   287: iload           6
        //   289: putfield        okhttp3/internal/ws/WebSocketReader.isMasked:Z
        //   292: aload_0        
        //   293: getfield        okhttp3/internal/ws/WebSocketReader.isMasked:Z
        //   296: aload_0        
        //   297: getfield        okhttp3/internal/ws/WebSocketReader.isClient:Z
        //   300: if_icmpeq       368
        //   303: aload_0        
        //   304: iload           4
        //   306: bipush          127
        //   308: iand           
        //   309: i2l            
        //   310: putfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   313: aload_0        
        //   314: getfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   317: ldc2_w          126
        //   320: lcmp           
        //   321: ifne            396
        //   324: aload_0        
        //   325: aload_0        
        //   326: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //   329: invokeinterface okio/BufferedSource.readShort:()S
        //   334: i2l            
        //   335: ldc2_w          65535
        //   338: land           
        //   339: putfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   342: aload_0        
        //   343: lconst_0       
        //   344: putfield        okhttp3/internal/ws/WebSocketReader.frameBytesRead:J
        //   347: aload_0        
        //   348: getfield        okhttp3/internal/ws/WebSocketReader.isControlFrame:Z
        //   351: ifne            481
        //   354: aload_0        
        //   355: getfield        okhttp3/internal/ws/WebSocketReader.isMasked:Z
        //   358: ifne            516
        //   361: return         
        //   362: iconst_1       
        //   363: istore          6
        //   365: goto            286
        //   368: aload_0        
        //   369: getfield        okhttp3/internal/ws/WebSocketReader.isClient:Z
        //   372: ifne            389
        //   375: ldc             "Client-sent frames must be masked."
        //   377: astore          8
        //   379: new             Ljava/net/ProtocolException;
        //   382: dup            
        //   383: aload           8
        //   385: invokespecial   java/net/ProtocolException.<init>:(Ljava/lang/String;)V
        //   388: athrow         
        //   389: ldc             "Server-sent frames must not be masked."
        //   391: astore          8
        //   393: goto            379
        //   396: aload_0        
        //   397: getfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   400: ldc2_w          127
        //   403: lcmp           
        //   404: ifne            342
        //   407: aload_0        
        //   408: aload_0        
        //   409: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //   412: invokeinterface okio/BufferedSource.readLong:()J
        //   417: putfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   420: aload_0        
        //   421: getfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   424: lconst_0       
        //   425: lcmp           
        //   426: iflt            475
        //   429: iconst_1       
        //   430: istore          4
        //   432: iload           4
        //   434: ifne            342
        //   437: new             Ljava/net/ProtocolException;
        //   440: dup            
        //   441: new             Ljava/lang/StringBuilder;
        //   444: dup            
        //   445: invokespecial   java/lang/StringBuilder.<init>:()V
        //   448: ldc             "Frame length 0x"
        //   450: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   453: aload_0        
        //   454: getfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   457: invokestatic    java/lang/Long.toHexString:(J)Ljava/lang/String;
        //   460: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   463: ldc             " > 0x7FFFFFFFFFFFFFFF"
        //   465: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   468: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   471: invokespecial   java/net/ProtocolException.<init>:(Ljava/lang/String;)V
        //   474: athrow         
        //   475: iconst_0       
        //   476: istore          4
        //   478: goto            432
        //   481: aload_0        
        //   482: getfield        okhttp3/internal/ws/WebSocketReader.frameLength:J
        //   485: ldc2_w          125
        //   488: lcmp           
        //   489: ifgt            510
        //   492: iload_1        
        //   493: istore          4
        //   495: iload           4
        //   497: ifne            354
        //   500: new             Ljava/net/ProtocolException;
        //   503: dup            
        //   504: ldc             "Control frame must be less than 125B."
        //   506: invokespecial   java/net/ProtocolException.<init>:(Ljava/lang/String;)V
        //   509: athrow         
        //   510: iconst_0       
        //   511: istore          4
        //   513: goto            495
        //   516: aload_0        
        //   517: getfield        okhttp3/internal/ws/WebSocketReader.source:Lokio/BufferedSource;
        //   520: aload_0        
        //   521: getfield        okhttp3/internal/ws/WebSocketReader.maskKey:[B
        //   524: invokeinterface okio/BufferedSource.readFully:([B)V
        //   529: goto            361
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  35     46     180    202    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicaland:boolean(logicalnot:boolean(var_7_8E:boolean), cmpeq:boolean(var_5_34:int, ldc:int(0)))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
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
    
    private void readMessage(final Buffer buffer) throws IOException {
        while (!this.closed) {
            if (this.frameBytesRead == this.frameLength) {
                if (this.isFinalFrame) {
                    return;
                }
                this.readUntilNonControlFrame();
                if (this.opcode != 0) {
                    throw new ProtocolException("Expected continuation opcode. Got: " + Integer.toHexString(this.opcode));
                }
                if (this.isFinalFrame && this.frameLength == 0L) {
                    return;
                }
            }
            final long a = this.frameLength - this.frameBytesRead;
            long read;
            if (!this.isMasked) {
                if ((read = this.source.read(buffer, a)) == -1L) {
                    throw new EOFException();
                }
            }
            else {
                read = this.source.read(this.maskBuffer, 0, (int)Math.min(a, this.maskBuffer.length));
                if (read == -1L) {
                    throw new EOFException();
                }
                WebSocketProtocol.toggleMask(this.maskBuffer, read, this.maskKey, this.frameBytesRead);
                buffer.write(this.maskBuffer, 0, (int)read);
            }
            this.frameBytesRead += read;
        }
        throw new IOException("closed");
    }
    
    private void readMessageFrame() throws IOException {
        final int opcode = this.opcode;
        if (opcode != 1 && opcode != 2) {
            throw new ProtocolException("Unknown opcode: " + Integer.toHexString(opcode));
        }
        final Buffer buffer = new Buffer();
        this.readMessage(buffer);
        if (opcode != 1) {
            this.frameCallback.onReadMessage(buffer.readByteString());
        }
        else {
            this.frameCallback.onReadMessage(buffer.readUtf8());
        }
    }
    
    void processNextFrame() throws IOException {
        this.readHeader();
        if (!this.isControlFrame) {
            this.readMessageFrame();
        }
        else {
            this.readControlFrame();
        }
    }
    
    void readUntilNonControlFrame() throws IOException {
        while (!this.closed) {
            this.readHeader();
            if (!this.isControlFrame) {
                break;
            }
            this.readControlFrame();
        }
    }
    
    public interface FrameCallback
    {
        void onReadClose(final int p0, final String p1);
        
        void onReadMessage(final String p0) throws IOException;
        
        void onReadMessage(final ByteString p0) throws IOException;
        
        void onReadPing(final ByteString p0);
        
        void onReadPong(final ByteString p0);
    }
}
