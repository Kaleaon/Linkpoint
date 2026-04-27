// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.dispnames;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.RequestQueue;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.dispnames:
//            SLDisplayNameFetcher

class this._cls0
    implements Runnable
{

    final SLDisplayNameFetcher this$0;

    public void run()
    {
        RequestQueue requestqueue;
        Object obj;
        requestqueue = SLDisplayNameFetcher._2D_get2(SLDisplayNameFetcher.this).getUserNameRequestQueue();
        obj = new HashSet();
_L3:
        if (SLDisplayNameFetcher._2D_get1(SLDisplayNameFetcher.this).get())
        {
            break MISSING_BLOCK_LABEL_94;
        }
        ((Set) (obj)).clear();
        ((Set) (obj)).add((UUID)requestqueue.waitForRequest());
_L1:
        UUID uuid;
        if (((Set) (obj)).size() >= 4)
        {
            break MISSING_BLOCK_LABEL_128;
        }
        uuid = (UUID)requestqueue.getNextRequest();
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_128;
        }
        ((Set) (obj)).add(uuid);
          goto _L1
        InterruptedException interruptedexception;
        interruptedexception;
        Debug.Warning(interruptedexception);
        for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); requestqueue.returnRequest((UUID)((Iterator) (obj)).next())) { }
        break; /* Loop/switch isn't completed */
        SLDisplayNameFetcher._2D_wrap0(SLDisplayNameFetcher.this, ((Set) (obj)), requestqueue);
        for (Iterator iterator = ((Iterable) (obj)).iterator(); iterator.hasNext(); requestqueue.returnRequest((UUID)iterator.next())) { }
        ((Set) (obj)).clear();
        if (true) goto _L3; else goto _L2
_L2:
    }

    ()
    {
        this$0 = SLDisplayNameFetcher.this;
        super();
    }
}
