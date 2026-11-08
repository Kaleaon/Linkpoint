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
import java.util.UUID;
import java.util.concurrent.Future;
import okhttp3.Request;
import okhttp3.Response;
/* loaded from: classes.dex */
public class MeshCache extends ResourceFileCache<UUID, MeshData> {
    private static final int MAX_ATTEMPTS = 2;
    private static volatile File baseDir;
    private final Object capURLlock = new Object();
    private volatile String capURL = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MeshDownloadRequest extends ResourceRequest<UUID, MeshData> implements Runnable {
        private volatile Future<?> downloadTask;
        private final File outputFile;

        public MeshDownloadRequest(UUID uuid, ResourceManager<UUID, MeshData> resourceManager, File file) {
            super(uuid, resourceManager);
            this.outputFile = file;
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            Future<?> future = this.downloadTask;
            if (future != null) {
                future.cancel(true);
            }
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            this.downloadTask = HTTPFetchExecutor.getInstance().submit(this);
        }

        @Override // java.lang.Runnable
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
            String str2 = str + "/?mesh_id=" + getParams().toString();
            Debug.Printf("Fetching mesh: %s", str2);
            for (int i = 0; i < 2; i++) {
                try {
                    Response execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url(str2).header(HttpHeaders.ACCEPT, "application/octet-stream").build()).execute();
                    if (execute == null) {
                        throw new IOException("Null response");
                    }
                    if (!execute.isSuccessful()) {
                        throw new IOException("Error response code " + execute.code());
                    }
                    File parentFile = file.getParentFile();
                    if (parentFile != null) {
                        parentFile.mkdirs();
                    }
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                    long copy = ByteStreams.copy(execute.body().byteStream(), bufferedOutputStream);
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    File parentFile2 = this.outputFile.getParentFile();
                    if (parentFile2 != null) {
                        parentFile2.mkdirs();
                    }
                    file.renameTo(this.outputFile);
                    Debug.Printf("MeshFetch: Saved %d bytes to %s", Long.valueOf(copy), this.outputFile.toString());
                    completeRequest(new MeshData(this.outputFile));
                    execute.close();
                } catch (IOException e2) {
                    Debug.Warning(e2);
                }
            }
            if (this.downloadTask.isCancelled()) {
                return;
            }
            completeRequest(null);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.res.ResourceFileCache
    public MeshData createResourceFromFile(UUID uuid, File file) {
        try {
            return new MeshData(file);
        } catch (IOException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.res.ResourceFileCache
    public ResourceRequest<UUID, MeshData> createResourceGenRequest(UUID uuid, ResourceManager<UUID, MeshData> resourceManager, File file) {
        return new MeshDownloadRequest(uuid, resourceManager, file);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.res.ResourceFileCache
    public File getResourceFile(UUID uuid) {
        int hashCode = uuid.hashCode();
        return new File(getBaseDir(), String.format("%02x/%s.mesh", Integer.valueOf(((hashCode >> 24) ^ (((hashCode >> 8) ^ hashCode) ^ (hashCode >> 16))) & 255), uuid.toString()));
    }

    public void setCapURL(String str) {
        synchronized (this.capURLlock) {
            this.capURL = str;
            this.capURLlock.notifyAll();
        }
    }
}
