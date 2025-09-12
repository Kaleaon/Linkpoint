// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.lumiyaviewer.lumiya.res.executors.ResourceCleanupExecutor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// Referenced classes of package com.lumiyaviewer.lumiya.res:
//            ResourceConsumer, ResourceRequest

public abstract class ResourceManager
{

    private final Queue cancelledRequests = new ConcurrentLinkedQueue();
    private final Runnable cleanup = new Runnable() {

        final ResourceManager this$0;

        public void run()
        {
            collectReferences();
        }

            
            {
                this$0 = ResourceManager.this;
                super();
            }
    };
    private volatile ScheduledFuture cleanupFuture;
    private final Cache consumerMap;
    private final Object lock = new Object();
    private final RemovalListener removalListener = new RemovalListener() {

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

            
            {
                this$0 = ResourceManager.this;
                super();
            }
    };
    private final LoadingCache requestMap = CacheBuilder.newBuilder().weakValues().build(new CacheLoader() {

        final ResourceManager this$0;

        public ResourceRequest load(Object obj)
            throws Exception
        {
            return CreateNewRequest(obj, ResourceManager.this);
        }

        public volatile Object load(Object obj)
            throws Exception
        {
            return load(obj);
        }

            
            {
                this$0 = ResourceManager.this;
                super();
            }
    });

    static Queue _2D_get0(ResourceManager resourcemanager)
    {
        return resourcemanager.cancelledRequests;
    }

    static LoadingCache _2D_get1(ResourceManager resourcemanager)
    {
        return resourcemanager.requestMap;
    }

    public ResourceManager()
    {
        cleanupFuture = null;
        consumerMap = CacheBuilder.newBuilder().weakKeys().removalListener(removalListener).build();
    }

    public void CancelRequest(ResourceConsumer resourceconsumer)
    {
        if (resourceconsumer == null)
        {
            break MISSING_BLOCK_LABEL_27;
        }
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        consumerMap.invalidate(resourceconsumer);
        obj;
        JVM INSTR monitorexit ;
        collectReferences();
        return;
        resourceconsumer;
        throw resourceconsumer;
    }

    public void CompleteRequest(Object obj, Object obj1, Set set)
    {
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        requestMap.invalidate(obj);
        obj = new ArrayList(set.size());
        set = set.iterator();
_L2:
        ResourceConsumer resourceconsumer;
        do
        {
            if (!set.hasNext())
            {
                break MISSING_BLOCK_LABEL_94;
            }
            resourceconsumer = (ResourceConsumer)set.next();
        } while (resourceconsumer == null);
        ((List) (obj)).add(resourceconsumer);
        consumerMap.invalidate(resourceconsumer);
        if (true) goto _L2; else goto _L1
_L1:
        obj;
        throw obj;
        obj2;
        JVM INSTR monitorexit ;
        for (set = ((Iterable) (obj)).iterator(); set.hasNext(); ((ResourceConsumer)set.next()).OnResourceReady(obj1, false)) { }
        ((List) (obj)).clear();
        collectReferences();
        return;
    }

    protected abstract ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager);

    public void IntermediateResult(Object obj, Object obj1, Set set)
    {
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        ArrayList arraylist;
        arraylist = new ArrayList(set.size());
        set = set.iterator();
_L2:
        ResourceConsumer resourceconsumer;
        do
        {
            if (!set.hasNext())
            {
                break MISSING_BLOCK_LABEL_72;
            }
            resourceconsumer = (ResourceConsumer)set.next();
        } while (resourceconsumer == null);
        arraylist.add(resourceconsumer);
        if (true) goto _L2; else goto _L1
_L1:
        obj1;
        throw obj1;
        obj;
        JVM INSTR monitorexit ;
        for (obj = arraylist.iterator(); ((Iterator) (obj)).hasNext(); ((ResourceConsumer)((Iterator) (obj)).next()).OnResourceReady(obj1, true)) { }
        arraylist.clear();
        collectReferences();
        return;
    }

    public void RequestResource(Object obj, ResourceConsumer resourceconsumer)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        boolean flag;
        obj = (ResourceRequest)requestMap.getUnchecked(obj);
        consumerMap.put(resourceconsumer, obj);
        ((ResourceRequest) (obj)).addConsumer(resourceconsumer);
        flag = ((ResourceRequest) (obj)).willStart();
        if (cleanupFuture == null)
        {
            cleanupFuture = ResourceCleanupExecutor.getInstance().scheduleAtFixedRate(cleanup, 1L, 1L, TimeUnit.SECONDS);
        }
        obj1;
        JVM INSTR monitorexit ;
        if (flag)
        {
            ((ResourceRequest) (obj)).execute();
        }
        return;
        obj;
        throw obj;
    }

    protected void collectReferences()
    {
        Object obj = null;
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        requestMap.cleanUp();
        consumerMap.cleanUp();
_L1:
        ResourceRequest resourcerequest = (ResourceRequest)cancelledRequests.poll();
        Object obj1;
        if (resourcerequest == null)
        {
            break MISSING_BLOCK_LABEL_79;
        }
        obj1 = obj;
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_60;
        }
        obj1 = new ArrayList();
        ((List) (obj1)).add(resourcerequest);
        obj = obj1;
          goto _L1
        obj;
        throw obj;
        if (requestMap.size() == 0L && consumerMap.size() == 0L && cancelledRequests.size() == 0)
        {
            if (cleanupFuture != null)
            {
                cleanupFuture.cancel(false);
            }
            cleanupFuture = null;
        }
        obj2;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            for (Iterator iterator = ((Iterable) (obj)).iterator(); iterator.hasNext(); ((ResourceRequest)iterator.next()).cancelRequest()) { }
            ((List) (obj)).clear();
        }
        return;
    }
}
