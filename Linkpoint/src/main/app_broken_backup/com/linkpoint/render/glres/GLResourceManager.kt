package com.linkpoint.render.glres

import com.linkpoint.render.TextureMemoryTracker
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.util.*

/**
 * Manages GL resources and their cleanup via phantom references
 * Ensures proper cleanup when resources are garbage collected
 */
class GLResourceManager {
    
    private val glCleanableLock = Any()
    private val glCleanables: MutableSet<GLCleanable> = Collections.newSetFromMap(WeakHashMap())
    
    internal val refQueue = ReferenceQueue<GLGenericResource>()
    internal val refSet: MutableSet<GLGenericResourceReference> = 
        Collections.synchronizedSet(Collections.newSetFromMap(IdentityHashMap()))

    /**
     * Base phantom reference for generic GL resources
     */
    abstract class GLGenericResourceReference(
        resource: GLGenericResource,
        manager: GLResourceManager
    ) : PhantomReference<GLGenericResource>(resource, manager.refQueue) {
        
        init {
            manager.refSet.add(this)
        }

        /**
         * Free the GL resource
         */
        abstract fun GLFree()
    }

    /**
     * Phantom reference for GL resources with handles
     */
    abstract class GLResourceReference(
        resource: GLResource,
        protected val handle: Int,
        manager: GLResourceManager
    ) : GLGenericResourceReference(resource, manager) {
        
        init {
            manager.refSet.add(this)
        }
    }

    /**
     * Clean up any pending GL resources
     */
    fun Cleanup() {
        synchronized(glCleanableLock) {
            glCleanables.size
        }
        
        while (true) {
            val ref = refQueue.poll() ?: return
            
            if (ref is GLGenericResourceReference) {
                ref.GLFree()
                refSet.remove(ref)
            }
        }
    }

    /**
     * Flush all GL resources and cleanup
     */
    fun Flush() {
        synchronized(glCleanableLock) {
            glCleanables.forEach { cleanable ->
                cleanable?.GLCleanup()
            }
            glCleanables.clear()
        }
        
        // Clear reference queue
        while (refQueue.poll() != null) {
            // Just drain the queue
        }
        
        refSet.clear()
        TextureMemoryTracker.releaseAllGLMemory()
    }

    /**
     * Add a cleanable resource
     */
    fun addCleanable(cleanable: GLCleanable) {
        synchronized(glCleanableLock) {
            glCleanables.add(cleanable)
        }
    }
}
