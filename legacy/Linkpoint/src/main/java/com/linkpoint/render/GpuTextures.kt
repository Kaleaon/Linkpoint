package com.linkpoint.render

import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Texture
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

/**
 * Memory-budgeted LRU cache for uploaded Filament GPU textures with Android ComponentCallbacks2
 * memory pressure listeners.
 *
 * Tracks VRAM footprint across all mipmap levels and enforces hardware budget caps.
 * Pinned textures (e.g. visible in active camera frustum) are preserved during LRU
 * eviction and memory pressure purges. Evicted textures are marked so that
 * [WorldRenderer.refreshTextures] can request re-uploading from TextureFetcher when visible again.
 */
class GpuTextures(
    private val context: Context? = null,
    private val engine: Engine? = null,
    private var renderThreadExecutor: ((Runnable) -> Unit)? = null,
    private val customDestroyer: ((Texture) -> Unit)? = null
) : ComponentCallbacks2 {

    enum class TextureState {
        ACTIVE,
        PINNED,
        EVICTED
    }

    enum class TextureFormat {
        RGBA_8888,
        RGB_888,
        RGB_565,
        RGBA_4444,
        ETC2_RGB8,
        ETC2_RGBA8,
        ASTC_4x4,
        UNKNOWN
    }

    data class TextureEntry(
        val id: UUID,
        var texture: Texture?,
        val width: Int,
        val height: Int,
        val mipLevels: Int,
        val format: TextureFormat,
        val vramSizeBytes: Long,
        var state: TextureState,
        var lastAccessTimeNanos: Long
    )

    companion object {
        private const val TAG = "GpuTextures"

        val LOW_RAM_BUDGET_BYTES: Long = 256L * 1024L * 1024L // 256 MB
        val HIGH_RAM_BUDGET_BYTES: Long = 512L * 1024L * 1024L // 512 MB

        /**
         * Calculate the VRAM byte footprint for a texture, including all mipmap levels.
         */
        fun calculateVramSizeBytes(
            width: Int,
            height: Int,
            format: TextureFormat = TextureFormat.RGBA_8888,
            mipLevels: Int = 1
        ): Long {
            var totalBytes = 0L
            val levels = max(1, mipLevels)

            for (i in 0 until levels) {
                val w = max(1, width shr i)
                val h = max(1, height shr i)
                val levelBytes = when (format) {
                    TextureFormat.ETC2_RGB8 -> {
                        val blocksX = (w + 3) / 4
                        val blocksY = (h + 3) / 4
                        max(1, blocksX) * max(1, blocksY) * 8L
                    }
                    TextureFormat.ETC2_RGBA8, TextureFormat.ASTC_4x4 -> {
                        val blocksX = (w + 3) / 4
                        val blocksY = (h + 3) / 4
                        max(1, blocksX) * max(1, blocksY) * 16L
                    }
                    TextureFormat.RGB_888 -> w.toLong() * h.toLong() * 3L
                    TextureFormat.RGB_565, TextureFormat.RGBA_4444 -> w.toLong() * h.toLong() * 2L
                    TextureFormat.RGBA_8888, TextureFormat.UNKNOWN -> w.toLong() * h.toLong() * 4L
                }
                totalBytes += levelBytes
            }
            return totalBytes
        }

        private fun logI(msg: String) { runCatching { Log.i(TAG, msg) } }
        private fun logW(msg: String) { runCatching { Log.w(TAG, msg) } }
        private fun logE(msg: String, e: Throwable? = null) { runCatching { Log.e(TAG, msg, e) } }

        /**
         * Determines initial VRAM budget cap based on device RAM tier.
         */
        fun determineDefaultVramBudget(context: Context?): Long {
            if (context == null) return HIGH_RAM_BUDGET_BYTES
            return try {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                if (am != null) {
                    val mi = ActivityManager.MemoryInfo()
                    am.getMemoryInfo(mi)
                    val totalGb = mi.totalMem / (1024L * 1024L * 1024L)
                    if (totalGb < 4) LOW_RAM_BUDGET_BYTES else HIGH_RAM_BUDGET_BYTES
                } else {
                    HIGH_RAM_BUDGET_BYTES
                }
            } catch (e: Exception) {
                logW("Failed to query system RAM tier, defaulting to 512MB budget: ${e.message}")
                HIGH_RAM_BUDGET_BYTES
            }
        }
    }

    private val lock = Any()
    private val entries = LinkedHashMap<UUID, TextureEntry>(16, 0.75f, true)
    private val currentVramBytes = AtomicLong(0L)
    @Volatile private var maxVramBudgetBytes: Long = determineDefaultVramBudget(context)
    @Volatile private var isRegistered = false

    init {
        registerComponentCallbacks()
    }

    fun setRenderThreadExecutor(executor: (Runnable) -> Unit) {
        this.renderThreadExecutor = executor
    }

    fun registerComponentCallbacks() {
        if (!isRegistered && context != null) {
            try {
                context.applicationContext.registerComponentCallbacks(this)
                isRegistered = true
                logI("Registered ComponentCallbacks2 for VRAM pressure events")
            } catch (e: Exception) {
                logW("Could not register ComponentCallbacks2: ${e.message}")
            }
        }
    }

    fun unregisterComponentCallbacks() {
        if (isRegistered && context != null) {
            try {
                context.applicationContext.unregisterComponentCallbacks(this)
                isRegistered = false
                logI("Unregistered ComponentCallbacks2")
            } catch (e: Exception) {
                logW("Could not unregister ComponentCallbacks2: ${e.message}")
            }
        }
    }

    fun setMaxVramBudgetBytes(bytes: Long) {
        require(bytes > 0) { "Budget must be positive" }
        maxVramBudgetBytes = bytes
        synchronized(lock) {
            enforceBudget(0L)
        }
    }

    fun getMaxVramBudgetBytes(): Long = maxVramBudgetBytes

    fun getCurrentVramUsageBytes(): Long = currentVramBytes.get()

    fun getActiveTextureCount(): Int {
        synchronized(lock) {
            return entries.values.count { it.state != TextureState.EVICTED }
        }
    }

    fun getEvictedTextureCount(): Int {
        synchronized(lock) {
            return entries.values.count { it.state == TextureState.EVICTED }
        }
    }

    fun getTextureState(id: UUID): TextureState {
        synchronized(lock) {
            return entries[id]?.state ?: TextureState.EVICTED
        }
    }

    /**
     * Upload / register a texture in the VRAM cache.
     * Triggers automatic LRU eviction of unpinned textures if VRAM budget is exceeded.
     */
    fun uploadTexture(
        id: UUID,
        texture: Texture?,
        width: Int,
        height: Int,
        format: TextureFormat = TextureFormat.RGBA_8888,
        mipLevels: Int = 1,
        isPinned: Boolean = false
    ): Boolean {
        val vramBytes = calculateVramSizeBytes(width, height, format, mipLevels)

        synchronized(lock) {
            val existing = entries[id]
            val netAddition = if (existing != null && existing.state != TextureState.EVICTED) {
                vramBytes - existing.vramSizeBytes
            } else {
                vramBytes
            }

            // Evict off-screen textures if exceeding budget
            if (currentVramBytes.get() + netAddition > maxVramBudgetBytes) {
                enforceBudget(netAddition)
            }

            if (existing != null) {
                if (existing.state != TextureState.EVICTED) {
                    currentVramBytes.addAndGet(-existing.vramSizeBytes)
                }
                existing.texture = texture
                existing.state = if (isPinned) TextureState.PINNED else TextureState.ACTIVE
                existing.lastAccessTimeNanos = System.nanoTime()
                currentVramBytes.addAndGet(vramBytes)
                entries[id] = existing // refresh LRU position
            } else {
                val state = if (isPinned) TextureState.PINNED else TextureState.ACTIVE
                val entry = TextureEntry(
                    id = id,
                    texture = texture,
                    width = width,
                    height = height,
                    mipLevels = mipLevels,
                    format = format,
                    vramSizeBytes = vramBytes,
                    state = state,
                    lastAccessTimeNanos = System.nanoTime()
                )
                entries[id] = entry
                currentVramBytes.addAndGet(vramBytes)
            }
        }
        return true
    }

    /**
     * Retrieves an active texture and updates its LRU access timestamp.
     */
    fun getTexture(id: UUID): Texture? {
        synchronized(lock) {
            val entry = entries[id] ?: return null
            if (entry.state == TextureState.EVICTED) return null
            entry.lastAccessTimeNanos = System.nanoTime()
            entries[id] = entry // Move to MRU position in LinkedHashMap
            return entry.texture
        }
    }

    /**
     * Pins visible textures so they cannot be evicted during frame rendering.
     */
    fun pin(id: UUID) {
        synchronized(lock) {
            val entry = entries[id]
            if (entry != null && entry.state != TextureState.EVICTED) {
                entry.state = TextureState.PINNED
            }
        }
    }

    /**
     * Pins multiple visible textures.
     */
    fun pinAll(ids: Collection<UUID>) {
        synchronized(lock) {
            for (id in ids) {
                val entry = entries[id]
                if (entry != null && entry.state != TextureState.EVICTED) {
                    entry.state = TextureState.PINNED
                }
            }
        }
    }

    /**
     * Unpins a texture, returning it to ACTIVE state.
     */
    fun unpin(id: UUID) {
        synchronized(lock) {
            val entry = entries[id]
            if (entry != null && entry.state == TextureState.PINNED) {
                entry.state = TextureState.ACTIVE
            }
        }
    }

    /**
     * Unpins all currently pinned textures.
     */
    fun unpinAll() {
        synchronized(lock) {
            for (entry in entries.values) {
                if (entry.state == TextureState.PINNED) {
                    entry.state = TextureState.ACTIVE
                }
            }
        }
    }

    /**
     * Evicts unpinned LRU textures to free up space for [requiredAdditionBytes].
     */
    private fun enforceBudget(requiredAdditionBytes: Long) {
        val targetLimit = maxVramBudgetBytes - requiredAdditionBytes
        var simulatedBytes = currentVramBytes.get()
        val toEvict = mutableListOf<TextureEntry>()

        for (entry in entries.values) {
            if (simulatedBytes <= targetLimit) break
            if (entry.state != TextureState.PINNED && entry.state != TextureState.EVICTED) {
                toEvict.add(entry)
                simulatedBytes -= entry.vramSizeBytes
            }
        }

        for (entry in toEvict) {
            destroyAndEvictEntryLocked(entry)
        }
    }

    /**
     * Purges all unpinned off-screen textures from VRAM.
     * Safe to call on OS memory pressure events.
     */
    fun purgeUnpinned() {
        val action = Runnable {
            synchronized(lock) {
                val unpinnedEntries = entries.values.filter {
                    it.state != TextureState.PINNED && it.state != TextureState.EVICTED
                }
                logI("Purging ${unpinnedEntries.size} unpinned VRAM textures on memory pressure signal")
                for (entry in unpinnedEntries) {
                    destroyAndEvictEntryLocked(entry)
                }
            }
        }

        val executor = renderThreadExecutor
        if (executor != null) {
            executor.invoke(action)
        } else {
            action.run()
        }
    }

    private fun destroyAndEvictEntryLocked(entry: TextureEntry) {
        val texToDestroy = entry.texture
        entry.texture = null
        entry.state = TextureState.EVICTED
        currentVramBytes.addAndGet(-entry.vramSizeBytes)

        if (texToDestroy != null) {
            destroyTextureHandle(texToDestroy)
        }
    }

    private fun destroyTextureHandle(texture: Texture) {
        if (customDestroyer != null) {
            customDestroyer.invoke(texture)
            return
        }
        val eng = engine ?: return
        val action = Runnable {
            try {
                eng.destroyTexture(texture)
            } catch (e: Exception) {
                logE("Error destroying Filament texture handle: ${e.message}", e)
            }
        }
        val executor = renderThreadExecutor
        if (executor != null) {
            executor.invoke(action)
        } else {
            action.run()
        }
    }

    // --- ComponentCallbacks2 Implementation ---

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onTrimMemory(level: Int) {
        logI("onTrimMemory received level=$level")
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> {
                purgeUnpinned()
            }
        }
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onLowMemory() {
        logW("onLowMemory callback triggered by OS")
        purgeUnpinned()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // No-op
    }
}
