// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory

class > extends SimpleRequestHandler
{

    final SLInventory this$0;

    public volatile void onRequest(Object obj)
    {
        onRequest((UUID)obj);
    }

    public void onRequest(UUID uuid)
    {
        SLInventory._2D_wrap3(SLInventory.this, uuid);
    }

    ()
    {
        this$0 = SLInventory.this;
        super();
    }
}
