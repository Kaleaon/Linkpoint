// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import java.util.logging.Level;
import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;
import com.google.common.annotations.VisibleForTesting;
import java.util.logging.Logger;

public final class ExecutionList
{
    @VisibleForTesting
    static final Logger log;
    @GuardedBy("this")
    private boolean executed;
    @GuardedBy("this")
    private RunnableExecutorPair runnables;
    
    static {
        log = Logger.getLogger(ExecutionList.class.getName());
    }
    
    private static void executeListener(final Runnable obj, final Executor obj2) {
        try {
            obj2.execute(obj);
        }
        catch (final RuntimeException thrown) {
            ExecutionList.log.log(Level.SEVERE, "RuntimeException while executing runnable " + obj + " with executor " + obj2, thrown);
        }
    }
    
    public void add(final Runnable runnable, final Executor executor) {
        Preconditions.checkNotNull(runnable, (Object)"Runnable was null.");
        Preconditions.checkNotNull(executor, (Object)"Executor was null.");
        synchronized (this) {
            if (this.executed) {
                monitorexit(this);
                executeListener(runnable, executor);
                return;
            }
            this.runnables = new RunnableExecutorPair(runnable, executor, this.runnables);
        }
    }
    
    public void execute() {
    Label_0032_Outer:
        while (true) {
            while (true) {
                final RunnableExecutorPair next3;
            Label_0062:
                while (true) {
                    RunnableExecutorPair runnables;
                    synchronized (this) {
                        if (this.executed) {
                            return;
                        }
                        this.executed = true;
                        runnables = this.runnables;
                        this.runnables = null;
                        monitorexit(this);
                        final RunnableExecutorPair next = null;
                        if (runnables == null) {
                            if (next == null) {
                                return;
                            }
                            break Label_0062;
                        }
                    }
                    final RunnableExecutorPair next2 = runnables.next;
                    runnables.next = next3;
                    final RunnableExecutorPair next = runnables;
                    runnables = next2;
                    continue Label_0032_Outer;
                }
                executeListener(next3.runnable, next3.executor);
                final RunnableExecutorPair next = next3.next;
                continue;
            }
        }
    }
    
    private static final class RunnableExecutorPair
    {
        final Executor executor;
        @Nullable
        RunnableExecutorPair next;
        final Runnable runnable;
        
        RunnableExecutorPair(final Runnable runnable, final Executor executor, final RunnableExecutorPair next) {
            this.runnable = runnable;
            this.executor = executor;
            this.next = next;
        }
    }
}
