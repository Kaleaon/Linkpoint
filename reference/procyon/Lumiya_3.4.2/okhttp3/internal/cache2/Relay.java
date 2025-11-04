// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.cache2;

import okio.Timeout;
import java.io.Closeable;
import okhttp3.internal.Util;
import java.io.IOException;
import java.io.File;
import okio.Source;
import java.io.RandomAccessFile;
import okio.Buffer;
import okio.ByteString;

final class Relay
{
    private static final long FILE_HEADER_SIZE = 32L;
    static final ByteString PREFIX_CLEAN;
    static final ByteString PREFIX_DIRTY;
    private static final int SOURCE_FILE = 2;
    private static final int SOURCE_UPSTREAM = 1;
    final Buffer buffer;
    final long bufferMaxSize;
    boolean complete;
    RandomAccessFile file;
    private final ByteString metadata;
    int sourceCount;
    Source upstream;
    final Buffer upstreamBuffer;
    long upstreamPos;
    Thread upstreamReader;
    
    static {
        PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
        PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
    }
    
    private Relay(final RandomAccessFile file, final Source upstream, final long upstreamPos, final ByteString metadata, final long bufferMaxSize) {
        this.upstreamBuffer = new Buffer();
        this.buffer = new Buffer();
        this.file = file;
        this.upstream = upstream;
        this.complete = (upstream == null);
        this.upstreamPos = upstreamPos;
        this.metadata = metadata;
        this.bufferMaxSize = bufferMaxSize;
    }
    
    public static Relay edit(final File file, final Source source, final ByteString byteString, final long n) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        final Relay relay = new Relay(randomAccessFile, source, 0L, byteString, n);
        randomAccessFile.setLength(0L);
        relay.writeHeader(Relay.PREFIX_DIRTY, -1L, -1L);
        return relay;
    }
    
    public static Relay read(final File file) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        final FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
        final Buffer buffer = new Buffer();
        fileOperator.read(0L, buffer, 32L);
        if (buffer.readByteString(Relay.PREFIX_CLEAN.size()).equals(Relay.PREFIX_CLEAN)) {
            final long long1 = buffer.readLong();
            final long long2 = buffer.readLong();
            final Buffer buffer2 = new Buffer();
            fileOperator.read(32L + long1, buffer2, long2);
            return new Relay(randomAccessFile, null, long1, buffer2.readByteString(), 0L);
        }
        throw new IOException("unreadable cache file");
    }
    
    private void writeHeader(final ByteString byteString, final long n, final long n2) throws IOException {
        final Buffer buffer = new Buffer();
        buffer.write(byteString);
        buffer.writeLong(n);
        buffer.writeLong(n2);
        if (buffer.size() != 32L) {
            throw new IllegalArgumentException();
        }
        new FileOperator(this.file.getChannel()).write(0L, buffer, 32L);
    }
    
    private void writeMetadata(final long n) throws IOException {
        final Buffer buffer = new Buffer();
        buffer.write(this.metadata);
        new FileOperator(this.file.getChannel()).write(32L + n, buffer, this.metadata.size());
    }
    
    void commit(final long n) throws IOException {
        this.writeMetadata(n);
        this.file.getChannel().force(false);
        this.writeHeader(Relay.PREFIX_CLEAN, n, this.metadata.size());
        this.file.getChannel().force(false);
        synchronized (this) {
            this.complete = true;
            monitorexit(this);
            Util.closeQuietly(this.upstream);
            this.upstream = null;
        }
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
                ++this.sourceCount;
                monitorexit(this);
                return new RelaySource();
            }
            return null;
        }
    }
    
    class RelaySource implements Source
    {
        private FileOperator fileOperator;
        private long sourcePos;
        private final Timeout timeout;
        
        RelaySource() {
            this.timeout = new Timeout();
            this.fileOperator = new FileOperator(Relay.this.file.getChannel());
        }
        
        @Override
        public void close() throws IOException {
            RandomAccessFile file = null;
            if (this.fileOperator == null) {
                return;
            }
            while (true) {
                this.fileOperator = null;
                Label_0078: {
                    synchronized (Relay.this) {
                        final Relay this$0 = Relay.this;
                        --this$0.sourceCount;
                        if (Relay.this.sourceCount == 0) {
                            file = Relay.this.file;
                            Relay.this.file = null;
                        }
                        monitorexit(Relay.this);
                        if (file == null) {
                            return;
                        }
                        break Label_0078;
                    }
                }
                final Closeable closeable;
                Util.closeQuietly(closeable);
            }
        }
        
        @Override
        public long read(final Buffer p0, final long p1) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        okhttp3/internal/cache2/Relay$RelaySource.fileOperator:Lokhttp3/internal/cache2/FileOperator;
            //     4: ifnull          158
            //     7: aload_0        
            //     8: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    11: astore          4
            //    13: aload           4
            //    15: monitorenter   
            //    16: aload_0        
            //    17: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //    20: lstore          5
            //    22: aload_0        
            //    23: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    26: getfield        okhttp3/internal/cache2/Relay.upstreamPos:J
            //    29: lstore          7
            //    31: lload           5
            //    33: lload           7
            //    35: lcmp           
            //    36: ifne            195
            //    39: aload_0        
            //    40: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    43: getfield        okhttp3/internal/cache2/Relay.complete:Z
            //    46: ifne            168
            //    49: aload_0        
            //    50: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    53: getfield        okhttp3/internal/cache2/Relay.upstreamReader:Ljava/lang/Thread;
            //    56: ifnonnull       175
            //    59: aload_0        
            //    60: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    63: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
            //    66: putfield        okhttp3/internal/cache2/Relay.upstreamReader:Ljava/lang/Thread;
            //    69: iconst_1       
            //    70: istore          9
            //    72: aload           4
            //    74: monitorexit    
            //    75: iload           9
            //    77: iconst_2       
            //    78: if_icmpeq       290
            //    81: aload_0        
            //    82: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    85: getfield        okhttp3/internal/cache2/Relay.upstream:Lokio/Source;
            //    88: aload_0        
            //    89: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    92: getfield        okhttp3/internal/cache2/Relay.upstreamBuffer:Lokio/Buffer;
            //    95: aload_0        
            //    96: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //    99: getfield        okhttp3/internal/cache2/Relay.bufferMaxSize:J
            //   102: invokeinterface okio/Source.read:(Lokio/Buffer;J)J
            //   107: lstore          5
            //   109: lload           5
            //   111: ldc2_w          -1
            //   114: lcmp           
            //   115: ifne            337
            //   118: aload_0        
            //   119: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   122: lload           7
            //   124: invokevirtual   okhttp3/internal/cache2/Relay.commit:(J)V
            //   127: aload_0        
            //   128: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   131: astore          4
            //   133: aload           4
            //   135: monitorenter   
            //   136: aload_0        
            //   137: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   140: aconst_null    
            //   141: putfield        okhttp3/internal/cache2/Relay.upstreamReader:Ljava/lang/Thread;
            //   144: aload_0        
            //   145: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   148: invokevirtual   java/lang/Object.notifyAll:()V
            //   151: aload           4
            //   153: monitorexit    
            //   154: ldc2_w          -1
            //   157: lreturn        
            //   158: new             Ljava/lang/IllegalStateException;
            //   161: dup            
            //   162: ldc             "closed"
            //   164: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
            //   167: athrow         
            //   168: aload           4
            //   170: monitorexit    
            //   171: ldc2_w          -1
            //   174: lreturn        
            //   175: aload_0        
            //   176: getfield        okhttp3/internal/cache2/Relay$RelaySource.timeout:Lokio/Timeout;
            //   179: aload_0        
            //   180: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   183: invokevirtual   okio/Timeout.waitUntilNotified:(Ljava/lang/Object;)V
            //   186: goto            16
            //   189: astore_1       
            //   190: aload           4
            //   192: monitorexit    
            //   193: aload_1        
            //   194: athrow         
            //   195: lload           7
            //   197: aload_0        
            //   198: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   201: getfield        okhttp3/internal/cache2/Relay.buffer:Lokio/Buffer;
            //   204: invokevirtual   okio/Buffer.size:()J
            //   207: lsub           
            //   208: lstore          5
            //   210: aload_0        
            //   211: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   214: lload           5
            //   216: lcmp           
            //   217: iflt            237
            //   220: iconst_1       
            //   221: istore          9
            //   223: iload           9
            //   225: ifne            243
            //   228: iconst_2       
            //   229: istore          9
            //   231: aload           4
            //   233: monitorexit    
            //   234: goto            75
            //   237: iconst_0       
            //   238: istore          9
            //   240: goto            223
            //   243: lload_2        
            //   244: lload           7
            //   246: aload_0        
            //   247: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   250: lsub           
            //   251: invokestatic    java/lang/Math.min:(JJ)J
            //   254: lstore_2       
            //   255: aload_0        
            //   256: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   259: getfield        okhttp3/internal/cache2/Relay.buffer:Lokio/Buffer;
            //   262: aload_1        
            //   263: aload_0        
            //   264: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   267: lload           5
            //   269: lsub           
            //   270: lload_2        
            //   271: invokevirtual   okio/Buffer.copyTo:(Lokio/Buffer;JJ)Lokio/Buffer;
            //   274: pop            
            //   275: aload_0        
            //   276: aload_0        
            //   277: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   280: lload_2        
            //   281: ladd           
            //   282: putfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   285: aload           4
            //   287: monitorexit    
            //   288: lload_2        
            //   289: lreturn        
            //   290: lload_2        
            //   291: lload           7
            //   293: aload_0        
            //   294: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   297: lsub           
            //   298: invokestatic    java/lang/Math.min:(JJ)J
            //   301: lstore_2       
            //   302: aload_0        
            //   303: getfield        okhttp3/internal/cache2/Relay$RelaySource.fileOperator:Lokhttp3/internal/cache2/FileOperator;
            //   306: ldc2_w          32
            //   309: aload_0        
            //   310: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   313: ladd           
            //   314: aload_1        
            //   315: lload_2        
            //   316: invokevirtual   okhttp3/internal/cache2/FileOperator.read:(JLokio/Buffer;J)V
            //   319: aload_0        
            //   320: aload_0        
            //   321: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   324: lload_2        
            //   325: ladd           
            //   326: putfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   329: lload_2        
            //   330: lreturn        
            //   331: astore_1       
            //   332: aload           4
            //   334: monitorexit    
            //   335: aload_1        
            //   336: athrow         
            //   337: lload           5
            //   339: lload_2        
            //   340: invokestatic    java/lang/Math.min:(JJ)J
            //   343: lstore_2       
            //   344: aload_0        
            //   345: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   348: getfield        okhttp3/internal/cache2/Relay.upstreamBuffer:Lokio/Buffer;
            //   351: aload_1        
            //   352: lconst_0       
            //   353: lload_2        
            //   354: invokevirtual   okio/Buffer.copyTo:(Lokio/Buffer;JJ)Lokio/Buffer;
            //   357: pop            
            //   358: aload_0        
            //   359: aload_0        
            //   360: getfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   363: lload_2        
            //   364: ladd           
            //   365: putfield        okhttp3/internal/cache2/Relay$RelaySource.sourcePos:J
            //   368: aload_0        
            //   369: getfield        okhttp3/internal/cache2/Relay$RelaySource.fileOperator:Lokhttp3/internal/cache2/FileOperator;
            //   372: ldc2_w          32
            //   375: lload           7
            //   377: ladd           
            //   378: aload_0        
            //   379: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   382: getfield        okhttp3/internal/cache2/Relay.upstreamBuffer:Lokio/Buffer;
            //   385: invokevirtual   okio/Buffer.clone:()Lokio/Buffer;
            //   388: lload           5
            //   390: invokevirtual   okhttp3/internal/cache2/FileOperator.write:(JLokio/Buffer;J)V
            //   393: aload_0        
            //   394: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   397: astore_1       
            //   398: aload_1        
            //   399: monitorenter   
            //   400: aload_0        
            //   401: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   404: getfield        okhttp3/internal/cache2/Relay.buffer:Lokio/Buffer;
            //   407: aload_0        
            //   408: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   411: getfield        okhttp3/internal/cache2/Relay.upstreamBuffer:Lokio/Buffer;
            //   414: lload           5
            //   416: invokevirtual   okio/Buffer.write:(Lokio/Buffer;J)V
            //   419: aload_0        
            //   420: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   423: getfield        okhttp3/internal/cache2/Relay.buffer:Lokio/Buffer;
            //   426: invokevirtual   okio/Buffer.size:()J
            //   429: aload_0        
            //   430: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   433: getfield        okhttp3/internal/cache2/Relay.bufferMaxSize:J
            //   436: lcmp           
            //   437: ifgt            523
            //   440: iconst_1       
            //   441: istore          9
            //   443: iload           9
            //   445: ifne            476
            //   448: aload_0        
            //   449: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   452: getfield        okhttp3/internal/cache2/Relay.buffer:Lokio/Buffer;
            //   455: aload_0        
            //   456: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   459: getfield        okhttp3/internal/cache2/Relay.buffer:Lokio/Buffer;
            //   462: invokevirtual   okio/Buffer.size:()J
            //   465: aload_0        
            //   466: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   469: getfield        okhttp3/internal/cache2/Relay.bufferMaxSize:J
            //   472: lsub           
            //   473: invokevirtual   okio/Buffer.skip:(J)V
            //   476: aload_0        
            //   477: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   480: astore          4
            //   482: aload           4
            //   484: aload           4
            //   486: getfield        okhttp3/internal/cache2/Relay.upstreamPos:J
            //   489: lload           5
            //   491: ladd           
            //   492: putfield        okhttp3/internal/cache2/Relay.upstreamPos:J
            //   495: aload_1        
            //   496: monitorexit    
            //   497: aload_0        
            //   498: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   501: astore_1       
            //   502: aload_1        
            //   503: monitorenter   
            //   504: aload_0        
            //   505: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   508: aconst_null    
            //   509: putfield        okhttp3/internal/cache2/Relay.upstreamReader:Ljava/lang/Thread;
            //   512: aload_0        
            //   513: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   516: invokevirtual   java/lang/Object.notifyAll:()V
            //   519: aload_1        
            //   520: monitorexit    
            //   521: lload_2        
            //   522: lreturn        
            //   523: iconst_0       
            //   524: istore          9
            //   526: goto            443
            //   529: astore          4
            //   531: aload_1        
            //   532: monitorexit    
            //   533: aload           4
            //   535: athrow         
            //   536: astore          4
            //   538: aload_0        
            //   539: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   542: astore_1       
            //   543: aload_1        
            //   544: monitorenter   
            //   545: aload_0        
            //   546: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   549: aconst_null    
            //   550: putfield        okhttp3/internal/cache2/Relay.upstreamReader:Ljava/lang/Thread;
            //   553: aload_0        
            //   554: getfield        okhttp3/internal/cache2/Relay$RelaySource.this$0:Lokhttp3/internal/cache2/Relay;
            //   557: invokevirtual   java/lang/Object.notifyAll:()V
            //   560: aload_1        
            //   561: monitorexit    
            //   562: aload           4
            //   564: athrow         
            //   565: astore          4
            //   567: aload_1        
            //   568: monitorexit    
            //   569: aload           4
            //   571: athrow         
            //   572: astore          4
            //   574: aload_1        
            //   575: monitorexit    
            //   576: aload           4
            //   578: athrow         
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  16     31     189    195    Any
            //  39     69     189    195    Any
            //  72     75     189    195    Any
            //  81     109    536    579    Any
            //  118    127    536    579    Any
            //  136    154    331    337    Any
            //  168    171    189    195    Any
            //  175    186    189    195    Any
            //  190    193    189    195    Any
            //  195    220    189    195    Any
            //  231    234    189    195    Any
            //  243    288    189    195    Any
            //  332    335    331    337    Any
            //  337    400    536    579    Any
            //  400    440    529    536    Any
            //  448    476    529    536    Any
            //  476    497    529    536    Any
            //  504    521    565    572    Any
            //  531    533    529    536    Any
            //  533    536    536    579    Any
            //  545    562    572    579    Any
            //  567    569    565    572    Any
            //  574    576    572    579    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0158:
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
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
        public Timeout timeout() {
            return this.timeout;
        }
    }
}
