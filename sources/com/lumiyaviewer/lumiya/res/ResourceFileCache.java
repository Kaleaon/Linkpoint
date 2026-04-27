// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.File;

// Referenced classes of package com.lumiyaviewer.lumiya.res:
//            ResourceMemoryCache, ResourceManager, ResourceRequest

public abstract class ResourceFileCache extends ResourceMemoryCache
{
    private class ResourceLoadRequest extends ResourceRequest
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

        public ResourceLoadRequest(Object obj, ResourceManager resourcemanager, File file1)
        {
            this$0 = ResourceFileCache.this;
            super(obj, resourcemanager);
            file = file1;
        }
    }


    public ResourceFileCache()
    {
    }

    protected ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        File file = getResourceFile(obj);
        if (file.exists())
        {
            return new ResourceLoadRequest(obj, resourcemanager, file);
        } else
        {
            return createResourceGenRequest(obj, resourcemanager, file);
        }
    }

    protected abstract Object createResourceFromFile(Object obj, File file);

    protected abstract ResourceRequest createResourceGenRequest(Object obj, ResourceManager resourcemanager, File file);

    protected abstract File getResourceFile(Object obj);
}
