package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import javax.annotation.Nonnull;

public abstract class GLLoadQueue {
    final WeakQueue<GLLoadable> loadQueue = new WeakQueue();

    interface GLLoadHandler {
        void GLResourceLoaded(GLLoadable gLLoadable);
    }

    interface GLLoadable {
        void GLCompleteLoad();

        int GLGetLoadSize();

        int GLLoad(RenderContext renderContext, GLLoadHandler gLLoadHandler);
    }

    public abstract void RunLoadQueue(@Nonnull RenderContext renderContext);

    public void StopLoadQueue() {
        this.loadQueue.clear();
    }

    public void add(@Nonnull GLLoadable gLLoadable) {
        this.loadQueue.offer(gLLoadable);
    }

    public void remove(@Nonnull GLLoadable gLLoadable) {
        this.loadQueue.remove(gLLoadable);
    }
}
