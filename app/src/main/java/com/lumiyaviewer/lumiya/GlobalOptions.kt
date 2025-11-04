package com.lumiyaviewer.lumiya

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.res.mesh.MeshCache
import com.lumiyaviewer.lumiya.res.textures.TextureCache
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds
import com.lumiyaviewer.lumiya.ui.settings.NotificationType
import com.lumiyaviewer.lumiya.ui.settings.ThemeChangedEvent
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Kotlin translation of the original Lumiya GlobalOptions singleton.
 * Behaviour matches the historical Java implementation while applying
 * modern Kotlin idioms and null-safety.
 */
object GlobalOptions : SharedPreferences.OnSharedPreferenceChangeListener {

    fun getInstance(): GlobalOptions = this

    enum class MeshRendering(val lodName: String?) {
        HIGH("high_lod"),
        MEDIUM("medium_lod"),
        LOW("low_lod"),
        LOWEST("lowest_lod"),
        DISABLED(null)
    }

    data class GlobalOptionsChangedEvent(val preferences: SharedPreferences)

    private val baseCacheDirRef = AtomicReference<File?>()
    private val availableCacheDirsRef: AtomicReference<ImmutableList<File>> =
        AtomicReference(ImmutableList.of())
    private val cacheDirUsed = AtomicBoolean(false)

    private var themeResourceId: Int = R.style.Theme_Lumiya_Light
    private var legacyUserNames: Boolean = false
    private var showTimestamps: Boolean = true

    private var highQualityTextures: Boolean = false
    private var compressedTextures: Boolean = true
    private var terrainTextures: Boolean = true
    private var renderClouds: Boolean = true
    private var useFXAA: Boolean = false

    private var keepWifiOn: Boolean = false
    private var cloudSyncEnabled: Boolean = false
    private var voiceEnabled: Boolean = false

    private var meshRendering: MeshRendering = MeshRendering.MEDIUM
    private var maxTextureDownloads: Int = 2
    private var rlvEnabled: Boolean = false

    private var autoReconnect: Boolean = true
    private var maxReconnectAttempts: Int = 10

    private var hoverTextEnableHUDs: Boolean = true
    private var hoverTextEnableObjects: Boolean = false

    private var advancedRendering: Boolean = true
    private var forceDaylightTime: Boolean = false
    private var forceDaylightHour: Float = 0.5f

    /** Initialisation **/

    fun initialize() {
        val prefs = LumiyaApp.getDefaultSharedPreferences()
        updateFromPreferences(LumiyaApp.getContext(), prefs)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        updateFromPreferences(LumiyaApp.getContext(), sharedPreferences)
        EventBus.getInstance().publish(GlobalOptionsChangedEvent(sharedPreferences))
    }

    /** Public surface **/

    fun enableVoice() {
        LumiyaApp.getDefaultSharedPreferences()
            .edit()
            .putBoolean("enableVoice", true)
            .apply()
    }

    fun isCacheDirUsed(): Boolean = cacheDirUsed.get()
    fun getAvailableCacheDirs(): ImmutableList<File> = availableCacheDirsRef.get()
    fun getBaseCacheDir(): File? = baseCacheDirRef.get()
    fun getThemeResourceId(): Int = themeResourceId
    fun isLegacyUserNames(): Boolean = legacyUserNames
    fun getShowTimestamps(): Boolean = showTimestamps
    fun getHighQualityTextures(): Boolean = highQualityTextures
    fun getCompressedTextures(): Boolean = compressedTextures
    fun getKeepWifiOn(): Boolean = keepWifiOn
    fun isCloudSyncEnabled(): Boolean = cloudSyncEnabled
    fun isVoiceEnabled(): Boolean = voiceEnabled
    fun getMeshRendering(): MeshRendering = meshRendering
    fun getMaxTextureDownloads(): Int = maxTextureDownloads
    fun isRlvEnabled(): Boolean = rlvEnabled
    fun isAdvancedRendering(): Boolean = advancedRendering
    fun getForceDaylightTime(): Boolean = forceDaylightTime
    fun getForceDaylightHour(): Float = forceDaylightHour
    fun getTerrainTextures(): Boolean = terrainTextures
    fun getRenderClouds(): Boolean = renderClouds
    fun isFxaaEnabled(): Boolean = useFXAA
    fun getAutoReconnect(): Boolean = autoReconnect
    fun getMaxReconnectAttempts(): Int = maxReconnectAttempts
    fun getHoverTextEnableHUDs(): Boolean = hoverTextEnableHUDs
    fun getHoverTextEnableObjects(): Boolean = hoverTextEnableObjects

    fun getCacheDir(child: String): File {
        cacheDirUsed.set(true)
        val parent = baseCacheDirRef.get() ?: LumiyaApp.getContext().cacheDir
        val directory = File(parent, child)
        try {
            directory.mkdirs()
        } catch (_: Exception) {
        }
        return directory
    }

    /** Preference reconciliation **/

    private fun updateFromPreferences(context: Context, prefs: SharedPreferences) {
        Debug.Printf("Updating options from preferences.")

        updateNotificationSoundDefault(prefs, NotificationType.Private)
        updateNotificationSoundDefault(prefs, NotificationType.Group)
        updateNotificationSoundDefault(prefs, NotificationType.LocalChat)

        if (!prefs.getBoolean("system_defaults_set", false)) {
            val editor = prefs.edit()
            val totalMemoryKb = getTotalMemory()
            val processors = Runtime.getRuntime().availableProcessors()

            editor.putBoolean(
                "high_quality_textures",
                processors >= 2 && totalMemoryKb > 524_288
            )

            val memoryMb = when {
                totalMemoryKb == 0L -> 64
                totalMemoryKb <= 262_144 -> 32
                totalMemoryKb <= 524_288 -> 64
                else -> 128
            }
            editor.putString("texture_memory_limit", memoryMb.toString())

            val downloads = when {
                processors >= 4 && totalMemoryKb > 524_288 -> 8
                processors >= 2 -> 4
                else -> 2
            }
            editor.putString("max_texture_downloads", downloads.toString())
            editor.putBoolean("system_defaults_set", true)
            editor.commit()
        }

        val previousTheme = themeResourceId
        themeResourceId = when (Strings.nullToEmpty(prefs.getString("theme", "light"))) {
            "dark" -> R.style.Theme_Lumiya
            "pink" -> R.style.Theme_Lumiya_Pink
            else -> R.style.Theme_Lumiya_Light
        }

        legacyUserNames = prefs.getBoolean("legacyUserNames", false)
        showTimestamps = prefs.getBoolean("chatTimestamps", true)
        highQualityTextures = prefs.getBoolean("high_quality_textures", false)
        compressedTextures = prefs.getBoolean("compressed_textures", true)
        keepWifiOn = prefs.getBoolean("keep_wifi_on", true)
        cloudSyncEnabled = prefs.getBoolean("sync_to_gdrive", false)

        voiceEnabled = prefs.getBoolean("enableVoice", false) &&
            VoicePluginServiceConnection.isPluginSupported()

        maxTextureDownloads = prefs.getString("max_texture_downloads", "2")
            ?.toIntOrNull()?.coerceAtLeast(1) ?: 2
        TextureCache.getInstance().setMaxTextureDownloads(maxTextureDownloads)

        terrainTextures = prefs.getBoolean("terrain_textures", true)

        val memoryLimitMb = prefs.getString("texture_memory_limit", "64")
            ?.toIntOrNull() ?: 64
        TextureMemoryTracker.setMemoryLimit(memoryLimitMb * 1_048_576)

        meshRendering = prefs.getString("mesh_rendering", "high")
            ?.let { value ->
                runCatching { MeshRendering.valueOf(value.uppercase()) }.getOrNull()
            } ?: MeshRendering.HIGH

        rlvEnabled = prefs.getBoolean("rlv_enabled", false)
        autoReconnect = prefs.getBoolean("auto_reconnect", true)
        maxReconnectAttempts = prefs.getString("reconnect_attempts", "10")
            ?.toIntOrNull() ?: 10

        updateCacheDir(context, prefs)

        when (prefs.getString("hover_text", "huds")) {
            "all" -> {
                hoverTextEnableHUDs = true
                hoverTextEnableObjects = true
            }
            "none" -> {
                hoverTextEnableHUDs = false
                hoverTextEnableObjects = false
            }
            else -> {
                hoverTextEnableHUDs = true
                hoverTextEnableObjects = false
            }
        }

        advancedRendering = prefs.getBoolean("advanced_rendering", true)
        useFXAA = prefs.getBoolean("fxaa_enable", false)
        renderClouds = prefs.getBoolean("clouds_enable", true)

        val renderTime = prefs.getString("render_time_of_day", "sim") ?: "sim"
        if (renderTime.equals("sim", ignoreCase = true)) {
            forceDaylightTime = false
            forceDaylightHour = 0.5f
        } else {
            val parsed = renderTime.toFloatOrNull()
            if (parsed != null) {
                forceDaylightTime = true
                forceDaylightHour = parsed
            } else {
                forceDaylightTime = false
                forceDaylightHour = 0.5f
            }
        }

        if (previousTheme != themeResourceId) {
            EventBus.getInstance().publish(ThemeChangedEvent(themeResourceId))
        }
    }

    private fun updateNotificationSoundDefault(prefs: SharedPreferences, type: NotificationType) {
        if (prefs.contains(type.ringtoneKey)) return

        val defaultSound = NotificationSounds.defaultSounds[type] ?: return
        val uri: Uri = defaultSound.uri
        prefs.edit()
            .putString(type.ringtoneKey, uri.toString())
            .apply()
        Debug.Printf(
            "NotificationSounds: Updated %s preference to %s",
            type.ringtoneKey,
            uri
        )
    }

    private fun updateCacheDir(context: Context, prefs: SharedPreferences) {
        val current = baseCacheDirRef.get()
        val requestedPath = Strings.nullToEmpty(prefs.getString("cache_location", "")).trim()
        var selected: File? = null

        if (current != null && requestedPath.isEmpty() && isCacheDirectoryWritable(current)) {
            selected = current
        }

        val discovered = mutableListOf<File>()
        ContextCompat.getExternalCacheDirs(context)?.forEach { cache ->
            if (cache != null) discovered += cache
        }
        context.cacheDir?.let(discovered::add)

        val builder = ImmutableList.builder<File>()
        for (candidate in discovered) {
            Debug.Printf("Cache: checking cache location %s", candidate)
            if (!isCacheDirectoryWritable(candidate)) continue
            builder.add(candidate)

            if (requestedPath.isNotEmpty() && candidate.absolutePath == requestedPath) {
                selected = candidate
            } else if (selected == null) {
                selected = when {
                    requestedPath.isEmpty() && current != null -> current
                    requestedPath.isEmpty() -> candidate
                    else -> selected
                }
            }
        }

        val resolved = selected ?: context.cacheDir ?: return
        Debug.Printf("Cache: cache location set to %s", resolved.absolutePath)

        availableCacheDirsRef.set(builder.build())
        baseCacheDirRef.set(resolved)

        try {
            resolved.mkdirs()
            if (resolved.exists()) {
                File(resolved, ".nomedia").createNewFile()
            }
            if (current != null && current != resolved) {
                Debug.Printf("Cache: Cache location has been changed.")
                TextureCache.getInstance().onCacheDirChanged()
                MeshCache.onCacheDirChanged()
            }
        } catch (t: Exception) {
            Debug.Warning(t)
        }
    }

    private fun isCacheDirectoryWritable(directory: File?): Boolean {
        if (directory == null) return false
        return try {
            directory.mkdirs()
            if (!directory.exists()) return false

            val testFile = File(directory, ".tmp")
            if (testFile.exists() && !testFile.delete()) return false
            if (!testFile.createNewFile()) return false
            if (!testFile.exists()) return false
            testFile.delete()
        } catch (_: IOException) {
            false
        }
    }

    private fun getTotalMemory(): Long {
        return try {
            BufferedReader(FileReader("/proc/meminfo"), 8192).use { reader ->
                generateSequence { reader.readLine() }
                    .firstOrNull { it.startsWith("MemTotal:") }
                    ?.split(Regex("\\s+"))
                    ?.getOrNull(1)
                    ?.toLongOrNull()
                    ?: 0L
            }
        } catch (_: Exception) {
            0L
        }
    }
}
