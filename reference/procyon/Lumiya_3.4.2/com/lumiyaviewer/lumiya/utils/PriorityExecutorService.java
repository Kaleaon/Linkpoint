// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import java.util.concurrent.ThreadFactory;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

public class PriorityExecutorService
{
    private ThreadPoolExecutor exe;
    private PriorityBinQueue<Runnable> queue;
    
    public PriorityExecutorService(final String s, final int n, final int n2, final OnExecutionCompleteListener onExecutionCompleteListener) {
        this.queue = new PriorityBinQueue<Runnable>(n2);
        this.exe = new ExecutorWithListener(s, n, n, 10L, TimeUnit.SECONDS, this.queue, onExecutionCompleteListener);
    }
    
    public void cancel(final Runnable runnable) {
        this.queue.remove(runnable);
    }
    
    public void execute(final Runnable command) {
        this.exe.execute(command);
    }
    
    public int getNumThreads() {
        return this.exe.getCorePoolSize();
    }
    
    public int getNumWaitingTasks() {
        return this.exe.getQueue().size() + this.exe.getActiveCount();
    }
    
    public boolean isShutdown() {
        return this.exe.isShutdown();
    }
    
    public void setNumThreads(final int n) {
        if (n != this.exe.getCorePoolSize() && n > 0) {
            this.exe.setCorePoolSize(n);
            this.exe.setMaximumPoolSize(n);
        }
    }
    
    public void shutdownNow() {
        this.exe.shutdownNow();
    }
    
    public void updatePriority(final Runnable runnable) {
        this.queue.updatePriority(runnable);
    }
    
    private static class ComparePriority<T extends Runnable> implements Comparator<Runnable>
    {
        @Override
        public int compare(final Runnable runnable, final Runnable runnable2) {
            int priority = 0;
            int priority2;
            if (runnable instanceof HasPriority) {
                priority2 = ((HasPriority)runnable).getPriority();
            }
            else {
                priority2 = 0;
            }
            if (runnable2 instanceof HasPriority) {
                priority = ((HasPriority)runnable2).getPriority();
            }
            return priority2 - priority;
        }
    }
    
    private static class ExecutorWithListener extends ThreadPoolExecutor
    {
        private OnExecutionCompleteListener onExecutionCompleteListener;
        
        public ExecutorWithListener(final String s, final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final OnExecutionCompleteListener onExecutionCompleteListener) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new ThreadFactory() {
                @Override
                public Thread newThread(final Runnable task) {
                    return new Thread(task, s);
                }
            });
            this.onExecutionCompleteListener = null;
            this.onExecutionCompleteListener = onExecutionCompleteListener;
        }
        
        @Override
        protected void afterExecute(final Runnable runnable, final Throwable t) {
            if (this.onExecutionCompleteListener != null) {
                this.onExecutionCompleteListener.onExecutionComplete(runnable, t);
            }
        }
    }
    
    public interface OnExecutionCompleteListener
    {
        void onExecutionComplete(final Runnable p0, final Throwable p1);
    }
}
