// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.res:
//            ResourceManager, ResourceConsumer

public abstract class ResourceRequest
{

    private final Set consumers = Collections.newSetFromMap(new WeakHashMap(4, 0.5F));
    private boolean isCancelled;
    private boolean isCompleted;
    private final ResourceManager manager;
    private final Object params;
    private boolean started;

    public ResourceRequest(Object obj, ResourceManager resourcemanager)
    {
        started = false;
        isCompleted = false;
        isCancelled = false;
        params = obj;
        manager = resourcemanager;
    }

    public void addConsumer(ResourceConsumer resourceconsumer)
    {
        consumers.add(resourceconsumer);
    }

    public void cancelRequest()
    {
    }

    public void completeRequest(Object obj)
    {
        isCompleted = true;
        manager.CompleteRequest(params, obj, consumers);
    }

    public abstract void execute();

    protected final Object getParams()
    {
        return params;
    }

    public void intermediateResult(Object obj)
    {
        manager.IntermediateResult(params, obj, consumers);
    }

    public boolean isCancelled()
    {
        return isCancelled;
    }

    public final boolean isCompleted()
    {
        return isCompleted;
    }

    public boolean isStale()
    {
        boolean flag = false;
        if (consumers.size() == 0)
        {
            flag = true;
        }
        return flag;
    }

    public boolean removeConsumer(ResourceConsumer resourceconsumer)
    {
        boolean flag = false;
        consumers.remove(resourceconsumer);
        if (consumers.size() == 0)
        {
            flag = true;
        }
        return flag;
    }

    public void setCancelled(boolean flag)
    {
        isCancelled = flag;
    }

    public final boolean willStart()
    {
        if (!started)
        {
            started = true;
            return true;
        } else
        {
            return false;
        }
    }
}
