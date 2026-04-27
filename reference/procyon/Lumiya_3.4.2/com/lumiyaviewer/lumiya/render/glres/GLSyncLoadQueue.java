// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.render.RenderContext;

public class GLSyncLoadQueue extends GLLoadQueue implements GLLoadHandler
{
    private static final int MAX_LOADS_PER_FRAME = 16;
    private static final int MAX_SIZE_PER_FRAME = 4194304;
    private static final int WAIT_FRAMES_AFTER_LOAD = 3;
    private int framesWait;
    
    public GLSyncLoadQueue() {
        this.framesWait = 0;
    }
    
    @Override
    public void GLResourceLoaded(final GLLoadable glLoadable) {
        glLoadable.GLCompleteLoad();
    }
    
    @Override
    public void RunLoadQueue(@Nonnull final RenderContext renderContext) {
        if (this.framesWait != 0) {
            --this.framesWait;
            return;
        }
        int n = 0;
        int n2 = 0;
        while (true) {
            Label_0153: {
                int i;
                int j;
                while (true) {
                    i = n;
                    j = n2;
                    if (!TextureMemoryTracker.canAllocateMemory(0)) {
                        break;
                    }
                    final GLLoadable glLoadable = this.loadQueue.poll();
                    i = n;
                    j = n2;
                    if (glLoadable == null) {
                        break;
                    }
                    if (!TextureMemoryTracker.canAllocateMemory(glLoadable.GLGetLoadSize())) {
                        TextureMemoryTracker.stall();
                        this.loadQueue.add(glLoadable);
                        j = n2;
                        i = n;
                        break;
                    }
                    n += glLoadable.GLLoad(renderContext, this);
                    this.framesWait = 3;
                    if (++n2 >= 16 || n >= 4194304) {
                        break Label_0153;
                    }
                }
                if (j != 0) {
                    Debug.Printf("waitForMemory: loadedCount %d, size %d", j, i);
                }
                return;
            }
            int i = n;
            int j = n2;
            continue;
        }
    }
}
