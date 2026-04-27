// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.UnsubscribableOne;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            InventoryManager

private class <init>
    implements com.lumiyaviewer.lumiya.react.derSubscription, com.lumiyaviewer.lumiya.react.derSubscription, UnsubscribableOne
{

    private final InventoryQuery query;
    private final Subscription subscription;
    final InventoryManager this$0;

    public void onData(SLInventoryEntry slinventoryentry)
    {
        if (slinventoryentry != null)
        {
            Debug.Printf("Inventory: folder subscription got name: %s with folderId = '%s'", new Object[] {
                slinventoryentry.name, slinventoryentry.uuid
            });
        }
        InventoryManager._2D_get1(InventoryManager.this).onResultData(query, query.query(slinventoryentry, InventoryManager._2D_get3(InventoryManager.this)));
    }

    public volatile void onData(Object obj)
    {
        onData((SLInventoryEntry)obj);
    }

    public void onError(Throwable throwable)
    {
        Debug.Printf("Inventory: subscription error: %s", new Object[] {
            throwable
        });
        Debug.Warning(throwable);
        InventoryManager._2D_get1(InventoryManager.this).onResultError(query, throwable);
    }

    public void unsubscribe()
    {
        subscription.unsubscribe();
    }

    private (InventoryQuery inventoryquery, UUID uuid)
    {
        this$0 = InventoryManager.this;
        super();
        query = inventoryquery;
        Debug.Printf("Inventory: folder subscription: folderId = '%s'", new Object[] {
            uuid
        });
        subscription = InventoryManager._2D_get2(InventoryManager.this).subscribe(uuid, InventoryManager._2D_get4(InventoryManager.this), this, this);
    }

    subscription(InventoryQuery inventoryquery, UUID uuid, subscription subscription1)
    {
        this(inventoryquery, uuid);
    }
}
