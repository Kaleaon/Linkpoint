// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.util.Queue;

// Referenced classes of package com.lumiyaviewer.lumiya.res:
//            ResourceManager, ResourceConsumer, ResourceRequest

class this._cls0
    implements RemovalListener
{

    final ResourceManager this$0;

    public void onRemoval(RemovalNotification removalnotification)
    {
        ResourceConsumer resourceconsumer = (ResourceConsumer)removalnotification.getKey();
        removalnotification = (ResourceRequest)removalnotification.getValue();
        if (removalnotification != null && !removalnotification.isCompleted() && removalnotification.isCancelled() ^ true)
        {
            if (resourceconsumer != null)
            {
                removalnotification.removeConsumer(resourceconsumer);
            }
            if (removalnotification.isStale())
            {
                removalnotification.setCancelled(true);
                ResourceManager._2D_get0(ResourceManager.this).add(removalnotification);
                if ((ResourceRequest)ResourceManager._2D_get1(ResourceManager.this).getIfPresent(removalnotification.getParams()) == removalnotification)
                {
                    ResourceManager._2D_get1(ResourceManager.this).invalidate(removalnotification.getParams());
                }
            }
        }
    }

    ()
    {
        this$0 = ResourceManager.this;
        super();
    }
}
