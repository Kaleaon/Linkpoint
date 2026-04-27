package com.linkpoint

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableList.Builder
import com.linkpoint.eventbus.EventBus
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.res.mesh.MeshCache
import com.linkpoint.res.textures.TextureCache
import com.linkpoint.ui.media.NotificationSounds
import com.linkpoint.ui.settings.NotificationType
import com.linkpoint.ui.settings.ThemeChangedEvent
import com.linkpoint.voiceintf.VoicePluginServiceConnection
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList
import java.util.Iterator
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

class GlobalOptions : OnSharedPreferenceChangeListener {
    private Boolean RLVEnabled = false
    private Boolean advancedRendering = true
    private Boolean autoReconnect = true
    private val AtomicReference<ImmutableList<File>> availableCacheDirs = AtomicReference(ImmutableList.of())
    private val AtomicReference<File> baseCacheDir = AtomicReference()
    private val AtomicBoolean cacheDirUsed = AtomicBoolean(false)
    private Boolean cloudSyncEnabled = false
    private Boolean compressedTextures = true
    private Float forceDaylightHour = 0.5f
    private Boolean forceDaylightTime = false
    private Boolean highQualityTextures = false
    private Boolean hoverTextEnableHUDs = true
    private Boolean hoverTextEnableObjects = false
    private Boolean keepWifiOn = false
    private Boolean legacyUserNames = false
    private Int maxReconnectAttempts = 10
    private Int maxTextureDownloads = 2
    private MeshRendering meshRendering = MeshRendering.medium
    private Boolean renderClouds = true
    private Boolean showTimestamps = true
    private Boolean terrainTextures = true
    private Int themeResourceId = R.style.f47Theme.Linkpoint.Light
    private Boolean useFXAA = false
    private Boolean voiceEnabled = false

    @JvmStatic
    class GlobalOptionsChangedEvent {
        val SharedPreferences preferences

        public GlobalOptionsChangedEvent(SharedPreferences sharedPreferences) {
            this.preferences = sharedPreferences
        }
    }

    @JvmStatic
private class InstanceHolder {
        private const val GlobalOptions Instance = GlobalOptions()

        private InstanceHolder() {
        }
    }

    enum class MeshRendering {
        high("high_lod"),
        medium("medium_lod"),
        low("low_lod"),
        lowest("lowest_lod"),
        disabled(null)
        
        private String lodName

        private MeshRendering(String str) {
            this.lodName = str
        }

         public fun getLODName(): String {
            return this.lodName
        }
    }

    @JvmStatic
     fun getInstance(): GlobalOptions {
        return InstanceHolder.Instance
    }

    @JvmStatic
 private fun getTotalMemory(): Long {
        val j: Long = 0
        try {
            String readLine
            val bufferedReader: BufferedReader = BufferedReader(FileReader("/proc/meminfo"), 8192)
            do {
                readLine = bufferedReader.readLine()
                if (readLine == null) {
                    break
                }
            } while (!readLine.startsWith("MemTotal:"))
            val split: Array<String> = readLine.split("\\s+")
            if (split.length >= 2) {
                j = Long.parseLong(split[1])
            }
            for (Int i = 0; i < split.length; i++) {
                Debug.Log("Memory " + i + ":" + split[i])
            }
            bufferedReader.close()
        } catch (Exception e) {
        }
        return j
    }

     private fun isCacheDirectoryWriteable(file: File): Boolean {
        if (file == null) {
            return false
        }
        try {
            file.mkdirs()
            if (!file.exists()) {
                return false
            }
            val file2: File = File(file, ".tmp")
            if (file2.exists()) {
                file2.delete()
                if (file2.exists()) {
                    return false
                }
            }
            file2.createNewFile()
            if (!file2.exists()) {
                return false
            }
            file2.delete()
            return true
        } catch (IOException e) {
            return false
        }
    }

     private fun updateCacheDir(context: Context, sharedPreferences: SharedPreferences) {
        val file: File = null
        val file2: File = (File) this.baseCacheDir.get()
        val string: String = sharedPreferences.getString("cache_location", "")
        if (file2 != null && isCacheDirectoryWriteable(file2) && string.isEmpty()) {
            file = file2
        }
        val arrayList: Iterable = ArrayList()
        val externalCacheDirs: Array<File> = ContextCompat.getExternalCacheDirs(context)
        if (externalCacheDirs != null) {
            for (Object obj : externalCacheDirs) {
                if (obj != null) {
                    arrayList.add(obj)
                }
            }
        }
        val cacheDir: File = context.getCacheDir()
        if (cacheDir != null) {
            arrayList.add(cacheDir)
        }
        val builder: Builder = ImmutableList.builder()
        val it: Iterator = arrayList.iterator()
        while (true) {
            cacheDir = file
            if (!it.hasNext()) {
                break
            }
            file = (File) it.next()
            Debug.Printf("Cache: checking cache location %s", file)
            if (isCacheDirectoryWriteable(file)) {
                builder.add((Object) file)
                if (cacheDir != null) {
                    if (string.isEmpty()) {
                        file = cacheDir
                    } else if (file.getAbsolutePath().equals(string)) {
                    }
                }
            }
            file = cacheDir
        }
        if (cacheDir == null) {
            cacheDir = context.getCacheDir()
        }
        Debug.Printf("Cache: cache location set to %s", cacheDir.getAbsolutePath())
        this.availableCacheDirs.set(builder.build())
        this.baseCacheDir.set(cacheDir)
        try {
            cacheDir.mkdirs()
            if (cacheDir.exists()) {
                File(cacheDir, ".nomedia").createNewFile()
            }
        } catch (Exception e) {
        }
        if (file2 != null && !file2.equals(cacheDir)) {
            Debug.Printf("Cache: Cache location has been changed.", Object[0])
            TextureCache.getInstance().onCacheDirChanged()
            MeshCache.onCacheDirChanged()
        }
    }

     private fun updateNotificationSoundDefault(sharedPreferences: SharedPreferences, notificationType: NotificationType) {
        if (!sharedPreferences.contains(notificationType.getRingtoneKey())) {
            val notificationSounds: NotificationSounds = (NotificationSounds) NotificationSounds.defaultSounds.get(notificationType)
            if (notificationSounds != null) {
                val uri: Uri = notificationSounds.getUri()
                val edit: Editor = sharedPreferences.edit()
                edit.putString(notificationType.getRingtoneKey(), uri.toString())
                edit.apply()
                Debug.Printf("NotificationSounds: Updated %s preference to %s", notificationType.getRingtoneKey(), uri)
            }
        }
    }

    fun enableVoice() {
        val edit: Editor = LinkpointApp.getDefaultSharedPreferences().edit()
        edit.putBoolean("enableVoice", true)
        edit.apply()
    }

     public fun getAdvancedRendering(): Boolean {
        return this.advancedRendering
    }

    val Boolean getAutoReconnect() {
        return this.autoReconnect
    }

    public ImmutableList<File> getAvailableCacheDirs() {
        return (ImmutableList) this.availableCacheDirs.get()
    }

     public fun getBaseCacheDir(): File {
        return (File) this.baseCacheDir.get()
    }

    val File getCacheDir(String str) {
        this.cacheDirUsed.set(true)
        val file: File = (File) this.baseCacheDir.get()
        if (file == null) {
            file = LinkpointApp.getContext().getCacheDir()
        }
        val file2: File = File(file, str)
        try {
            file2.mkdirs()
        } catch (Exception e) {
        }
        return file2
    }

    val Boolean getCompressedTextures() {
        return this.compressedTextures
    }

     public fun getForceDaylightHour(): Float {
        return this.forceDaylightHour
    }

     public fun getForceDaylightTime(): Boolean {
        return this.forceDaylightTime
    }

    val Boolean getHighQualityTextures() {
        return this.highQualityTextures
    }

    val Boolean getHoverTextEnableHUDs() {
        return this.hoverTextEnableHUDs
    }

    val Boolean getHoverTextEnableObjects() {
        return this.hoverTextEnableObjects
    }

     public fun getKeepWifiOn(): Boolean {
        return this.keepWifiOn
    }

    val Int getMaxReconnectAttempts() {
        return this.maxReconnectAttempts
    }

    val Int getMaxTextureDownloads() {
        return this.maxTextureDownloads
    }

    val MeshRendering getMeshRendering() {
        return this.meshRendering
    }

    val Boolean getRLVEnabled() {
        return this.RLVEnabled
    }

     public fun getRenderClouds(): Boolean {
        return this.renderClouds
    }

     public fun getShowTimestamps(): Boolean {
        return this.showTimestamps
    }

    val Boolean getTerrainTextures() {
        return this.terrainTextures
    }

     public fun getThemeResourceId(): Int {
        return this.themeResourceId
    }

     public fun getUseFXAA(): Boolean {
        return this.useFXAA
    }

     public fun getVoiceEnabled(): Boolean {
        return this.voiceEnabled
    }

    fun initialize() {
        val defaultSharedPreferences: SharedPreferences = LinkpointApp.getDefaultSharedPreferences()
        updateFromPreferences(LinkpointApp.getContext(), defaultSharedPreferences)
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

     public fun isCacheDirUsed(): Boolean {
        return this.cacheDirUsed.get()
    }

     public fun isLegacyUserNames(): Boolean {
        return this.legacyUserNames
    }

    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, str: String) {
        updateFromPreferences(LinkpointApp.getContext(), sharedPreferences)
        EventBus.getInstance().publish(GlobalOptionsChangedEvent(sharedPreferences))
    }

    fun updateFromPreferences(context: Context, sharedPreferences: SharedPreferences) {
        Debug.Printf("Updating options from preferences.", Object[0])
        updateNotificationSoundDefault(sharedPreferences, NotificationType.Private)
        updateNotificationSoundDefault(sharedPreferences, NotificationType.Group)
        updateNotificationSoundDefault(sharedPreferences, NotificationType.LocalChat)
        if (!sharedPreferences.getBoolean("system_defaults_set", false)) {
            val edit: Editor = sharedPreferences.edit()
            val totalMemory: Long = getTotalMemory()
            val availableProcessors: Int = Runtime.getRuntime().availableProcessors()
            if (availableProcessors < 2 || totalMemory <= 524288) {
                edit.putBoolean("high_quality_textures", false)
            } else {
                edit.putBoolean("high_quality_textures", true)
            }
            i = 64
            if (totalMemory != 0) {
                i = totalMemory <= 262144 ? 32 : totalMemory <= 524288 ? 64 : 128
            }
            edit.putString("texture_memory_limit", Integer.toString(i))
            i = 2
            if (availableProcessors >= 4 && totalMemory > 524288) {
                i = 8
            } else if (availableProcessors >= 2) {
                i = 4
            }
            edit.putString("max_texture_downloads", Integer.toString(i))
            edit.putBoolean("system_defaults_set", true)
            edit.commit()
        }
        val i2: Int = this.themeResourceId
        val nullToEmpty: String = Strings.nullToEmpty(sharedPreferences.getString("theme", "light"))
        if (nullToEmpty.equals("dark")) {
            this.themeResourceId = R.style.f46Theme.Linkpoint
        } else if (nullToEmpty.equals("pink")) {
            this.themeResourceId = R.style.f50Theme.Linkpoint.Pink
        } else {
            this.themeResourceId = R.style.f47Theme.Linkpoint.Light
        }
        this.legacyUserNames = sharedPreferences.getBoolean("legacyUserNames", false)
        this.showTimestamps = sharedPreferences.getBoolean("chatTimestamps", true)
        this.highQualityTextures = sharedPreferences.getBoolean("high_quality_textures", false)
        this.compressedTextures = sharedPreferences.getBoolean("compressed_textures", true)
        this.keepWifiOn = sharedPreferences.getBoolean("keep_wifi_on", true)
        this.cloudSyncEnabled = sharedPreferences.getBoolean("sync_to_gdrive", false)
        this.voiceEnabled = sharedPreferences.getBoolean("enableVoice", false) ? VoicePluginServiceConnection.isPluginSupported() : false
        try {
            i = Integer.parseInt(sharedPreferences.getString("max_texture_downloads", "2"))
            if (i < 1) {
                i = 1
            }
        } catch (Exception e) {
            i = 2
        }
        if (i != this.maxTextureDownloads) {
            this.maxTextureDownloads = i
            TextureCache.getInstance().setMaxTextureDownloads(this.maxTextureDownloads)
        }
        this.terrainTextures = sharedPreferences.getBoolean("terrain_textures", true)
        i = 64
        try {
            i = Integer.parseInt(sharedPreferences.getString("texture_memory_limit", "64"))
        } catch (Exception e2) {
        }
        try {
            this.meshRendering = MeshRendering.valueOf(sharedPreferences.getString("mesh_rendering", "high"))
        } catch (Exception e3) {
        }
        TextureMemoryTracker.setMemoryLimit((i * 1024) * 1024)
        this.RLVEnabled = sharedPreferences.getBoolean("rlv_enabled", false)
        this.autoReconnect = sharedPreferences.getBoolean("auto_reconnect", true)
        try {
            this.maxReconnectAttempts = Integer.parseInt(sharedPreferences.getString("reconnect_attempts", "10"))
        } catch (Exception e4) {
        }
        updateCacheDir(context, sharedPreferences)
        nullToEmpty = sharedPreferences.getString("hover_text", "huds")
        if (nullToEmpty.equals("all")) {
            this.hoverTextEnableHUDs = true
            this.hoverTextEnableObjects = true
        } else if (nullToEmpty.equals("none")) {
            this.hoverTextEnableHUDs = false
            this.hoverTextEnableObjects = false
        } else {
            this.hoverTextEnableHUDs = true
            this.hoverTextEnableObjects = false
        }
        this.advancedRendering = sharedPreferences.getBoolean("advanced_rendering", true)
        this.useFXAA = sharedPreferences.getBoolean("fxaa_enable", false)
        this.renderClouds = sharedPreferences.getBoolean("clouds_enable", true)
        nullToEmpty = sharedPreferences.getString("render_time_of_day", "sim")
        if (nullToEmpty.equalsIgnoreCase("sim")) {
            this.forceDaylightTime = false
            this.forceDaylightHour = 0.5f
        } else {
            try {
                this.forceDaylightTime = true
                this.forceDaylightHour = Float.parseFloat(nullToEmpty)
            } catch (Exception e5) {
                this.forceDaylightTime = false
                this.forceDaylightHour = 0.5f
            }
        }
        if (i2 != this.themeResourceId) {
            EventBus.getInstance().publish(ThemeChangedEvent(this.themeResourceId))
        }
    }
}
