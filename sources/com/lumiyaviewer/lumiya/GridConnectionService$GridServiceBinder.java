// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.os.Binder;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            GridConnectionService

public class this._cls0 extends Binder
{

    final GridConnectionService this$0;

    public EventBus getEventBus()
    {
        return GridConnectionService._2D_get0(GridConnectionService.this);
    }

    public SLGridConnection getGridConn()
    {
        return GridConnectionService._2D_get1();
    }

    public ()
    {
        this$0 = GridConnectionService.this;
        super();
    }
}
