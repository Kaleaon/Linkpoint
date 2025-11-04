// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import com.lumiyaviewer.lumiya.GlobalOptions;

public class HTTPFetchExecutor extends WeakExecutor
{
    private HTTPFetchExecutor() {
        super("ResourceHTTPFetch", GlobalOptions.getInstance().getMaxTextureDownloads(), new PriorityBlockingQueue<Runnable>());
    }
    
    public static HTTPFetchExecutor getInstance() {
        return InstanceHolder.Instance;
    }
    
    private static class InstanceHolder
    {
        private static final HTTPFetchExecutor Instance;
        
        static {
            Instance = new HTTPFetchExecutor(null);
        }
    }
}
