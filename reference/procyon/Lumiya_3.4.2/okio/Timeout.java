// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class Timeout
{
    public static final Timeout NONE;
    private long deadlineNanoTime;
    private boolean hasDeadline;
    private long timeoutNanos;
    
    static {
        NONE = new Timeout() {
            @Override
            public Timeout deadlineNanoTime(final long n) {
                return this;
            }
            
            @Override
            public void throwIfReached() throws IOException {
            }
            
            @Override
            public Timeout timeout(final long n, final TimeUnit timeUnit) {
                return this;
            }
        };
    }
    
    public Timeout clearDeadline() {
        this.hasDeadline = false;
        return this;
    }
    
    public Timeout clearTimeout() {
        this.timeoutNanos = 0L;
        return this;
    }
    
    public final Timeout deadline(final long n, final TimeUnit timeUnit) {
        int n2;
        if (n > 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("duration <= 0: " + n);
        }
        if (timeUnit != null) {
            return this.deadlineNanoTime(System.nanoTime() + timeUnit.toNanos(n));
        }
        throw new IllegalArgumentException("unit == null");
    }
    
    public long deadlineNanoTime() {
        if (this.hasDeadline) {
            return this.deadlineNanoTime;
        }
        throw new IllegalStateException("No deadline");
    }
    
    public Timeout deadlineNanoTime(final long deadlineNanoTime) {
        this.hasDeadline = true;
        this.deadlineNanoTime = deadlineNanoTime;
        return this;
    }
    
    public boolean hasDeadline() {
        return this.hasDeadline;
    }
    
    public void throwIfReached() throws IOException {
        boolean b = false;
        if (!Thread.interrupted()) {
            if (this.hasDeadline) {
                if (this.deadlineNanoTime - System.nanoTime() > 0L) {
                    b = true;
                }
                if (!b) {
                    throw new InterruptedIOException("deadline reached");
                }
            }
            return;
        }
        throw new InterruptedIOException("thread interrupted");
    }
    
    public Timeout timeout(final long n, final TimeUnit timeUnit) {
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("timeout < 0: " + n);
        }
        if (timeUnit != null) {
            this.timeoutNanos = timeUnit.toNanos(n);
            return this;
        }
        throw new IllegalArgumentException("unit == null");
    }
    
    public long timeoutNanos() {
        return this.timeoutNanos;
    }
    
    public final void waitUntilNotified(final Object p0) throws InterruptedIOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: lstore_2       
        //     2: aload_0        
        //     3: invokevirtual   okio/Timeout.hasDeadline:()Z
        //     6: istore          4
        //     8: aload_0        
        //     9: invokevirtual   okio/Timeout.timeoutNanos:()J
        //    12: lstore          5
        //    14: iload           4
        //    16: ifeq            115
        //    19: invokestatic    java/lang/System.nanoTime:()J
        //    22: lstore          7
        //    24: iload           4
        //    26: ifne            127
        //    29: iload           4
        //    31: ifne            151
        //    34: lload           5
        //    36: lconst_0       
        //    37: lcmp           
        //    38: ifgt            167
        //    41: iconst_1       
        //    42: istore          9
        //    44: iload           9
        //    46: ifne            77
        //    49: lload           5
        //    51: ldc2_w          1000000
        //    54: ldiv           
        //    55: lstore_2       
        //    56: aload_1        
        //    57: lload_2        
        //    58: lload           5
        //    60: lload_2        
        //    61: ldc2_w          1000000
        //    64: lmul           
        //    65: lsub           
        //    66: l2i            
        //    67: invokevirtual   java/lang/Object.wait:(JI)V
        //    70: invokestatic    java/lang/System.nanoTime:()J
        //    73: lload           7
        //    75: lsub           
        //    76: lstore_2       
        //    77: lload_2        
        //    78: lload           5
        //    80: lcmp           
        //    81: ifge            173
        //    84: iconst_1       
        //    85: istore          9
        //    87: iload           9
        //    89: ifne            179
        //    92: new             Ljava/io/InterruptedIOException;
        //    95: astore_1       
        //    96: aload_1        
        //    97: ldc             "timeout"
        //    99: invokespecial   java/io/InterruptedIOException.<init>:(Ljava/lang/String;)V
        //   102: aload_1        
        //   103: athrow         
        //   104: astore_1       
        //   105: new             Ljava/io/InterruptedIOException;
        //   108: dup            
        //   109: ldc             "interrupted"
        //   111: invokespecial   java/io/InterruptedIOException.<init>:(Ljava/lang/String;)V
        //   114: athrow         
        //   115: lload           5
        //   117: lconst_0       
        //   118: lcmp           
        //   119: ifne            19
        //   122: aload_1        
        //   123: invokevirtual   java/lang/Object.wait:()V
        //   126: return         
        //   127: lload           5
        //   129: lconst_0       
        //   130: lcmp           
        //   131: ifeq            29
        //   134: lload           5
        //   136: aload_0        
        //   137: invokevirtual   okio/Timeout.deadlineNanoTime:()J
        //   140: lload           7
        //   142: lsub           
        //   143: invokestatic    java/lang/Math.min:(JJ)J
        //   146: lstore          5
        //   148: goto            34
        //   151: aload_0        
        //   152: invokevirtual   okio/Timeout.deadlineNanoTime:()J
        //   155: lstore          5
        //   157: lload           5
        //   159: lload           7
        //   161: lsub           
        //   162: lstore          5
        //   164: goto            34
        //   167: iconst_0       
        //   168: istore          9
        //   170: goto            44
        //   173: iconst_0       
        //   174: istore          9
        //   176: goto            87
        //   179: return         
        //    Exceptions:
        //  throws java.io.InterruptedIOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  2      14     104    115    Ljava/lang/InterruptedException;
        //  19     24     104    115    Ljava/lang/InterruptedException;
        //  49     77     104    115    Ljava/lang/InterruptedException;
        //  92     104    104    115    Ljava/lang/InterruptedException;
        //  122    126    104    115    Ljava/lang/InterruptedException;
        //  134    148    104    115    Ljava/lang/InterruptedException;
        //  151    157    104    115    Ljava/lang/InterruptedException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(__lcmp:long(var_5_0C:long, ldc:long(0L)), ldc:int(0))
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
}
