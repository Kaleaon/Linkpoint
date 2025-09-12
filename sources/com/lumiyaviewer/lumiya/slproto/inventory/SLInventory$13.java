// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.SLMessage;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory, SLInventoryEntry

class tListener extends com.lumiyaviewer.lumiya.slproto.istener.SLMessageBaseEventListener
{

    final SLInventory this$0;
    final nventoryCallbackListener val$callbackListener;
    final SLInventoryEntry val$entry;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        super.onMessageAcknowledged(slmessage);
        val$callbackListener.onInventoryCallback(val$entry);
    }

    y()
    {
        this$0 = final_slinventory;
        val$callbackListener = nventorycallbacklistener;
        val$entry = SLInventoryEntry.this;
        super();
    }
}
