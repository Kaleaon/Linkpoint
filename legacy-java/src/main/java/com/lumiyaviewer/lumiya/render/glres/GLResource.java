package com.lumiyaviewer.lumiya.render.glres;

/* loaded from: classes.dex */
public abstract class GLResource implements GLGenericResource {
    public final int handle;

    public GLResource(GLResourceManager gLResourceManager) {
        this.handle = Allocate(gLResourceManager);
    }

    protected abstract int Allocate(GLResourceManager gLResourceManager);
}
