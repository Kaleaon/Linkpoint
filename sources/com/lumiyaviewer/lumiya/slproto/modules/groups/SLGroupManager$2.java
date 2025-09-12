// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.groups:
//            SLGroupManager

class this._cls0 extends SimpleRequestHandler
{

    final SLGroupManager this$0;

    public volatile void onRequest(Object obj)
    {
        onRequest((UUID)obj);
    }

    public void onRequest(UUID uuid)
    {
        Debug.Printf("GroupTitles: [%s] network requesting for group %s", new Object[] {
            Thread.currentThread().getName(), uuid.toString()
        });
        SLGroupManager._2D_wrap1(SLGroupManager.this, uuid);
    }

    ()
    {
        this$0 = SLGroupManager.this;
        super();
    }
}
