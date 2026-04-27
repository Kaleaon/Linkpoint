package com.lumiyaviewer.lumiya.res.textures;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.res.executors.Startable;
import com.lumiyaviewer.lumiya.res.executors.StartingExecutor;
import com.lumiyaviewer.lumiya.res.executors.WeakExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextureCache extends ResourceMemoryCache<DrawableTextureParams, OpenJPEG> {
    private File baseDir;
    private final Object cacheDirLock = new Object();
    /* access modifiers changed from: private */
    public final ExecutorService decompressorExecutor = new WeakExecutor("TextureDecompressor", 1, new PriorityBlockingQueue());
    private final AtomicBoolean isLowMemory = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public final StartingExecutor memoryAwareExecutor = new StartingExecutor();
    /* access modifiers changed from: private */
    public final TextureCompressedCache textureCompressedCache = new TextureCompressedCache();
    private File textureTempDir;

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final TextureCache Instance = new TextureCache();

        private InstanceHolder() {
        }
    }

    private class TextureDecompressRequest extends ResourceRequest<DrawableTextureParams, OpenJPEG> implements ResourceConsumer, Runnable, HasPriority, Startable {

        /* renamed from: -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues  reason: not valid java name */
        private static final /* synthetic */ int[] f52comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$render$tex$TextureClass;
        private volatile File compressedFile;
        private volatile Future<?> decompressorFuture;
        private volatile boolean lowQualityDone = false;

        /* renamed from: -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues  reason: not valid java name */
        private static /* synthetic */ int[] m116getcomlumiyaviewerlumiyarendertexTextureClassSwitchesValues() {
            if (f52comlumiyaviewerlumiyarendertexTextureClassSwitchesValues != null) {
                return f52comlumiyaviewerlumiyarendertexTextureClassSwitchesValues;
            }
            int[] iArr = new int[TextureClass.values().length];
            try {
                iArr[TextureClass.Asset.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[TextureClass.Baked.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[TextureClass.Prim.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[TextureClass.Sculpt.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[TextureClass.Terrain.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            f52comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = iArr;
            return iArr;
        }

        public TextureDecompressRequest(DrawableTextureParams drawableTextureParams, ResourceManager<DrawableTextureParams, OpenJPEG> resourceManager) {
            super(drawableTextureParams, resourceManager);
        }

        private OpenJPEG decompress(File file, File file2, boolean z) {
            try {
                DrawableTextureParams drawableTextureParams = (DrawableTextureParams) getParams();
                if (file2.exists()) {
                    return new OpenJPEG(file2, drawableTextureParams.textureClass(), OpenJPEG.ImageFormat.Raw, z);
                }
                Object[] objArr = new Object[4];
                objArr[0] = z ? "high" : "low";
                objArr[1] = drawableTextureParams.textureClass().toString();
                objArr[2] = drawableTextureParams.uuid().toString();
                objArr[3] = file2.getAbsolutePath();
                Debug.Printf("Decompressing (%s) %s texture %s to %s", objArr);
                OpenJPEG openJPEG = new OpenJPEG(file, drawableTextureParams.textureClass(), OpenJPEG.ImageFormat.JPEG2000, z);
                file2.getParentFile().mkdirs();
                File file3 = new File(file2.getAbsolutePath() + ".tmpdec");
                if (drawableTextureParams.textureClass() == TextureClass.Prim && GlobalOptions.getInstance().getCompressedTextures()) {
                    openJPEG.CompressETC1();
                }
                openJPEG.SaveRaw(file3);
                file3.renameTo(file2);
                Debug.Log("Decompressed texture " + file2.getAbsolutePath());
                return openJPEG;
            } catch (IOException e) {
                Debug.Log("Failed to decompress texture " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }
        }

        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof File) {
                this.compressedFile = (File) obj;
                if (((DrawableTextureParams) getParams()).textureClass() == TextureClass.Prim) {
                    TextureCache.this.memoryAwareExecutor.queueRequest(this);
                } else {
                    this.decompressorFuture = TextureCache.this.decompressorExecutor.submit(this);
                }
            } else if (obj == null) {
                completeRequest(null);
            }
        }

        public void cancelRequest() {
            Debug.Printf("DecompressRequest: cancelled (%s)", ((DrawableTextureParams) getParams()).uuid().toString());
            Future<?> future = this.decompressorFuture;
            if (future != null) {
                future.cancel(false);
            }
            TextureCache.this.textureCompressedCache.CancelRequest(this);
            TextureCache.this.memoryAwareExecutor.cancelRequest(this);
            super.cancelRequest();
        }

        public void execute() {
            TextureCache.this.textureCompressedCache.RequestResource((DrawableTextureParams) getParams(), (ResourceConsumer) this);
        }

        public int getPriority() {
            DrawableTextureParams drawableTextureParams = (DrawableTextureParams) getParams();
            if (TextureCache.this.canBeLowQuality(drawableTextureParams) && this.lowQualityDone) {
                return 4;
            }
            switch (m116getcomlumiyaviewerlumiyarendertexTextureClassSwitchesValues()[drawableTextureParams.textureClass().ordinal()]) {
                case 1:
                    return 2;
                case 2:
                    return 1;
                default:
                    return 3;
            }
        }

        public void run() {
            DrawableTextureParams drawableTextureParams = (DrawableTextureParams) getParams();
            if (drawableTextureParams.textureClass() == TextureClass.Sculpt || drawableTextureParams.textureClass() == TextureClass.Baked) {
                completeRequest(decompress(this.compressedFile, TextureCache.this.getResourceFile(drawableTextureParams, true), true));
            } else if (!TextureCache.this.canBeLowQuality(drawableTextureParams) || !(!this.lowQualityDone)) {
                completeRequest(decompress(this.compressedFile, TextureCache.this.getResourceFile(drawableTextureParams, true), true));
            } else {
                OpenJPEG decompress = decompress(this.compressedFile, TextureCache.this.getResourceFile(drawableTextureParams, false), false);
                if (decompress == null) {
                    completeRequest(null);
                } else if (GlobalOptions.getInstance().getHighQualityTextures()) {
                    this.lowQualityDone = true;
                    intermediateResult(decompress);
                    this.decompressorFuture = TextureCache.this.decompressorExecutor.submit(this);
                } else {
                    completeRequest(decompress);
                }
            }
            TextureCache.this.memoryAwareExecutor.completeRequest(this);
        }

        public void start() {
            this.decompressorFuture = TextureCache.this.decompressorExecutor.submit(this);
        }
    }

    private class TextureLoadRequest extends ResourceRequest<DrawableTextureParams, OpenJPEG> implements Runnable, Startable {
        private final File rawFile;

        public TextureLoadRequest(DrawableTextureParams drawableTextureParams, ResourceManager<DrawableTextureParams, OpenJPEG> resourceManager, File file) {
            super(drawableTextureParams, resourceManager);
            this.rawFile = file;
        }

        public void cancelRequest() {
            TextureCache.this.memoryAwareExecutor.cancelRequest(this);
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute() {
            if (((DrawableTextureParams) getParams()).textureClass() == TextureClass.Prim) {
                TextureCache.this.memoryAwareExecutor.queueRequest(this);
            } else {
                LoaderExecutor.getInstance().execute(this);
            }
        }

        public void run() {
            try {
                completeRequest(new OpenJPEG(this.rawFile.getAbsoluteFile(), ((DrawableTextureParams) getParams()).textureClass(), OpenJPEG.ImageFormat.Raw, true));
            } catch (IOException e) {
                Debug.Warning(e);
                completeRequest(null);
            }
            TextureCache.this.memoryAwareExecutor.completeRequest(this);
        }

        public void start() {
            LoaderExecutor.getInstance().execute(this);
        }
    }

    /* access modifiers changed from: private */
    public boolean canBeLowQuality(DrawableTextureParams drawableTextureParams) {
        return drawableTextureParams.textureClass() == TextureClass.Prim;
    }

    public static TextureCache getInstance() {
        return InstanceHolder.Instance;
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<DrawableTextureParams, OpenJPEG> CreateNewRequest(DrawableTextureParams drawableTextureParams, ResourceManager<DrawableTextureParams, OpenJPEG> resourceManager) {
        if (GlobalOptions.getInstance().getHighQualityTextures() || (!canBeLowQuality(drawableTextureParams))) {
            File resourceFile = getResourceFile(drawableTextureParams, true);
            if (resourceFile.exists()) {
                return new TextureLoadRequest(drawableTextureParams, resourceManager, resourceFile);
            }
        }
        if (canBeLowQuality(drawableTextureParams) && (!GlobalOptions.getInstance().getHighQualityTextures())) {
            File resourceFile2 = getResourceFile(drawableTextureParams, false);
            if (resourceFile2.exists()) {
                return new TextureLoadRequest(drawableTextureParams, resourceManager, resourceFile2);
            }
        }
        return new TextureDecompressRequest(drawableTextureParams, resourceManager);
    }

    /* access modifiers changed from: protected */
    public File getBaseDir() {
        File file;
        synchronized (this.cacheDirLock) {
            if (this.baseDir == null) {
                this.baseDir = GlobalOptions.getInstance().getCacheDir("tex2");
            }
            file = this.baseDir;
        }
        return file;
    }

    public File getBitmapsBaseDir() {
        return GlobalOptions.getInstance().getCacheDir("bitmaps");
    }

    public ExecutorService getDecompressorExecutor() {
        return this.decompressorExecutor;
    }

    public synchronized File getResourceFile(DrawableTextureParams drawableTextureParams, boolean z) {
        return drawableTextureParams.getTextureRawPath(getBaseDir(), z);
    }

    public TextureCompressedCache getTextureCompressedCache() {
        return this.textureCompressedCache;
    }

    public File getTextureCompressedFile(DrawableTextureParams drawableTextureParams) {
        UUID uuid = drawableTextureParams.uuid();
        int hashCode = uuid.hashCode();
        return new File(getTextureTempDir(), String.format("%02x/%s.jp2", new Object[]{Integer.valueOf(((hashCode >> 24) ^ (((hashCode >> 8) ^ hashCode) ^ (hashCode >> 16))) & 255), uuid.toString()}));
    }

    public File getTextureCompressedFileOld(DrawableTextureParams drawableTextureParams) {
        return new File(getTextureTempDir(), drawableTextureParams.uuid().toString() + ".jp2");
    }

    /* access modifiers changed from: protected */
    public File getTextureTempDir() {
        File file;
        synchronized (this.cacheDirLock) {
            if (this.textureTempDir == null) {
                this.textureTempDir = GlobalOptions.getInstance().getCacheDir("textures");
            }
            file = this.textureTempDir;
        }
        return file;
    }

    public void onCacheDirChanged() {
        synchronized (this.cacheDirLock) {
            this.baseDir = null;
            this.textureTempDir = null;
        }
    }

    public void setFetcher(SLTextureFetcher sLTextureFetcher) {
        this.textureCompressedCache.setFetcher(sLTextureFetcher);
    }

    public void setMaxTextureDownloads(int i) {
        this.textureCompressedCache.setMaxTextureDownloads(i);
    }

    public void setTextureMemoryState(boolean z) {
        if (this.isLowMemory.getAndSet(z) == z) {
            return;
        }
        if (z) {
            this.memoryAwareExecutor.pause();
        } else {
            this.memoryAwareExecutor.unpause();
        }
    }
}
