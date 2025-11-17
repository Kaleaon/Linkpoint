package com.linkpoint.render.glres

abstract class GLSizedResource(
    manager: GLResourceManager,
    private val loadedSize: Int,
) : GLResource(manager) {
    fun getLoadedSize(): Int = loadedSize
}
