package com.lumiyaviewer.lumiya.render.glres;

public abstract class GLResource implements GLGenericResource {
    public final int handle;

    public GLResource(GLResourceManager gLResourceManager) {
        this.handle = Allocate(gLResourceManager);
    }

    protected abstract int Allocate(GLResourceManager gLResourceManager);
}
