// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.res:
//            ResourceManager, ResourceConsumer

public abstract class ResourceMemoryCache extends ResourceManager
{

    private final Cache finalResults = CacheBuilder.newBuilder().weakValues().build();
    private final Cache intermediateResults = CacheBuilder.newBuilder().weakValues().build();

    public ResourceMemoryCache()
    {
    }

    public void CompleteRequest(Object obj, Object obj1, Set set)
    {
        if (obj1 != null)
        {
            finalResults.put(obj, obj1);
        } else
        {
            finalResults.invalidate(obj);
        }
        super.CompleteRequest(obj, obj1, set);
    }

    public void IntermediateResult(Object obj, Object obj1, Set set)
    {
        if (obj1 != null)
        {
            intermediateResults.put(obj, obj1);
        } else
        {
            intermediateResults.invalidate(obj);
        }
        super.IntermediateResult(obj, obj1, set);
    }

    public void RequestResource(Object obj, ResourceConsumer resourceconsumer)
    {
        Object obj1 = finalResults.getIfPresent(obj);
        if (obj1 != null)
        {
            resourceconsumer.OnResourceReady(obj1, false);
            return;
        }
        obj1 = intermediateResults.getIfPresent(obj);
        if (obj1 != null)
        {
            resourceconsumer.OnResourceReady(obj1, true);
        }
        super.RequestResource(obj, resourceconsumer);
    }
}
