// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;

public abstract class GLLoadQueue
{
    final WeakQueue<GLLoadable> loadQueue;
    
    public GLLoadQueue() {
        this.loadQueue = new WeakQueue<GLLoadable>();
    }
    
    public abstract void RunLoadQueue(@Nonnull final RenderContext p0);
    
    public void StopLoadQueue() {
        this.loadQueue.clear();
    }
    
    public void add(@Nonnull final GLLoadable glLoadable) {
        this.loadQueue.offer(glLoadable);
    }
    
    public void remove(@Nonnull final GLLoadable glLoadable) {
        this.loadQueue.remove(glLoadable);
    }
    
    interface GLLoadHandler
    {
        void GLResourceLoaded(final GLLoadable p0);
    }
    
    interface GLLoadable
    {
        void GLCompleteLoad();
        
        int GLGetLoadSize();
        
        int GLLoad(final RenderContext p0, final GLLoadHandler p1);
    }
}
