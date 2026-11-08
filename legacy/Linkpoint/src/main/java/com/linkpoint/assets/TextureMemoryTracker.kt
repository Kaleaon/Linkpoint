package com.linkpoint.assets

import android.util.Log
import java.util.concurrent.atomic.AtomicLong

/**
 * Native + GPU texture memory accounting for [LinkpointTexture].
 *
 * Mirrors the role of Lumiya's `TextureMemoryTracker.allocOpenJpegMemory(...)`:
 * every native-heap allocation, mmap, and Filament upload that backs a texture
 * is reported here so the cache and pressure handlers have a single source of
 * truth for "how much texture memory is in flight right now".
 *
 * This is intentionally a process-wide singleton with atomic counters — the
 * call sites are on hot paths (per-texture-load and per-eviction), so a lock
 * is not acceptable.
 */
object TextureMemoryTracker {

    private const val TAG = "TextureMemoryTracker"

    private val nativeHeapBytes = AtomicLong(0)
    private val mmappedBytes = AtomicLong(0)
    private val gpuBytes = AtomicLong(0)
    private val liveTextures = AtomicLong(0)

    fun allocNative(bytes: Long) {
        require(bytes >= 0) { "negative alloc: $bytes" }
        nativeHeapBytes.addAndGet(bytes)
    }

    fun freeNative(bytes: Long) {
        require(bytes >= 0) { "negative free: $bytes" }
        val after = nativeHeapBytes.addAndGet(-bytes)
        if (after < 0) Log.w(TAG, "nativeHeapBytes went negative: $after (double-free?)")
    }

    fun allocMmapped(bytes: Long) {
        require(bytes >= 0) { "negative alloc: $bytes" }
        mmappedBytes.addAndGet(bytes)
    }

    fun freeMmapped(bytes: Long) {
        require(bytes >= 0) { "negative free: $bytes" }
        val after = mmappedBytes.addAndGet(-bytes)
        if (after < 0) Log.w(TAG, "mmappedBytes went negative: $after (double-free?)")
    }

    fun allocGpu(bytes: Long) {
        require(bytes >= 0) { "negative alloc: $bytes" }
        gpuBytes.addAndGet(bytes)
    }

    fun freeGpu(bytes: Long) {
        require(bytes >= 0) { "negative free: $bytes" }
        val after = gpuBytes.addAndGet(-bytes)
        if (after < 0) Log.w(TAG, "gpuBytes went negative: $after (double-free?)")
    }

    fun textureOpened() {
        liveTextures.incrementAndGet()
    }

    fun textureClosed() {
        val after = liveTextures.decrementAndGet()
        if (after < 0) Log.w(TAG, "liveTextures went negative: $after (double-close?)")
    }

    fun snapshot(): Snapshot = Snapshot(
        nativeHeapBytes = nativeHeapBytes.get(),
        mmappedBytes = mmappedBytes.get(),
        gpuBytes = gpuBytes.get(),
        liveTextures = liveTextures.get(),
    )

    data class Snapshot(
        val nativeHeapBytes: Long,
        val mmappedBytes: Long,
        val gpuBytes: Long,
        val liveTextures: Long,
    ) {
        val totalBytes: Long get() = nativeHeapBytes + mmappedBytes + gpuBytes
    }
}
