package com.linkpoint.render.glres

import com.linkpoint.render.TextureMemoryTracker
import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.util.Collections
import java.util.IdentityHashMap
import java.util.Set
import java.util.WeakHashMap

class GLResourceManager {
    private val Object glCleanableLock = Object()
    private val Set<GLCleanable> glCleanables = Collections.newSetFromMap(WeakHashMap())
    /* access modifiers changed from: private */
    val ReferenceQueue<GLGenericResource> refQueue = ReferenceQueue<>()
    /* access modifiers changed from: private */
    val Set<GLGenericResourceReference> refSet = Collections.synchronizedSet(Collections.newSetFromMap(IdentityHashMap()))

    @JvmStatic
    abstract class GLGenericResourceReference : PhantomReference()<GLGenericResource> {
        public GLGenericResourceReference(GLGenericResource gLGenericResource, GLResourceManager gLResourceManager) {
            super(gLGenericResource, gLResourceManager.refQueue)
            gLResourceManager.refSet.add(this)
        }

        public abstract Unit GLFree()
    }

    @JvmStatic
    abstract class GLResourceReference : GLGenericResourceReference() {
        protected val Int handle

        public GLResourceReference(GLResource gLResource, Int i, GLResourceManager gLResourceManager) {
            super(gLResource, gLResourceManager)
            this.handle = i
            gLResourceManager.refSet.add(this)
        }
    }

    fun Cleanup() {
        synchronized (this.glCleanableLock) {
            this.glCleanables.size()
        }
        while (true) {
            val poll: Reference<? : GLGenericResource> = this.refQueue.poll()
            if (poll == null) {
                return
            }
            if (poll instanceof GLGenericResourceReference) {
                ((GLGenericResourceReference) poll).GLFree()
                this.refSet.remove(poll)
            }
        }
    }

    fun Flush() {
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

    fun addCleanable(gLCleanable: GLCleanable) {
        synchronized (this.glCleanableLock) {
            this.glCleanables.add(gLCleanable)
        }
    }
}
