package com.linkpoint.render

import com.linkpoint.Debug
import com.linkpoint.res.textures.TextureCache
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tracks texture-related memory consumption and coordinates with [TextureCache]
 * to throttle loading when the client approaches the configured budget.
 *
 * Ported from the original Lumiya Java implementation, keeping the same
 * reservation semantics while tightening the concurrency story with Kotlin.
 */
object TextureMemoryTracker {

    private const val PAGE_SIZE = 4096
    private const val RELEASE_DELAY_FRAMES = 4
    private const val TEXTURE_MAX_RESERVED_MEMORY = 32 * 1024 * 1024 // 32 MiB

    private val textureMemoryUsed = AtomicInteger(0)
    private val openJpegMemoryUsed = AtomicInteger(0)
    private val openJpegMemoryMmapped = AtomicInteger(0)
    private val textureMemoryLimit = AtomicInteger(64 * 1024 * 1024) // 64 MiB default
    private val textureMemoryReserved = AtomicInteger(TEXTURE_MAX_RESERVED_MEMORY)
    private val inflightLowMemory = AtomicBoolean(false)
    private val stalled = AtomicBoolean(false)
    private val bufMemory = AtomicInteger(0)

    private val delayedRelease = Array(RELEASE_DELAY_FRAMES) { AtomicInteger(0) }
    private val delayedReleaseBuf = Array(RELEASE_DELAY_FRAMES) { AtomicInteger(0) }
    private val delayedReleaseIndex = AtomicInteger(0)

    private val rendererLock = Any()
    private var activeRendererRef: WeakReference<Any>? = null

    private fun actualSize(bytes: Int): Int {
        val positive = if (bytes < 0) 0 else bytes
        return ((positive + PAGE_SIZE - 1) / PAGE_SIZE) * PAGE_SIZE
    }

    fun setMemoryLimit(limitBytes: Int) {
        textureMemoryLimit.set(limitBytes)
        val reserved = (limitBytes / 4).coerceAtMost(TEXTURE_MAX_RESERVED_MEMORY)
        textureMemoryReserved.set(reserved)
    }

    fun allocTextureMemory(bytes: Int) {
        textureMemoryUsed.addAndGet(actualSize(bytes))
        updateInflightMemoryLow()
        printStats()
    }

    fun allocBufferMemory(bytes: Int) {
        bufMemory.addAndGet(actualSize(bytes))
        printStats()
    }

    fun allocOpenJpegMemory(bytes: Int, memoryMapped: Boolean) {
        val size = actualSize(bytes)
        if (memoryMapped) {
            openJpegMemoryMmapped.addAndGet(size)
        }
        openJpegMemoryUsed.addAndGet(size)
        updateInflightMemoryLow()
        printStats()
    }

    fun releaseTextureMemory(bytes: Int) {
        delayedRelease[currentReleaseIndex()].addAndGet(actualSize(bytes))
    }

    fun releaseBufferMemory(bytes: Int) {
        delayedReleaseBuf[currentReleaseIndex()].addAndGet(actualSize(bytes))
    }

    fun releaseOpenJpegMemory(bytes: Int, memoryMapped: Boolean) {
        val size = actualSize(bytes)
        if (memoryMapped) {
            openJpegMemoryMmapped.addAndGet(-size)
        }
        openJpegMemoryUsed.addAndGet(-size)
        updateInflightMemoryLow()
        printStats()
    }

    fun releaseAllGLMemory() {
        textureMemoryUsed.set(0)
        bufMemory.set(0)
        stalled.set(false)
        updateInflightMemoryLow()
        printStats()
    }

    fun releaseAllFrameMemory() {
        for (i in 0 until RELEASE_DELAY_FRAMES) {
            delayedRelease[i].set(0)
            delayedReleaseBuf[i].set(0)
        }
        delayedReleaseIndex.set(0)
    }

    fun releaseFrameMemory() {
        var index = delayedReleaseIndex.incrementAndGet()
        if (index >= RELEASE_DELAY_FRAMES) {
            delayedReleaseIndex.set(0)
            index = 0
        }

        val textureBytes = delayedRelease[index].getAndSet(0)
        val bufBytes = delayedReleaseBuf[index].getAndSet(0)

        var changed = false
        if (textureBytes != 0) {
            textureMemoryUsed.addAndGet(-textureBytes)
            changed = true
        }
        if (bufBytes != 0) {
            bufMemory.addAndGet(-bufBytes)
            changed = true
        }

        if (changed) {
            updateInflightMemoryLow()
            printStats()
            stalled.set(false)
        }
    }

    fun stall() {
        stalled.set(true)
    }

    fun canAllocateMemory(bytes: Int): Boolean {
        if (stalled.get()) return false
        val requested = actualSize(bytes)
        val total = textureMemoryUsed.get() + bufMemory.get() + requested + textureMemoryReserved.get()
        return total < textureMemoryLimit.get()
    }

    fun setActiveRenderer(renderer: Any) {
        synchronized(rendererLock) {
            activeRendererRef = WeakReference(renderer)
        }
    }

    fun clearActiveRenderer(renderer: Any) {
        synchronized(rendererLock) {
            val active = activeRendererRef?.get()
            if (active == renderer || active == null) {
                activeRendererRef = null
            }
        }
    }

    fun hasActiveRenderer(): Boolean {
        synchronized(rendererLock) {
            return activeRendererRef?.get() != null
        }
    }

    private fun updateInflightMemoryLow() {
        val limit = textureMemoryLimit.get()
        val used = textureMemoryUsed.get() + openJpegMemoryUsed.get()
        val low = used + bufMemory.get() + textureMemoryReserved.get() > limit
        setInflightMemoryLow(low)
    }

    private fun setInflightMemoryLow(low: Boolean) {
        if (inflightLowMemory.getAndSet(low) != low) {
            TextureCache.getInstance().setTextureMemoryState(low)
        }
    }

    private fun currentReleaseIndex(): Int = delayedReleaseIndex.get()

    private fun printStats() {
        if (!Debug.isDebugBuild()) return
        Debug.Printf(
            "Texture mem used: %d Mb oj %d mmap %d tot %d limit %d Mb buf %dk",
            textureMemoryUsed.get() / 1_048_576,
            openJpegMemoryUsed.get() / 1_048_576,
            openJpegMemoryMmapped.get() / 1_048_576,
            (textureMemoryUsed.get() + openJpegMemoryUsed.get()) / 1_048_576,
            textureMemoryLimit.get() / 1_048_576,
            bufMemory.get() / 1024
        )
    }
}
