// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            Debug, LumiyaApp

public class GlobalOptions
    implements android.content.SharedPreferences.OnSharedPreferenceChangeListener
{
    public static class GlobalOptionsChangedEvent
    {

        public final SharedPreferences preferences;

        public GlobalOptionsChangedEvent(SharedPreferences sharedpreferences)
        {
            preferences = sharedpreferences;
        }
    }

    private static class InstanceHolder
    {

        private static final GlobalOptions Instance = new GlobalOptions();

        static GlobalOptions _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }

    public static final class MeshRendering extends Enum
    {

        private static final MeshRendering $VALUES[];
        public static final MeshRendering disabled;
        public static final MeshRendering high;
        public static final MeshRendering low;
        public static final MeshRendering lowest;
        public static final MeshRendering medium;
        private String lodName;

        public static MeshRendering valueOf(String s)
        {
            return (MeshRendering)Enum.valueOf(com/lumiyaviewer/lumiya/GlobalOptions$MeshRendering, s);
        }

        public static MeshRendering[] values()
        {
            return $VALUES;
        }

        public String getLODName()
        {
            return lodName;
        }

        static 
        {
            high = new MeshRendering("high", 0, "high_lod");
            medium = new MeshRendering("medium", 1, "medium_lod");
            low = new MeshRendering("low", 2, "low_lod");
            lowest = new MeshRendering("lowest", 3, "lowest_lod");
            disabled = new MeshRendering("disabled", 4, null);
            $VALUES = (new MeshRendering[] {
                high, medium, low, lowest, disabled
            });
        }

        private MeshRendering(String s, int i, String s1)
        {
            super(s, i);
            lodName = s1;
        }
    }


    private boolean RLVEnabled;
    private boolean advancedRendering;
    private boolean autoReconnect;
    private final AtomicReference availableCacheDirs = new AtomicReference(ImmutableList.of());
    private final AtomicReference baseCacheDir = new AtomicReference();
    private final AtomicBoolean cacheDirUsed = new AtomicBoolean(false);
    private boolean cloudSyncEnabled;
    private boolean compressedTextures;
    private float forceDaylightHour;
    private boolean forceDaylightTime;
    private boolean highQualityTextures;
    private boolean hoverTextEnableHUDs;
    private boolean hoverTextEnableObjects;
    private boolean keepWifiOn;
    private boolean legacyUserNames;
    private int maxReconnectAttempts;
    private int maxTextureDownloads;
    private MeshRendering meshRendering;
    private boolean renderClouds;
    private boolean showTimestamps;
    private boolean terrainTextures;
    private int themeResourceId;
    private boolean useFXAA;
    private boolean voiceEnabled;

    public GlobalOptions()
    {
        themeResourceId = 0x7f0b002c;
        legacyUserNames = false;
        showTimestamps = true;
        highQualityTextures = false;
        compressedTextures = true;
        maxTextureDownloads = 2;
        terrainTextures = true;
        meshRendering = MeshRendering.medium;
        RLVEnabled = false;
        keepWifiOn = false;
        autoReconnect = true;
        maxReconnectAttempts = 10;
        hoverTextEnableHUDs = true;
        hoverTextEnableObjects = false;
        advancedRendering = true;
        useFXAA = false;
        renderClouds = true;
        forceDaylightTime = false;
        forceDaylightHour = 0.5F;
        cloudSyncEnabled = false;
        voiceEnabled = false;
    }

    public static GlobalOptions getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    private static long getTotalMemory()
    {
        int i;
        long l2 = 0L;
        long l1 = l2;
        BufferedReader bufferedreader;
        String s;
        String as[];
        long l;
        long l3;
        try
        {
            bufferedreader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
        }
        catch (Exception exception)
        {
            return l1;
        }
        l1 = l2;
        s = bufferedreader.readLine();
        l3 = l2;
        if (s == null) goto _L2; else goto _L1
_L1:
        l1 = l2;
        if (!s.startsWith("MemTotal:"))
        {
            break MISSING_BLOCK_LABEL_27;
        }
        l1 = l2;
        as = s.split("\\s+");
        l1 = l2;
        l = l2;
        if (as.length < 2) goto _L4; else goto _L3
_L3:
        l1 = l2;
        l = Long.parseLong(as[1]);
          goto _L4
_L7:
        l3 = l;
        l1 = l;
        if (i >= as.length) goto _L2; else goto _L5
_L5:
        l1 = l;
        Debug.Log((new StringBuilder()).append("Memory ").append(i).append(":").append(as[i]).toString());
        i++;
        continue; /* Loop/switch isn't completed */
_L2:
        l1 = l3;
        bufferedreader.close();
        return l3;
_L4:
        i = 0;
        if (true) goto _L7; else goto _L6
_L6:
    }

    private boolean isCacheDirectoryWriteable(File file)
    {
        if (file == null)
        {
            return false;
        }
        file.mkdirs();
        if (!file.exists())
        {
            return false;
        }
        file = new File(file, ".tmp");
        if (!file.exists())
        {
            break MISSING_BLOCK_LABEL_52;
        }
        file.delete();
        if (file.exists())
        {
            return false;
        }
        file.createNewFile();
        if (!file.exists())
        {
            return false;
        }
        try
        {
            file.delete();
        }
        // Misplaced declaration of an exception variable
        catch (File file)
        {
            return false;
        }
        return true;
    }

    private void updateCacheDir(Context context, SharedPreferences sharedpreferences)
    {
        File file;
        String s;
        com.google.common.collect.ImmutableList.Builder builder;
        Iterator iterator;
        ArrayList arraylist = null;
        file = (File)baseCacheDir.get();
        s = sharedpreferences.getString("cache_location", "");
        sharedpreferences = arraylist;
        if (file != null)
        {
            sharedpreferences = arraylist;
            if (isCacheDirectoryWriteable(file))
            {
                sharedpreferences = arraylist;
                if (s.isEmpty())
                {
                    sharedpreferences = file;
                }
            }
        }
        arraylist = new ArrayList();
        File afile[] = ContextCompat.getExternalCacheDirs(context);
        if (afile != null)
        {
            int j = afile.length;
            for (int i = 0; i < j; i++)
            {
                File file3 = afile[i];
                if (file3 != null)
                {
                    arraylist.add(file3);
                }
            }

        }
        File file1 = context.getCacheDir();
        if (file1 != null)
        {
            arraylist.add(file1);
        }
        builder = ImmutableList.builder();
        iterator = arraylist.iterator();
_L2:
        Object obj;
        if (!iterator.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        File file2 = (File)iterator.next();
        Debug.Printf("Cache: checking cache location %s", new Object[] {
            file2
        });
        if (!isCacheDirectoryWriteable(file2))
        {
            break MISSING_BLOCK_LABEL_362;
        }
        builder.add(file2);
        obj = file2;
        if (sharedpreferences != null)
        {
            if (!s.isEmpty())
            {
                if (!file2.getAbsolutePath().equals(s))
                {
                    break MISSING_BLOCK_LABEL_362;
                }
                obj = file2;
            } else
            {
                obj = sharedpreferences;
            }
        }
_L3:
        sharedpreferences = ((SharedPreferences) (obj));
        if (true) goto _L2; else goto _L1
_L1:
        obj = sharedpreferences;
        if (sharedpreferences == null)
        {
            obj = context.getCacheDir();
        }
        Debug.Printf("Cache: cache location set to %s", new Object[] {
            ((File) (obj)).getAbsolutePath()
        });
        availableCacheDirs.set(builder.build());
        baseCacheDir.set(obj);
        try
        {
            ((File) (obj)).mkdirs();
            if (((File) (obj)).exists())
            {
                (new File(((File) (obj)), ".nomedia")).createNewFile();
            }
        }
        // Misplaced declaration of an exception variable
        catch (Context context) { }
        if (file != null && !file.equals(obj))
        {
            Debug.Printf("Cache: Cache location has been changed.", new Object[0]);
            TextureCache.getInstance().onCacheDirChanged();
            MeshCache.onCacheDirChanged();
        }
        return;
        obj = sharedpreferences;
          goto _L3
    }

    private void updateNotificationSoundDefault(SharedPreferences sharedpreferences, NotificationType notificationtype)
    {
        if (!sharedpreferences.contains(notificationtype.getRingtoneKey()))
        {
            Object obj = (NotificationSounds)NotificationSounds.defaultSounds.get(notificationtype);
            if (obj != null)
            {
                obj = ((NotificationSounds) (obj)).getUri();
                sharedpreferences = sharedpreferences.edit();
                sharedpreferences.putString(notificationtype.getRingtoneKey(), ((Uri) (obj)).toString());
                sharedpreferences.apply();
                Debug.Printf("NotificationSounds: Updated %s preference to %s", new Object[] {
                    notificationtype.getRingtoneKey(), obj
                });
            }
        }
    }

    public void enableVoice()
    {
        android.content.SharedPreferences.Editor editor = LumiyaApp.getDefaultSharedPreferences().edit();
        editor.putBoolean("enableVoice", true);
        editor.apply();
    }

    public boolean getAdvancedRendering()
    {
        return advancedRendering;
    }

    public final boolean getAutoReconnect()
    {
        return autoReconnect;
    }

    public ImmutableList getAvailableCacheDirs()
    {
        return (ImmutableList)availableCacheDirs.get();
    }

    public File getBaseCacheDir()
    {
        return (File)baseCacheDir.get();
    }

    public final File getCacheDir(String s)
    {
        cacheDirUsed.set(true);
        File file1 = (File)baseCacheDir.get();
        File file = file1;
        if (file1 == null)
        {
            file = LumiyaApp.getContext().getCacheDir();
        }
        s = new File(file, s);
        try
        {
            s.mkdirs();
        }
        catch (Exception exception)
        {
            return s;
        }
        return s;
    }

    public final boolean getCompressedTextures()
    {
        return compressedTextures;
    }

    public float getForceDaylightHour()
    {
        return forceDaylightHour;
    }

    public boolean getForceDaylightTime()
    {
        return forceDaylightTime;
    }

    public final boolean getHighQualityTextures()
    {
        return highQualityTextures;
    }

    public final boolean getHoverTextEnableHUDs()
    {
        return hoverTextEnableHUDs;
    }

    public final boolean getHoverTextEnableObjects()
    {
        return hoverTextEnableObjects;
    }

    public boolean getKeepWifiOn()
    {
        return keepWifiOn;
    }

    public final int getMaxReconnectAttempts()
    {
        return maxReconnectAttempts;
    }

    public final int getMaxTextureDownloads()
    {
        return maxTextureDownloads;
    }

    public final MeshRendering getMeshRendering()
    {
        return meshRendering;
    }

    public final boolean getRLVEnabled()
    {
        return RLVEnabled;
    }

    public boolean getRenderClouds()
    {
        return renderClouds;
    }

    public boolean getShowTimestamps()
    {
        return showTimestamps;
    }

    public final boolean getTerrainTextures()
    {
        return terrainTextures;
    }

    public int getThemeResourceId()
    {
        return themeResourceId;
    }

    public boolean getUseFXAA()
    {
        return useFXAA;
    }

    public boolean getVoiceEnabled()
    {
        return voiceEnabled;
    }

    public void initialize()
    {
        SharedPreferences sharedpreferences = LumiyaApp.getDefaultSharedPreferences();
        updateFromPreferences(LumiyaApp.getContext(), sharedpreferences);
        sharedpreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public boolean isCacheDirUsed()
    {
        return cacheDirUsed.get();
    }

    public boolean isLegacyUserNames()
    {
        return legacyUserNames;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedpreferences, String s)
    {
        updateFromPreferences(LumiyaApp.getContext(), sharedpreferences);
        EventBus.getInstance().publish(new GlobalOptionsChangedEvent(sharedpreferences));
    }

    public void updateFromPreferences(Context context, SharedPreferences sharedpreferences)
    {
        int i;
        Debug.Printf("Updating options from preferences.", new Object[0]);
        updateNotificationSoundDefault(sharedpreferences, NotificationType.Private);
        updateNotificationSoundDefault(sharedpreferences, NotificationType.Group);
        updateNotificationSoundDefault(sharedpreferences, NotificationType.LocalChat);
        Object obj;
        boolean flag;
        if (!sharedpreferences.getBoolean("system_defaults_set", false))
        {
            obj = sharedpreferences.edit();
            long l = getTotalMemory();
            int j = Runtime.getRuntime().availableProcessors();
            int k;
            if (j >= 2 && l > 0x80000L)
            {
                ((android.content.SharedPreferences.Editor) (obj)).putBoolean("high_quality_textures", true);
            } else
            {
                ((android.content.SharedPreferences.Editor) (obj)).putBoolean("high_quality_textures", false);
            }
            i = 64;
            if (l != 0L)
            {
                if (l <= 0x40000L)
                {
                    i = 32;
                } else
                if (l <= 0x80000L)
                {
                    i = 64;
                } else
                {
                    i = 128;
                }
            }
            ((android.content.SharedPreferences.Editor) (obj)).putString("texture_memory_limit", Integer.toString(i));
            i = 2;
            if (j >= 4 && l > 0x80000L)
            {
                i = 8;
            } else
            if (j >= 2)
            {
                i = 4;
            }
            ((android.content.SharedPreferences.Editor) (obj)).putString("max_texture_downloads", Integer.toString(i));
            ((android.content.SharedPreferences.Editor) (obj)).putBoolean("system_defaults_set", true);
            ((android.content.SharedPreferences.Editor) (obj)).commit();
        }
        k = themeResourceId;
        obj = Strings.nullToEmpty(sharedpreferences.getString("theme", "light"));
        if (((String) (obj)).equals("dark"))
        {
            themeResourceId = 0x7f0b002b;
        } else
        if (((String) (obj)).equals("pink"))
        {
            themeResourceId = 0x7f0b002f;
        } else
        {
            themeResourceId = 0x7f0b002c;
        }
        legacyUserNames = sharedpreferences.getBoolean("legacyUserNames", false);
        showTimestamps = sharedpreferences.getBoolean("chatTimestamps", true);
        highQualityTextures = sharedpreferences.getBoolean("high_quality_textures", false);
        compressedTextures = sharedpreferences.getBoolean("compressed_textures", true);
        keepWifiOn = sharedpreferences.getBoolean("keep_wifi_on", true);
        cloudSyncEnabled = sharedpreferences.getBoolean("sync_to_gdrive", false);
        if (sharedpreferences.getBoolean("enableVoice", false))
        {
            flag = VoicePluginServiceConnection.isPluginSupported();
        } else
        {
            flag = false;
        }
        voiceEnabled = flag;
        j = Integer.parseInt(sharedpreferences.getString("max_texture_downloads", "2"));
        i = j;
        if (j < 1)
        {
            i = 1;
        }
_L1:
        if (i != maxTextureDownloads)
        {
            maxTextureDownloads = i;
            TextureCache.getInstance().setMaxTextureDownloads(maxTextureDownloads);
        }
        terrainTextures = sharedpreferences.getBoolean("terrain_textures", true);
        i = 64;
        j = Integer.parseInt(sharedpreferences.getString("texture_memory_limit", "64"));
        i = j;
_L2:
        Exception exception;
        try
        {
            meshRendering = MeshRendering.valueOf(sharedpreferences.getString("mesh_rendering", "high"));
        }
        catch (Exception exception2) { }
        TextureMemoryTracker.setMemoryLimit(i * 1024 * 1024);
        RLVEnabled = sharedpreferences.getBoolean("rlv_enabled", false);
        autoReconnect = sharedpreferences.getBoolean("auto_reconnect", true);
        try
        {
            maxReconnectAttempts = Integer.parseInt(sharedpreferences.getString("reconnect_attempts", "10"));
        }
        catch (Exception exception1) { }
        updateCacheDir(context, sharedpreferences);
        context = sharedpreferences.getString("hover_text", "huds");
        if (context.equals("all"))
        {
            hoverTextEnableHUDs = true;
            hoverTextEnableObjects = true;
        } else
        if (context.equals("none"))
        {
            hoverTextEnableHUDs = false;
            hoverTextEnableObjects = false;
        } else
        {
            hoverTextEnableHUDs = true;
            hoverTextEnableObjects = false;
        }
        advancedRendering = sharedpreferences.getBoolean("advanced_rendering", true);
        useFXAA = sharedpreferences.getBoolean("fxaa_enable", false);
        renderClouds = sharedpreferences.getBoolean("clouds_enable", true);
        context = sharedpreferences.getString("render_time_of_day", "sim");
        if (context.equalsIgnoreCase("sim"))
        {
            forceDaylightTime = false;
            forceDaylightHour = 0.5F;
        } else
        {
            try
            {
                forceDaylightTime = true;
                forceDaylightHour = Float.parseFloat(context);
            }
            // Misplaced declaration of an exception variable
            catch (Context context)
            {
                forceDaylightTime = false;
                forceDaylightHour = 0.5F;
            }
        }
        if (k != themeResourceId)
        {
            EventBus.getInstance().publish(new ThemeChangedEvent(themeResourceId));
        }
        return;
        exception;
        i = 2;
          goto _L1
        Exception exception3;
        exception3;
          goto _L2
    }
}
