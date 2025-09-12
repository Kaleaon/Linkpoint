// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLCircuit, SLMessage, SLGridConnection, SLCircuitInfo

public class SLThreadingCircuit extends SLCircuit
    implements Executor
{

    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private final BlockingQueue queue = new LinkedBlockingQueue();
    private volatile boolean workEnabled;
    private final Runnable workingRunnable = new Runnable() {

        final SLThreadingCircuit this$0;

        public void run()
        {
            Debug.Printf("SLThreadingCircuit: working thread started.", new Object[0]);
            do
            {
                if (!SLThreadingCircuit._2D_get1(SLThreadingCircuit.this))
                {
                    break;
                }
                Runnable runnable;
                try
                {
                    runnable = (Runnable)SLThreadingCircuit._2D_get0(SLThreadingCircuit.this).poll(1000L, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException interruptedexception)
                {
                    break;
                }
                if (runnable != null)
                {
                    runnable.run();
                } else
                {
                    InvokeProcessIdle();
                }
            } while (true);
            Debug.Printf("SLThreadingCircuit: working thread exiting.", new Object[0]);
        }

            
            {
                this$0 = SLThreadingCircuit.this;
                super();
            }
    };
    private final Thread workingThread;

    static BlockingQueue _2D_get0(SLThreadingCircuit slthreadingcircuit)
    {
        return slthreadingcircuit.queue;
    }

    static boolean _2D_get1(SLThreadingCircuit slthreadingcircuit)
    {
        return slthreadingcircuit.workEnabled;
    }

    public SLThreadingCircuit(SLGridConnection slgridconnection, SLCircuitInfo slcircuitinfo, SLAuthReply slauthreply, SLCircuit slcircuit)
        throws IOException
    {
        super(slgridconnection, slcircuitinfo, slauthreply, slcircuit);
        workingThread = new Thread(workingRunnable, "SLCircuit");
        workEnabled = true;
        workingThread.start();
    }

    private void stopThread()
    {
        workEnabled = false;
        workingThread.interrupt();
    }

    public void HandleMessage(SLMessage slmessage)
    {
        queue.offer(new _2D_.Lambda.YxYPv04qlnFzJCNumOXxffqwZwU(this, slmessage));
    }

    public void ProcessCloseCircuit()
    {
        stopThread();
    }

    public void ProcessNetworkError()
    {
        stopThread();
    }

    public void ProcessTimeout()
    {
        stopThread();
    }

    public void execute(Runnable runnable)
    {
        queue.offer(runnable);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833(SLMessage slmessage)
    {
        slmessage.Handle(this);
    }
}
