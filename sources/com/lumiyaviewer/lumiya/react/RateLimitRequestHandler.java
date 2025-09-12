// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler, RequestQueue, RequestSource, ResultHandler, 
//            RequestHandlerLimits

public class RateLimitRequestHandler
    implements RequestHandler, RequestQueue, RequestSource, ResultHandler
{

    private boolean cancellable;
    private final Object lock = new Object();
    private int maxInFlight;
    private final Set pendingRequests = new HashSet();
    private RequestHandler requestHandler;
    private long requestTimeout;
    private final Map requestsInFlight = new HashMap();
    private final ResultHandler resultHandler;

    public RateLimitRequestHandler(RequestSource requestsource)
    {
        cancellable = false;
        maxInFlight = 0x7fffffff;
        requestTimeout = 0x7fffffffffffffffL;
        requestHandler = null;
        resultHandler = requestsource.attachRequestHandler(this);
    }

    private void runPendingRequests()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        long l;
        Debug.Printf("UserPic: RateLimitHandler: pending count %d, in flight %d", new Object[] {
            Integer.valueOf(pendingRequests.size()), Integer.valueOf(requestsInFlight.size())
        });
        if (requestHandler == null)
        {
            break MISSING_BLOCK_LABEL_265;
        }
        l = System.currentTimeMillis();
        if (requestsInFlight.size() >= maxInFlight && requestTimeout != 0x7fffffffffffffffL)
        {
            Iterator iterator = requestsInFlight.entrySet().iterator();
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                Object obj1 = (java.util.Map.Entry)iterator.next();
                if (l >= ((Long)((java.util.Map.Entry) (obj1)).getValue()).longValue() + requestTimeout)
                {
                    obj1 = ((java.util.Map.Entry) (obj1)).getKey();
                    iterator.remove();
                    if (cancellable)
                    {
                        requestHandler.onRequestCancelled(obj1);
                    }
                    pendingRequests.remove(obj1);
                }
            } while (true);
        }
        break MISSING_BLOCK_LABEL_191;
        Exception exception;
        exception;
        throw exception;
        if (requestsInFlight.size() < maxInFlight)
        {
            Iterator iterator1 = pendingRequests.iterator();
            if (iterator1.hasNext())
            {
                Object obj2 = iterator1.next();
                iterator1.remove();
                requestsInFlight.put(obj2, Long.valueOf(l));
                requestHandler.onRequest(obj2);
            }
        }
        if (!pendingRequests.isEmpty())
        {
            lock.notifyAll();
        }
        obj;
        JVM INSTR monitorexit ;
    }

    public ResultHandler attachRequestHandler(RequestHandler requesthandler)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (requestHandler == requesthandler) goto _L2; else goto _L1
_L1:
        if (requestHandler != null)
        {
            requestHandler = null;
            requestsInFlight.clear();
        }
        requestHandler = requesthandler;
        if (!(requesthandler instanceof RequestHandlerLimits)) goto _L4; else goto _L3
_L3:
        requesthandler = (RequestHandlerLimits)requesthandler;
        cancellable = requesthandler.isRequestCancellable();
        maxInFlight = requesthandler.getMaxRequestsInFlight();
        requestTimeout = requesthandler.getRequestTimeout();
_L2:
        obj;
        JVM INSTR monitorexit ;
        runPendingRequests();
        return this;
_L4:
        cancellable = true;
        maxInFlight = 0x7fffffff;
        requestTimeout = 0x7fffffffffffffffL;
        if (true) goto _L2; else goto _L5
_L5:
        requesthandler;
        throw requesthandler;
    }

    public void detachRequestHandler(RequestHandler requesthandler)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (requestHandler == requesthandler)
        {
            requestHandler = null;
            requestsInFlight.clear();
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        requesthandler;
        throw requesthandler;
    }

    public Object getNextRequest()
    {
        Object obj = null;
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        Iterator iterator = pendingRequests.iterator();
        if (iterator.hasNext())
        {
            obj = iterator.next();
            iterator.remove();
            requestsInFlight.put(obj, Long.valueOf(System.currentTimeMillis()));
        }
        obj1;
        JVM INSTR monitorexit ;
        return obj;
        Exception exception;
        exception;
        throw exception;
    }

    public ResultHandler getResultHandler()
    {
        return this;
    }

    public void onRequest(Object obj)
    {
        Debug.Printf("UserPic: RateLimitHandler: new for %s", new Object[] {
            obj.toString()
        });
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        pendingRequests.add(obj);
        obj1;
        JVM INSTR monitorexit ;
        runPendingRequests();
        return;
        obj;
        throw obj;
    }

    public void onRequestCancelled(Object obj)
    {
        Debug.Printf("UserPic: RateLimitHandler: cancelled for %s", new Object[] {
            obj.toString()
        });
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        pendingRequests.remove(obj);
        if (requestHandler != null && cancellable && requestsInFlight.remove(obj) != null)
        {
            requestHandler.onRequestCancelled(obj);
        }
        obj1;
        JVM INSTR monitorexit ;
        runPendingRequests();
        return;
        obj;
        throw obj;
    }

    public void onResultData(Object obj, Object obj1)
    {
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        pendingRequests.remove(obj);
        requestsInFlight.remove(obj);
        obj2;
        JVM INSTR monitorexit ;
        resultHandler.onResultData(obj, obj1);
        runPendingRequests();
        return;
        obj;
        throw obj;
    }

    public void onResultError(Object obj, Throwable throwable)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        pendingRequests.remove(obj);
        requestsInFlight.remove(obj);
        obj1;
        JVM INSTR monitorexit ;
        resultHandler.onResultError(obj, throwable);
        runPendingRequests();
        return;
        obj;
        throw obj;
    }

    public void returnRequest(Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        if (requestsInFlight.remove(obj) != null)
        {
            pendingRequests.add(obj);
        }
        obj1;
        JVM INSTR monitorexit ;
        runPendingRequests();
        return;
        obj;
        throw obj;
    }

    public Object waitForRequest()
        throws InterruptedException
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
_L2:
        Object obj1;
        Iterator iterator = pendingRequests.iterator();
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_43;
        }
        obj1 = iterator.next();
        iterator.remove();
        obj;
        JVM INSTR monitorexit ;
        return obj1;
        lock.wait();
        if (true) goto _L2; else goto _L1
_L1:
        Exception exception;
        exception;
        throw exception;
    }
}
