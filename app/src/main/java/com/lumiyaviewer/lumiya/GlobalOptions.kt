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
 * Modern Kotlin GlobalOptions singleton
 * Manages application-wide settings and preferences
 */
object GlobalOptions : SharedPreferences.OnSharedPreferenceChangeListener {
    
    // Rendering options
    private var rlvEnabled = false
    private var advancedRendering = true
    private var compressedTextures = true
    private var highQualityTextures = false
    private var terrainTextures = true
    private var renderClouds = true
    private var useFXAA = false
    
    // Connection options
    private var autoReconnect = true
    private var maxReconnectAttempts = 10
    private var keepWifiOn = false
    
    // Cache management
    private val availableCacheDirs = AtomicReference<ImmutableList<File>>(ImmutableList.of())
    private val baseCacheDir = AtomicReference<File?>()
    private val cacheDirUsed = AtomicBoolean(false)
    
    // Cloud and voice
    private var cloudSyncEnabled = false
    private var voiceEnabled = false
    
    // Display options
    private var forceDaylightHour = 0.5f
    private var forceDaylightTime = false
    private var hoverTextEnableHUDs = true
    private var hoverTextEnableObjects = false
    private var showTimestamps = true
    private var legacyUserNames = false
    
    // Performance options
    private var maxTextureDownloads = 2
    private var meshRendering = MeshRendering.MEDIUM
    
    // Theme
    private var themeResourceId = R.style.Theme_Lumiya_Light

    /**
     * Event published when global options change
     */
    data class GlobalOptionsChangedEvent(val preferences: SharedPreferences)

    /**
     * Mesh rendering quality levels
     */
    enum class MeshRendering(val lodName: String?) {
        HIGH("high_lod"),
        MEDIUM("medium_lod"),
        LOW("low_lod"),
        LOWEST("lowest_lod"),
        DISABLED(null)
    }

    /**
     * Gets the singleton instance (for Java compatibility)
     */
    @JvmStatic
    fun getInstance(): GlobalOptions = this

    /**
     * Reads total system memory from /proc/meminfo
     */
    private fun getTotalMemory(): Long {
        return try {
            BufferedReader(FileReader("/proc/meminfo"), 8192).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line?.startsWith("MemTotal:") == true) {
                        val parts = line!!.split("\\s+".toRegex())
                        if (parts.size >= 2) {
                            return parts[1].toLongOrNull() ?: 0L
                        }
                    }
                }
                0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Checks if a cache directory is writable
     */
    private fun isCacheDirectoryWriteable(file: File?): Boolean {
        if (file == null) return false
        
        return try {
            file.mkdirs()
            if (!file.exists()) return false
            
            val testFile = File(file, ".tmp")
            if (testFile.exists()) {
                testFile.delete()
                if (testFile.exists()) return false
            }
            
            testFile.createNewFile()
            val exists = testFile.exists()
            testFile.delete()
            exists
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Updates the cache directory based on preferences
     */
    private fun updateCacheDir(context: Context, prefs: SharedPreferences) {
        val currentBaseDir = baseCacheDir.get()
        val cacheLocation = prefs.getString("cache_location", "") ?: ""
        
        // Check if current directory is still valid
        var selectedDir: File? = if (currentBaseDir != null && 
            isCacheDirectoryWriteable(currentBaseDir) && 
            cacheLocation.isEmpty()) {
            currentBaseDir
        } else {
            null
        }
        
        // Build list of available cache directories
        val availableDirs = mutableListOf<File>()
        
        // Add external cache directories
        ContextCompat.getExternalCacheDirs(context)?.forEach { dir ->
            if (dir != null) {
                availableDirs.add(dir)
            }
        }
        
        // Add internal cache directory
        context.cacheDir?.let { availableDirs.add(it) }
        
        // Find valid cache directories
        val validDirs = ImmutableList.builder<File>()
        for (dir in availableDirs) {
            Debug.Printf("Cache: checking cache location %s", dir)
            if (isCacheDirectoryWriteable(dir)) {
                validDirs.add(dir)
                
                // Select directory based on preference or first valid one
                if (selectedDir == null) {
                    selectedDir = dir
                } else if (cacheLocation.isNotEmpty() && dir.absolutePath == cacheLocation) {
                    selectedDir = dir
                }
            }
        }
        
        // Fallback to internal cache if nothing selected
        if (selectedDir == null) {
            selectedDir = context.cacheDir
        }
        
        Debug.Printf("Cache: cache location set to %s", selectedDir?.absolutePath ?: "null")
        
        this.availableCacheDirs.set(validDirs.build())
        this.baseCacheDir.set(selectedDir)
        
        // Create .nomedia file
        selectedDir?.let { dir ->
            try {
                dir.mkdirs()
                if (dir.exists()) {
                    File(dir, ".nomedia").createNewFile()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        
        // Notify caches if directory changed
        if (currentBaseDir != null && currentBaseDir != selectedDir) {
            Debug.Printf("Cache: Cache location has been changed.")
            TextureCache.getInstance().onCacheDirChanged()
            MeshCache.onCacheDirChanged()
        }
    }

    /**
     * Updates notification sound defaults
     */
    private fun updateNotificationSoundDefault(
        prefs: SharedPreferences,
        notificationType: NotificationType
    ) {
        if (!prefs.contains(notificationType.ringtoneKey)) {
            val defaultSound = NotificationSounds.defaultSounds[notificationType]
            defaultSound?.uri?.let { uri ->
                prefs.edit().apply {
                    putString(notificationType.ringtoneKey, uri.toString())
                    apply()
                }
                Debug.Printf(
                    "NotificationSounds: Updated %s preference to %s",
                    notificationType.ringtoneKey,
                    uri
                )
            }
        }
    }

    /**
     * Enables voice functionality
     */
    @JvmStatic
    fun enableVoice() {
        LumiyaApp.getDefaultSharedPreferences().edit().apply {
            putBoolean("enableVoice", true)
            apply()
        }
    }

    // Getters
    @JvmStatic fun getAdvancedRendering(): Boolean = advancedRendering
    @JvmStatic fun getAutoReconnect(): Boolean = autoReconnect
    @JvmStatic fun getAvailableCacheDirs(): ImmutableList<File> = availableCacheDirs.get()
    @JvmStatic fun getBaseCacheDir(): File? = baseCacheDir.get()
    @JvmStatic fun getCompressedTextures(): Boolean = compressedTextures
    @JvmStatic fun getForceDaylightHour(): Float = forceDaylightHour
    @JvmStatic fun getForceDaylightTime(): Boolean = forceDaylightTime
    @JvmStatic fun getHighQualityTextures(): Boolean = highQualityTextures
    @JvmStatic fun getHoverTextEnableHUDs(): Boolean = hoverTextEnableHUDs
    @JvmStatic fun getHoverTextEnableObjects(): Boolean = hoverTextEnableObjects
    @JvmStatic fun getKeepWifiOn(): Boolean = keepWifiOn
    @JvmStatic fun getMaxReconnectAttempts(): Int = maxReconnectAttempts
    @JvmStatic fun getMaxTextureDownloads(): Int = maxTextureDownloads
    @JvmStatic fun getMeshRendering(): MeshRendering = meshRendering
    @JvmStatic fun getRLVEnabled(): Boolean = rlvEnabled
    @JvmStatic fun getRenderClouds(): Boolean = renderClouds
    @JvmStatic fun getShowTimestamps(): Boolean = showTimestamps
    @JvmStatic fun getTerrainTextures(): Boolean = terrainTextures
    @JvmStatic fun getThemeResourceId(): Int = themeResourceId
    @JvmStatic fun getUseFXAA(): Boolean = useFXAA
    @JvmStatic fun getVoiceEnabled(): Boolean = voiceEnabled
    @JvmStatic fun isLegacyUserNames(): Boolean = legacyUserNames
    @JvmStatic fun isCacheDirUsed(): Boolean = cacheDirUsed.get()

    /**
     * Gets a cache subdirectory, creating it if necessary
     */
    @JvmStatic
    fun getCacheDir(subdir: String): File {
        cacheDirUsed.set(true)
        val base = baseCacheDir.get() ?: LumiyaApp.getContext().cacheDir
        val dir = File(base, subdir)
        try {
            dir.mkdirs()
        } catch (e: Exception) {
            // Ignore
        }
        return dir
    }

    /**
     * Initializes global options from preferences
     */
    @JvmStatic
    fun initialize() {
        val prefs = LumiyaApp.getDefaultSharedPreferences()
        updateFromPreferences(LumiyaApp.getContext(), prefs)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * Called when shared preferences change
     */
    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        updateFromPreferences(LumiyaApp.getContext(), prefs)
        EventBus.getInstance().publish(GlobalOptionsChangedEvent(prefs))
    }

    /**
     * Updates all options from shared preferences
     */
    @JvmStatic
    fun updateFromPreferences(context: Context, prefs: SharedPreferences) {
        Debug.Printf("Updating options from preferences.")
        
        // Update notification defaults
        updateNotificationSoundDefault(prefs, NotificationType.Private)
        updateNotificationSoundDefault(prefs, NotificationType.Group)
        updateNotificationSoundDefault(prefs, NotificationType.LocalChat)
        
        // Set system defaults if not already set
        if (!prefs.getBoolean("system_defaults_set", false)) {
            val totalMemory = getTotalMemory()
            val processors = Runtime.getRuntime().availableProcessors()
            
            prefs.edit().apply {
                // High quality textures for devices with good specs
                putBoolean("high_quality_textures", processors >= 2 && totalMemory > 524288)
                
                // Texture memory limit based on available RAM
                val memLimit = when {
                    totalMemory == 0L -> 64
                    totalMemory <= 262144 -> 32
                    totalMemory <= 524288 -> 64
                    else -> 128
                }
                putString("texture_memory_limit", memLimit.toString())
                
                // Max texture downloads based on CPU cores
                val maxDownloads = when {
                    processors >= 4 && totalMemory > 524288 -> 8
                    processors >= 2 -> 4
                    else -> 2
                }
                putString("max_texture_downloads", maxDownloads.toString())
                
                putBoolean("system_defaults_set", true)
                commit()
            }
        }
        
        // Store old theme for change detection
        val oldThemeId = themeResourceId
        
        // Update theme
        themeResourceId = when (prefs.getString("theme", "light")) {
            "dark" -> R.style.Theme_Lumiya
            "pink" -> R.style.Theme_Lumiya_Pink
            else -> R.style.Theme_Lumiya_Light
        }
        
        // Update all settings
        legacyUserNames = prefs.getBoolean("legacyUserNames", false)
        showTimestamps = prefs.getBoolean("chatTimestamps", true)
        highQualityTextures = prefs.getBoolean("high_quality_textures", false)
        compressedTextures = prefs.getBoolean("compressed_textures", true)
        keepWifiOn = prefs.getBoolean("keep_wifi_on", true)
        cloudSyncEnabled = prefs.getBoolean("sync_to_gdrive", false)
        
        // Voice enabled only if plugin is supported
        voiceEnabled = prefs.getBoolean("enableVoice", false) && 
                      VoicePluginServiceConnection.isPluginSupported()
        
        // Max texture downloads
        val newMaxDownloads = prefs.getString("max_texture_downloads", "2")
            ?.toIntOrNull()?.coerceAtLeast(1) ?: 2
        if (newMaxDownloads != maxTextureDownloads) {
            maxTextureDownloads = newMaxDownloads
            TextureCache.getInstance().setMaxTextureDownloads(maxTextureDownloads)
        }
        
        // Terrain textures
        terrainTextures = prefs.getBoolean("terrain_textures", true)
        
        // Texture memory limit
        val memLimit = prefs.getString("texture_memory_limit", "64")
            ?.toIntOrNull() ?: 64
        TextureMemoryTracker.setMemoryLimit(memLimit * 1024 * 1024)
        
        // Mesh rendering
        meshRendering = try {
            MeshRendering.valueOf(
                prefs.getString("mesh_rendering", "MEDIUM")?.uppercase() ?: "MEDIUM"
            )
        } catch (e: Exception) {
            MeshRendering.MEDIUM
        }
        
        // RLV and reconnection
        rlvEnabled = prefs.getBoolean("rlv_enabled", false)
        autoReconnect = prefs.getBoolean("auto_reconnect", true)
        maxReconnectAttempts = prefs.getString("reconnect_attempts", "10")
            ?.toIntOrNull() ?: 10
        
        // Update cache directory
        updateCacheDir(context, prefs)
        
        // Hover text settings
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
        
        // Advanced rendering options
        advancedRendering = prefs.getBoolean("advanced_rendering", true)
        useFXAA = prefs.getBoolean("fxaa_enable", false)
        renderClouds = prefs.getBoolean("clouds_enable", true)
        
        // Time of day settings
        val timeOfDay = prefs.getString("render_time_of_day", "sim") ?: "sim"
        if (timeOfDay.equals("sim", ignoreCase = true)) {
            forceDaylightTime = false
            forceDaylightHour = 0.5f
        } else {
            try {
                forceDaylightTime = true
                forceDaylightHour = timeOfDay.toFloat()
            } catch (e: Exception) {
                forceDaylightTime = false
                forceDaylightHour = 0.5f
            }
        }
        
        // Publish theme change event if theme changed
        if (oldThemeId != themeResourceId) {
            EventBus.getInstance().publish(ThemeChangedEvent(themeResourceId))
        }
    }
}