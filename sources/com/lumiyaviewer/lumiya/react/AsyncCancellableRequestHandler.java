// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler

public class AsyncCancellableRequestHandler
    implements RequestHandler
{

    private final Map activeRequests = new ConcurrentHashMap();
    private final RequestHandler baseHandler;
    private final ExecutorService executor;
    private final Object lock = new Object();

    public AsyncCancellableRequestHandler(ExecutorService executorservice, RequestHandler requesthandler)
    {
        executor = executorservice;
        baseHandler = requesthandler;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_AsyncCancellableRequestHandler_993(Object obj)
    {
        baseHandler.onRequest(obj);
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        activeRequests.remove(obj);
        obj1;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
        Exception exception;
        exception;
        obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        activeRequests.remove(obj);
        obj1;
        JVM INSTR monitorexit ;
        throw exception;
        obj;
        throw obj;
    }

    public void onRequest(Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        Future future = executor.submit(new _2D_.Lambda.W2IjgG3sQFB_2D_K_ukBg8_XysJz_I(this, obj));
        activeRequests.put(obj, future);
        obj1;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    public void onRequestCancelled(Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        obj = (Future)activeRequests.remove(obj);
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_33;
        }
        ((Future) (obj)).cancel(true);
        obj1;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }
}
