// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

public abstract class GLSizedResource extends GLResource
{
    private final int loadedSize;
    
    protected GLSizedResource(final GLResourceManager glResourceManager, final int loadedSize) {
        super(glResourceManager);
        this.loadedSize = loadedSize;
    }
    
    public final int getLoadedSize() {
        return this.loadedSize;
    }
}
