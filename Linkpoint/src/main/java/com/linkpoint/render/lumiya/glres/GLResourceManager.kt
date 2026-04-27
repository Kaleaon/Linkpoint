package com.linkpoint.render.lumiya.glres

import android.opengl.GLES32
import android.util.Log
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Manages the lifecycle of GPU resources (textures, buffers, VAOs, FBOs).
 *
 * Design lineage: Lumiya `GLResourceManager.java`, modernised.
 *
 * Resources are tracked via phantom references so that the GL handle is
 * cleaned up even if the owner is garbage-collected without an explicit
 * `destroy()` call.  Deferred cleanup runs once per frame on the GL thread.
 */
class GLResourceManager(private val glThreadGuard: ((String) -> Unit)? = null) {

    companion object {
        private const val TAG = "GLResourceManager"
    }

    /** Tracks total GPU memory allocated (bytes, approximate). */
    @Volatile
    var allocatedBytes: Long = 0L; private set

    // Deferred-delete queues (filled from any thread, drained on GL thread)
    private val pendingTextureDeletes = ConcurrentLinkedQueue<Int>()
    private val pendingBufferDeletes = ConcurrentLinkedQueue<Int>()
    private val pendingVAODeletes = ConcurrentLinkedQueue<Int>()
    private val pendingFBODeletes = ConcurrentLinkedQueue<Int>()
    private val pendingRBODeletes = ConcurrentLinkedQueue<Int>()

    // Phantom-reference tracking for automatic cleanup
    private val refQueue = ReferenceQueue<Any>()
    private val trackedRefs = mutableSetOf<GLResourceRef>()

    // ── Allocation helpers ───────────────────────────────────────────────

    fun createTexture(): Int {
        requireGlThread("createTexture")
        val buf = IntArray(1)
        GLES32.glGenTextures(1, buf, 0)
        return buf[0]
    }

    fun createBuffer(): Int {
        requireGlThread("createBuffer")
        val buf = IntArray(1)
        GLES32.glGenBuffers(1, buf, 0)
        return buf[0]
    }

    fun createVAO(): Int {
        requireGlThread("createVAO")
        val buf = IntArray(1)
        GLES32.glGenVertexArrays(1, buf, 0)
        return buf[0]
    }

    fun createFramebuffer(): Int {
        requireGlThread("createFramebuffer")
        val buf = IntArray(1)
        GLES32.glGenFramebuffers(1, buf, 0)
        return buf[0]
    }

    fun createRenderbuffer(): Int {
        requireGlThread("createRenderbuffer")
        val buf = IntArray(1)
        GLES32.glGenRenderbuffers(1, buf, 0)
        return buf[0]
    }

    // ── Deferred deletion (thread-safe) ──────────────────────────────────

    fun deleteTexture(handle: Int) { if (handle != 0) pendingTextureDeletes.add(handle) }
    fun deleteBuffer(handle: Int)  { if (handle != 0) pendingBufferDeletes.add(handle) }
    fun deleteVAO(handle: Int)     { if (handle != 0) pendingVAODeletes.add(handle) }
    fun deleteFBO(handle: Int)     { if (handle != 0) pendingFBODeletes.add(handle) }
    fun deleteRBO(handle: Int)     { if (handle != 0) pendingRBODeletes.add(handle) }

    // ── Tracking (optional automatic cleanup) ────────────────────────────

    fun track(owner: Any, type: ResourceType, handle: Int) {
        val ref = GLResourceRef(owner, refQueue, type, handle)
        synchronized(trackedRefs) { trackedRefs.add(ref) }
    }

    fun addMemory(bytes: Long)    { allocatedBytes += bytes }
    fun removeMemory(bytes: Long) { allocatedBytes -= bytes }

    // ── Per-frame cleanup (call on GL thread) ────────────────────────────

    fun cleanup() {
        requireGlThread("cleanup")
        drainQueue(pendingTextureDeletes) { GLES32.glDeleteTextures(1, intArrayOf(it), 0) }
        drainQueue(pendingBufferDeletes)  { GLES32.glDeleteBuffers(1, intArrayOf(it), 0) }
        drainQueue(pendingVAODeletes)     { GLES32.glDeleteVertexArrays(1, intArrayOf(it), 0) }
        drainQueue(pendingFBODeletes)     { GLES32.glDeleteFramebuffers(1, intArrayOf(it), 0) }
        drainQueue(pendingRBODeletes)     { GLES32.glDeleteRenderbuffers(1, intArrayOf(it), 0) }

        // Process phantom references
        processPhantomRefs()
    }

    /** Full flush – destroy everything pending. */
    fun flush() {
        requireGlThread("flush")
        cleanup()
        synchronized(trackedRefs) { trackedRefs.clear() }
        Log.i(TAG, "Resource manager flushed (allocated ≈ ${allocatedBytes / 1024} KB)")
    }

    // ── Internals ────────────────────────────────────────────────────────

    fun assertGlThread(apiName: String) {
        requireGlThread(apiName)
    }

    private fun requireGlThread(apiName: String) {
        glThreadGuard?.invoke("GLResourceManager.$apiName")
    }

    private inline fun drainQueue(queue: ConcurrentLinkedQueue<Int>, delete: (Int) -> Unit) {
        var handle = queue.poll()
        while (handle != null) {
            delete(handle)
            handle = queue.poll()
        }
    }

    private fun processPhantomRefs() {
        var ref = refQueue.poll()
        while (ref != null) {
            if (ref is GLResourceRef) {
                when (ref.type) {
                    ResourceType.TEXTURE -> pendingTextureDeletes.add(ref.handle)
                    ResourceType.BUFFER -> pendingBufferDeletes.add(ref.handle)
                    ResourceType.VAO -> pendingVAODeletes.add(ref.handle)
                    ResourceType.FBO -> pendingFBODeletes.add(ref.handle)
                    ResourceType.RBO -> pendingRBODeletes.add(ref.handle)
                }
                synchronized(trackedRefs) { trackedRefs.remove(ref) }
            }
            ref = refQueue.poll()
        }
    }

    // ── Inner types ──────────────────────────────────────────────────────

    enum class ResourceType { TEXTURE, BUFFER, VAO, FBO, RBO }

    private class GLResourceRef(
        referent: Any,
        queue: ReferenceQueue<Any>,
        val type: ResourceType,
        val handle: Int
    ) : PhantomReference<Any>(referent, queue)
}
