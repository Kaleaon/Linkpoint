// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            OpportunisticExecutor

class this._cls0
    implements Runnable
{

    final OpportunisticExecutor this$0;

    public void run()
    {
_L5:
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).lock();
_L7:
        Object obj = (Runnable)OpportunisticExecutor._2D_get2(OpportunisticExecutor.this).poll();
        boolean flag;
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_189;
        }
        flag = false;
_L3:
        if (OpportunisticExecutor._2D_get3(OpportunisticExecutor.this).isEmpty()) goto _L2; else goto _L1
_L1:
        Runnable runnable;
        obj = OpportunisticExecutor._2D_get3(OpportunisticExecutor.this).iterator();
        if (!((Iterator) (obj)).hasNext())
        {
            break MISSING_BLOCK_LABEL_168;
        }
        runnable = (Runnable)((Iterator) (obj)).next();
        ((Iterator) (obj)).remove();
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
        runnable.run();
_L4:
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).lock();
        flag = true;
          goto _L3
        obj;
        Debug.Warning(((Throwable) (obj)));
          goto _L4
        obj;
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).lock();
        throw obj;
        obj;
        try
        {
            OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
            throw obj;
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            Debug.Warning(((Throwable) (obj)));
        }
          goto _L5
        flag = true;
_L2:
        if (flag) goto _L7; else goto _L6
_L6:
        OpportunisticExecutor._2D_get1(OpportunisticExecutor.this).await();
          goto _L7
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
        ((Runnable) (obj)).run();
          goto _L5
    }

    ()
    {
        this$0 = OpportunisticExecutor.this;
        super();
    }
}
