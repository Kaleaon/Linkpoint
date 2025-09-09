package com.lumiyaviewer.lumiya.render.glres;

public abstract class GLResource implements GLGenericResource {
    public final int handle;

    public GLResource(GLResourceManager gLResourceManager) {
        this.handle = Allocate(gLResourceManager);
    }

    /* access modifiers changed from: protected */
    public abstract int Allocate(GLResourceManager gLResourceManager);
}
