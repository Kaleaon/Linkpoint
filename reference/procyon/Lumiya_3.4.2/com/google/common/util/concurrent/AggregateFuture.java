// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import javax.annotation.Nullable;
import java.util.logging.Level;
import java.util.concurrent.Future;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import com.google.common.collect.ImmutableCollection;
import java.util.Set;
import java.util.logging.Logger;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AggregateFuture<InputT, OutputT> extends TrustedFuture<OutputT>
{
    private static final Logger logger;
    private RunningState runningState;
    
    static {
        logger = Logger.getLogger(AggregateFuture.class.getName());
    }
    
    private static boolean addCausalChain(final Set<Throwable> set, Throwable cause) {
        while (cause != null) {
            if (!set.add(cause)) {
                return false;
            }
            cause = cause.getCause();
        }
        return true;
    }
    
    @Override
    public final boolean cancel(final boolean b) {
        ImmutableCollection access$000 = null;
        boolean b2 = false;
        final RunningState runningState = this.runningState;
        if (runningState != null) {
            access$000 = runningState.futures;
        }
        final boolean cancel = super.cancel(b);
        if (access$000 != null) {
            b2 = true;
        }
        if (b2 & cancel) {
            final Iterator iterator = access$000.iterator();
            while (iterator.hasNext()) {
                ((ListenableFuture)iterator.next()).cancel(b);
            }
        }
        return cancel;
    }
    
    @Override
    final void done() {
        super.done();
        this.runningState = null;
    }
    
    final void init(final RunningState runningState) {
        (this.runningState = runningState).init();
    }
    
    @GwtIncompatible("Interruption not supported")
    @Override
    protected final void interruptTask() {
        final RunningState runningState = this.runningState;
        if (runningState != null) {
            runningState.interruptTask();
        }
    }
    
    abstract class RunningState extends AggregateFutureState implements Runnable
    {
        private final boolean allMustSucceed;
        private final boolean collectsValues;
        private ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;
        
        RunningState(final ImmutableCollection<? extends ListenableFuture<? extends InputT>> collection, final boolean allMustSucceed, final boolean collectsValues) {
            super(collection.size());
            this.futures = Preconditions.checkNotNull(collection);
            this.allMustSucceed = allMustSucceed;
            this.collectsValues = collectsValues;
        }
        
        private void decrementCountAndMaybeComplete() {
            boolean b = false;
            final int decrementRemainingAndGet = this.decrementRemainingAndGet();
            if (decrementRemainingAndGet >= 0) {
                b = true;
            }
            Preconditions.checkState(b, (Object)"Less than 0 remaining futures");
            if (decrementRemainingAndGet == 0) {
                this.processCompleted();
            }
        }
        
        private void handleException(final Throwable t) {
            boolean b = false;
            Preconditions.checkNotNull(t);
            boolean access$400;
            boolean setException;
            if (!this.allMustSucceed) {
                access$400 = true;
                setException = false;
            }
            else {
                setException = AggregateFuture.this.setException(t);
                if (!setException) {
                    access$400 = addCausalChain(this.getOrInitSeenExceptions(), t);
                }
                else {
                    this.releaseResourcesAfterFailure();
                    access$400 = true;
                }
            }
            final boolean b2 = t instanceof Error;
            final boolean allMustSucceed = this.allMustSucceed;
            if (!setException) {
                b = true;
            }
            if ((access$400 & (allMustSucceed & b)) | b2) {
                String msg;
                if (!(t instanceof Error)) {
                    msg = "Got more than one input Future failure. Logging failures after the first";
                }
                else {
                    msg = "Input Future failed with Error";
                }
                AggregateFuture.logger.log(Level.SEVERE, msg, t);
            }
        }
        
        private void handleOneInputDone(final int p0, final Future<? extends InputT> p1) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: istore_3       
            //     2: aload_0        
            //     3: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.allMustSucceed:Z
            //     6: ifeq            45
            //     9: iconst_1       
            //    10: istore_3       
            //    11: iload_3        
            //    12: ldc             "Future was done before all dependencies completed"
            //    14: invokestatic    com/google/common/base/Preconditions.checkState:(ZLjava/lang/Object;)V
            //    17: aload_2        
            //    18: invokeinterface java/util/concurrent/Future.isDone:()Z
            //    23: ldc             "Tried to set value from future which is not done"
            //    25: invokestatic    com/google/common/base/Preconditions.checkState:(ZLjava/lang/Object;)V
            //    28: aload_0        
            //    29: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.allMustSucceed:Z
            //    32: ifne            68
            //    35: aload_0        
            //    36: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.collectsValues:Z
            //    39: istore_3       
            //    40: iload_3        
            //    41: ifne            135
            //    44: return         
            //    45: aload_0        
            //    46: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.this$0:Lcom/google/common/util/concurrent/AggregateFuture;
            //    49: invokevirtual   com/google/common/util/concurrent/AggregateFuture.isDone:()Z
            //    52: ifeq            9
            //    55: aload_0        
            //    56: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.this$0:Lcom/google/common/util/concurrent/AggregateFuture;
            //    59: invokevirtual   com/google/common/util/concurrent/AggregateFuture.isCancelled:()Z
            //    62: ifne            9
            //    65: goto            11
            //    68: aload_2        
            //    69: invokeinterface java/util/concurrent/Future.isCancelled:()Z
            //    74: ifne            114
            //    77: aload_2        
            //    78: invokestatic    com/google/common/util/concurrent/Uninterruptibles.getUninterruptibly:(Ljava/util/concurrent/Future;)Ljava/lang/Object;
            //    81: astore_2       
            //    82: aload_0        
            //    83: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.collectsValues:Z
            //    86: ifeq            44
            //    89: aload_0        
            //    90: aload_0        
            //    91: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.allMustSucceed:Z
            //    94: iload_1        
            //    95: aload_2        
            //    96: invokevirtual   com/google/common/util/concurrent/AggregateFuture$RunningState.collectOneValue:(ZILjava/lang/Object;)V
            //    99: goto            44
            //   102: astore_2       
            //   103: aload_0        
            //   104: aload_2        
            //   105: invokevirtual   java/util/concurrent/ExecutionException.getCause:()Ljava/lang/Throwable;
            //   108: invokespecial   com/google/common/util/concurrent/AggregateFuture$RunningState.handleException:(Ljava/lang/Throwable;)V
            //   111: goto            44
            //   114: aload_0        
            //   115: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.this$0:Lcom/google/common/util/concurrent/AggregateFuture;
            //   118: iconst_0       
            //   119: invokestatic    com/google/common/util/concurrent/AggregateFuture.access$601:(Lcom/google/common/util/concurrent/AggregateFuture;Z)Z
            //   122: pop            
            //   123: goto            44
            //   126: astore_2       
            //   127: aload_0        
            //   128: aload_2        
            //   129: invokespecial   com/google/common/util/concurrent/AggregateFuture$RunningState.handleException:(Ljava/lang/Throwable;)V
            //   132: goto            44
            //   135: aload_2        
            //   136: invokeinterface java/util/concurrent/Future.isCancelled:()Z
            //   141: ifne            44
            //   144: aload_0        
            //   145: aload_0        
            //   146: getfield        com/google/common/util/concurrent/AggregateFuture$RunningState.allMustSucceed:Z
            //   149: iload_1        
            //   150: aload_2        
            //   151: invokestatic    com/google/common/util/concurrent/Uninterruptibles.getUninterruptibly:(Ljava/util/concurrent/Future;)Ljava/lang/Object;
            //   154: invokevirtual   com/google/common/util/concurrent/AggregateFuture$RunningState.collectOneValue:(ZILjava/lang/Object;)V
            //   157: goto            44
            //    Signature:
            //  (ILjava/util/concurrent/Future<+TInputT;>;)V
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                     
            //  -----  -----  -----  -----  -----------------------------------------
            //  17     40     102    114    Ljava/util/concurrent/ExecutionException;
            //  17     40     126    135    Ljava/lang/Throwable;
            //  68     99     102    114    Ljava/util/concurrent/ExecutionException;
            //  68     99     126    135    Ljava/lang/Throwable;
            //  114    123    102    114    Ljava/util/concurrent/ExecutionException;
            //  114    123    126    135    Ljava/lang/Throwable;
            //  135    157    102    114    Ljava/util/concurrent/ExecutionException;
            //  135    157    126    135    Ljava/lang/Throwable;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokevirtual:boolean(TrustedFuture<V>::isDone, getfield:AggregateFuture[expected:TrustedFuture](RunningState::this$0, this:RunningState)))
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
        
        private void init() {
            if (!this.futures.isEmpty()) {
                if (!this.allMustSucceed) {
                    final Iterator iterator = this.futures.iterator();
                    while (iterator.hasNext()) {
                        ((ListenableFuture)iterator.next()).addListener(this, MoreExecutors.directExecutor());
                    }
                }
                else {
                    final Iterator iterator2 = this.futures.iterator();
                    int n = 0;
                    while (iterator2.hasNext()) {
                        final ListenableFuture listenableFuture = iterator2.next();
                        listenableFuture.addListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    RunningState.this.handleOneInputDone(n, listenableFuture);
                                }
                                finally {
                                    RunningState.this.decrementCountAndMaybeComplete();
                                }
                            }
                        }, MoreExecutors.directExecutor());
                        ++n;
                    }
                }
                return;
            }
            this.handleAllCompleted();
        }
        
        private void processCompleted() {
            final boolean collectsValues = this.collectsValues;
            boolean b;
            if (this.allMustSucceed) {
                b = false;
            }
            else {
                b = true;
            }
            if (b & collectsValues) {
                final Iterator iterator = this.futures.iterator();
                int n = 0;
                while (iterator.hasNext()) {
                    this.handleOneInputDone(n, iterator.next());
                    ++n;
                }
            }
            this.handleAllCompleted();
        }
        
        @Override
        final void addInitialException(final Set<Throwable> set) {
            if (!AggregateFuture.this.isCancelled()) {
                addCausalChain(set, AggregateFuture.this.trustedGetException());
            }
        }
        
        abstract void collectOneValue(final boolean p0, final int p1, @Nullable final InputT p2);
        
        abstract void handleAllCompleted();
        
        void interruptTask() {
        }
        
        void releaseResourcesAfterFailure() {
            this.futures = null;
        }
        
        @Override
        public final void run() {
            this.decrementCountAndMaybeComplete();
        }
    }
}
