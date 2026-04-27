// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.GlobalOptions;
import java.util.concurrent.PriorityBlockingQueue;

// Referenced classes of package com.lumiyaviewer.lumiya.res.executors:
//            WeakExecutor

public class HTTPFetchExecutor extends WeakExecutor
{
    private static class InstanceHolder
    {

        private static final HTTPFetchExecutor Instance = new HTTPFetchExecutor(null);

        static HTTPFetchExecutor _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private HTTPFetchExecutor()
    {
        super("ResourceHTTPFetch", GlobalOptions.getInstance().getMaxTextureDownloads(), new PriorityBlockingQueue());
    }

    HTTPFetchExecutor(HTTPFetchExecutor httpfetchexecutor)
    {
        this();
    }

    public static HTTPFetchExecutor getInstance()
    {
        return InstanceHolder._2D_get0();
    }
}
