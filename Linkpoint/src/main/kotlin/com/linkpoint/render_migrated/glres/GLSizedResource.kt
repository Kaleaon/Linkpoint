package com.linkpoint.render.glres

abstract class GLSizedResource : GLResource() {
    private val Int loadedSize

    protected GLSizedResource(GLResourceManager gLResourceManager, Int i) {
        super(gLResourceManager)
        this.loadedSize = i
    }

    val Int getLoadedSize() {
        return this.loadedSize
    }
}
