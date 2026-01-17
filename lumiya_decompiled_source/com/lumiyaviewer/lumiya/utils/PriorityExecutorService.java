package com.lumiyaviewer.lumiya.utils;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class PriorityExecutorService {
    private ThreadPoolExecutor exe;
    private PriorityBinQueue<Runnable> queue;

    /* loaded from: classes.dex */
    private static class ComparePriority<T extends Runnable> implements Comparator<Runnable> {
        private ComparePriority() {
        }

        @Override // java.util.Comparator
        public int compare(Runnable runnable, Runnable runnable2) {
            return (runnable instanceof HasPriority ? ((HasPriority) runnable).getPriority() : 0) - (runnable2 instanceof HasPriority ? ((HasPriority) runnable2).getPriority() : 0);
        }
    }

    /* loaded from: classes.dex */
    private static class ExecutorWithListener extends ThreadPoolExecutor {
        private OnExecutionCompleteListener onExecutionCompleteListener;

        public ExecutorWithListener(final String str, int i, int i2, long j, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, OnExecutionCompleteListener onExecutionCompleteListener) {
            super(i, i2, j, timeUnit, blockingQueue, new ThreadFactory() { // from class: com.lumiyaviewer.lumiya.utils.PriorityExecutorService.ExecutorWithListener.1
                @Override // java.util.concurrent.ThreadFactory
                public Thread newThread(Runnable runnable) {
                    return new Thread(runnable, str);
                }
            });
            this.onExecutionCompleteListener = null;
            this.onExecutionCompleteListener = onExecutionCompleteListener;
        }

        @Override // java.util.concurrent.ThreadPoolExecutor
        protected void afterExecute(Runnable runnable, Throwable th) {
            if (this.onExecutionCompleteListener != null) {
                this.onExecutionCompleteListener.onExecutionComplete(runnable, th);
            }
        }
    }

    /* loaded from: classes.dex */
    public interface OnExecutionCompleteListener {
        void onExecutionComplete(Runnable runnable, Throwable th);
    }

    public PriorityExecutorService(String str, int i, int i2, OnExecutionCompleteListener onExecutionCompleteListener) {
        this.queue = new PriorityBinQueue<>(i2);
        this.exe = new ExecutorWithListener(str, i, i, 10L, TimeUnit.SECONDS, this.queue, onExecutionCompleteListener);
    }

    public void cancel(Runnable runnable) {
        this.queue.remove(runnable);
    }

    public void execute(Runnable runnable) {
        this.exe.execute(runnable);
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

    public void setNumThreads(int i) {
        if (i == this.exe.getCorePoolSize() || i <= 0) {
            return;
        }
        this.exe.setCorePoolSize(i);
        this.exe.setMaximumPoolSize(i);
    }

    public void shutdownNow() {
        this.exe.shutdownNow();
    }

    public void updatePriority(Runnable runnable) {
        this.queue.updatePriority(runnable);
    }
}
