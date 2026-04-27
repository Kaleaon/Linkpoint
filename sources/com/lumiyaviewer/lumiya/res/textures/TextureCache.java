// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

// Referenced classes of package com.lumiyaviewer.lumiya.res.textures:
//            TextureCompressedCache

public class TextureCache extends ResourceMemoryCache
{
    private static class InstanceHolder
    {

        private static final TextureCache Instance = new TextureCache();

        static TextureCache _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }

    private class TextureDecompressRequest extends ResourceRequest
        implements ResourceConsumer, Runnable, HasPriority, Startable
    {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$render$tex$TextureClass[];
        private volatile File compressedFile;
        private volatile Future decompressorFuture;
        private volatile boolean lowQualityDone;
        final TextureCache this$0;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues;
            }
            int ai[] = new int[TextureClass.values().length];
            try
            {
                ai[TextureClass.Asset.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror4) { }
            try
            {
                ai[TextureClass.Baked.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[TextureClass.Prim.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[TextureClass.Sculpt.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[TextureClass.Terrain.ordinal()] = 5;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues = ai;
            return ai;
        }

        private OpenJPEG decompress(File file, File file1, boolean flag)
        {
            Object obj;
            DrawableTextureParams drawabletextureparams;
            File file2;
            try
            {
                drawabletextureparams = (DrawableTextureParams)getParams();
                if (file1.exists())
                {
                    return new OpenJPEG(file1, drawabletextureparams.textureClass(), com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.Raw, flag);
                }
            }
            // Misplaced declaration of an exception variable
            catch (File file1)
            {
                Debug.Log((new StringBuilder()).append("Failed to decompress texture ").append(file.getAbsolutePath()).toString());
                file1.printStackTrace();
                return null;
            }
            if (flag)
            {
                obj = "high";
            } else
            {
                obj = "low";
            }
            Debug.Printf("Decompressing (%s) %s texture %s to %s", new Object[] {
                obj, drawabletextureparams.textureClass().toString(), drawabletextureparams.uuid().toString(), file1.getAbsolutePath()
            });
            obj = new OpenJPEG(file, drawabletextureparams.textureClass(), com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.JPEG2000, flag);
            file1.getParentFile().mkdirs();
            file2 = new File((new StringBuilder()).append(file1.getAbsolutePath()).append(".tmpdec").toString());
            if (drawabletextureparams.textureClass() == TextureClass.Prim && GlobalOptions.getInstance().getCompressedTextures())
            {
                ((OpenJPEG) (obj)).CompressETC1();
            }
            ((OpenJPEG) (obj)).SaveRaw(file2);
            file2.renameTo(file1);
            Debug.Log((new StringBuilder()).append("Decompressed texture ").append(file1.getAbsolutePath()).toString());
            return ((OpenJPEG) (obj));
        }

        public void OnResourceReady(Object obj, boolean flag)
        {
            if (!(obj instanceof File)) goto _L2; else goto _L1
_L1:
            compressedFile = (File)obj;
            if (((DrawableTextureParams)getParams()).textureClass() != TextureClass.Prim) goto _L4; else goto _L3
_L3:
            TextureCache._2D_get1(TextureCache.this).queueRequest(this);
_L6:
            return;
_L4:
            decompressorFuture = TextureCache._2D_get0(TextureCache.this).submit(this);
            return;
_L2:
            if (obj == null)
            {
                completeRequest(null);
                return;
            }
            if (true) goto _L6; else goto _L5
_L5:
        }

        public void cancelRequest()
        {
            Debug.Printf("DecompressRequest: cancelled (%s)", new Object[] {
                ((DrawableTextureParams)getParams()).uuid().toString()
            });
            Future future = decompressorFuture;
            if (future != null)
            {
                future.cancel(false);
            }
            TextureCache._2D_get2(TextureCache.this).CancelRequest(this);
            TextureCache._2D_get1(TextureCache.this).cancelRequest(this);
            super.cancelRequest();
        }

        public void execute()
        {
            TextureCache._2D_get2(TextureCache.this).RequestResource((DrawableTextureParams)getParams(), this);
        }

        public int getPriority()
        {
            DrawableTextureParams drawabletextureparams = (DrawableTextureParams)getParams();
            if (TextureCache._2D_wrap0(TextureCache.this, drawabletextureparams) && lowQualityDone)
            {
                return 4;
            }
            switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues()[drawabletextureparams.textureClass().ordinal()])
            {
            default:
                return 3;

            case 2: // '\002'
                return 1;

            case 1: // '\001'
                return 2;
            }
        }

        public void run()
        {
            Object obj = (DrawableTextureParams)getParams();
            if (((DrawableTextureParams) (obj)).textureClass() == TextureClass.Sculpt || ((DrawableTextureParams) (obj)).textureClass() == TextureClass.Baked)
            {
                completeRequest(decompress(compressedFile, getResourceFile(((DrawableTextureParams) (obj)), true), true));
            } else
            if (TextureCache._2D_wrap0(TextureCache.this, ((DrawableTextureParams) (obj))) && lowQualityDone ^ true)
            {
                obj = decompress(compressedFile, getResourceFile(((DrawableTextureParams) (obj)), false), false);
                if (obj == null)
                {
                    completeRequest(null);
                } else
                if (GlobalOptions.getInstance().getHighQualityTextures())
                {
                    lowQualityDone = true;
                    intermediateResult(obj);
                    decompressorFuture = TextureCache._2D_get0(TextureCache.this).submit(this);
                } else
                {
                    completeRequest(obj);
                }
            } else
            {
                completeRequest(decompress(compressedFile, getResourceFile(((DrawableTextureParams) (obj)), true), true));
            }
            TextureCache._2D_get1(TextureCache.this).completeRequest(this);
        }

        public void start()
        {
            decompressorFuture = TextureCache._2D_get0(TextureCache.this).submit(this);
        }

        public TextureDecompressRequest(DrawableTextureParams drawabletextureparams, ResourceManager resourcemanager)
        {
            this$0 = TextureCache.this;
            super(drawabletextureparams, resourcemanager);
            lowQualityDone = false;
        }
    }

    private class TextureLoadRequest extends ResourceRequest
        implements Runnable, Startable
    {

        private final File rawFile;
        final TextureCache this$0;

        public void cancelRequest()
        {
            TextureCache._2D_get1(TextureCache.this).cancelRequest(this);
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute()
        {
            if (((DrawableTextureParams)getParams()).textureClass() == TextureClass.Prim)
            {
                TextureCache._2D_get1(TextureCache.this).queueRequest(this);
                return;
            } else
            {
                LoaderExecutor.getInstance().execute(this);
                return;
            }
        }

        public void run()
        {
            try
            {
                completeRequest(new OpenJPEG(rawFile.getAbsoluteFile(), ((DrawableTextureParams)getParams()).textureClass(), com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.Raw, true));
            }
            catch (IOException ioexception)
            {
                Debug.Warning(ioexception);
                completeRequest(null);
            }
            TextureCache._2D_get1(TextureCache.this).completeRequest(this);
        }

        public void start()
        {
            LoaderExecutor.getInstance().execute(this);
        }

        public TextureLoadRequest(DrawableTextureParams drawabletextureparams, ResourceManager resourcemanager, File file)
        {
            this$0 = TextureCache.this;
            super(drawabletextureparams, resourcemanager);
            rawFile = file;
        }
    }


    private File baseDir;
    private final Object cacheDirLock = new Object();
    private final ExecutorService decompressorExecutor = new WeakExecutor("TextureDecompressor", 1, new PriorityBlockingQueue());
    private final AtomicBoolean isLowMemory = new AtomicBoolean(false);
    private final StartingExecutor memoryAwareExecutor = new StartingExecutor();
    private final TextureCompressedCache textureCompressedCache = new TextureCompressedCache();
    private File textureTempDir;

    static ExecutorService _2D_get0(TextureCache texturecache)
    {
        return texturecache.decompressorExecutor;
    }

    static StartingExecutor _2D_get1(TextureCache texturecache)
    {
        return texturecache.memoryAwareExecutor;
    }

    static TextureCompressedCache _2D_get2(TextureCache texturecache)
    {
        return texturecache.textureCompressedCache;
    }

    static boolean _2D_wrap0(TextureCache texturecache, DrawableTextureParams drawabletextureparams)
    {
        return texturecache.canBeLowQuality(drawabletextureparams);
    }

    public TextureCache()
    {
    }

    private boolean canBeLowQuality(DrawableTextureParams drawabletextureparams)
    {
        return drawabletextureparams.textureClass() == TextureClass.Prim;
    }

    public static TextureCache getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    protected ResourceRequest CreateNewRequest(DrawableTextureParams drawabletextureparams, ResourceManager resourcemanager)
    {
        if (GlobalOptions.getInstance().getHighQualityTextures() || canBeLowQuality(drawabletextureparams) ^ true)
        {
            File file = getResourceFile(drawabletextureparams, true);
            if (file.exists())
            {
                return new TextureLoadRequest(drawabletextureparams, resourcemanager, file);
            }
        }
        if (canBeLowQuality(drawabletextureparams) && GlobalOptions.getInstance().getHighQualityTextures() ^ true)
        {
            File file1 = getResourceFile(drawabletextureparams, false);
            if (file1.exists())
            {
                return new TextureLoadRequest(drawabletextureparams, resourcemanager, file1);
            }
        }
        return new TextureDecompressRequest(drawabletextureparams, resourcemanager);
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((DrawableTextureParams)obj, resourcemanager);
    }

    protected File getBaseDir()
    {
        Object obj = cacheDirLock;
        obj;
        JVM INSTR monitorenter ;
        File file;
        if (baseDir == null)
        {
            baseDir = GlobalOptions.getInstance().getCacheDir("tex2");
        }
        file = baseDir;
        obj;
        JVM INSTR monitorexit ;
        return file;
        Exception exception;
        exception;
        throw exception;
    }

    public File getBitmapsBaseDir()
    {
        return GlobalOptions.getInstance().getCacheDir("bitmaps");
    }

    public ExecutorService getDecompressorExecutor()
    {
        return decompressorExecutor;
    }

    public File getResourceFile(DrawableTextureParams drawabletextureparams, boolean flag)
    {
        this;
        JVM INSTR monitorenter ;
        drawabletextureparams = drawabletextureparams.getTextureRawPath(getBaseDir(), flag);
        this;
        JVM INSTR monitorexit ;
        return drawabletextureparams;
        drawabletextureparams;
        throw drawabletextureparams;
    }

    public TextureCompressedCache getTextureCompressedCache()
    {
        return textureCompressedCache;
    }

    public File getTextureCompressedFile(DrawableTextureParams drawabletextureparams)
    {
        drawabletextureparams = drawabletextureparams.uuid();
        int i = drawabletextureparams.hashCode();
        return new File(getTextureTempDir(), String.format("%02x/%s.jp2", new Object[] {
            Integer.valueOf((i >> 24 ^ (i >> 8 ^ i ^ i >> 16)) & 0xff), drawabletextureparams.toString()
        }));
    }

    public File getTextureCompressedFileOld(DrawableTextureParams drawabletextureparams)
    {
        return new File(getTextureTempDir(), (new StringBuilder()).append(drawabletextureparams.uuid().toString()).append(".jp2").toString());
    }

    protected File getTextureTempDir()
    {
        Object obj = cacheDirLock;
        obj;
        JVM INSTR monitorenter ;
        File file;
        if (textureTempDir == null)
        {
            textureTempDir = GlobalOptions.getInstance().getCacheDir("textures");
        }
        file = textureTempDir;
        obj;
        JVM INSTR monitorexit ;
        return file;
        Exception exception;
        exception;
        throw exception;
    }

    public void onCacheDirChanged()
    {
        Object obj = cacheDirLock;
        obj;
        JVM INSTR monitorenter ;
        baseDir = null;
        textureTempDir = null;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void setFetcher(SLTextureFetcher sltexturefetcher)
    {
        textureCompressedCache.setFetcher(sltexturefetcher);
    }

    public void setMaxTextureDownloads(int i)
    {
        textureCompressedCache.setMaxTextureDownloads(i);
    }

    public void setTextureMemoryState(boolean flag)
    {
label0:
        {
            if (isLowMemory.getAndSet(flag) != flag)
            {
                if (!flag)
                {
                    break label0;
                }
                memoryAwareExecutor.pause();
            }
            return;
        }
        memoryAwareExecutor.unpause();
    }
}
