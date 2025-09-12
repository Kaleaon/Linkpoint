// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            AsyncRequestHandler, RequestHandlerLimits, RequestHandler

public class AsyncLimitsRequestHandler extends AsyncRequestHandler
    implements RequestHandlerLimits
{

    private final boolean isCancellable;
    private final int maxRequests;
    private final long requestTimeout;

    public AsyncLimitsRequestHandler(Executor executor, RequestHandler requesthandler, boolean flag, int i, long l)
    {
        super(executor, requesthandler);
        isCancellable = flag;
        maxRequests = i;
        requestTimeout = l;
    }

    public int getMaxRequestsInFlight()
    {
        return maxRequests;
    }

    public long getRequestTimeout()
    {
        return requestTimeout;
    }

    public boolean isRequestCancellable()
    {
        return isCancellable;
    }
}
