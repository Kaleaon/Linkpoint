// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public class SLThreadingCircuit extends SLCircuit implements Executor
{
    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private final BlockingQueue<Runnable> queue;
    private volatile boolean workEnabled;
    private final Runnable workingRunnable;
    private final Thread workingThread;
    
    public SLThreadingCircuit(final SLGridConnection slGridConnection, final SLCircuitInfo slCircuitInfo, final SLAuthReply slAuthReply, final SLCircuit slCircuit) throws IOException {
        super(slGridConnection, slCircuitInfo, slAuthReply, slCircuit);
        this.workingRunnable = new Runnable() {
            @Override
            public void run() {
                Debug.Printf("SLThreadingCircuit: working thread started.", new Object[0]);
                while (SLThreadingCircuit.this.workEnabled) {
                    try {
                        final Runnable runnable = SLThreadingCircuit.this.queue.poll(1000L, TimeUnit.MILLISECONDS);
                        if (runnable != null) {
                            runnable.run();
                            continue;
                        }
                        SLThreadingCircuit.this.InvokeProcessIdle();
                        continue;
                    }
                    catch (final InterruptedException ex) {}
                    break;
                }
                Debug.Printf("SLThreadingCircuit: working thread exiting.", new Object[0]);
            }
        };
        this.workingThread = new Thread(this.workingRunnable, "SLCircuit");
        this.queue = new LinkedBlockingQueue<Runnable>();
        this.workEnabled = true;
        this.workingThread.start();
    }
    
    private void stopThread() {
        this.workEnabled = false;
        this.workingThread.interrupt();
    }
    
    @Override
    public void HandleMessage(final SLMessage slMessage) {
        this.queue.offer(new _$Lambda$YxYPv04qlnFzJCNumOXxffqwZwU(this, slMessage));
    }
    
    @Override
    public void ProcessCloseCircuit() {
        this.stopThread();
    }
    
    @Override
    public void ProcessNetworkError() {
        this.stopThread();
    }
    
    @Override
    public void ProcessTimeout() {
        this.stopThread();
    }
    
    @Override
    public void execute(final Runnable runnable) {
        this.queue.offer(runnable);
    }
}
