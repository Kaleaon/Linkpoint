package com.linkpoint.render.glres

abstract class GLSizedResource(
    gLResourceManager: GLResourceManager,
    private val loadedSize: Int
) : GLResource(gLResourceManager) {
    
    fun getLoadedSize(): Int {
        return loadedSize
    }
}