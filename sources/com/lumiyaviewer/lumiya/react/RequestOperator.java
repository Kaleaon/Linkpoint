// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler, ResultHandler

public abstract class RequestOperator
    implements RequestHandler
{

    private final Executor executor;
    private final ResultHandler resultHandler;
    private final RequestHandler toHandler;

    public RequestOperator(RequestHandler requesthandler, ResultHandler resulthandler)
    {
        toHandler = requesthandler;
        resultHandler = resulthandler;
        executor = null;
    }

    public RequestOperator(RequestHandler requesthandler, ResultHandler resulthandler, Executor executor1)
    {
        toHandler = requesthandler;
        resultHandler = resulthandler;
        executor = executor1;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestOperator_1579(Object obj)
    {
        toHandler.onRequestCancelled(obj);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestOperator_996(Object obj)
    {
        Object obj1 = processRequest(obj);
        if (obj1 != null)
        {
            resultHandler.onResultData(obj, obj1);
            return;
        } else
        {
            toHandler.onRequest(obj);
            return;
        }
    }

    public void onRequest(Object obj)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda._cls3htMVvcf7XlS6QCgMv3cESjj4go(this, obj));
            return;
        }
        Object obj1 = processRequest(obj);
        if (obj1 != null)
        {
            resultHandler.onResultData(obj, obj1);
            return;
        } else
        {
            toHandler.onRequest(obj);
            return;
        }
    }

    public void onRequestCancelled(Object obj)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda._cls3htMVvcf7XlS6QCgMv3cESjj4go._cls1(this, obj));
            return;
        } else
        {
            toHandler.onRequestCancelled(obj);
            return;
        }
    }

    protected abstract Object processRequest(Object obj);
}
