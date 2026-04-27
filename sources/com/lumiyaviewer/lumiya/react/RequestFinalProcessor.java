// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler, RequestSource, ResultHandler

public abstract class RequestFinalProcessor
    implements RequestHandler
{

    private final Executor executor;
    private final ResultHandler resultHandler;

    public RequestFinalProcessor(RequestSource requestsource, Executor executor1)
    {
        executor = executor1;
        resultHandler = requestsource.attachRequestHandler(this);
    }

    protected void cancelRequest(Object obj)
    {
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestFinalProcessor_1437(Object obj)
    {
        cancelRequest(obj);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestFinalProcessor_673(Object obj)
    {
        try
        {
            resultHandler.onResultData(obj, processRequest(obj));
            return;
        }
        catch (Throwable throwable)
        {
            resultHandler.onResultError(obj, throwable);
        }
    }

    public void onRequest(Object obj)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda.psFcS6_2D_5kKyuCZBH4SbOZwtpXG8(this, obj));
            return;
        }
        try
        {
            resultHandler.onResultData(obj, processRequest(obj));
            return;
        }
        catch (Throwable throwable)
        {
            resultHandler.onResultError(obj, throwable);
        }
    }

    public void onRequestCancelled(Object obj)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda.psFcS6_2D_5kKyuCZBH4SbOZwtpXG8._cls1(this, obj));
            return;
        } else
        {
            cancelRequest(obj);
            return;
        }
    }

    protected abstract Object processRequest(Object obj)
        throws Throwable;
}
