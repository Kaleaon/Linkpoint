package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import javax.annotation.Nonnull;

public class GLSyncLoadQueue extends GLLoadQueue implements GLLoadHandler {
    private static final int MAX_LOADS_PER_FRAME = 16;
    private static final int MAX_SIZE_PER_FRAME = 4194304;
    private static final int WAIT_FRAMES_AFTER_LOAD = 3;
    private int framesWait = 0;

    public void GLResourceLoaded(GLLoadable gLLoadable) {
        gLLoadable.GLCompleteLoad();
    }

    public void RunLoadQueue(@Nonnull RenderContext renderContext) {
        if (this.framesWait != 0) {
            this.framesWait--;
            return;
        }
        int i = 0;
        int i2 = 0;
        while (TextureMemoryTracker.canAllocateMemory(0)) {
            GLLoadable gLLoadable = (GLLoadable) this.loadQueue.poll();
            if (gLLoadable == null) {
                break;
            } else if (!TextureMemoryTracker.canAllocateMemory(gLLoadable.GLGetLoadSize())) {
                TextureMemoryTracker.stall();
                this.loadQueue.add(gLLoadable);
                break;
            } else {
                int GLLoad = gLLoadable.GLLoad(renderContext, this) + i;
                this.framesWait = 3;
                i = i2 + 1;
                if (i >= 16 || GLLoad >= 4194304) {
                    i2 = i;
                    i = GLLoad;
                    break;
                }
                i2 = i;
                i = GLLoad;
            }
        }
        if (i2 != 0) {
            Debug.Printf("waitForMemory: loadedCount %d, size %d", Integer.valueOf(i2), Integer.valueOf(i));
        }
    }
}
