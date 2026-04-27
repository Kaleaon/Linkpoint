// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLThreadingCircuit

class this._cls0
    implements Runnable
{

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

    ()
    {
        this$0 = SLThreadingCircuit.this;
        super();
    }
}
