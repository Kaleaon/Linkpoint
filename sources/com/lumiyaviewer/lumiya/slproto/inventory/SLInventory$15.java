// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory

class val.onResult
    implements Function
{

    final SLInventory this$0;
    final ImmutableSet val$immutableEntries;
    final Function val$onResult;
    final int val$taskLocalID;

    public volatile Object apply(Object obj)
    {
        return apply((UUID)obj);
    }

    public Void apply(UUID uuid)
    {
        if (uuid != null)
        {
            UUID uuid1;
            for (Iterator iterator = val$immutableEntries.iterator(); iterator.hasNext(); SLInventory._2D_wrap0(SLInventory.this, uuid, val$taskLocalID, uuid1))
            {
                uuid1 = (UUID)iterator.next();
            }

        }
        val$onResult.apply(uuid);
        return null;
    }

    ()
    {
        this$0 = final_slinventory;
        val$immutableEntries = immutableset;
        val$taskLocalID = i;
        val$onResult = Function.this;
        super();
    }
}
