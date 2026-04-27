// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.mesh;

import com.google.common.io.ByteStreams;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.res.ResourceFileCache;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.HTTPFetchExecutor;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MeshCache extends ResourceFileCache
{
    private class MeshDownloadRequest extends ResourceRequest
        implements Runnable
    {

        private volatile Future downloadTask;
        private final File outputFile;
        final MeshCache this$0;

        public void cancelRequest()
        {
            Future future = downloadTask;
            if (future != null)
            {
                future.cancel(true);
            }
            super.cancelRequest();
        }

        public void execute()
        {
            downloadTask = HTTPFetchExecutor.getInstance().submit(this);
        }

        public void run()
        {
            Object obj1 = MeshCache._2D_get1(MeshCache.this);
            obj1;
            JVM INSTR monitorenter ;
            Object obj = null;
_L2:
            String s;
            s = ((String) (obj));
            if (obj != null)
            {
                break MISSING_BLOCK_LABEL_48;
            }
            s = MeshCache._2D_get0(MeshCache.this);
            obj = s;
            if (s == null)
            {
label0:
                {
                    try
                    {
                        MeshCache._2D_get1(MeshCache.this).wait();
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        if (s == null)
                        {
                            completeRequest(null);
                            return;
                        }
                        break label0;
                    }
                    finally
                    {
                        throw exception;
                    }
                    obj = s;
                }
            }
            if (true) goto _L2; else goto _L1
_L1:
            int i;
            exception = new File((new StringBuilder()).append(outputFile.getAbsolutePath()).append(".tmp").toString());
            s = (new StringBuilder()).append(s).append("/?mesh_id=").append(((UUID)getParams()).toString()).toString();
            Debug.Printf("Fetching mesh: %s", new Object[] {
                s
            });
            i = 0;
_L4:
            if (i >= 2)
            {
                break MISSING_BLOCK_LABEL_207;
            }
            obj1 = (new okhttp3.Request.Builder()).url(s).header("Accept", "application/octet-stream").build();
            obj1 = SLHTTPSConnection.getOkHttpClient().newCall(((okhttp3.Request) (obj1))).execute();
            if (obj1 != null)
            {
                break MISSING_BLOCK_LABEL_225;
            }
            try
            {
                throw new IOException("Null response");
            }
            // Misplaced declaration of an exception variable
            catch (Exception exception)
            {
                Debug.Warning(exception);
            }
            if (!downloadTask.isCancelled())
            {
                completeRequest(null);
            }
            return;
            if (!((Response) (obj1)).isSuccessful())
            {
                throw new IOException((new StringBuilder()).append("Error response code ").append(((Response) (obj1)).code()).toString());
            }
            break MISSING_BLOCK_LABEL_269;
            exception;
            ((Response) (obj1)).close();
            throw exception;
            Object obj2 = exception.getParentFile();
            if (obj2 == null)
            {
                break MISSING_BLOCK_LABEL_286;
            }
            ((File) (obj2)).mkdirs();
            long l;
            obj2 = new BufferedOutputStream(new FileOutputStream(exception));
            l = ByteStreams.copy(((Response) (obj1)).body().byteStream(), ((java.io.OutputStream) (obj2)));
            ((BufferedOutputStream) (obj2)).flush();
            ((BufferedOutputStream) (obj2)).close();
            obj2 = outputFile.getParentFile();
            if (obj2 == null)
            {
                break MISSING_BLOCK_LABEL_347;
            }
            ((File) (obj2)).mkdirs();
            exception.renameTo(outputFile);
            Debug.Printf("MeshFetch: Saved %d bytes to %s", new Object[] {
                Long.valueOf(l), outputFile.toString()
            });
            completeRequest(new MeshData(outputFile));
            ((Response) (obj1)).close();
            i++;
            if (true) goto _L4; else goto _L3
_L3:
        }

        public MeshDownloadRequest(UUID uuid, ResourceManager resourcemanager, File file)
        {
            this$0 = MeshCache.this;
            super(uuid, resourcemanager);
            outputFile = file;
        }
    }


    private static final int MAX_ATTEMPTS = 2;
    private static volatile File baseDir;
    private volatile String capURL;
    private final Object capURLlock = new Object();

    static String _2D_get0(MeshCache meshcache)
    {
        return meshcache.capURL;
    }

    static Object _2D_get1(MeshCache meshcache)
    {
        return meshcache.capURLlock;
    }

    public MeshCache()
    {
        capURL = null;
    }

    private static File getBaseDir()
    {
        if (baseDir == null)
        {
            baseDir = GlobalOptions.getInstance().getCacheDir("mesh");
        }
        return baseDir;
    }

    public static void onCacheDirChanged()
    {
        baseDir = null;
    }

    protected MeshData createResourceFromFile(UUID uuid, File file)
    {
        try
        {
            uuid = new MeshData(file);
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            return null;
        }
        return uuid;
    }

    protected volatile Object createResourceFromFile(Object obj, File file)
    {
        return createResourceFromFile((UUID)obj, file);
    }

    protected volatile ResourceRequest createResourceGenRequest(Object obj, ResourceManager resourcemanager, File file)
    {
        return createResourceGenRequest((UUID)obj, resourcemanager, file);
    }

    protected ResourceRequest createResourceGenRequest(UUID uuid, ResourceManager resourcemanager, File file)
    {
        return new MeshDownloadRequest(uuid, resourcemanager, file);
    }

    protected volatile File getResourceFile(Object obj)
    {
        return getResourceFile((UUID)obj);
    }

    protected File getResourceFile(UUID uuid)
    {
        int i = uuid.hashCode();
        return new File(getBaseDir(), String.format("%02x/%s.mesh", new Object[] {
            Integer.valueOf((i >> 24 ^ (i >> 8 ^ i ^ i >> 16)) & 0xff), uuid.toString()
        }));
    }

    public void setCapURL(String s)
    {
        Object obj = capURLlock;
        obj;
        JVM INSTR monitorenter ;
        capURL = s;
        capURLlock.notifyAll();
        obj;
        JVM INSTR monitorexit ;
        return;
        s;
        throw s;
    }
}
