// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.events.SLInventoryUpdatedEvent;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory

class this._cls0
    implements SLMessageEventListener
{

    final SLInventory this$0;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        slmessage = new SLInventoryUpdatedEvent(null, null, true);
        SLInventory._2D_get0(SLInventory.this).publish(slmessage);
    }

    public void onMessageTimeout(SLMessage slmessage)
    {
    }

    dEvent()
    {
        this$0 = SLInventory.this;
        super();
    }
}
