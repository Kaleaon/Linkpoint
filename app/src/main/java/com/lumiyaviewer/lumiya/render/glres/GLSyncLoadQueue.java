package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import javax.annotation.Nonnull;

public class GLSyncLoadQueue extends GLLoadQueue implements GLLoadQueue.GLLoadHandler {
    private static final int MAX_LOADS_PER_FRAME = 16;
    private static final int MAX_SIZE_PER_FRAME = 4194304;
    private static final int WAIT_FRAMES_AFTER_LOAD = 3;
    private int framesWait = 0;

    public void GLResourceLoaded(GLLoadQueue.GLLoadable gLLoadable) {
        gLLoadable.GLCompleteLoad();
    }

    public void RunLoadQueue(@Nonnull RenderContext renderContext) {
        GLLoadQueue.GLLoadable gLLoadable;
        if (this.framesWait != 0) {
            this.framesWait--;
            return;
        }
        int i = 0;
        int i2 = 0;
        while (true) {
            if (!TextureMemoryTracker.canAllocateMemory(0) || (gLLoadable = (GLLoadQueue.GLLoadable) this.loadQueue.poll()) == null) {
                break;
            } else if (!TextureMemoryTracker.canAllocateMemory(gLLoadable.GLGetLoadSize())) {
                TextureMemoryTracker.stall();
                this.loadQueue.add(gLLoadable);
                break;
            } else {
                int GLLoad = gLLoadable.GLLoad(renderContext, this) + i;
                this.framesWait = 3;
                int i3 = i2 + 1;
                if (i3 >= 16 || GLLoad >= 4194304) {
                    i2 = i3;
                    i = GLLoad;
                } else {
                    i2 = i3;
                    i = GLLoad;
                }
            }
        }
        if (i2 != 0) {
            Debug.Printf("waitForMemory: loadedCount %d, size %d", Integer.valueOf(i2), Integer.valueOf(i));
        }
    }
}
