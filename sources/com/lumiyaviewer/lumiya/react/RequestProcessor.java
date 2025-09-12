// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler, RequestSource, ResultHandler, Refreshable

public abstract class RequestProcessor
    implements RequestHandler, RequestSource, ResultHandler, Refreshable
{

    private final Executor executor;
    private RequestHandler requestHandler;
    private final ResultHandler resultHandler;

    public RequestProcessor(RequestSource requestsource, Executor executor1)
    {
        executor = executor1;
        resultHandler = requestsource.attachRequestHandler(this);
    }

    private void processRequestInternal(Object obj)
    {
        Object obj1 = processRequest(obj);
        if (obj1 != null)
        {
            resultHandler.onResultData(obj, obj1);
        }
        if (!isRequestComplete(obj, obj1) && requestHandler != null)
        {
            requestHandler.onRequest(obj);
        }
    }

    private void requestUpdateInternal(Object obj)
    {
        if (requestHandler != null)
        {
            requestHandler.onRequest(obj);
        }
    }

    public ResultHandler attachRequestHandler(RequestHandler requesthandler)
    {
        requestHandler = requesthandler;
        return this;
    }

    public void detachRequestHandler(RequestHandler requesthandler)
    {
        if (requestHandler == requesthandler)
        {
            requestHandler = null;
        }
    }

    protected boolean isRequestComplete(Object obj, Object obj1)
    {
        return obj1 != null;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestProcessor_1507(Object obj)
    {
        requestUpdateInternal(obj);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestProcessor_2159(Object obj, Object obj1)
    {
        resultHandler.onResultData(obj, processResult(obj, obj1));
    }

    /* access modifiers changed from: private */
    /* renamed from: processRequestInternal */
    public void m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(Object obj)
    {
        Object obj1 = processRequest(obj);
        if (obj1 != null)
        {
            resultHandler.onResultData(obj, obj1);
        }
        if (!isRequestComplete(obj, obj1) && requestHandler != null)
        {
            requestHandler.onRequest(obj);
        }
    }

    public void onRequest(Object obj)
    {
        if (executor != null)
        {
            executor.execute(() -> m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(obj));
            return;
        } else
        {
            processRequestInternal(obj);
            return;
        }
    }

    public void onRequestCancelled(Object obj)
    {
        if (requestHandler != null)
        {
            requestHandler.onRequestCancelled(obj);
        }
    }

    public void onResultData(Object obj, Object obj1)
    {
        if (executor != null)
        {
            executor.execute(() -> resultHandler.onResultData(obj, processResult(obj, obj1)));
            return;
        } else
        {
            resultHandler.onResultData(obj, processResult(obj, obj1));
            return;
        }
    }

    public void onResultError(Object obj, Throwable throwable)
    {
        resultHandler.onResultError(obj, throwable);
    }

    protected abstract Object processRequest(Object obj);

    protected abstract Object processResult(Object obj, Object obj1);

    public void requestUpdate(Object obj)
    {
        if (executor != null)
        {
            executor.execute(() -> requestUpdateInternal(obj));
            return;
        } else
        {
            requestUpdateInternal(obj);
            return;
        }
    }
}
