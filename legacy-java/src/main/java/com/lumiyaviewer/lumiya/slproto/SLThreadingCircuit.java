package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class SLThreadingCircuit extends SLCircuit implements Executor {
    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private final BlockingQueue<Runnable> queue;
    private volatile boolean workEnabled;
    private final Runnable workingRunnable;
    private final Thread workingThread;

    public SLThreadingCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCircuit sLCircuit) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, sLCircuit);
        this.workingRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.SLThreadingCircuit.1
            @Override // java.lang.Runnable
            public void run() {
                Debug.Printf("SLThreadingCircuit: working thread started.", new Object[0]);
                while (SLThreadingCircuit.this.workEnabled) {
                    try {
                        Runnable runnable = (Runnable) SLThreadingCircuit.this.queue.poll(1000L, TimeUnit.MILLISECONDS);
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
        this.workingThread = new Thread(this.workingRunnable, "SLCircuit");
        this.queue = new LinkedBlockingQueue();
        this.workEnabled = true;
        this.workingThread.start();
    }

    private void stopThread() {
        this.workEnabled = false;
        this.workingThread.interrupt();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void HandleMessage(final SLMessage sLMessage) {
        this.queue.offer(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.-$Lambda$YxYPv04qlnFzJCNumOXxffqwZwU
            private final /* synthetic */ void $m$0() {
                ((SLThreadingCircuit) this).m146lambda$com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833((SLMessage) sLMessage);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void ProcessCloseCircuit() {
        stopThread();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void ProcessNetworkError() {
        stopThread();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLCircuit
    public void ProcessTimeout() {
        stopThread();
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable runnable) {
        this.queue.offer(runnable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833, reason: not valid java name */
    public /* synthetic */ void m146lambda$com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833(SLMessage sLMessage) {
        sLMessage.Handle(this);
    }
}
