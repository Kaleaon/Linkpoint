// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

public abstract class GLResource implements GLGenericResource
{
    public final int handle;
    
    public GLResource(final GLResourceManager glResourceManager) {
        this.handle = this.Allocate(glResourceManager);
    }
    
    protected abstract int Allocate(final GLResourceManager p0);
}
