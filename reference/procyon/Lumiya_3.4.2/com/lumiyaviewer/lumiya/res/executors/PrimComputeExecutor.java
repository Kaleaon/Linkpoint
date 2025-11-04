// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PrimComputeExecutor extends WeakExecutor
{
    private boolean isPaused;
    private ReentrantLock pauseLock;
    private Condition unpaused;
    
    private PrimComputeExecutor() {
        super("PrimCompute", 1);
        this.pauseLock = new ReentrantLock();
        this.unpaused = this.pauseLock.newCondition();
    }
    
    public static PrimComputeExecutor getInstance() {
        return InstanceHolder.Instance;
    }
    
    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
        this.pauseLock.lock();
        try {
            Label_0045: {
                try {
                    while (this.isPaused) {
                        this.unpaused.await();
                    }
                    break Label_0045;
                }
                catch (final InterruptedException ex) {
                    t.interrupt();
                }
                return;
            }
            this.pauseLock.unlock();
        }
        finally {
            this.pauseLock.unlock();
        }
    }
    
    public void pause() {
        this.pauseLock.lock();
        try {
            this.isPaused = true;
        }
        finally {
            this.pauseLock.unlock();
        }
    }
    
    public void resume() {
        this.pauseLock.lock();
        try {
            this.isPaused = false;
            this.unpaused.signalAll();
        }
        finally {
            this.pauseLock.unlock();
        }
    }
    
    private static class InstanceHolder
    {
        private static final PrimComputeExecutor Instance;
        
        static {
            Instance = new PrimComputeExecutor(null);
        }
    }
}
