// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.Iterator;
import java.util.logging.Level;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Queue;
import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

final class ListenerCallQueue<L> implements Runnable
{
    private static final Logger logger;
    private final Executor executor;
    @GuardedBy("this")
    private boolean isThreadScheduled;
    private final L listener;
    @GuardedBy("this")
    private final Queue<Callback<L>> waitQueue;
    
    static {
        logger = Logger.getLogger(ListenerCallQueue.class.getName());
    }
    
    ListenerCallQueue(final L l, final Executor executor) {
        this.waitQueue = (Queue<Callback<L>>)Queues.newArrayDeque();
        this.listener = Preconditions.checkNotNull(l);
        this.executor = Preconditions.checkNotNull(executor);
    }
    
    void add(final Callback<L> callback) {
        synchronized (this) {
            this.waitQueue.add(callback);
        }
    }
    
    void execute() {
        while (true) {
            int n = 0;
            synchronized (this) {
                if (!this.isThreadScheduled) {
                    this.isThreadScheduled = true;
                    n = 1;
                }
                monitorexit(this);
                if (n == 0) {
                    return;
                }
            }
            try {
                this.executor.execute(this);
            }
            catch (final RuntimeException thrown) {
                synchronized (this) {
                    this.isThreadScheduled = false;
                    monitorexit(this);
                    ListenerCallQueue.logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, thrown);
                    throw thrown;
                }
            }
        }
    }
    
    @Override
    public void run() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: iconst_1       
        //     3: istore_2       
        //     4: iload_2        
        //     5: istore_3       
        //     6: aload_0        
        //     7: monitorenter   
        //     8: iload_1        
        //     9: istore          4
        //    11: aload_0        
        //    12: getfield        com/google/common/util/concurrent/ListenerCallQueue.isThreadScheduled:Z
        //    15: invokestatic    com/google/common/base/Preconditions.checkState:(Z)V
        //    18: iload_1        
        //    19: istore          4
        //    21: aload_0        
        //    22: getfield        com/google/common/util/concurrent/ListenerCallQueue.waitQueue:Ljava/util/Queue;
        //    25: invokeinterface java/util/Queue.poll:()Ljava/lang/Object;
        //    30: checkcast       Lcom/google/common/util/concurrent/ListenerCallQueue$Callback;
        //    33: astore          5
        //    35: aload           5
        //    37: ifnull          142
        //    40: iload_1        
        //    41: istore          4
        //    43: aload_0        
        //    44: monitorexit    
        //    45: iload_2        
        //    46: istore_3       
        //    47: aload           5
        //    49: aload_0        
        //    50: getfield        com/google/common/util/concurrent/ListenerCallQueue.listener:Ljava/lang/Object;
        //    53: invokevirtual   com/google/common/util/concurrent/ListenerCallQueue$Callback.call:(Ljava/lang/Object;)V
        //    56: goto            4
        //    59: astore          6
        //    61: iload_2        
        //    62: istore_3       
        //    63: getstatic       com/google/common/util/concurrent/ListenerCallQueue.logger:Ljava/util/logging/Logger;
        //    66: astore          7
        //    68: iload_2        
        //    69: istore_3       
        //    70: getstatic       java/util/logging/Level.SEVERE:Ljava/util/logging/Level;
        //    73: astore          8
        //    75: iload_2        
        //    76: istore_3       
        //    77: new             Ljava/lang/StringBuilder;
        //    80: astore          9
        //    82: iload_2        
        //    83: istore_3       
        //    84: aload           9
        //    86: invokespecial   java/lang/StringBuilder.<init>:()V
        //    89: iload_2        
        //    90: istore_3       
        //    91: aload           7
        //    93: aload           8
        //    95: aload           9
        //    97: ldc             "Exception while executing callback: "
        //    99: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   102: aload_0        
        //   103: getfield        com/google/common/util/concurrent/ListenerCallQueue.listener:Ljava/lang/Object;
        //   106: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   109: ldc             "."
        //   111: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   114: aload           5
        //   116: invokestatic    com/google/common/util/concurrent/ListenerCallQueue$Callback.access$000:(Lcom/google/common/util/concurrent/ListenerCallQueue$Callback;)Ljava/lang/String;
        //   119: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   122: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   125: aload           6
        //   127: invokevirtual   java/util/logging/Logger.log:(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   130: goto            4
        //   133: astore          6
        //   135: iload_3        
        //   136: ifne            166
        //   139: aload           6
        //   141: athrow         
        //   142: iload_1        
        //   143: istore          4
        //   145: aload_0        
        //   146: iconst_0       
        //   147: putfield        com/google/common/util/concurrent/ListenerCallQueue.isThreadScheduled:Z
        //   150: aload_0        
        //   151: monitorexit    
        //   152: return         
        //   153: astore          6
        //   155: iload           4
        //   157: istore_3       
        //   158: iload_3        
        //   159: istore          4
        //   161: aload_0        
        //   162: monitorexit    
        //   163: aload           6
        //   165: athrow         
        //   166: aload_0        
        //   167: monitorenter   
        //   168: aload_0        
        //   169: iconst_0       
        //   170: putfield        com/google/common/util/concurrent/ListenerCallQueue.isThreadScheduled:Z
        //   173: aload_0        
        //   174: monitorexit    
        //   175: goto            139
        //   178: astore          6
        //   180: aload_0        
        //   181: monitorexit    
        //   182: aload           6
        //   184: athrow         
        //   185: astore          6
        //   187: iconst_0       
        //   188: istore_3       
        //   189: goto            158
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  6      8      133    185    Any
        //  11     18     153    158    Any
        //  21     35     153    158    Any
        //  43     45     153    158    Any
        //  47     56     59     133    Ljava/lang/RuntimeException;
        //  47     56     133    185    Any
        //  63     68     133    185    Any
        //  70     75     133    185    Any
        //  77     82     133    185    Any
        //  84     89     133    185    Any
        //  91     130    133    185    Any
        //  145    150    153    158    Any
        //  150    152    185    192    Any
        //  161    163    153    158    Any
        //  163    166    133    185    Any
        //  168    175    178    185    Any
        //  180    182    178    185    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0139:
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
    
    abstract static class Callback<L>
    {
        private final String methodCall;
        
        Callback(final String methodCall) {
            this.methodCall = methodCall;
        }
        
        abstract void call(final L p0);
        
        void enqueueOn(final Iterable<ListenerCallQueue<L>> iterable) {
            final Iterator<ListenerCallQueue<L>> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                iterator.next().add(this);
            }
        }
    }
}
