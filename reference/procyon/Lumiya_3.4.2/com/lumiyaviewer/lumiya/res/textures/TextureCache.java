// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.textures;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.Future;
import com.lumiyaviewer.lumiya.res.executors.Startable;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import java.util.UUID;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.util.concurrent.BlockingQueue;
import com.lumiyaviewer.lumiya.res.executors.WeakExecutor;
import java.util.concurrent.PriorityBlockingQueue;
import com.lumiyaviewer.lumiya.res.executors.StartingExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.io.File;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class TextureCache extends ResourceMemoryCache<DrawableTextureParams, OpenJPEG>
{
    private File baseDir;
    private final Object cacheDirLock;
    private final ExecutorService decompressorExecutor;
    private final AtomicBoolean isLowMemory;
    private final StartingExecutor memoryAwareExecutor;
    private final TextureCompressedCache textureCompressedCache;
    private File textureTempDir;
    
    public TextureCache() {
        this.cacheDirLock = new Object();
        this.isLowMemory = new AtomicBoolean(false);
        this.textureCompressedCache = new TextureCompressedCache();
        this.decompressorExecutor = new WeakExecutor("TextureDecompressor", 1, new PriorityBlockingQueue<Runnable>());
        this.memoryAwareExecutor = new StartingExecutor();
    }
    
    private boolean canBeLowQuality(final DrawableTextureParams drawableTextureParams) {
        return drawableTextureParams.textureClass() == TextureClass.Prim;
    }
    
    public static TextureCache getInstance() {
        return InstanceHolder.Instance;
    }
    
    @Override
    protected ResourceRequest<DrawableTextureParams, OpenJPEG> CreateNewRequest(final DrawableTextureParams drawableTextureParams, final ResourceManager<DrawableTextureParams, OpenJPEG> resourceManager) {
        if (GlobalOptions.getInstance().getHighQualityTextures() || (this.canBeLowQuality(drawableTextureParams) ^ true)) {
            final File resourceFile = this.getResourceFile(drawableTextureParams, true);
            if (resourceFile.exists()) {
                return new TextureLoadRequest(drawableTextureParams, resourceManager, resourceFile);
            }
        }
        if (this.canBeLowQuality(drawableTextureParams) && (GlobalOptions.getInstance().getHighQualityTextures() ^ true)) {
            final File resourceFile2 = this.getResourceFile(drawableTextureParams, false);
            if (resourceFile2.exists()) {
                return new TextureLoadRequest(drawableTextureParams, resourceManager, resourceFile2);
            }
        }
        return new TextureDecompressRequest(drawableTextureParams, resourceManager);
    }
    
    protected File getBaseDir() {
        synchronized (this.cacheDirLock) {
            if (this.baseDir == null) {
                this.baseDir = GlobalOptions.getInstance().getCacheDir("tex2");
            }
            return this.baseDir;
        }
    }
    
    public File getBitmapsBaseDir() {
        return GlobalOptions.getInstance().getCacheDir("bitmaps");
    }
    
    public ExecutorService getDecompressorExecutor() {
        return this.decompressorExecutor;
    }
    
    public File getResourceFile(final DrawableTextureParams drawableTextureParams, final boolean b) {
        synchronized (this) {
            return drawableTextureParams.getTextureRawPath(this.getBaseDir(), b);
        }
    }
    
    public TextureCompressedCache getTextureCompressedCache() {
        return this.textureCompressedCache;
    }
    
    public File getTextureCompressedFile(final DrawableTextureParams drawableTextureParams) {
        final UUID uuid = drawableTextureParams.uuid();
        final int hashCode = uuid.hashCode();
        return new File(this.getTextureTempDir(), String.format("%02x/%s.jp2", (hashCode >> 24 ^ (hashCode >> 8 ^ hashCode ^ hashCode >> 16)) & 0xFF, uuid.toString()));
    }
    
    public File getTextureCompressedFileOld(final DrawableTextureParams drawableTextureParams) {
        return new File(this.getTextureTempDir(), drawableTextureParams.uuid().toString() + ".jp2");
    }
    
    protected File getTextureTempDir() {
        synchronized (this.cacheDirLock) {
            if (this.textureTempDir == null) {
                this.textureTempDir = GlobalOptions.getInstance().getCacheDir("textures");
            }
            return this.textureTempDir;
        }
    }
    
    public void onCacheDirChanged() {
        synchronized (this.cacheDirLock) {
            this.baseDir = null;
            this.textureTempDir = null;
        }
    }
    
    public void setFetcher(final SLTextureFetcher fetcher) {
        this.textureCompressedCache.setFetcher(fetcher);
    }
    
    public void setMaxTextureDownloads(final int maxTextureDownloads) {
        this.textureCompressedCache.setMaxTextureDownloads(maxTextureDownloads);
    }
    
    public void setTextureMemoryState(final boolean newValue) {
        if (this.isLowMemory.getAndSet(newValue) != newValue) {
            if (newValue) {
                this.memoryAwareExecutor.pause();
            }
            else {
                this.memoryAwareExecutor.unpause();
            }
        }
    }
    
    private static class InstanceHolder
    {
        private static final TextureCache Instance;
        
        static {
            Instance = new TextureCache();
        }
    }
    
    private class TextureDecompressRequest extends ResourceRequest<DrawableTextureParams, OpenJPEG> implements ResourceConsumer, Runnable, HasPriority, Startable
    {
        private volatile File compressedFile;
        private volatile Future<?> decompressorFuture;
        private volatile boolean lowQualityDone;
        
        private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues() {
            if (TextureDecompressRequest.-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues != null) {
                return TextureDecompressRequest.-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues;
            }
            int[] -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = new int[TextureClass.values().length];
            while (true) {
                try {
                    -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Asset.ordinal()] = 3;
                    try {
                        -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Baked.ordinal()] = 1;
                        try {
                            -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Prim.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Sculpt.ordinal()] = 2;
                                try {
                                    -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Terrain.ordinal()] = 5;
                                    return -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues;
                                }
                                catch (final NoSuchFieldError noSuchFieldError) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError2) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError3) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError4) {}
                }
                catch (final NoSuchFieldError noSuchFieldError5) {
                    continue;
                }
                break;
            }
        }
        
        public TextureDecompressRequest(final DrawableTextureParams drawableTextureParams, final ResourceManager<DrawableTextureParams, OpenJPEG> resourceManager) {
            super(drawableTextureParams, resourceManager);
            this.lowQualityDone = false;
        }
        
        private OpenJPEG decompress(final File file, final File dest, final boolean b) {
            try {
                final DrawableTextureParams drawableTextureParams = ((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams();
                if (dest.exists()) {
                    return new OpenJPEG(dest, drawableTextureParams.textureClass(), OpenJPEG.ImageFormat.Raw, b);
                }
                String s;
                if (b) {
                    s = "high";
                }
                else {
                    s = "low";
                }
                Debug.Printf("Decompressing (%s) %s texture %s to %s", s, drawableTextureParams.textureClass().toString(), drawableTextureParams.uuid().toString(), dest.getAbsolutePath());
                final OpenJPEG openJPEG = new OpenJPEG(file, drawableTextureParams.textureClass(), OpenJPEG.ImageFormat.JPEG2000, b);
                dest.getParentFile().mkdirs();
                final File file2 = new File(dest.getAbsolutePath() + ".tmpdec");
                if (drawableTextureParams.textureClass() == TextureClass.Prim && GlobalOptions.getInstance().getCompressedTextures()) {
                    openJPEG.CompressETC1();
                }
                openJPEG.SaveRaw(file2);
                file2.renameTo(dest);
                Debug.Log("Decompressed texture " + dest.getAbsolutePath());
                return openJPEG;
            }
            catch (final IOException ex) {
                Debug.Log("Failed to decompress texture " + file.getAbsolutePath());
                ex.printStackTrace();
                return null;
            }
        }
        
        @Override
        public void OnResourceReady(final Object o, final boolean b) {
            if (o instanceof File) {
                this.compressedFile = (File)o;
                if (((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams().textureClass() == TextureClass.Prim) {
                    TextureCache.this.memoryAwareExecutor.queueRequest(this);
                }
                else {
                    this.decompressorFuture = TextureCache.this.decompressorExecutor.submit(this);
                }
            }
            else if (o == null) {
                ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(null);
            }
        }
        
        @Override
        public void cancelRequest() {
            Debug.Printf("DecompressRequest: cancelled (%s)", ((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams().uuid().toString());
            final Future<?> decompressorFuture = this.decompressorFuture;
            if (decompressorFuture != null) {
                decompressorFuture.cancel(false);
            }
            TextureCache.this.textureCompressedCache.CancelRequest(this);
            TextureCache.this.memoryAwareExecutor.cancelRequest(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            TextureCache.this.textureCompressedCache.RequestResource(((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams(), this);
        }
        
        @Override
        public int getPriority() {
            final DrawableTextureParams drawableTextureParams = ((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams();
            if (TextureCache.this.canBeLowQuality(drawableTextureParams) && this.lowQualityDone) {
                return 4;
            }
            switch (-getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues()[drawableTextureParams.textureClass().ordinal()]) {
                default: {
                    return 3;
                }
                case 2: {
                    return 1;
                }
                case 1: {
                    return 2;
                }
            }
        }
        
        @Override
        public void run() {
            final DrawableTextureParams drawableTextureParams = ((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams();
            if (drawableTextureParams.textureClass() == TextureClass.Sculpt || drawableTextureParams.textureClass() == TextureClass.Baked) {
                ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(this.decompress(this.compressedFile, TextureCache.this.getResourceFile(drawableTextureParams, true), true));
            }
            else if (TextureCache.this.canBeLowQuality(drawableTextureParams) && (this.lowQualityDone ^ true)) {
                final OpenJPEG decompress = this.decompress(this.compressedFile, TextureCache.this.getResourceFile(drawableTextureParams, false), false);
                if (decompress == null) {
                    ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(null);
                }
                else if (GlobalOptions.getInstance().getHighQualityTextures()) {
                    this.lowQualityDone = true;
                    ((ResourceRequest<ResourceParams, OpenJPEG>)this).intermediateResult(decompress);
                    this.decompressorFuture = TextureCache.this.decompressorExecutor.submit(this);
                }
                else {
                    ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(decompress);
                }
            }
            else {
                ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(this.decompress(this.compressedFile, TextureCache.this.getResourceFile(drawableTextureParams, true), true));
            }
            TextureCache.this.memoryAwareExecutor.completeRequest(this);
        }
        
        @Override
        public void start() {
            this.decompressorFuture = TextureCache.this.decompressorExecutor.submit(this);
        }
    }
    
    private class TextureLoadRequest extends ResourceRequest<DrawableTextureParams, OpenJPEG> implements Runnable, Startable
    {
        private final File rawFile;
        
        public TextureLoadRequest(final DrawableTextureParams drawableTextureParams, final ResourceManager<DrawableTextureParams, OpenJPEG> resourceManager, final File rawFile) {
            super(drawableTextureParams, resourceManager);
            this.rawFile = rawFile;
        }
        
        @Override
        public void cancelRequest() {
            TextureCache.this.memoryAwareExecutor.cancelRequest(this);
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            if (((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams().textureClass() == TextureClass.Prim) {
                TextureCache.this.memoryAwareExecutor.queueRequest(this);
            }
            else {
                LoaderExecutor.getInstance().execute(this);
            }
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(new OpenJPEG(this.rawFile.getAbsoluteFile(), ((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams().textureClass(), OpenJPEG.ImageFormat.Raw, true));
                    TextureCache.this.memoryAwareExecutor.completeRequest(this);
                }
                catch (final IOException ex) {
                    Debug.Warning(ex);
                    ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(null);
                    continue;
                }
                break;
            }
        }
        
        @Override
        public void start() {
            LoaderExecutor.getInstance().execute(this);
        }
    }
}
