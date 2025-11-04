// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import javax.annotation.Nonnull;
import android.content.SharedPreferences$Editor;
import android.net.Uri;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.content.Context;
import java.io.IOException;
import javax.annotation.Nullable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.File;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.atomic.AtomicReference;
import android.content.SharedPreferences$OnSharedPreferenceChangeListener;

public class GlobalOptions implements SharedPreferences$OnSharedPreferenceChangeListener
{
    private boolean RLVEnabled;
    private boolean advancedRendering;
    private boolean autoReconnect;
    private final AtomicReference<ImmutableList<File>> availableCacheDirs;
    private final AtomicReference<File> baseCacheDir;
    private final AtomicBoolean cacheDirUsed;
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
    
    public GlobalOptions() {
        this.themeResourceId = 2131427372;
        this.legacyUserNames = false;
        this.showTimestamps = true;
        this.highQualityTextures = false;
        this.compressedTextures = true;
        this.maxTextureDownloads = 2;
        this.terrainTextures = true;
        this.meshRendering = MeshRendering.medium;
        this.RLVEnabled = false;
        this.keepWifiOn = false;
        this.baseCacheDir = new AtomicReference<File>();
        this.availableCacheDirs = new AtomicReference<ImmutableList<File>>(ImmutableList.of());
        this.cacheDirUsed = new AtomicBoolean(false);
        this.autoReconnect = true;
        this.maxReconnectAttempts = 10;
        this.hoverTextEnableHUDs = true;
        this.hoverTextEnableObjects = false;
        this.advancedRendering = true;
        this.useFXAA = false;
        this.renderClouds = true;
        this.forceDaylightTime = false;
        this.forceDaylightHour = 0.5f;
        this.cloudSyncEnabled = false;
        this.voiceEnabled = false;
    }
    
    public static GlobalOptions getInstance() {
        return InstanceHolder.Instance;
    }
    
    private static long getTotalMemory() {
        long n2;
        final long n = n2 = 0L;
        try {
            n2 = n;
            n2 = n;
            final FileReader in = new FileReader("/proc/meminfo");
            n2 = n;
            final BufferedReader bufferedReader = new BufferedReader(in, 8192);
        Label_0174:
            while (true) {
                String line;
                do {
                    n2 = n;
                    line = bufferedReader.readLine();
                    final long n3 = n;
                    if (line == null) {
                        n2 = n3;
                        bufferedReader.close();
                        n2 = n3;
                        return n2;
                    }
                    n2 = n;
                } while (!line.startsWith("MemTotal:"));
                n2 = n;
                final String[] split = line.split("\\s+");
                long long1 = n;
                n2 = n;
                if (split.length >= 2) {
                    n2 = n;
                    long1 = Long.parseLong(split[1]);
                }
                int i = 0;
                while (true) {
                    final long n3 = long1;
                    n2 = long1;
                    if (i >= split.length) {
                        continue Label_0174;
                    }
                    n2 = long1;
                    n2 = long1;
                    final StringBuilder sb = new StringBuilder();
                    n2 = long1;
                    Debug.Log(sb.append("Memory ").append(i).append(":").append(split[i]).toString());
                    ++i;
                }
                break;
            }
        }
        catch (final Exception ex) {
            return n2;
        }
    }
    
    private boolean isCacheDirectoryWriteable(@Nullable final File parent) {
        if (parent == null) {
            return false;
        }
        try {
            parent.mkdirs();
            if (!parent.exists()) {
                return false;
            }
            final File file = new File(parent, ".tmp");
            if (file.exists()) {
                file.delete();
                if (file.exists()) {
                    return false;
                }
            }
            file.createNewFile();
            if (!file.exists()) {
                return false;
            }
            file.delete();
            return true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    private void updateCacheDir(final Context context, final SharedPreferences sharedPreferences) {
        final File file = null;
        final File file2 = this.baseCacheDir.get();
        final String string = sharedPreferences.getString("cache_location", "");
        File file3 = file;
        if (file2 != null) {
            file3 = file;
            if (this.isCacheDirectoryWriteable(file2)) {
                file3 = file;
                if (string.isEmpty()) {
                    file3 = file2;
                }
            }
        }
        final ArrayList list = new ArrayList();
        final File[] externalCacheDirs = ContextCompat.getExternalCacheDirs(context);
        if (externalCacheDirs != null) {
            for (final File file4 : externalCacheDirs) {
                if (file4 != null) {
                    list.add(file4);
                }
            }
        }
        final File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            list.add(cacheDir);
        }
        final ImmutableList.Builder<File> builder = ImmutableList.builder();
        final Iterator iterator = list.iterator();
    Label_0236_Outer:
        while (true) {
            while (true) {
                Label_0361: {
                    if (!iterator.hasNext()) {
                        File cacheDir2;
                        if ((cacheDir2 = file3) == null) {
                            cacheDir2 = context.getCacheDir();
                        }
                        Debug.Printf("Cache: cache location set to %s", cacheDir2.getAbsolutePath());
                        this.availableCacheDirs.set(builder.build());
                        this.baseCacheDir.set(cacheDir2);
                        while (true) {
                            try {
                                cacheDir2.mkdirs();
                                if (cacheDir2.exists()) {
                                    new File(cacheDir2, ".nomedia").createNewFile();
                                }
                                if (file2 != null && !file2.equals(cacheDir2)) {
                                    Debug.Printf("Cache: Cache location has been changed.", new Object[0]);
                                    TextureCache.getInstance().onCacheDirChanged();
                                    MeshCache.onCacheDirChanged();
                                }
                                return;
                            }
                            catch (final Exception ex) {
                                continue Label_0236_Outer;
                            }
                            break;
                        }
                        break Label_0361;
                    }
                    final File file5 = (File)iterator.next();
                    Debug.Printf("Cache: checking cache location %s", file5);
                    if (!this.isCacheDirectoryWriteable(file5)) {
                        break Label_0361;
                    }
                    builder.add(file5);
                    File file6 = file5;
                    if (file3 != null) {
                        if (!string.isEmpty()) {
                            if (!file5.getAbsolutePath().equals(string)) {
                                break Label_0361;
                            }
                            file6 = file5;
                        }
                        else {
                            file6 = file3;
                        }
                    }
                    file3 = file6;
                    continue Label_0236_Outer;
                }
                File file6 = file3;
                continue;
            }
        }
    }
    
    private void updateNotificationSoundDefault(final SharedPreferences sharedPreferences, final NotificationType notificationType) {
        if (!sharedPreferences.contains(notificationType.getRingtoneKey())) {
            final NotificationSounds notificationSounds = NotificationSounds.defaultSounds.get(notificationType);
            if (notificationSounds != null) {
                final Uri uri = notificationSounds.getUri();
                final SharedPreferences$Editor edit = sharedPreferences.edit();
                edit.putString(notificationType.getRingtoneKey(), uri.toString());
                edit.apply();
                Debug.Printf("NotificationSounds: Updated %s preference to %s", notificationType.getRingtoneKey(), uri);
            }
        }
    }
    
    public void enableVoice() {
        final SharedPreferences$Editor edit = LumiyaApp.getDefaultSharedPreferences().edit();
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
    
    public final File getCacheDir(@Nonnull String child) {
        this.cacheDirUsed.set(true);
        File cacheDir;
        if ((cacheDir = this.baseCacheDir.get()) == null) {
            cacheDir = LumiyaApp.getContext().getCacheDir();
        }
        child = (String)new File(cacheDir, child);
        try {
            ((File)child).mkdirs();
            return (File)child;
        }
        catch (final Exception ex) {
            return (File)child;
        }
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
        final SharedPreferences defaultSharedPreferences = LumiyaApp.getDefaultSharedPreferences();
        this.updateFromPreferences(LumiyaApp.getContext(), defaultSharedPreferences);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener((SharedPreferences$OnSharedPreferenceChangeListener)this);
    }
    
    public boolean isCacheDirUsed() {
        return this.cacheDirUsed.get();
    }
    
    public boolean isLegacyUserNames() {
        return this.legacyUserNames;
    }
    
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String s) {
        this.updateFromPreferences(LumiyaApp.getContext(), sharedPreferences);
        EventBus.getInstance().publish(new GlobalOptionsChangedEvent(sharedPreferences));
    }
    
    public void updateFromPreferences(final Context p0, final SharedPreferences p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: iconst_0       
        //     4: anewarray       Ljava/lang/Object;
        //     7: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    10: aload_0        
        //    11: aload_2        
        //    12: getstatic       com/lumiyaviewer/lumiya/ui/settings/NotificationType.Private:Lcom/lumiyaviewer/lumiya/ui/settings/NotificationType;
        //    15: invokespecial   com/lumiyaviewer/lumiya/GlobalOptions.updateNotificationSoundDefault:(Landroid/content/SharedPreferences;Lcom/lumiyaviewer/lumiya/ui/settings/NotificationType;)V
        //    18: aload_0        
        //    19: aload_2        
        //    20: getstatic       com/lumiyaviewer/lumiya/ui/settings/NotificationType.Group:Lcom/lumiyaviewer/lumiya/ui/settings/NotificationType;
        //    23: invokespecial   com/lumiyaviewer/lumiya/GlobalOptions.updateNotificationSoundDefault:(Landroid/content/SharedPreferences;Lcom/lumiyaviewer/lumiya/ui/settings/NotificationType;)V
        //    26: aload_0        
        //    27: aload_2        
        //    28: getstatic       com/lumiyaviewer/lumiya/ui/settings/NotificationType.LocalChat:Lcom/lumiyaviewer/lumiya/ui/settings/NotificationType;
        //    31: invokespecial   com/lumiyaviewer/lumiya/GlobalOptions.updateNotificationSoundDefault:(Landroid/content/SharedPreferences;Lcom/lumiyaviewer/lumiya/ui/settings/NotificationType;)V
        //    34: aload_2        
        //    35: ldc_w           "system_defaults_set"
        //    38: iconst_0       
        //    39: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //    44: ifne            187
        //    47: aload_2        
        //    48: invokeinterface android/content/SharedPreferences.edit:()Landroid/content/SharedPreferences$Editor;
        //    53: astore_3       
        //    54: invokestatic    com/lumiyaviewer/lumiya/GlobalOptions.getTotalMemory:()J
        //    57: lstore          4
        //    59: invokestatic    java/lang/Runtime.getRuntime:()Ljava/lang/Runtime;
        //    62: invokevirtual   java/lang/Runtime.availableProcessors:()I
        //    65: istore          6
        //    67: iload           6
        //    69: iconst_2       
        //    70: if_icmplt       649
        //    73: lload           4
        //    75: ldc2_w          524288
        //    78: lcmp           
        //    79: ifle            649
        //    82: aload_3        
        //    83: ldc_w           "high_quality_textures"
        //    86: iconst_1       
        //    87: invokeinterface android/content/SharedPreferences$Editor.putBoolean:(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;
        //    92: pop            
        //    93: bipush          64
        //    95: istore          7
        //    97: lload           4
        //    99: lconst_0       
        //   100: lcmp           
        //   101: ifeq            117
        //   104: lload           4
        //   106: ldc2_w          262144
        //   109: lcmp           
        //   110: ifgt            663
        //   113: bipush          32
        //   115: istore          7
        //   117: aload_3        
        //   118: ldc_w           "texture_memory_limit"
        //   121: iload           7
        //   123: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
        //   126: invokeinterface android/content/SharedPreferences$Editor.putString:(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
        //   131: pop            
        //   132: iconst_2       
        //   133: istore          7
        //   135: iload           6
        //   137: iconst_4       
        //   138: if_icmplt       687
        //   141: lload           4
        //   143: ldc2_w          524288
        //   146: lcmp           
        //   147: ifle            687
        //   150: bipush          8
        //   152: istore          7
        //   154: aload_3        
        //   155: ldc_w           "max_texture_downloads"
        //   158: iload           7
        //   160: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
        //   163: invokeinterface android/content/SharedPreferences$Editor.putString:(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
        //   168: pop            
        //   169: aload_3        
        //   170: ldc_w           "system_defaults_set"
        //   173: iconst_1       
        //   174: invokeinterface android/content/SharedPreferences$Editor.putBoolean:(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;
        //   179: pop            
        //   180: aload_3        
        //   181: invokeinterface android/content/SharedPreferences$Editor.commit:()Z
        //   186: pop            
        //   187: aload_0        
        //   188: getfield        com/lumiyaviewer/lumiya/GlobalOptions.themeResourceId:I
        //   191: istore          8
        //   193: aload_2        
        //   194: ldc_w           "theme"
        //   197: ldc_w           "light"
        //   200: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   205: invokestatic    com/google/common/base/Strings.nullToEmpty:(Ljava/lang/String;)Ljava/lang/String;
        //   208: astore_3       
        //   209: aload_3        
        //   210: ldc_w           "dark"
        //   213: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   216: ifeq            699
        //   219: aload_0        
        //   220: ldc_w           2131427371
        //   223: putfield        com/lumiyaviewer/lumiya/GlobalOptions.themeResourceId:I
        //   226: aload_0        
        //   227: aload_2        
        //   228: ldc_w           "legacyUserNames"
        //   231: iconst_0       
        //   232: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   237: putfield        com/lumiyaviewer/lumiya/GlobalOptions.legacyUserNames:Z
        //   240: aload_0        
        //   241: aload_2        
        //   242: ldc_w           "chatTimestamps"
        //   245: iconst_1       
        //   246: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   251: putfield        com/lumiyaviewer/lumiya/GlobalOptions.showTimestamps:Z
        //   254: aload_0        
        //   255: aload_2        
        //   256: ldc_w           "high_quality_textures"
        //   259: iconst_0       
        //   260: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   265: putfield        com/lumiyaviewer/lumiya/GlobalOptions.highQualityTextures:Z
        //   268: aload_0        
        //   269: aload_2        
        //   270: ldc_w           "compressed_textures"
        //   273: iconst_1       
        //   274: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   279: putfield        com/lumiyaviewer/lumiya/GlobalOptions.compressedTextures:Z
        //   282: aload_0        
        //   283: aload_2        
        //   284: ldc_w           "keep_wifi_on"
        //   287: iconst_1       
        //   288: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   293: putfield        com/lumiyaviewer/lumiya/GlobalOptions.keepWifiOn:Z
        //   296: aload_0        
        //   297: aload_2        
        //   298: ldc_w           "sync_to_gdrive"
        //   301: iconst_0       
        //   302: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   307: putfield        com/lumiyaviewer/lumiya/GlobalOptions.cloudSyncEnabled:Z
        //   310: aload_2        
        //   311: ldc_w           "enableVoice"
        //   314: iconst_0       
        //   315: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   320: ifeq            728
        //   323: invokestatic    com/lumiyaviewer/lumiya/voiceintf/VoicePluginServiceConnection.isPluginSupported:()Z
        //   326: istore          9
        //   328: aload_0        
        //   329: iload           9
        //   331: putfield        com/lumiyaviewer/lumiya/GlobalOptions.voiceEnabled:Z
        //   334: aload_2        
        //   335: ldc_w           "max_texture_downloads"
        //   338: ldc_w           "2"
        //   341: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   346: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //   349: istore          6
        //   351: iload           6
        //   353: istore          7
        //   355: iload           6
        //   357: iconst_1       
        //   358: if_icmpge       364
        //   361: iconst_1       
        //   362: istore          7
        //   364: iload           7
        //   366: aload_0        
        //   367: getfield        com/lumiyaviewer/lumiya/GlobalOptions.maxTextureDownloads:I
        //   370: if_icmpeq       389
        //   373: aload_0        
        //   374: iload           7
        //   376: putfield        com/lumiyaviewer/lumiya/GlobalOptions.maxTextureDownloads:I
        //   379: invokestatic    com/lumiyaviewer/lumiya/res/textures/TextureCache.getInstance:()Lcom/lumiyaviewer/lumiya/res/textures/TextureCache;
        //   382: aload_0        
        //   383: getfield        com/lumiyaviewer/lumiya/GlobalOptions.maxTextureDownloads:I
        //   386: invokevirtual   com/lumiyaviewer/lumiya/res/textures/TextureCache.setMaxTextureDownloads:(I)V
        //   389: aload_0        
        //   390: aload_2        
        //   391: ldc_w           "terrain_textures"
        //   394: iconst_1       
        //   395: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   400: putfield        com/lumiyaviewer/lumiya/GlobalOptions.terrainTextures:Z
        //   403: bipush          64
        //   405: istore          7
        //   407: aload_2        
        //   408: ldc_w           "texture_memory_limit"
        //   411: ldc_w           "64"
        //   414: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   419: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //   422: istore          6
        //   424: iload           6
        //   426: istore          7
        //   428: aload_0        
        //   429: aload_2        
        //   430: ldc_w           "mesh_rendering"
        //   433: ldc_w           "high"
        //   436: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   441: invokestatic    com/lumiyaviewer/lumiya/GlobalOptions$MeshRendering.valueOf:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/GlobalOptions$MeshRendering;
        //   444: putfield        com/lumiyaviewer/lumiya/GlobalOptions.meshRendering:Lcom/lumiyaviewer/lumiya/GlobalOptions$MeshRendering;
        //   447: iload           7
        //   449: sipush          1024
        //   452: imul           
        //   453: sipush          1024
        //   456: imul           
        //   457: invokestatic    com/lumiyaviewer/lumiya/render/TextureMemoryTracker.setMemoryLimit:(I)V
        //   460: aload_0        
        //   461: aload_2        
        //   462: ldc_w           "rlv_enabled"
        //   465: iconst_0       
        //   466: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   471: putfield        com/lumiyaviewer/lumiya/GlobalOptions.RLVEnabled:Z
        //   474: aload_0        
        //   475: aload_2        
        //   476: ldc_w           "auto_reconnect"
        //   479: iconst_1       
        //   480: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   485: putfield        com/lumiyaviewer/lumiya/GlobalOptions.autoReconnect:Z
        //   488: aload_0        
        //   489: aload_2        
        //   490: ldc_w           "reconnect_attempts"
        //   493: ldc_w           "10"
        //   496: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   501: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //   504: putfield        com/lumiyaviewer/lumiya/GlobalOptions.maxReconnectAttempts:I
        //   507: aload_0        
        //   508: aload_1        
        //   509: aload_2        
        //   510: invokespecial   com/lumiyaviewer/lumiya/GlobalOptions.updateCacheDir:(Landroid/content/Context;Landroid/content/SharedPreferences;)V
        //   513: aload_2        
        //   514: ldc_w           "hover_text"
        //   517: ldc_w           "huds"
        //   520: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   525: astore_1       
        //   526: aload_1        
        //   527: ldc_w           "all"
        //   530: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   533: ifeq            741
        //   536: aload_0        
        //   537: iconst_1       
        //   538: putfield        com/lumiyaviewer/lumiya/GlobalOptions.hoverTextEnableHUDs:Z
        //   541: aload_0        
        //   542: iconst_1       
        //   543: putfield        com/lumiyaviewer/lumiya/GlobalOptions.hoverTextEnableObjects:Z
        //   546: aload_0        
        //   547: aload_2        
        //   548: ldc_w           "advanced_rendering"
        //   551: iconst_1       
        //   552: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   557: putfield        com/lumiyaviewer/lumiya/GlobalOptions.advancedRendering:Z
        //   560: aload_0        
        //   561: aload_2        
        //   562: ldc_w           "fxaa_enable"
        //   565: iconst_0       
        //   566: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   571: putfield        com/lumiyaviewer/lumiya/GlobalOptions.useFXAA:Z
        //   574: aload_0        
        //   575: aload_2        
        //   576: ldc_w           "clouds_enable"
        //   579: iconst_1       
        //   580: invokeinterface android/content/SharedPreferences.getBoolean:(Ljava/lang/String;Z)Z
        //   585: putfield        com/lumiyaviewer/lumiya/GlobalOptions.renderClouds:Z
        //   588: aload_2        
        //   589: ldc_w           "render_time_of_day"
        //   592: ldc_w           "sim"
        //   595: invokeinterface android/content/SharedPreferences.getString:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   600: astore_1       
        //   601: aload_1        
        //   602: ldc_w           "sim"
        //   605: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   608: ifeq            777
        //   611: aload_0        
        //   612: iconst_0       
        //   613: putfield        com/lumiyaviewer/lumiya/GlobalOptions.forceDaylightTime:Z
        //   616: aload_0        
        //   617: ldc             0.5
        //   619: putfield        com/lumiyaviewer/lumiya/GlobalOptions.forceDaylightHour:F
        //   622: iload           8
        //   624: aload_0        
        //   625: getfield        com/lumiyaviewer/lumiya/GlobalOptions.themeResourceId:I
        //   628: if_icmpeq       648
        //   631: invokestatic    com/lumiyaviewer/lumiya/eventbus/EventBus.getInstance:()Lcom/lumiyaviewer/lumiya/eventbus/EventBus;
        //   634: new             Lcom/lumiyaviewer/lumiya/ui/settings/ThemeChangedEvent;
        //   637: dup            
        //   638: aload_0        
        //   639: getfield        com/lumiyaviewer/lumiya/GlobalOptions.themeResourceId:I
        //   642: invokespecial   com/lumiyaviewer/lumiya/ui/settings/ThemeChangedEvent.<init>:(I)V
        //   645: invokevirtual   com/lumiyaviewer/lumiya/eventbus/EventBus.publish:(Ljava/lang/Object;)V
        //   648: return         
        //   649: aload_3        
        //   650: ldc_w           "high_quality_textures"
        //   653: iconst_0       
        //   654: invokeinterface android/content/SharedPreferences$Editor.putBoolean:(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;
        //   659: pop            
        //   660: goto            93
        //   663: lload           4
        //   665: ldc2_w          524288
        //   668: lcmp           
        //   669: ifgt            679
        //   672: bipush          64
        //   674: istore          7
        //   676: goto            117
        //   679: sipush          128
        //   682: istore          7
        //   684: goto            117
        //   687: iload           6
        //   689: iconst_2       
        //   690: if_icmplt       154
        //   693: iconst_4       
        //   694: istore          7
        //   696: goto            154
        //   699: aload_3        
        //   700: ldc_w           "pink"
        //   703: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   706: ifeq            719
        //   709: aload_0        
        //   710: ldc_w           2131427375
        //   713: putfield        com/lumiyaviewer/lumiya/GlobalOptions.themeResourceId:I
        //   716: goto            226
        //   719: aload_0        
        //   720: ldc             2131427372
        //   722: putfield        com/lumiyaviewer/lumiya/GlobalOptions.themeResourceId:I
        //   725: goto            226
        //   728: iconst_0       
        //   729: istore          9
        //   731: goto            328
        //   734: astore_3       
        //   735: iconst_2       
        //   736: istore          7
        //   738: goto            364
        //   741: aload_1        
        //   742: ldc_w           "none"
        //   745: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   748: ifeq            764
        //   751: aload_0        
        //   752: iconst_0       
        //   753: putfield        com/lumiyaviewer/lumiya/GlobalOptions.hoverTextEnableHUDs:Z
        //   756: aload_0        
        //   757: iconst_0       
        //   758: putfield        com/lumiyaviewer/lumiya/GlobalOptions.hoverTextEnableObjects:Z
        //   761: goto            546
        //   764: aload_0        
        //   765: iconst_1       
        //   766: putfield        com/lumiyaviewer/lumiya/GlobalOptions.hoverTextEnableHUDs:Z
        //   769: aload_0        
        //   770: iconst_0       
        //   771: putfield        com/lumiyaviewer/lumiya/GlobalOptions.hoverTextEnableObjects:Z
        //   774: goto            546
        //   777: aload_0        
        //   778: iconst_1       
        //   779: putfield        com/lumiyaviewer/lumiya/GlobalOptions.forceDaylightTime:Z
        //   782: aload_0        
        //   783: aload_1        
        //   784: invokestatic    java/lang/Float.parseFloat:(Ljava/lang/String;)F
        //   787: putfield        com/lumiyaviewer/lumiya/GlobalOptions.forceDaylightHour:F
        //   790: goto            622
        //   793: astore_1       
        //   794: aload_0        
        //   795: iconst_0       
        //   796: putfield        com/lumiyaviewer/lumiya/GlobalOptions.forceDaylightTime:Z
        //   799: aload_0        
        //   800: ldc             0.5
        //   802: putfield        com/lumiyaviewer/lumiya/GlobalOptions.forceDaylightHour:F
        //   805: goto            622
        //   808: astore_3       
        //   809: goto            507
        //   812: astore_3       
        //   813: goto            447
        //   816: astore_3       
        //   817: goto            428
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  334    351    734    741    Ljava/lang/Exception;
        //  407    424    816    820    Ljava/lang/Exception;
        //  428    447    812    816    Ljava/lang/Exception;
        //  488    507    808    812    Ljava/lang/Exception;
        //  777    790    793    808    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0428:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static class GlobalOptionsChangedEvent
    {
        public final SharedPreferences preferences;
        
        public GlobalOptionsChangedEvent(final SharedPreferences preferences) {
            this.preferences = preferences;
        }
    }
    
    private static class InstanceHolder
    {
        private static final GlobalOptions Instance;
        
        static {
            Instance = new GlobalOptions();
        }
    }
    
    public enum MeshRendering
    {
        disabled("disabled", 4, (String)null), 
        high("high", 0, "high_lod"), 
        low("low", 2, "low_lod"), 
        lowest("lowest", 3, "lowest_lod"), 
        medium("medium", 1, "medium_lod");
        
        private String lodName;
        
        private MeshRendering(final String name, final int ordinal, final String lodName) {
            this.lodName = lodName;
        }
        
        public String getLODName() {
            return this.lodName;
        }
    }
}
