package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SLThreadingCircuit extends SLCircuit implements Executor {
    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    /* access modifiers changed from: private */
    public final BlockingQueue<Runnable> queue = new LinkedBlockingQueue();
    /* access modifiers changed from: private */
    public volatile boolean workEnabled = true;
    private final Runnable workingRunnable = new Runnable() {
        public void run() {
            Debug.Printf("SLThreadingCircuit: working thread started.", new Object[0]);
            while (SLThreadingCircuit.this.workEnabled) {
                try {
                    Runnable runnable = (Runnable) SLThreadingCircuit.this.queue.poll(1000, TimeUnit.MILLISECONDS);
                    if (runnable != null) {
                        runnable.run();
                    } else {
                        SLThreadingCircuit.this.InvokeProcessIdle();
                    }
                } catch (InterruptedException e) {
                }
            }
            Debug.Printf("SLThreadingCircuit: working thread exiting.", new Object[0]);
        }
    };
    private final Thread workingThread = new Thread(this.workingRunnable, "SLCircuit");

    public SLThreadingCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCircuit sLCircuit) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, sLCircuit);
        this.workingThread.start();
    }

    private void stopThread() {
        this.workEnabled = false;
        this.workingThread.interrupt();
    }

    public void HandleMessage(SLMessage sLMessage) {
        this.queue.offer(new $Lambda$YxYPv04qlnFzJCNumOXxffqwZwU(this, sLMessage));
    }

    public void ProcessCloseCircuit() {
        stopThread();
    }

    public void ProcessNetworkError() {
        stopThread();
    }

    public void ProcessTimeout() {
        stopThread();
    }

    public void execute(Runnable runnable) {
        this.queue.offer(runnable);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833  reason: not valid java name */
    public /* synthetic */ void m136lambda$com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833(SLMessage sLMessage) {
        sLMessage.Handle(this);
    }
}
