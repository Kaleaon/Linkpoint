package com.linkpoint.render.glres

abstract class GLResource(gLResourceManager: GLResourceManager) : GLGenericResource {
    val handle: Int = Allocate(gLResourceManager)

    protected abstract fun Allocate(gLResourceManager: GLResourceManager): Int
}