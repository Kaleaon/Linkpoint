package com.lumiyaviewer.lumiya.res.mesh;

import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
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
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Future;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class MeshCache extends ResourceFileCache<UUID, MeshData> {
    private static final int MAX_ATTEMPTS = 2;
    private static volatile File baseDir;
    private volatile String capURL = null;
    private final Object capURLlock = new Object();

    private class MeshDownloadRequest extends ResourceRequest<UUID, MeshData> implements Runnable {
        private volatile Future<?> downloadTask;
        private final File outputFile;

        public MeshDownloadRequest(UUID uuid, ResourceManager<UUID, MeshData> resourceManager, File file) {
            super(uuid, resourceManager);
            this.outputFile = file;
        }

        public void cancelRequest() {
            Future future = this.downloadTask;
            if (future != null) {
                future.cancel(true);
            }
            super.cancelRequest();
        }

        public void execute() {
            this.downloadTask = HTTPFetchExecutor.getInstance().submit(this);
        }

        public void run() {
            String str;
            synchronized (MeshCache.this.capURLlock) {
                str = null;
                while (str == null) {
                    str = MeshCache.this.capURL;
                    if (str == null) {
                        try {
                            MeshCache.this.capURLlock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            if (str == null) {
                completeRequest(null);
                return;
            }
            File file = new File(this.outputFile.getAbsolutePath() + ".tmp");
            Debug.Printf("Fetching mesh: %s", str + "/?mesh_id=" + ((UUID) getParams()).toString());
            int i = 0;
            while (i < 2) {
                Response execute;
                try {
                    execute = SLHTTPSConnection.getOkHttpClient().newCall(new Builder().url(r4).header(HttpHeaders.ACCEPT, "application/octet-stream").build()).execute();
                    if (execute == null) {
                        throw new IOException("Null response");
                    } else if (execute.isSuccessful()) {
                        File parentFile = file.getParentFile();
                        if (parentFile != null) {
                            parentFile.mkdirs();
                        }
                        OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                        long copy = ByteStreams.copy(execute.body().byteStream(), bufferedOutputStream);
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                        parentFile = this.outputFile.getParentFile();
                        if (parentFile != null) {
                            parentFile.mkdirs();
                        }
                        file.renameTo(this.outputFile);
                        Debug.Printf("MeshFetch: Saved %d bytes to %s", Long.valueOf(copy), this.outputFile.toString());
                        completeRequest(new MeshData(this.outputFile));
                        execute.close();
                        i++;
                    } else {
                        throw new IOException("Error response code " + execute.code());
                    }
                } catch (Throwable e2) {
                    Debug.Warning(e2);
                } catch (Throwable th) {
                    execute.close();
                }
            }
            if (!this.downloadTask.isCancelled()) {
                completeRequest(null);
            }
        }
    }

    private static File getBaseDir() {
        if (baseDir == null) {
            baseDir = GlobalOptions.getInstance().getCacheDir("mesh");
        }
        return baseDir;
    }

    public static void onCacheDirChanged() {
        baseDir = null;
    }

    protected MeshData createResourceFromFile(UUID uuid, File file) {
        try {
            return new MeshData(file);
        } catch (IOException e) {
            return null;
        }
    }

    protected ResourceRequest<UUID, MeshData> createResourceGenRequest(UUID uuid, ResourceManager<UUID, MeshData> resourceManager, File file) {
        return new MeshDownloadRequest(uuid, resourceManager, file);
    }

    protected File getResourceFile(UUID uuid) {
        int hashCode = uuid.hashCode();
        hashCode = ((hashCode >> 24) ^ (((hashCode >> 8) ^ hashCode) ^ (hashCode >> 16))) & 255;
        return new File(getBaseDir(), String.format("%02x/%s.mesh", new Object[]{Integer.valueOf(hashCode), uuid.toString()}));
    }

    public void setCapURL(String str) {
        synchronized (this.capURLlock) {
            this.capURL = str;
            this.capURLlock.notifyAll();
        }
    }
}
