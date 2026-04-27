// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Timer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLCircuit

public class SLConnection
    implements Runnable
{

    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private Selector selector;
    private volatile Timer timer;
    private Thread workingThread;

    public SLConnection()
    {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
        try
        {
            selector = Selector.open();
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        workingThread = null;
        timer = null;
    }

    public void AddCircuit(SLCircuit slcircuit)
    {
        this;
        JVM INSTR monitorenter ;
        if (workingThread == null)
        {
            workingThread = new Thread(this, "SLConnection");
            workingThread.start();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        slcircuit;
        throw slcircuit;
    }

    public Selector getSelector()
    {
        return selector;
    }

    public Timer getTimer()
    {
        this;
        JVM INSTR monitorenter ;
        Timer timer1;
        if (timer == null)
        {
            timer = new Timer("SLConnectionTimer", true);
        }
        timer1 = timer;
        this;
        JVM INSTR monitorexit ;
        return timer1;
        Exception exception;
        exception;
        throw exception;
    }

    public void run()
    {
        Debug.Log("working thread started");
_L6:
        if (selector.keys().isEmpty()) goto _L2; else goto _L1
_L1:
        int i = 1000;
        Iterator iterator = selector.keys().iterator();
_L4:
        SelectionKey selectionkey;
        SLCircuit slcircuit;
        if (!iterator.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        selectionkey = (SelectionKey)iterator.next();
        slcircuit = (SLCircuit)selectionkey.attachment();
        if (slcircuit == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        int j;
        if (!selectionkey.isValid())
        {
            continue; /* Loop/switch isn't completed */
        }
        slcircuit.ProcessWakeup();
        j = slcircuit.getIdleInterval();
        if (j < i)
        {
            i = j;
        }
        continue; /* Loop/switch isn't completed */
        Object obj;
        obj;
        break; /* Loop/switch isn't completed */
        if (true) goto _L4; else goto _L3
_L3:
        obj = selector.selectedKeys().iterator();
_L5:
        do
        {
            if (!((Iterator) (obj)).hasNext())
            {
                break MISSING_BLOCK_LABEL_211;
            }
            selectionkey = (SelectionKey)((Iterator) (obj)).next();
            ((Iterator) (obj)).remove();
            slcircuit = (SLCircuit)selectionkey.attachment();
        } while (slcircuit == null);
        if (selectionkey.isValid() && selectionkey.isReadable())
        {
            slcircuit.ProcessReceive();
        }
        if (selectionkey.isValid() && selectionkey.isWritable())
        {
            slcircuit.ProcessTransmit();
        }
        if (selectionkey.isValid())
        {
            slcircuit.UpdateSelectorOps();
            slcircuit.TryProcessIdle();
        }
          goto _L5
        obj;
_L12:
        selector.select(i);
          goto _L6
        obj;
        ((IOException) (obj)).printStackTrace();
        obj = selector.keys().iterator();
_L9:
        if (!((Iterator) (obj)).hasNext()) goto _L2; else goto _L7
_L7:
        selectionkey = (SelectionKey)((Iterator) (obj)).next();
        slcircuit = (SLCircuit)selectionkey.attachment();
        if (slcircuit == null) goto _L9; else goto _L8
_L8:
        if (!selectionkey.isValid()) goto _L9; else goto _L10
_L10:
        slcircuit.ProcessNetworkError();
          goto _L9
        obj;
_L2:
        Debug.Log("working thread exiting");
        this;
        JVM INSTR monitorenter ;
        workingThread = null;
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
        this;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
        obj;
        continue; /* Loop/switch isn't completed */
        obj;
        continue; /* Loop/switch isn't completed */
        obj;
        if (true) goto _L2; else goto _L11
_L11:
        obj;
          goto _L12
        obj;
          goto _L12
        obj;
          goto _L12
        obj;
          goto _L3
    }
}
