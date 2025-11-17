package com.linkpoint.render.glres

abstract class GLResource(manager: GLResourceManager) : GLGenericResource {
    val handle: Int = Allocate(manager)

    protected abstract fun Allocate(manager: GLResourceManager): Int
}
