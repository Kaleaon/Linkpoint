package com.lumiyaviewer.lumiya.res.textures;

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
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetchRequest;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetchRequest.TextureFetchCompleteListener;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.io.File;
import java.util.concurrent.Future;

public class TextureCompressedCache extends ResourceManager<DrawableTextureParams, File> {
    private final StartingExecutor downloadExecutor = new StartingExecutor(GlobalOptions.getInstance().getMaxTextureDownloads());
    private volatile SLTextureFetcher fetcher = null;
    private final Object lock = new Object();

    private class TextureFetchRequest extends ResourceRequest<DrawableTextureParams, File> implements Startable, TextureFetchCompleteListener, Runnable, HasPriority {
        /* renamed from: -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues */
        private static final /* synthetic */ int[] f509-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = null;
        private static final int MAX_RETRIES = 2;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$render$tex$TextureClass;
        private final File compressedFile;
        private volatile SLTextureFetchRequest fetchRequest;
        private volatile Future<?> fetchTask;
        private final SLTextureFetcher fetcher;

        /* renamed from: -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues */
        private static /* synthetic */ int[] m80-getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues() {
            if (f509-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues != null) {
                return f509-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues;
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
            f509-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = iArr;
            return iArr;
        }

        public TextureFetchRequest(DrawableTextureParams drawableTextureParams, ResourceManager<DrawableTextureParams, File> resourceManager, File file, SLTextureFetcher sLTextureFetcher) {
            super(drawableTextureParams, resourceManager);
            this.compressedFile = file;
            this.fetcher = sLTextureFetcher;
        }

        public void OnTextureFetchComplete(SLTextureFetchRequest sLTextureFetchRequest) {
            completeRequest(sLTextureFetchRequest.outputFile);
        }

        public void cancelRequest() {
            SLTextureFetchRequest sLTextureFetchRequest;
            SLTextureFetcher sLTextureFetcher;
            Future future;
            Debug.Printf("TextureFetchRequest: cancelled (%s)", ((DrawableTextureParams) getParams()).uuid().toString());
            synchronized (this) {
                sLTextureFetchRequest = this.fetchRequest;
                sLTextureFetcher = this.fetcher;
                future = this.fetchTask;
            }
            if (!(sLTextureFetcher == null || sLTextureFetchRequest == null)) {
                sLTextureFetcher.CancelFetch(sLTextureFetchRequest);
            }
            if (future != null) {
                future.cancel(true);
            }
            TextureCompressedCache.this.downloadExecutor.cancelRequest(this);
            super.cancelRequest();
        }

        public void completeRequest(File file) {
            TextureCompressedCache.this.downloadExecutor.completeRequest(this);
            super.completeRequest(file);
        }

        public void execute() {
            this.fetchTask = HTTPFetchExecutor.getInstance().submit(this);
        }

        public int getPriority() {
            switch (m80-getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues()[((DrawableTextureParams) getParams()).textureClass().ordinal()]) {
                case 1:
                    return 1;
                case 2:
                    return 0;
                default:
                    return 2;
            }
        }

        /* DevToolsApp WARNING: Removed duplicated region for block: B:64:0x01d2 A:{LOOP_END, LOOP:0: B:11:0x00bc->B:64:0x01d2} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:74:0x00fc A:{SYNTHETIC} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:74:0x00fc A:{SYNTHETIC} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:64:0x01d2 A:{LOOP_END, LOOP:0: B:11:0x00bc->B:64:0x01d2} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:64:0x01d2 A:{LOOP_END, LOOP:0: B:11:0x00bc->B:64:0x01d2} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:74:0x00fc A:{SYNTHETIC} */
        public void run() {
            // Check if fetcher is available
            if (this.fetcher == null) {
                completeRequest(null);
                return;
            }
            
            try {
                // Get texture parameters
                DrawableTextureParams params = (DrawableTextureParams) getParams();
                String capURL = this.fetcher.getCapURL();
                String agentAppearanceService = this.fetcher.getAgentAppearanceService();
                java.util.UUID avatarUUID = params.avatarUUID();
                com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex faceIndex = params.avatarFaceIndex();
                
                // Build texture URL
                java.net.URL textureURL;
                if (agentAppearanceService != null && avatarUUID != null && faceIndex != null) {
                    // Avatar texture URL with baked texture support
                    String avatarTextureURL = agentAppearanceService;
                    if (!avatarTextureURL.endsWith("/")) {
                        avatarTextureURL += "/";
                    }
                    avatarTextureURL += "texture/" + avatarUUID.toString() + "/" + 
                                      faceIndex.getBakedTextureName() + "/" + params.uuid().toString();
                    textureURL = new java.net.URL(avatarTextureURL);
                } else {
                    // Regular texture URL
                    String regularTextureURL = capURL + "/?texture_id=" + params.uuid().toString();
                    textureURL = new java.net.URL(regularTextureURL);
                }
                
                Debug.Log("TextureFetchRequest: Fetching texture " + params.uuid().toString() + ", url = " + textureURL);
                
                // Create temporary file for download
                File tempFile = new File(this.compressedFile.getAbsolutePath() + ".part");
                File parentDir = tempFile.getParentFile();
                boolean createResult = parentDir.mkdirs();
                
                Debug.Printf("TextureFetchRequest: tempOutputDir = %s, createResult = %b, exists = %b",
                    parentDir, createResult, parentDir.exists());
                
                // Attempt HTTP download with retry logic
                boolean downloadSuccess = false;
                for (int attempt = 0; attempt < 2 && !downloadSuccess; attempt++) {
                    okhttp3.Response response = null;
                    try {
                        Debug.Printf("TextureFetchRequest: getting connection", new Object[0]);
                        
                        // Build HTTP request
                        okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(textureURL)
                            .header("Accept", "image/x-j2c")
                            .build();
                        
                        // Execute request
                        okhttp3.OkHttpClient client = com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection.getOkHttpClient();
                        response = client.newCall(request).execute();
                        
                        if (response == null) {
                            throw new java.io.IOException("Null response");
                        }
                        
                        // Check if response is successful
                        if (!response.isSuccessful()) {
                            throw new java.io.IOException("Response code " + response.code());
                        }
                        
                        // Download response body to file using Google Common utilities
                        java.io.InputStream inputStream = response.body().byteStream();
                        java.io.BufferedOutputStream outputStream = new java.io.BufferedOutputStream(
                            new java.io.FileOutputStream(tempFile));
                        
                        try {
                            com.google.common.io.ByteStreams.copy(inputStream, outputStream);
                            downloadSuccess = true;
                        } finally {
                            outputStream.close();
                        }
                        
                    } catch (java.io.IOException e) {
                        Debug.Warning(e);
                        if (attempt == 1) {
                            // Last attempt failed, will try UDP fallback
                            break;
                        }
                    } finally {
                        if (response != null) {
                            response.close();
                        }
                    }
                }
                
                // If HTTP download succeeded, move temp file to final location
                if (downloadSuccess && tempFile.exists()) {
                    synchronized (TextureCompressedCache.this.lock) {
                        tempFile.renameTo(this.compressedFile);
                    }
                    completeRequest(this.compressedFile);
                    return;
                }
                
            } catch (java.net.MalformedURLException e) {
                Debug.Warning(e);
                completeRequest(null);
                return;
            }
            
            // If HTTP fetch failed, try UDP fallback
            if (this.fetchTask == null || !this.fetchTask.isCancelled()) {
                Debug.Log("TextureFetchRequest: HTTP fetch unsuccessful. Trying UDP.");
                TextureCompressedCache.this.downloadExecutor.queueRequest(this);
            }
        }

        public void start() {
            SLTextureFetcher sLTextureFetcher = this.fetcher;
            if (sLTextureFetcher == null) {
                completeRequest(null);
                return;
            }
            SLTextureFetchRequest sLTextureFetchRequest;
            DrawableTextureParams drawableTextureParams = (DrawableTextureParams) getParams();
            synchronized (this) {
                sLTextureFetchRequest = new SLTextureFetchRequest(drawableTextureParams.uuid(), 0, drawableTextureParams.textureClass(), drawableTextureParams.avatarFaceIndex(), drawableTextureParams.avatarUUID(), this.compressedFile);
                sLTextureFetchRequest.setOnFetchComplete(this);
                this.fetchRequest = sLTextureFetchRequest;
            }
            sLTextureFetcher.BeginFetch(sLTextureFetchRequest);
        }
    }

    protected ResourceRequest<DrawableTextureParams, File> CreateNewRequest(DrawableTextureParams drawableTextureParams, ResourceManager<DrawableTextureParams, File> resourceManager) {
        return new TextureFetchRequest(drawableTextureParams, resourceManager, TextureCache.getInstance().getTextureCompressedFile(drawableTextureParams), this.fetcher);
    }

    public void RequestResource(DrawableTextureParams drawableTextureParams, ResourceConsumer resourceConsumer) {
        Object textureCompressedFileOld = TextureCache.getInstance().getTextureCompressedFileOld(drawableTextureParams);
        File textureCompressedFile = TextureCache.getInstance().getTextureCompressedFile(drawableTextureParams);
        synchronized (this.lock) {
            if (!textureCompressedFileOld.exists()) {
                if (textureCompressedFile.exists()) {
                    File textureCompressedFileOld2 = textureCompressedFile;
                } else {
                    textureCompressedFileOld2 = null;
                }
            }
        }
        if (textureCompressedFileOld2 != null) {
            resourceConsumer.OnResourceReady(textureCompressedFileOld2, false);
        } else {
            super.RequestResource(drawableTextureParams, resourceConsumer);
        }
    }

    public void setFetcher(SLTextureFetcher sLTextureFetcher) {
        this.fetcher = sLTextureFetcher;
    }

    public void setMaxTextureDownloads(int i) {
        if (i > 0) {
            this.downloadExecutor.setMaxConcurrentTasks(i);
            HTTPFetchExecutor.getInstance().setCorePoolSize(i);
            HTTPFetchExecutor.getInstance().setMaximumPoolSize(i);
        }
    }
}
