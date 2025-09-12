// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLLoadQueue

public class GLSyncLoadQueue extends GLLoadQueue
    implements GLLoadQueue.GLLoadHandler
{

    private static final int MAX_LOADS_PER_FRAME = 16;
    private static final int MAX_SIZE_PER_FRAME = 0x400000;
    private static final int WAIT_FRAMES_AFTER_LOAD = 3;
    private int framesWait;

    public GLSyncLoadQueue()
    {
        framesWait = 0;
    }

    public void GLResourceLoaded(GLLoadQueue.GLLoadable glloadable)
    {
        glloadable.GLCompleteLoad();
    }

    public void RunLoadQueue(RenderContext rendercontext)
    {
        int i;
        int j;
        if (framesWait != 0)
        {
            framesWait = framesWait - 1;
            return;
        }
        j = 0;
        i = 0;
_L2:
        int k = j;
        int l = i;
        if (TextureMemoryTracker.canAllocateMemory(0))
        {
            GLLoadQueue.GLLoadable glloadable = (GLLoadQueue.GLLoadable)loadQueue.poll();
            k = j;
            l = i;
            if (glloadable != null)
            {
                if (!TextureMemoryTracker.canAllocateMemory(glloadable.GLGetLoadSize()))
                {
                    TextureMemoryTracker.stall();
                    loadQueue.add(glloadable);
                    l = i;
                    k = j;
                } else
                {
                    j = glloadable.GLLoad(rendercontext, this) + j;
                    framesWait = 3;
                    i++;
                    if (i < 16 && j < 0x400000)
                    {
                        continue; /* Loop/switch isn't completed */
                    }
                    l = i;
                    k = j;
                }
            }
        }
        if (l != 0)
        {
            Debug.Printf("waitForMemory: loadedCount %d, size %d", new Object[] {
                Integer.valueOf(l), Integer.valueOf(k)
            });
        }
        return;
        if (true) goto _L2; else goto _L1
_L1:
    }
}
