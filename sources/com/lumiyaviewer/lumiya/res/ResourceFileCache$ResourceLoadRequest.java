// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.File;

// Referenced classes of package com.lumiyaviewer.lumiya.res:
//            ResourceRequest, ResourceFileCache, ResourceManager

private class file extends ResourceRequest
    implements Runnable
{

    private final File file;
    final ResourceFileCache this$0;

    public void cancelRequest()
    {
        LoaderExecutor.getInstance().remove(this);
        super.cancelRequest();
    }

    public void execute()
    {
        LoaderExecutor.getInstance().execute(this);
    }

    public void run()
    {
        try
        {
            Object obj = getParams();
            completeRequest(createResourceFromFile(obj, file));
            return;
        }
        catch (Exception exception)
        {
            completeRequest(null);
        }
    }

    public (Object obj, ResourceManager resourcemanager, File file1)
    {
        this$0 = ResourceFileCache.this;
        super(obj, resourcemanager);
        file = file1;
    }
}
