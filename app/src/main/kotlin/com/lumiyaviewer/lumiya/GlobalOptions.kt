package com.lumiyaviewer.lumiya

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.preference.PreferenceManager
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Minimal Kotlin port of the legacy `GlobalOptions` singleton. The goal is to
 * provide a compilable, well-scoped baseline while the full feature set is
 * reconstructed. Only the behaviour that other modernised components depend on
 * is implemented; everything else is intentionally conservative.
 */
object GlobalOptions : SharedPreferences.OnSharedPreferenceChangeListener {

    enum class MeshRendering { HIGH, MEDIUM, LOW, LOWEST, DISABLED }

    data class GlobalOptionsChangedEvent(val preferences: SharedPreferences)

    private const val TAG = "GlobalOptions"

    private val initialized = AtomicBoolean(false)
    private val cacheDirUsed = AtomicBoolean(false)
    private val cacheBaseDirRef = AtomicReference<File?>()
    private val availableCacheDirsRef = AtomicReference<List<File>>(emptyList())
    private val maxTextureDownloads = AtomicInteger(2)

    @Volatile private var themeResId: Int = R.style.Theme_Lumiya_Light  // Uses dotted name Theme.Lumiya.Light
    @Volatile private var highQualityTextures: Boolean = false
    @Volatile private var compressedTextures: Boolean = true
    @Volatile private var keepWifiOn: Boolean = false
    @Volatile private var cloudSyncEnabled: Boolean = false
    @Volatile private var voiceEnabled: Boolean = false
    @Volatile private var meshRendering: MeshRendering = MeshRendering.MEDIUM

    fun initialize(context: Context? = null) {
        if (!initialized.compareAndSet(false, true)) {
            return
        }

        val targetContext = context ?: runCatching { LumiyaApp.getContext() }
            .getOrElse { error ->
                Log.w(TAG, "Skipping GlobalOptions initialisation; application context unavailable", error)
                initialized.set(false)
                return
            }

        val prefs = runCatching { PreferenceManager.getDefaultSharedPreferences(targetContext) }
            .getOrElse { error ->
                Log.e(TAG, "Failed to access default shared preferences", error)
                initialized.set(false)
                return
            }

        updateState(targetContext, prefs)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        val context = runCatching { LumiyaApp.getContext() }.getOrElse { error ->
            Log.w(TAG, "Preference change ignored; application context unavailable", error)
            return
        }
        updateState(context, sharedPreferences)
    }

    fun getThemeResourceId(): Int = themeResId
    fun isHighQualityTextures(): Boolean = highQualityTextures
    fun isCompressedTextures(): Boolean = compressedTextures
    fun getMaxTextureDownloads(): Int = maxTextureDownloads.get()
    fun keepWifiOn(): Boolean = keepWifiOn
    fun isCloudSyncEnabled(): Boolean = cloudSyncEnabled
    fun isVoiceEnabled(): Boolean = voiceEnabled
    fun getMeshRendering(): MeshRendering = meshRendering

    fun isCacheDirUsed(): Boolean = cacheDirUsed.get()

    fun getAvailableCacheDirs(): List<File> = availableCacheDirsRef.get()

    fun getCacheDir(child: String): File {
        val base = cacheBaseDirRef.get() ?: runCatching { LumiyaApp.getContext().cacheDir }
            .getOrElse { error ->
                Log.e(TAG, "Failed to resolve cache directory; application context unavailable", error)
                throw IllegalStateException("Unable to resolve cache directory", error)
            }
        val target = File(base, child)
        cacheDirUsed.set(true)
        if (!target.exists()) {
            target.mkdirs()
        }
        return target
    }

    @VisibleForTesting
    internal fun resetForTests() {
        initialized.set(false)
        cacheDirUsed.set(false)
        cacheBaseDirRef.set(null)
        availableCacheDirsRef.set(emptyList())
        maxTextureDownloads.set(2)
    }

    private fun updateState(context: Context, prefs: SharedPreferences) {
        cacheBaseDirRef.compareAndSet(null, context.cacheDir)
        availableCacheDirsRef.set(resolveCandidateCacheDirs(context))

        themeResId = when (prefs.getString("theme", "light")) {
            "dark" -> R.style.Theme_Lumiya
            "pink" -> R.style.Theme_Lumiya_Pink
            else -> R.style.Theme_Lumiya_Light
        }

        highQualityTextures = prefs.getBoolean("high_quality_textures", false)
        compressedTextures = prefs.getBoolean("compressed_textures", true)
        keepWifiOn = prefs.getBoolean("keep_wifi_on", false)
        cloudSyncEnabled = prefs.getBoolean("sync_to_gdrive", false)
        voiceEnabled = prefs.getBoolean("enableVoice", false)

        val downloads = prefs.getString("max_texture_downloads", null)
            ?.toIntOrNull()?.coerceAtLeast(1) ?: autoDetectTextureDownloads()
        maxTextureDownloads.set(downloads)

        meshRendering = prefs.getString("mesh_rendering", "medium")
            ?.let { runCatching { MeshRendering.valueOf(it.uppercase()) }.getOrNull() }
            ?: MeshRendering.MEDIUM
    }

    private fun resolveCandidateCacheDirs(context: Context): List<File> {
        val dirs = buildList {
            context.cacheDir?.let { add(it) }
            context.externalCacheDirs?.forEach { dir ->
                if (dir != null) add(dir)
            }
        }
        return dirs.distinct()
    }

    private fun autoDetectTextureDownloads(): Int {
        val processors = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        return when {
            processors >= 4 -> 6
            processors >= 2 -> 4
            else -> 2
        }
    }
}
