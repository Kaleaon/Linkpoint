package com.linkpoint.render.glres

abstract class GLResource : GLGenericResource {
    val Int handle

    public GLResource(GLResourceManager gLResourceManager) {
        this.handle = Allocate(gLResourceManager)
    }

    /* access modifiers changed from: protected */
    public abstract Int Allocate(GLResourceManager gLResourceManager)
}
