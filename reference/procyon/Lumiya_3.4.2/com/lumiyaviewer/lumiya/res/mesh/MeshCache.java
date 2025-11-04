// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.mesh;

import okhttp3.Response;
import java.io.Serializable;
import com.google.common.io.ByteStreams;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import okhttp3.Request;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.executors.HTTPFetchExecutor;
import java.util.concurrent.Future;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import java.io.IOException;
import com.lumiyaviewer.lumiya.GlobalOptions;
import java.io.File;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import java.util.UUID;
import com.lumiyaviewer.lumiya.res.ResourceFileCache;

public class MeshCache extends ResourceFileCache<UUID, MeshData>
{
    private static final int MAX_ATTEMPTS = 2;
    private static volatile File baseDir;
    private volatile String capURL;
    private final Object capURLlock;
    
    public MeshCache() {
        this.capURLlock = new Object();
        this.capURL = null;
    }
    
    private static File getBaseDir() {
        if (MeshCache.baseDir == null) {
            MeshCache.baseDir = GlobalOptions.getInstance().getCacheDir("mesh");
        }
        return MeshCache.baseDir;
    }
    
    public static void onCacheDirChanged() {
        MeshCache.baseDir = null;
    }
    
    @Override
    protected MeshData createResourceFromFile(final UUID uuid, final File file) {
        try {
            return new MeshData(file);
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    @Override
    protected ResourceRequest<UUID, MeshData> createResourceGenRequest(final UUID uuid, final ResourceManager<UUID, MeshData> resourceManager, final File file) {
        return new MeshDownloadRequest(uuid, resourceManager, file);
    }
    
    @Override
    protected File getResourceFile(final UUID uuid) {
        final int hashCode = uuid.hashCode();
        return new File(getBaseDir(), String.format("%02x/%s.mesh", (hashCode >> 24 ^ (hashCode >> 8 ^ hashCode ^ hashCode >> 16)) & 0xFF, uuid.toString()));
    }
    
    public void setCapURL(final String capURL) {
        synchronized (this.capURLlock) {
            this.capURL = capURL;
            this.capURLlock.notifyAll();
        }
    }
    
    private class MeshDownloadRequest extends ResourceRequest<UUID, MeshData> implements Runnable
    {
        private volatile Future<?> downloadTask;
        private final File outputFile;
        
        public MeshDownloadRequest(final UUID uuid, final ResourceManager<UUID, MeshData> resourceManager, final File outputFile) {
            super(uuid, resourceManager);
            this.outputFile = outputFile;
        }
        
        @Override
        public void cancelRequest() {
            final Future<?> downloadTask = this.downloadTask;
            if (downloadTask != null) {
                downloadTask.cancel(true);
            }
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            this.downloadTask = HTTPFetchExecutor.getInstance().submit(this);
        }
        
        @Override
        public void run() {
            final Object -get1 = MeshCache.this.capURLlock;
            monitorenter(-get1);
            String -get2 = null;
        Label_0048_Outer:
            while (true) {
                while (true) {
                    String s;
                    if ((s = -get2) == null) {
                        try {
                            s = (-get2 = MeshCache.this.capURL);
                            if (s != null) {
                                continue Label_0048_Outer;
                            }
                            try {
                                MeshCache.this.capURLlock.wait();
                                -get2 = s;
                                continue Label_0048_Outer;
                            }
                            catch (final InterruptedException ex) {}
                            monitorexit(-get1);
                            if (s == null) {
                                ((ResourceRequest<ResourceParams, MeshData>)this).completeRequest(null);
                                return;
                            }
                        }
                        finally {
                            monitorexit(-get1);
                        }
                        break;
                    }
                    continue;
                }
            }
            final File file = new File(this.outputFile.getAbsolutePath() + ".tmp");
            final String str;
            Serializable string = str + "/?mesh_id=" + ((ResourceRequest<UUID, ResourceType>)this).getParams().toString();
            Debug.Printf("Fetching mesh: %s", string);
            for (int i = 0; i < 2; ++i) {
                Response execute = null;
                Label_0229: {
                    try {
                        execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url((String)string).header("Accept", "application/octet-stream").build()).execute();
                        if (execute == null) {
                            throw new IOException("Null response");
                        }
                        break Label_0229;
                    }
                    catch (final IOException ex2) {
                        Debug.Warning(ex2);
                    }
                    break;
                    try {
                        if (!execute.isSuccessful()) {
                            string = new StringBuilder();
                            throw new IOException(((StringBuilder)string).append("Error response code ").append(execute.code()).toString());
                        }
                    }
                    finally {
                        execute.close();
                    }
                }
                final File file2;
                final File parentFile = file2.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
                final long copy = ByteStreams.copy(execute.body().byteStream(), bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                final File parentFile2 = this.outputFile.getParentFile();
                if (parentFile2 != null) {
                    parentFile2.mkdirs();
                }
                file2.renameTo(this.outputFile);
                Debug.Printf("MeshFetch: Saved %d bytes to %s", copy, this.outputFile.toString());
                ((ResourceRequest<ResourceParams, MeshData>)this).completeRequest(new MeshData(this.outputFile));
                execute.close();
            }
            if (!this.downloadTask.isCancelled()) {
                ((ResourceRequest<ResourceParams, MeshData>)this).completeRequest(null);
            }
        }
    }
}
