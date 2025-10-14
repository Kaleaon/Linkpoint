package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.util.Collections
import java.util.IdentityHashMap
import java.util.Set
import java.util.WeakHashMap

class GLResourceManager {
    private val glCleanableLock: Any = new Object()
    private val glCleanables: Set<GLCleanable> = Collections.newSetFromMap(new WeakHashMap())
    /* access modifiers changed from: private */
    ReferenceQueue<GLGenericResource> refQueue = new ReferenceQueue<>()
    /* access modifiers changed from: private */
    Set<GLGenericResourceReference> refSet = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap()))

    abstract class GLGenericResourceReference extends PhantomReference<GLGenericResource> {
        GLGenericResourceReference(GLGenericResource gLGenericResource, GLResourceManager gLResourceManager) {
            super(gLGenericResource, gLResourceManager.refQueue)
            gLResourceManager.refSet.add(this)
        }

        abstract fun GLFree(): Unit
    }

    abstract class GLResourceReference extends GLGenericResourceReference {
        protected int handle

        GLResourceReference(GLResource gLResource, int i, GLResourceManager gLResourceManager) {
            super(gLResource, gLResourceManager)
            this.handle = i
            gLResourceManager.refSet.add(this)
        }
    }

    fun Cleanup(): Unit {
        synchronized (this.glCleanableLock) {
            this.glCleanables.size()
        }
        while (true) {
            Reference<? extends GLGenericResource> poll = this.refQueue.poll()
            if (poll == null) {
                return
            }
            if (poll instanceof GLGenericResourceReference) {
                ((GLGenericResourceReference) poll).GLFree()
                this.refSet.remove(poll)
            }
        }
    }

    fun Flush(): Unit {
        synchronized (this.glCleanableLock) {
            for (GLCleanable gLCleanable : this.glCleanables) {
                if (gLCleanable != null) {
                    gLCleanable.GLCleanup()
                }
            }
            this.glCleanables.clear()
        }
        do {
        } while (this.refQueue.poll() != null)
        this.refSet.clear()
        TextureMemoryTracker.releaseAllGLMemory()
    }

    fun addCleanable(gLCleanable: GLCleanable): Unit {
        synchronized (this.glCleanableLock) {
            this.glCleanables.add(gLCleanable)
        }
    }
}
