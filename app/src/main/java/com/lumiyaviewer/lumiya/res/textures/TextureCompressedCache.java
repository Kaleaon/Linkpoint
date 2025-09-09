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
            /*
            r10 = this;
            r9 = 2;
            r4 = 1;
            r3 = 0;
            r7 = 0;
            r0 = r10.fetcher;
            if (r0 != 0) goto L_0x000c;
        L_0x0008:
            r10.completeRequest(r7);
            return;
        L_0x000c:
            r0 = r10.fetcher;
            r2 = r0.getCapURL();
            r0 = r10.fetcher;
            r1 = r0.getAgentAppearanceService();
            r0 = r10.getParams();
            r0 = (com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams) r0;
            r5 = r0.avatarUUID();	 Catch:{ MalformedURLException -> 0x0173 }
            r6 = r0.avatarFaceIndex();	 Catch:{ MalformedURLException -> 0x0173 }
            if (r1 == 0) goto L_0x002a;
        L_0x0028:
            if (r5 != 0) goto L_0x010f;
        L_0x002a:
            r1 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x0173 }
            r5 = new java.lang.StringBuilder;	 Catch:{ MalformedURLException -> 0x0173 }
            r5.<init>();	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r5.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r5 = "/?texture_id=";
            r2 = r2.append(r5);	 Catch:{ MalformedURLException -> 0x0173 }
            r5 = r0.uuid();	 Catch:{ MalformedURLException -> 0x0173 }
            r5 = r5.toString();	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r2.append(r5);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r2.toString();	 Catch:{ MalformedURLException -> 0x0173 }
            r1.<init>(r2);	 Catch:{ MalformedURLException -> 0x0173 }
        L_0x004f:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r5 = "TextureFetchRequest: Fetching texture ";
            r2 = r2.append(r5);
            r0 = r0.uuid();
            r0 = r0.toString();
            r0 = r2.append(r0);
            r2 = ", url = ";
            r0 = r0.append(r2);
            r0 = r0.append(r1);
            r0 = r0.toString();
            com.lumiyaviewer.lumiya.Debug.Log(r0);
            r6 = new java.io.File;
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r2 = r10.compressedFile;
            r2 = r2.getAbsolutePath();
            r0 = r0.append(r2);
            r2 = ".part";
            r0 = r0.append(r2);
            r0 = r0.toString();
            r6.<init>(r0);
            r0 = r6.getParentFile();
            r2 = r0.mkdirs();
            r5 = "TextureFetchRequest: tempOutputDir = %s, createResult = %b, exists = %b";
            r7 = 3;
            r7 = new java.lang.Object[r7];
            r7[r3] = r0;
            r2 = java.lang.Boolean.valueOf(r2);
            r7[r4] = r2;
            r0 = r0.exists();
            r0 = java.lang.Boolean.valueOf(r0);
            r7[r9] = r0;
            com.lumiyaviewer.lumiya.Debug.Printf(r5, r7);
            r5 = r3;
        L_0x00bc:
            if (r5 >= r9) goto L_0x01d7;
        L_0x00be:
            r0 = "TextureFetchRequest: getting connection";
            r2 = 0;
            r2 = new java.lang.Object[r2];	 Catch:{ IOException -> 0x00f5 }
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r2);	 Catch:{ IOException -> 0x00f5 }
            r0 = new okhttp3.Request$Builder;	 Catch:{ IOException -> 0x00f5 }
            r0.<init>();	 Catch:{ IOException -> 0x00f5 }
            r0 = r0.url(r1);	 Catch:{ IOException -> 0x00f5 }
            r2 = "Accept";
            r7 = "image/x-j2c";
            r0 = r0.header(r2, r7);	 Catch:{ IOException -> 0x00f5 }
            r0 = r0.build();	 Catch:{ IOException -> 0x00f5 }
            r2 = com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection.getOkHttpClient();	 Catch:{ IOException -> 0x00f5 }
            r0 = r2.newCall(r0);	 Catch:{ IOException -> 0x00f5 }
            r7 = r0.execute();	 Catch:{ IOException -> 0x00f5 }
            if (r7 != 0) goto L_0x017b;
        L_0x00ec:
            r0 = new java.io.IOException;	 Catch:{ IOException -> 0x00f5 }
            r2 = "Null response";
            r0.<init>(r2);	 Catch:{ IOException -> 0x00f5 }
            throw r0;	 Catch:{ IOException -> 0x00f5 }
        L_0x00f5:
            r0 = move-exception;
            r2 = r3;
        L_0x00f7:
            com.lumiyaviewer.lumiya.Debug.Warning(r0);
        L_0x00fa:
            if (r2 == 0) goto L_0x01d2;
        L_0x00fc:
            r0 = com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache.this;
            r1 = r0.lock;
            monitor-enter(r1);
            r0 = r10.compressedFile;	 Catch:{ all -> 0x01cf }
            r6.renameTo(r0);	 Catch:{ all -> 0x01cf }
            monitor-exit(r1);
            r0 = r10.compressedFile;
            r10.completeRequest(r0);
            return;
        L_0x010f:
            if (r6 == 0) goto L_0x002a;
        L_0x0111:
            r2 = "/";
            r2 = r1.endsWith(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            if (r2 != 0) goto L_0x012e;
        L_0x011a:
            r2 = new java.lang.StringBuilder;	 Catch:{ MalformedURLException -> 0x0173 }
            r2.<init>();	 Catch:{ MalformedURLException -> 0x0173 }
            r1 = r2.append(r1);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = "/";
            r1 = r1.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r1 = r1.toString();	 Catch:{ MalformedURLException -> 0x0173 }
        L_0x012e:
            r2 = new java.lang.StringBuilder;	 Catch:{ MalformedURLException -> 0x0173 }
            r2.<init>();	 Catch:{ MalformedURLException -> 0x0173 }
            r1 = r2.append(r1);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = "texture/";
            r1 = r1.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r5.toString();	 Catch:{ MalformedURLException -> 0x0173 }
            r1 = r1.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = "/";
            r1 = r1.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r6.getBakedTextureName();	 Catch:{ MalformedURLException -> 0x0173 }
            r1 = r1.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = "/";
            r1 = r1.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r0.uuid();	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r2.toString();	 Catch:{ MalformedURLException -> 0x0173 }
            r1 = r1.append(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            r2 = r1.toString();	 Catch:{ MalformedURLException -> 0x0173 }
            r1 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x0173 }
            r1.<init>(r2);	 Catch:{ MalformedURLException -> 0x0173 }
            goto L_0x004f;
        L_0x0173:
            r0 = move-exception;
            com.lumiyaviewer.lumiya.Debug.Warning(r0);
            r10.completeRequest(r7);
            return;
        L_0x017b:
            r0 = r7.isSuccessful();	 Catch:{ all -> 0x01a3 }
            if (r0 != 0) goto L_0x01ac;
        L_0x0181:
            r0 = new java.io.IOException;	 Catch:{ all -> 0x01a3 }
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01a3 }
            r2.<init>();	 Catch:{ all -> 0x01a3 }
            r8 = "Response code ";
            r2 = r2.append(r8);	 Catch:{ all -> 0x01a3 }
            r8 = r7.code();	 Catch:{ all -> 0x01a3 }
            r8 = java.lang.Integer.toString(r8);	 Catch:{ all -> 0x01a3 }
            r2 = r2.append(r8);	 Catch:{ all -> 0x01a3 }
            r2 = r2.toString();	 Catch:{ all -> 0x01a3 }
            r0.<init>(r2);	 Catch:{ all -> 0x01a3 }
            throw r0;	 Catch:{ all -> 0x01a3 }
        L_0x01a3:
            r0 = move-exception;
            r2 = r3;
        L_0x01a5:
            r7.close();	 Catch:{ IOException -> 0x01a9 }
            throw r0;	 Catch:{ IOException -> 0x01a9 }
        L_0x01a9:
            r0 = move-exception;
            goto L_0x00f7;
        L_0x01ac:
            r0 = r7.body();	 Catch:{ all -> 0x01a3 }
            r0 = r0.byteStream();	 Catch:{ all -> 0x01a3 }
            r2 = new java.io.BufferedOutputStream;	 Catch:{ all -> 0x01a3 }
            r8 = new java.io.FileOutputStream;	 Catch:{ all -> 0x01a3 }
            r8.<init>(r6);	 Catch:{ all -> 0x01a3 }
            r2.<init>(r8);	 Catch:{ all -> 0x01a3 }
            com.google.common.io.ByteStreams.copy(r0, r2);	 Catch:{ all -> 0x01ca }
            r2.close();	 Catch:{ all -> 0x01f3 }
            r7.close();	 Catch:{ IOException -> 0x01ef }
            r2 = r4;
            goto L_0x00fa;
        L_0x01ca:
            r0 = move-exception;
            r2.close();	 Catch:{ all -> 0x01a3 }
            throw r0;	 Catch:{ all -> 0x01a3 }
        L_0x01cf:
            r0 = move-exception;
            monitor-exit(r1);
            throw r0;
        L_0x01d2:
            r0 = r5 + 1;
            r5 = r0;
            goto L_0x00bc;
        L_0x01d7:
            r0 = r10.fetchTask;
            r0 = r0.isCancelled();
            if (r0 != 0) goto L_0x01ee;
        L_0x01df:
            r0 = "TextureFetchRequest: HTTP fetch unsuccessful. Trying UDP.";
            com.lumiyaviewer.lumiya.Debug.Log(r0);
            r0 = com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache.this;
            r0 = r0.downloadExecutor;
            r0.queueRequest(r10);
        L_0x01ee:
            return;
        L_0x01ef:
            r0 = move-exception;
            r2 = r4;
            goto L_0x00f7;
        L_0x01f3:
            r0 = move-exception;
            r2 = r4;
            goto L_0x01a5;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache.TextureFetchRequest.run():void");
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
