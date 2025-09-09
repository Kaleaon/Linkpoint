package com.lumiyaviewer.lumiya;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.ui.settings.ThemeChangedEvent;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GlobalOptions implements SharedPreferences.OnSharedPreferenceChangeListener {
    private boolean RLVEnabled = false;
    private boolean advancedRendering = true;
    private boolean autoReconnect = true;
    private final AtomicReference<ImmutableList<File>> availableCacheDirs = new AtomicReference<>(ImmutableList.of());
    private final AtomicReference<File> baseCacheDir = new AtomicReference<>();
    private final AtomicBoolean cacheDirUsed = new AtomicBoolean(false);
    private boolean cloudSyncEnabled = false;
    private boolean compressedTextures = true;
    private float forceDaylightHour = 0.5f;
    private boolean forceDaylightTime = false;
    private boolean highQualityTextures = false;
    private boolean hoverTextEnableHUDs = true;
    private boolean hoverTextEnableObjects = false;
    private boolean keepWifiOn = false;
    private boolean legacyUserNames = false;
    private int maxReconnectAttempts = 10;
    private int maxTextureDownloads = 2;
    private MeshRendering meshRendering = MeshRendering.medium;
    private boolean renderClouds = true;
    private boolean showTimestamps = true;
    private boolean terrainTextures = true;
    private int themeResourceId = R.style.Theme_Lumiya_Light;
    private boolean useFXAA = false;
    private boolean voiceEnabled = false;

    public static class GlobalOptionsChangedEvent {
        public final SharedPreferences preferences;

        public GlobalOptionsChangedEvent(SharedPreferences sharedPreferences) {
            this.preferences = sharedPreferences;
        }
    }

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final GlobalOptions Instance = new GlobalOptions();

        private InstanceHolder() {
        }
    }

    public enum MeshRendering {
        high("high_lod"),
        medium("medium_lod"),
        low("low_lod"),
        lowest("lowest_lod"),
        disabled((String) null);
        
        private String lodName;

        private MeshRendering(String str) {
            this.lodName = str;
        }

        public String getLODName() {
            return this.lodName;
        }
    }

    public static GlobalOptions getInstance() {
        return InstanceHolder.Instance;
    }

    private static long getTotalMemory() {
        long j = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                } else if (readLine.startsWith("MemTotal:")) {
                    String[] split = readLine.split("\\s+");
                    if (split.length >= 2) {
                        j = Long.parseLong(split[1]);
                    }
                    for (int i = 0; i < split.length; i++) {
                        Debug.Log("Memory " + i + ":" + split[i]);
                    }
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
        }
        return j;
    }

    private boolean isCacheDirectoryWriteable(@Nullable File file) {
        if (file == null) {
            return false;
        }
        try {
            file.mkdirs();
            if (!file.exists()) {
                return false;
            }
            File file2 = new File(file, ".tmp");
            if (file2.exists()) {
                file2.delete();
                if (file2.exists()) {
                    return false;
                }
            }
            file2.createNewFile();
            if (!file2.exists()) {
                return false;
            }
            file2.delete();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void updateCacheDir(Context context, SharedPreferences sharedPreferences) {
        File file;
        File file2;
        File file3 = null;
        File file4 = this.baseCacheDir.get();
        String string = sharedPreferences.getString("cache_location", "");
        if (file4 != null && isCacheDirectoryWriteable(file4) && string.isEmpty()) {
            file3 = file4;
        }
        ArrayList arrayList = new ArrayList();
        File[] externalCacheDirs = ContextCompat.getExternalCacheDirs(context);
        if (externalCacheDirs != null) {
            for (File file5 : externalCacheDirs) {
                if (file5 != null) {
                    arrayList.add(file5);
                }
            }
        }
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            arrayList.add(cacheDir);
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        Iterator it = arrayList.iterator();
        while (true) {
            file2 = file;
            if (!it.hasNext()) {
                break;
            }
            file = (File) it.next();
            Debug.Printf("Cache: checking cache location %s", file);
            if (isCacheDirectoryWriteable(file)) {
                builder.add((Object) file);
                if (file2 != null) {
                    if (string.isEmpty()) {
                        file = file2;
                    } else if (file.getAbsolutePath().equals(string)) {
                    }
                }
            }
            file = file2;
        }
        if (file2 == null) {
            file2 = context.getCacheDir();
        }
        Debug.Printf("Cache: cache location set to %s", file2.getAbsolutePath());
        this.availableCacheDirs.set(builder.build());
        this.baseCacheDir.set(file2);
        try {
            file2.mkdirs();
            if (file2.exists()) {
                new File(file2, ".nomedia").createNewFile();
            }
        } catch (Exception e) {
        }
        if (file4 != null && !file4.equals(file2)) {
            Debug.Printf("Cache: Cache location has been changed.", new Object[0]);
            TextureCache.getInstance().onCacheDirChanged();
            MeshCache.onCacheDirChanged();
        }
    }

    private void updateNotificationSoundDefault(SharedPreferences sharedPreferences, NotificationType notificationType) {
        NotificationSounds notificationSounds;
        if (!sharedPreferences.contains(notificationType.getRingtoneKey()) && (notificationSounds = NotificationSounds.defaultSounds.get(notificationType)) != null) {
            Uri uri = notificationSounds.getUri();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(notificationType.getRingtoneKey(), uri.toString());
            edit.apply();
            Debug.Printf("NotificationSounds: Updated %s preference to %s", notificationType.getRingtoneKey(), uri);
        }
    }

    public void enableVoice() {
        SharedPreferences.Editor edit = LumiyaApp.getDefaultSharedPreferences().edit();
        edit.putBoolean("enableVoice", true);
        edit.apply();
    }

    public boolean getAdvancedRendering() {
        return this.advancedRendering;
    }

    public final boolean getAutoReconnect() {
        return this.autoReconnect;
    }

    public ImmutableList<File> getAvailableCacheDirs() {
        return this.availableCacheDirs.get();
    }

    public File getBaseCacheDir() {
        return this.baseCacheDir.get();
    }

    public final File getCacheDir(@Nonnull String str) {
        this.cacheDirUsed.set(true);
        File file = this.baseCacheDir.get();
        if (file == null) {
            file = LumiyaApp.getContext().getCacheDir();
        }
        File file2 = new File(file, str);
        try {
            file2.mkdirs();
        } catch (Exception e) {
        }
        return file2;
    }

    public final boolean getCompressedTextures() {
        return this.compressedTextures;
    }

    public float getForceDaylightHour() {
        return this.forceDaylightHour;
    }

    public boolean getForceDaylightTime() {
        return this.forceDaylightTime;
    }

    public final boolean getHighQualityTextures() {
        return this.highQualityTextures;
    }

    public final boolean getHoverTextEnableHUDs() {
        return this.hoverTextEnableHUDs;
    }

    public final boolean getHoverTextEnableObjects() {
        return this.hoverTextEnableObjects;
    }

    public boolean getKeepWifiOn() {
        return this.keepWifiOn;
    }

    public final int getMaxReconnectAttempts() {
        return this.maxReconnectAttempts;
    }

    public final int getMaxTextureDownloads() {
        return this.maxTextureDownloads;
    }

    public final MeshRendering getMeshRendering() {
        return this.meshRendering;
    }

    public final boolean getRLVEnabled() {
        return this.RLVEnabled;
    }

    public boolean getRenderClouds() {
        return this.renderClouds;
    }

    public boolean getShowTimestamps() {
        return this.showTimestamps;
    }

    public final boolean getTerrainTextures() {
        return this.terrainTextures;
    }

    public int getThemeResourceId() {
        return this.themeResourceId;
    }

    public boolean getUseFXAA() {
        return this.useFXAA;
    }

    public boolean getVoiceEnabled() {
        return this.voiceEnabled;
    }

    public void initialize() {
        SharedPreferences defaultSharedPreferences = LumiyaApp.getDefaultSharedPreferences();
        updateFromPreferences(LumiyaApp.getContext(), defaultSharedPreferences);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public boolean isCacheDirUsed() {
        return this.cacheDirUsed.get();
    }

    public boolean isLegacyUserNames() {
        return this.legacyUserNames;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        updateFromPreferences(LumiyaApp.getContext(), sharedPreferences);
        EventBus.getInstance().publish(new GlobalOptionsChangedEvent(sharedPreferences));
    }

    public void updateFromPreferences(Context context, SharedPreferences sharedPreferences) {
        int i;
        Debug.Printf("Updating options from preferences.", new Object[0]);
        updateNotificationSoundDefault(sharedPreferences, NotificationType.Private);
        updateNotificationSoundDefault(sharedPreferences, NotificationType.Group);
        updateNotificationSoundDefault(sharedPreferences, NotificationType.LocalChat);
        if (!sharedPreferences.getBoolean("system_defaults_set", false)) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            long totalMemory = getTotalMemory();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            if (availableProcessors < 2 || totalMemory <= 524288) {
                edit.putBoolean("high_quality_textures", false);
            } else {
                edit.putBoolean("high_quality_textures", true);
            }
            int i2 = 64;
            if (totalMemory != 0) {
                i2 = totalMemory <= 262144 ? 32 : totalMemory <= 524288 ? 64 : 128;
            }
            edit.putString("texture_memory_limit", Integer.toString(i2));
            int i3 = 2;
            if (availableProcessors >= 4 && totalMemory > 524288) {
                i3 = 8;
            } else if (availableProcessors >= 2) {
                i3 = 4;
            }
            edit.putString("max_texture_downloads", Integer.toString(i3));
            edit.putBoolean("system_defaults_set", true);
            edit.commit();
        }
        int i4 = this.themeResourceId;
        String nullToEmpty = Strings.nullToEmpty(sharedPreferences.getString("theme", "light"));
        if (nullToEmpty.equals("dark")) {
            this.themeResourceId = R.style.Theme_Lumiya;
        } else if (nullToEmpty.equals("pink")) {
            this.themeResourceId = R.style.Theme_Lumiya_Pink;
        } else {
            this.themeResourceId = R.style.Theme_Lumiya_Light;
        }
        this.legacyUserNames = sharedPreferences.getBoolean("legacyUserNames", false);
        this.showTimestamps = sharedPreferences.getBoolean("chatTimestamps", true);
        this.highQualityTextures = sharedPreferences.getBoolean("high_quality_textures", false);
        this.compressedTextures = sharedPreferences.getBoolean("compressed_textures", true);
        this.keepWifiOn = sharedPreferences.getBoolean("keep_wifi_on", true);
        this.cloudSyncEnabled = sharedPreferences.getBoolean("sync_to_gdrive", false);
        this.voiceEnabled = sharedPreferences.getBoolean("enableVoice", false) ? VoicePluginServiceConnection.isPluginSupported() : false;
        try {
            i = Integer.parseInt(sharedPreferences.getString("max_texture_downloads", "2"));
            if (i < 1) {
                i = 1;
            }
        } catch (Exception e) {
            i = 2;
        }
        if (i != this.maxTextureDownloads) {
            this.maxTextureDownloads = i;
            TextureCache.getInstance().setMaxTextureDownloads(this.maxTextureDownloads);
        }
        this.terrainTextures = sharedPreferences.getBoolean("terrain_textures", true);
        int i5 = 64;
        try {
            i5 = Integer.parseInt(sharedPreferences.getString("texture_memory_limit", "64"));
        } catch (Exception e2) {
        }
        try {
            this.meshRendering = MeshRendering.valueOf(sharedPreferences.getString("mesh_rendering", "high"));
        } catch (Exception e3) {
        }
        TextureMemoryTracker.setMemoryLimit(i5 * 1024 * 1024);
        this.RLVEnabled = sharedPreferences.getBoolean("rlv_enabled", false);
        this.autoReconnect = sharedPreferences.getBoolean("auto_reconnect", true);
        try {
            this.maxReconnectAttempts = Integer.parseInt(sharedPreferences.getString("reconnect_attempts", "10"));
        } catch (Exception e4) {
        }
        updateCacheDir(context, sharedPreferences);
        String string = sharedPreferences.getString("hover_text", "huds");
        if (string.equals("all")) {
            this.hoverTextEnableHUDs = true;
            this.hoverTextEnableObjects = true;
        } else if (string.equals("none")) {
            this.hoverTextEnableHUDs = false;
            this.hoverTextEnableObjects = false;
        } else {
            this.hoverTextEnableHUDs = true;
            this.hoverTextEnableObjects = false;
        }
        this.advancedRendering = sharedPreferences.getBoolean("advanced_rendering", true);
        this.useFXAA = sharedPreferences.getBoolean("fxaa_enable", false);
        this.renderClouds = sharedPreferences.getBoolean("clouds_enable", true);
        String string2 = sharedPreferences.getString("render_time_of_day", "sim");
        if (string2.equalsIgnoreCase("sim")) {
            this.forceDaylightTime = false;
            this.forceDaylightHour = 0.5f;
        } else {
            try {
                this.forceDaylightTime = true;
                this.forceDaylightHour = Float.parseFloat(string2);
            } catch (Exception e5) {
                this.forceDaylightTime = false;
                this.forceDaylightHour = 0.5f;
            }
        }
        if (i4 != this.themeResourceId) {
            EventBus.getInstance().publish(new ThemeChangedEvent(this.themeResourceId));
        }
    }
}
