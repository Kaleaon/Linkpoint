// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler

public class AsyncRequestHandler
    implements RequestHandler
{

    private final RequestHandler baseHandler;
    private final Executor executor;

    public AsyncRequestHandler(Executor executor1, RequestHandler requesthandler)
    {
        executor = executor1;
        baseHandler = requesthandler;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_AsyncRequestHandler_553(Object obj)
    {
        baseHandler.onRequest(obj);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_AsyncRequestHandler_690(Object obj)
    {
        baseHandler.onRequestCancelled(obj);
    }

    public void onRequest(Object obj)
    {
        executor.execute(new _2D_.Lambda.SNlq3T7EFvfEVtJpS5BhPof2E2o(this, obj));
    }

    public void onRequestCancelled(Object obj)
    {
        executor.execute(new _2D_.Lambda.SNlq3T7EFvfEVtJpS5BhPof2E2o._cls1(this, obj));
    }
}
