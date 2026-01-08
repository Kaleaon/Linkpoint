package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.render.glres.buffers.GLBuffer
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject

open class GLResourceManager {
    fun addCleanable(cleanable: GLCleanable) {}
    
    fun enqueueOcclusionQuery(query: GLQuery) {}
    
    open class GLGenericResourceReference(
        val resource: GLGenericResource,
        val manager: GLResourceManager
    ) {
        open fun GLFree() {}
    }

    open class GLResourceReference(
        resource: GLResource,
        val handle: Int, 
        manager: GLResourceManager
    ) : GLGenericResourceReference(resource, manager)
}

// Fixed: glResourceManager passed to super without val to avoid hiding
open class GLResource(glResourceManager: GLResourceManager) : GLGenericResource(glResourceManager) {
    var handle: Int = 0
    
    open fun Allocate(manager: GLResourceManager): Int {
        return 0
    }
}
