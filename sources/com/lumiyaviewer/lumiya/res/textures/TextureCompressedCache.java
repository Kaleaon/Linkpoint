// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.textures;

import com.google.common.io.ByteStreams;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.HTTPFetchExecutor;
import com.lumiyaviewer.lumiya.res.executors.Startable;
import com.lumiyaviewer.lumiya.res.executors.StartingExecutor;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetchRequest;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Future;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Referenced classes of package com.lumiyaviewer.lumiya.res.textures:
//            TextureCache

public class TextureCompressedCache extends ResourceManager
{
    private class TextureFetchRequest extends ResourceRequest
        implements Startable, com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetchRequest.TextureFetchCompleteListener, Runnable, HasPriority
    {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues[];
        private static final int MAX_RETRIES = 2;
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$render$tex$TextureClass[];
        private final File compressedFile;
        private volatile SLTextureFetchRequest fetchRequest;
        private volatile Future fetchTask;
        private final SLTextureFetcher fetcher;
        final TextureCompressedCache this$0;

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

        public void OnTextureFetchComplete(SLTextureFetchRequest sltexturefetchrequest)
        {
            completeRequest(sltexturefetchrequest.outputFile);
        }

        public void cancelRequest()
        {
            Debug.Printf("TextureFetchRequest: cancelled (%s)", new Object[] {
                ((DrawableTextureParams)getParams()).uuid().toString()
            });
            this;
            JVM INSTR monitorenter ;
            SLTextureFetchRequest sltexturefetchrequest;
            SLTextureFetcher sltexturefetcher;
            Future future;
            sltexturefetchrequest = fetchRequest;
            sltexturefetcher = fetcher;
            future = fetchTask;
            this;
            JVM INSTR monitorexit ;
            if (sltexturefetcher != null && sltexturefetchrequest != null)
            {
                sltexturefetcher.CancelFetch(sltexturefetchrequest);
            }
            if (future != null)
            {
                future.cancel(true);
            }
            TextureCompressedCache._2D_get0(TextureCompressedCache.this).cancelRequest(this);
            super.cancelRequest();
            return;
            Exception exception;
            exception;
            throw exception;
        }

        public void completeRequest(File file)
        {
            TextureCompressedCache._2D_get0(TextureCompressedCache.this).completeRequest(this);
            super.completeRequest(file);
        }

        public volatile void completeRequest(Object obj)
        {
            completeRequest((File)obj);
        }

        public void execute()
        {
            fetchTask = HTTPFetchExecutor.getInstance().submit(this);
        }

        public int getPriority()
        {
            switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues()[((DrawableTextureParams)getParams()).textureClass().ordinal()])
            {
            default:
                return 2;

            case 2: // '\002'
                return 0;

            case 1: // '\001'
                return 1;
            }
        }

        public void run()
        {
            obj2 = new URL((new StringBuilder()).append(((String) (obj))).append("/?texture_id=").append(((DrawableTextureParams) (obj3)).uuid().toString()).toString());
_L7:
            int i;
            Debug.Log((new StringBuilder()).append("TextureFetchRequest: Fetching texture ").append(((DrawableTextureParams) (obj3)).uuid().toString()).append(", url = ").append(obj2).toString());
            obj3 = new File((new StringBuilder()).append(compressedFile.getAbsolutePath()).append(".part").toString());
            obj = ((File) (obj3)).getParentFile();
            Debug.Printf("TextureFetchRequest: tempOutputDir = %s, createResult = %b, exists = %b", new Object[] {
                obj, Boolean.valueOf(((File) (obj)).mkdirs()), Boolean.valueOf(((File) (obj)).exists())
            });
            i = 0;
_L11:
            if (i >= 2) goto _L2; else goto _L1
_L1:
            Debug.Printf("TextureFetchRequest: getting connection", new Object[0]);
            obj = (new okhttp3.Request.Builder()).url(((URL) (obj2))).header("Accept", "image/x-j2c").build();
            obj4 = SLHTTPSConnection.getOkHttpClient().newCall(((okhttp3.Request) (obj))).execute();
            if (obj4 != null) goto _L4; else goto _L3
_L3:
            throw new IOException("Null response");
            Object obj1;
            obj1;
            boolean flag = false;
_L9:
            Debug.Warning(((Throwable) (obj1)));
_L10:
            if (!flag) goto _L6; else goto _L5
_L5:
            obj1 = TextureCompressedCache._2D_get1(TextureCompressedCache.this);
            obj1;
            JVM INSTR monitorenter ;
            ((File) (obj3)).renameTo(compressedFile);
            obj1;
            JVM INSTR monitorexit ;
            completeRequest(compressedFile);
            return;
            if (fetcher == null)
            {
                completeRequest(((File) (null)));
                return;
            }
            obj = fetcher.getCapURL();
            Object obj2 = fetcher.getAgentAppearanceService();
            obj3 = (DrawableTextureParams)getParams();
            AvatarTextureFaceIndex avatartexturefaceindex;
            try
            {
                obj4 = ((DrawableTextureParams) (obj3)).avatarUUID();
                avatartexturefaceindex = ((DrawableTextureParams) (obj3)).avatarFaceIndex();
            }
            // Misplaced declaration of an exception variable
            catch (Object obj1)
            {
                Debug.Warning(((Throwable) (obj1)));
                completeRequest(((File) (null)));
                return;
            }
            if (obj2 == null || obj4 == null || avatartexturefaceindex == null)
            {
                break MISSING_BLOCK_LABEL_58;
            }
            obj1 = obj2;
            if (!((String) (obj2)).endsWith("/"))
            {
                obj1 = (new StringBuilder()).append(((String) (obj2))).append("/").toString();
            }
            obj2 = new URL((new StringBuilder()).append(((String) (obj1))).append("texture/").append(((UUID) (obj4)).toString()).append("/").append(avatartexturefaceindex.getBakedTextureName()).append("/").append(((DrawableTextureParams) (obj3)).uuid().toString()).toString());
              goto _L7
_L4:
            if (!((Response) (obj4)).isSuccessful())
            {
                throw new IOException((new StringBuilder()).append("Response code ").append(Integer.toString(((Response) (obj4)).code())).toString());
            }
              goto _L8
            obj1;
            flag = false;
_L12:
            try
            {
                ((Response) (obj4)).close();
                throw obj1;
            }
            // Misplaced declaration of an exception variable
            catch (Object obj1) { }
              goto _L9
_L8:
            java.io.InputStream inputstream;
            inputstream = ((Response) (obj4)).body().byteStream();
            obj1 = new BufferedOutputStream(new FileOutputStream(((File) (obj3))));
            ByteStreams.copy(inputstream, ((java.io.OutputStream) (obj1)));
            ((BufferedOutputStream) (obj1)).close();
            ((Response) (obj4)).close();
            flag = true;
              goto _L10
            Exception exception1;
            exception1;
            ((BufferedOutputStream) (obj1)).close();
            throw exception1;
            Exception exception;
            exception;
            obj1;
            JVM INSTR monitorexit ;
            throw exception;
_L6:
            i++;
              goto _L11
_L2:
            if (!fetchTask.isCancelled())
            {
                Debug.Log("TextureFetchRequest: HTTP fetch unsuccessful. Trying UDP.");
                TextureCompressedCache._2D_get0(TextureCompressedCache.this).queueRequest(this);
            }
            return;
            obj1;
            flag = true;
              goto _L9
            obj1;
            flag = true;
              goto _L12
        }

        public void start()
        {
            SLTextureFetcher sltexturefetcher;
            sltexturefetcher = fetcher;
            if (sltexturefetcher == null)
            {
                completeRequest(((File) (null)));
                return;
            }
            Object obj = (DrawableTextureParams)getParams();
            this;
            JVM INSTR monitorenter ;
            obj = new SLTextureFetchRequest(((DrawableTextureParams) (obj)).uuid(), 0, ((DrawableTextureParams) (obj)).textureClass(), ((DrawableTextureParams) (obj)).avatarFaceIndex(), ((DrawableTextureParams) (obj)).avatarUUID(), compressedFile);
            ((SLTextureFetchRequest) (obj)).setOnFetchComplete(this);
            fetchRequest = ((SLTextureFetchRequest) (obj));
            this;
            JVM INSTR monitorexit ;
            sltexturefetcher.BeginFetch(((SLTextureFetchRequest) (obj)));
            return;
            Exception exception;
            exception;
            throw exception;
        }

        public TextureFetchRequest(DrawableTextureParams drawabletextureparams, ResourceManager resourcemanager, File file, SLTextureFetcher sltexturefetcher)
        {
            this$0 = TextureCompressedCache.this;
            super(drawabletextureparams, resourcemanager);
            compressedFile = file;
            fetcher = sltexturefetcher;
        }
    }


    private final StartingExecutor downloadExecutor = new StartingExecutor(GlobalOptions.getInstance().getMaxTextureDownloads());
    private volatile SLTextureFetcher fetcher;
    private final Object lock = new Object();

    static StartingExecutor _2D_get0(TextureCompressedCache texturecompressedcache)
    {
        return texturecompressedcache.downloadExecutor;
    }

    static Object _2D_get1(TextureCompressedCache texturecompressedcache)
    {
        return texturecompressedcache.lock;
    }

    public TextureCompressedCache()
    {
        fetcher = null;
    }

    protected ResourceRequest CreateNewRequest(DrawableTextureParams drawabletextureparams, ResourceManager resourcemanager)
    {
        return new TextureFetchRequest(drawabletextureparams, resourcemanager, TextureCache.getInstance().getTextureCompressedFile(drawabletextureparams), fetcher);
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((DrawableTextureParams)obj, resourcemanager);
    }

    public void RequestResource(DrawableTextureParams drawabletextureparams, ResourceConsumer resourceconsumer)
    {
        File file;
        File file1;
        file = TextureCache.getInstance().getTextureCompressedFileOld(drawabletextureparams);
        file1 = TextureCache.getInstance().getTextureCompressedFile(drawabletextureparams);
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = file.exists();
        if (!flag) goto _L2; else goto _L1
_L1:
        obj;
        JVM INSTR monitorexit ;
        if (file != null)
        {
            resourceconsumer.OnResourceReady(file, false);
            return;
        } else
        {
            super.RequestResource(drawabletextureparams, resourceconsumer);
            return;
        }
_L2:
        flag = file1.exists();
        if (flag)
        {
            file = file1;
        } else
        {
            file = null;
        }
        if (true) goto _L1; else goto _L3
_L3:
        drawabletextureparams;
        throw drawabletextureparams;
    }

    public volatile void RequestResource(Object obj, ResourceConsumer resourceconsumer)
    {
        RequestResource((DrawableTextureParams)obj, resourceconsumer);
    }

    public void setFetcher(SLTextureFetcher sltexturefetcher)
    {
        fetcher = sltexturefetcher;
    }

    public void setMaxTextureDownloads(int i)
    {
        if (i > 0)
        {
            downloadExecutor.setMaxConcurrentTasks(i);
            HTTPFetchExecutor.getInstance().setCorePoolSize(i);
            HTTPFetchExecutor.getInstance().setMaximumPoolSize(i);
        }
    }
}
