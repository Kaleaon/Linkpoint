// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLLoadQueue, GLSizedResource

public abstract class GLResourceCache extends ResourceMemoryCache
{
    private class LoadRequest extends ResourceRequest
        implements GLLoadQueue.GLLoadable, ResourceConsumer
    {

        private volatile boolean finalResult;
        private volatile boolean loadedFinal;
        private volatile GLSizedResource loadedResource;
        private volatile Object rawResource;
        final GLResourceCache this$0;

        public void GLCompleteLoad()
        {
            this;
            JVM INSTR monitorenter ;
            Object obj;
            boolean flag;
            obj = loadedResource;
            flag = loadedFinal;
            this;
            JVM INSTR monitorexit ;
            if (flag)
            {
                completeRequest(obj);
                return;
            } else
            {
                intermediateResult(obj);
                return;
            }
            obj;
            throw obj;
        }

        public int GLGetLoadSize()
        {
            this;
            JVM INSTR monitorenter ;
            Object obj = rawResource;
            this;
            JVM INSTR monitorexit ;
            Exception exception;
            if (obj != null)
            {
                return GetResourceSize(obj);
            } else
            {
                return 0;
            }
            exception;
            throw exception;
        }

        public int GLLoad(RenderContext rendercontext, GLLoadQueue.GLLoadHandler glloadhandler)
        {
            this;
            JVM INSTR monitorenter ;
            Object obj;
            boolean flag;
            obj = rawResource;
            flag = finalResult;
            this;
            JVM INSTR monitorexit ;
            rendercontext = LoadResource(getParams(), obj, rendercontext);
            int i;
            if (rendercontext != null)
            {
                i = rendercontext.getLoadedSize();
            } else
            {
                i = 0;
            }
            this;
            JVM INSTR monitorenter ;
            loadedResource = rendercontext;
            loadedFinal = flag;
            this;
            JVM INSTR monitorexit ;
            if (rendercontext != null)
            {
                glloadhandler.GLResourceLoaded(this);
            }
            return i;
            rendercontext;
            throw rendercontext;
            rendercontext;
            throw rendercontext;
        }

        public void OnResourceReady(Object obj, boolean flag)
        {
            if (obj == null) goto _L2; else goto _L1
_L1:
            this;
            JVM INSTR monitorenter ;
            rawResource = obj;
            finalResult = flag ^ true;
            this;
            JVM INSTR monitorexit ;
            GLResourceCache._2D_get0(GLResourceCache.this).add(this);
_L4:
            GLResourceCache._2D_wrap0(GLResourceCache.this);
            return;
            obj;
            this;
            JVM INSTR monitorexit ;
            throw obj;
            obj;
            Debug.Warning(((Throwable) (obj)));
            completeRequest(null);
            continue; /* Loop/switch isn't completed */
_L2:
            completeRequest(null);
            if (true) goto _L4; else goto _L3
_L3:
        }

        public void cancelRequest()
        {
            GLResourceCache._2D_get0(GLResourceCache.this).remove(this);
            CancelRawResource(this);
            super.cancelRequest();
        }

        public void execute()
        {
            RequestRawResource(getParams(), this);
        }

        public LoadRequest(Object obj, ResourceManager resourcemanager)
        {
            this$0 = GLResourceCache.this;
            super(obj, resourcemanager);
        }
    }


    private final GLLoadQueue loadQueue;

    static GLLoadQueue _2D_get0(GLResourceCache glresourcecache)
    {
        return glresourcecache.loadQueue;
    }

    static void _2D_wrap0(GLResourceCache glresourcecache)
    {
        glresourcecache.collectReferences();
    }

    protected GLResourceCache(GLLoadQueue glloadqueue)
    {
        loadQueue = glloadqueue;
    }

    protected abstract void CancelRawResource(ResourceConsumer resourceconsumer);

    protected ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return new LoadRequest(obj, resourcemanager);
    }

    protected abstract int GetResourceSize(Object obj);

    protected abstract GLSizedResource LoadResource(Object obj, Object obj1, RenderContext rendercontext);

    protected abstract void RequestRawResource(Object obj, ResourceConsumer resourceconsumer);
}
