// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.logging.Level;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.concurrent.GuardedBy;
import java.util.logging.Logger;
import java.util.concurrent.Executor;

final class SerializingExecutor implements Executor
{
    private static final Logger log;
    private final Executor executor;
    private final Object internalLock;
    @GuardedBy("internalLock")
    private boolean isWorkerRunning;
    @GuardedBy("internalLock")
    private final Deque<Runnable> queue;
    @GuardedBy("internalLock")
    private int suspensions;
    
    static {
        log = Logger.getLogger(SerializingExecutor.class.getName());
    }
    
    public SerializingExecutor(final Executor executor) {
        this.queue = new ArrayDeque<Runnable>();
        this.isWorkerRunning = false;
        this.suspensions = 0;
        this.internalLock = new Object();
        this.executor = Preconditions.checkNotNull(executor);
    }
    
    private void startQueueWorker() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/google/common/util/concurrent/SerializingExecutor.internalLock:Ljava/lang/Object;
        //     4: astore_1       
        //     5: aload_1        
        //     6: monitorenter   
        //     7: aload_0        
        //     8: getfield        com/google/common/util/concurrent/SerializingExecutor.queue:Ljava/util/Deque;
        //    11: invokeinterface java/util/Deque.peek:()Ljava/lang/Object;
        //    16: ifnull          63
        //    19: aload_0        
        //    20: getfield        com/google/common/util/concurrent/SerializingExecutor.suspensions:I
        //    23: ifgt            66
        //    26: aload_0        
        //    27: getfield        com/google/common/util/concurrent/SerializingExecutor.isWorkerRunning:Z
        //    30: ifne            69
        //    33: aload_0        
        //    34: iconst_1       
        //    35: putfield        com/google/common/util/concurrent/SerializingExecutor.isWorkerRunning:Z
        //    38: aload_1        
        //    39: monitorexit    
        //    40: aload_0        
        //    41: getfield        com/google/common/util/concurrent/SerializingExecutor.executor:Ljava/util/concurrent/Executor;
        //    44: astore_2       
        //    45: new             Lcom/google/common/util/concurrent/SerializingExecutor$QueueWorker;
        //    48: astore_1       
        //    49: aload_1        
        //    50: aload_0        
        //    51: aconst_null    
        //    52: invokespecial   com/google/common/util/concurrent/SerializingExecutor$QueueWorker.<init>:(Lcom/google/common/util/concurrent/SerializingExecutor;Lcom/google/common/util/concurrent/SerializingExecutor$1;)V
        //    55: aload_2        
        //    56: aload_1        
        //    57: invokeinterface java/util/concurrent/Executor.execute:(Ljava/lang/Runnable;)V
        //    62: return         
        //    63: aload_1        
        //    64: monitorexit    
        //    65: return         
        //    66: aload_1        
        //    67: monitorexit    
        //    68: return         
        //    69: aload_1        
        //    70: monitorexit    
        //    71: return         
        //    72: astore_2       
        //    73: aload_1        
        //    74: monitorexit    
        //    75: aload_2        
        //    76: athrow         
        //    77: astore_1       
        //    78: aload_0        
        //    79: getfield        com/google/common/util/concurrent/SerializingExecutor.internalLock:Ljava/lang/Object;
        //    82: astore_2       
        //    83: aload_2        
        //    84: monitorenter   
        //    85: aload_0        
        //    86: iconst_0       
        //    87: putfield        com/google/common/util/concurrent/SerializingExecutor.isWorkerRunning:Z
        //    90: aload_2        
        //    91: monitorexit    
        //    92: aload_1        
        //    93: athrow         
        //    94: astore_1       
        //    95: aload_2        
        //    96: monitorexit    
        //    97: aload_1        
        //    98: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  7      40     72     77     Any
        //  40     62     77     99     Any
        //  63     65     72     77     Any
        //  66     68     72     77     Any
        //  69     71     72     77     Any
        //  73     75     72     77     Any
        //  85     92     94     99     Any
        //  95     97     94     99     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0063:
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
    public void execute(final Runnable runnable) {
        synchronized (this.internalLock) {
            this.queue.add(runnable);
            monitorexit(this.internalLock);
            this.startQueueWorker();
        }
    }
    
    public void executeFirst(final Runnable runnable) {
        synchronized (this.internalLock) {
            this.queue.addFirst(runnable);
            monitorexit(this.internalLock);
            this.startQueueWorker();
        }
    }
    
    public void resume() {
        boolean b = false;
        synchronized (this.internalLock) {
            if (this.suspensions > 0) {
                b = true;
            }
            Preconditions.checkState(b);
            --this.suspensions;
            monitorexit(this.internalLock);
            this.startQueueWorker();
        }
    }
    
    public void suspend() {
        synchronized (this.internalLock) {
            ++this.suspensions;
        }
    }
    
    private final class QueueWorker implements Runnable
    {
        private void workOnQueue() {
            while (true) {
                final Object access$100 = SerializingExecutor.this.internalLock;
                synchronized (access$100) {
                    Runnable obj;
                    if (SerializingExecutor.this.suspensions != 0) {
                        obj = null;
                    }
                    else {
                        obj = SerializingExecutor.this.queue.poll();
                    }
                    if (obj == null) {
                        SerializingExecutor.this.isWorkerRunning = false;
                        return;
                    }
                    monitorexit(access$100);
                    try {
                        obj.run();
                    }
                    catch (final RuntimeException access$100) {
                        SerializingExecutor.log.log(Level.SEVERE, "Exception while executing runnable " + obj, (Throwable)access$100);
                    }
                }
            }
        }
        
        @Override
        public void run() {
            try {
                this.workOnQueue();
            }
            catch (final Error error) {
                synchronized (SerializingExecutor.this.internalLock) {
                    SerializingExecutor.this.isWorkerRunning = false;
                    monitorexit(SerializingExecutor.this.internalLock);
                    throw error;
                }
            }
        }
    }
}
