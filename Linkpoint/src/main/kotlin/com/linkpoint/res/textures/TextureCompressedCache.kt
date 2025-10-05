package com.linkpoint.res.textures

import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.render.tex.TextureClass
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.HTTPFetchExecutor
import com.linkpoint.res.executors.Startable
import com.linkpoint.res.executors.StartingExecutor
import com.linkpoint.slproto.modules.texfetcher.SLTextureFetchRequest
import com.linkpoint.slproto.modules.texfetcher.SLTextureFetcher
import com.linkpoint.utils.HasPriority
import java.io.File
import java.util.concurrent.Future

class TextureCompressedCache : ResourceManager()<DrawableTextureParams, File> {
    /* access modifiers changed from: private */
    val StartingExecutor downloadExecutor = StartingExecutor(GlobalOptions.getInstance().getMaxTextureDownloads())
    private volatile SLTextureFetcher fetcher = null
    /* access modifiers changed from: private */
    val Object lock = Object()

    private class TextureFetchRequest : ResourceRequest()<DrawableTextureParams, File> : Startable, SLTextureFetchRequest.TextureFetchCompleteListener, Runnable, HasPriority {

        /* renamed from: -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues  reason: not valid java name */
        private const val /* synthetic */ Int[] f53comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = null
        private const val MAX_RETRIES: Int = 2
        final /* synthetic */ Int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$render$tex$TextureClass
        private val File compressedFile
        private volatile SLTextureFetchRequest fetchRequest
        private volatile Future<?> fetchTask
        private val SLTextureFetcher fetcher

        /* renamed from: -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues  reason: not valid java name */
        @JvmStatic
private /* synthetic */ Int[] m119getcomlumiyaviewerlumiyarendertexTextureClassSwitchesValues() {
            if (f53comlumiyaviewerlumiyarendertexTextureClassSwitchesValues != null) {
                return f53comlumiyaviewerlumiyarendertexTextureClassSwitchesValues
            }
            Int[] iArr = Int[TextureClass.values().length]
            try {
                iArr[TextureClass.Asset.ordinal()] = 3
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[TextureClass.Baked.ordinal()] = 1
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[TextureClass.Prim.ordinal()] = 4
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[TextureClass.Sculpt.ordinal()] = 2
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[TextureClass.Terrain.ordinal()] = 5
            } catch (NoSuchFieldError e5) {
            }
            f53comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = iArr
            return iArr
        }

        public TextureFetchRequest(DrawableTextureParams drawableTextureParams, ResourceManager<DrawableTextureParams, File> resourceManager, File file, SLTextureFetcher sLTextureFetcher) {
            super(drawableTextureParams, resourceManager)
            this.compressedFile = file
            this.fetcher = sLTextureFetcher
        }

        public Unit OnTextureFetchComplete(SLTextureFetchRequest sLTextureFetchRequest) {
            completeRequest(sLTextureFetchRequest.outputFile)
        }

        public Unit cancelRequest() {
            SLTextureFetchRequest sLTextureFetchRequest
            SLTextureFetcher sLTextureFetcher
            Future<?> future
            Debug.Printf("TextureFetchRequest: cancelled (%s)", ((DrawableTextureParams) getParams()).uuid().toString())
            synchronized (this) {
                sLTextureFetchRequest = this.fetchRequest
                sLTextureFetcher = this.fetcher
                future = this.fetchTask
            }
            if (!(sLTextureFetcher == null || sLTextureFetchRequest == null)) {
                sLTextureFetcher.CancelFetch(sLTextureFetchRequest)
            }
            if (future != null) {
                future.cancel(true)
            }
            TextureCompressedCache.this.downloadExecutor.cancelRequest(this)
            super.cancelRequest()
        }

        public Unit completeRequest(File file) {
            TextureCompressedCache.this.downloadExecutor.completeRequest(this)
            super.completeRequest(file)
        }

        public Unit execute() {
            this.fetchTask = HTTPFetchExecutor.getInstance().submit(this)
        }

        public Int getPriority() {
            switch (m119getcomlumiyaviewerlumiyarendertexTextureClassSwitchesValues()[((DrawableTextureParams) getParams()).textureClass().ordinal()]) {
                case 1:
                    return 1
                case 2:
                    return 0
                default:
                    return 2
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:66:0x01d2 A[LOOP:0: B:11:0x00bc->B:66:0x01d2, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x00fc A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public Unit run() {
            /*
                r10 = this
                r9 = 2
                r4 = 1
                r3 = 0
                r7 = 0
                com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher r0 = r10.fetcher
                if (r0 != 0) goto L_0x000c
                r10.completeRequest((java.io.File) r7)
                return
            L_0x000c:
                com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher r0 = r10.fetcher
                java.lang.String r2 = r0.getCapURL()
                com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher r0 = r10.fetcher
                java.lang.String r1 = r0.getAgentAppearanceService()
                java.lang.Object r0 = r10.getParams()
                com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams r0 = (com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams) r0
                java.util.UUID r5 = r0.avatarUUID()     // Catch:{ MalformedURLException -> 0x0173 }
                com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex r6 = r0.avatarFaceIndex()     // Catch:{ MalformedURLException -> 0x0173 }
                if (r1 == 0) goto L_0x002a
                if (r5 != 0) goto L_0x010f
            L_0x002a:
                java.net.URL r1 = java.net.URL     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r5 = java.lang.StringBuilder     // Catch:{ MalformedURLException -> 0x0173 }
                r5.<init>()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r2 = r5.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r5 = "/?texture_id="
                java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ MalformedURLException -> 0x0173 }
                java.util.UUID r5 = r0.uuid()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r5 = r5.toString()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = r2.toString()     // Catch:{ MalformedURLException -> 0x0173 }
                r1.<init>(r2)     // Catch:{ MalformedURLException -> 0x0173 }
            L_0x004f:
                java.lang.StringBuilder r2 = java.lang.StringBuilder
                r2.<init>()
                java.lang.String r5 = "TextureFetchRequest: Fetching texture "
                java.lang.StringBuilder r2 = r2.append(r5)
                java.util.UUID r0 = r0.uuid()
                java.lang.String r0 = r0.toString()
                java.lang.StringBuilder r0 = r2.append(r0)
                java.lang.String r2 = ", url = "
                java.lang.StringBuilder r0 = r0.append(r2)
                java.lang.StringBuilder r0 = r0.append(r1)
                java.lang.String r0 = r0.toString()
                com.lumiyaviewer.lumiya.Debug.Log(r0)
                java.io.File r6 = java.io.File
                java.lang.StringBuilder r0 = java.lang.StringBuilder
                r0.<init>()
                java.io.File r2 = r10.compressedFile
                java.lang.String r2 = r2.getAbsolutePath()
                java.lang.StringBuilder r0 = r0.append(r2)
                java.lang.String r2 = ".part"
                java.lang.StringBuilder r0 = r0.append(r2)
                java.lang.String r0 = r0.toString()
                r6.<init>(r0)
                java.io.File r0 = r6.getParentFile()
                Boolean r2 = r0.mkdirs()
                java.lang.String r5 = "TextureFetchRequest: tempOutputDir = %s, createResult = %b, exists = %b"
                r7 = 3
                java.lang.Object[] r7 = java.lang.Object[r7]
                r7[r3] = r0
                java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
                r7[r4] = r2
                Boolean r0 = r0.exists()
                java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
                r7[r9] = r0
                com.lumiyaviewer.lumiya.Debug.Printf(r5, r7)
                r5 = r3
            L_0x00bc:
                if (r5 >= r9) goto L_0x01d7
                java.lang.String r0 = "TextureFetchRequest: getting connection"
                r2 = 0
                java.lang.Object[] r2 = java.lang.Object[r2]     // Catch:{ IOException -> 0x00f5 }
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r2)     // Catch:{ IOException -> 0x00f5 }
                okhttp3.Request$Builder r0 = okhttp3.Request$Builder     // Catch:{ IOException -> 0x00f5 }
                r0.<init>()     // Catch:{ IOException -> 0x00f5 }
                okhttp3.Request$Builder r0 = r0.url((java.net.URL) r1)     // Catch:{ IOException -> 0x00f5 }
                java.lang.String r2 = "Accept"
                java.lang.String r7 = "image/x-j2c"
                okhttp3.Request$Builder r0 = r0.header(r2, r7)     // Catch:{ IOException -> 0x00f5 }
                okhttp3.Request r0 = r0.build()     // Catch:{ IOException -> 0x00f5 }
                okhttp3.OkHttpClient r2 = com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection.getOkHttpClient()     // Catch:{ IOException -> 0x00f5 }
                okhttp3.Call r0 = r2.newCall(r0)     // Catch:{ IOException -> 0x00f5 }
                okhttp3.Response r7 = r0.execute()     // Catch:{ IOException -> 0x00f5 }
                if (r7 != 0) goto L_0x017b
                java.io.IOException r0 = java.io.IOException     // Catch:{ IOException -> 0x00f5 }
                java.lang.String r2 = "Null response"
                r0.<init>(r2)     // Catch:{ IOException -> 0x00f5 }
                throw r0     // Catch:{ IOException -> 0x00f5 }
            L_0x00f5:
                r0 = move-exception
                r2 = r3
            L_0x00f7:
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
            L_0x00fa:
                if (r2 == 0) goto L_0x01d2
                com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache r0 = com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache.this
                java.lang.Object r1 = r0.lock
                monitor-enter(r1)
                java.io.File r0 = r10.compressedFile     // Catch:{ all -> 0x01cf }
                r6.renameTo(r0)     // Catch:{ all -> 0x01cf }
                monitor-exit(r1)
                java.io.File r0 = r10.compressedFile
                r10.completeRequest((java.io.File) r0)
                return
            L_0x010f:
                if (r6 == 0) goto L_0x002a
                java.lang.String r2 = "/"
                Boolean r2 = r1.endsWith(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                if (r2 != 0) goto L_0x012e
                java.lang.StringBuilder r2 = java.lang.StringBuilder     // Catch:{ MalformedURLException -> 0x0173 }
                r2.<init>()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r1 = r2.append(r1)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = "/"
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r1 = r1.toString()     // Catch:{ MalformedURLException -> 0x0173 }
            L_0x012e:
                java.lang.StringBuilder r2 = java.lang.StringBuilder     // Catch:{ MalformedURLException -> 0x0173 }
                r2.<init>()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r1 = r2.append(r1)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = "texture/"
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = r5.toString()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = "/"
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = r6.getBakedTextureName()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = "/"
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.util.UUID r2 = r0.uuid()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = r2.toString()     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                java.lang.String r2 = r1.toString()     // Catch:{ MalformedURLException -> 0x0173 }
                java.net.URL r1 = java.net.URL     // Catch:{ MalformedURLException -> 0x0173 }
                r1.<init>(r2)     // Catch:{ MalformedURLException -> 0x0173 }
                goto L_0x004f
            L_0x0173:
                r0 = move-exception
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                r10.completeRequest((java.io.File) r7)
                return
            L_0x017b:
                Boolean r0 = r7.isSuccessful()     // Catch:{ all -> 0x01a3 }
                if (r0 != 0) goto L_0x01ac
                java.io.IOException r0 = java.io.IOException     // Catch:{ all -> 0x01a3 }
                java.lang.StringBuilder r2 = java.lang.StringBuilder     // Catch:{ all -> 0x01a3 }
                r2.<init>()     // Catch:{ all -> 0x01a3 }
                java.lang.String r8 = "Response code "
                java.lang.StringBuilder r2 = r2.append(r8)     // Catch:{ all -> 0x01a3 }
                Int r8 = r7.code()     // Catch:{ all -> 0x01a3 }
                java.lang.String r8 = java.lang.Integer.toString(r8)     // Catch:{ all -> 0x01a3 }
                java.lang.StringBuilder r2 = r2.append(r8)     // Catch:{ all -> 0x01a3 }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01a3 }
                r0.<init>(r2)     // Catch:{ all -> 0x01a3 }
                throw r0     // Catch:{ all -> 0x01a3 }
            L_0x01a3:
                r0 = move-exception
                r2 = r3
            L_0x01a5:
                r7.close()     // Catch:{ IOException -> 0x01a9 }
                throw r0     // Catch:{ IOException -> 0x01a9 }
            L_0x01a9:
                r0 = move-exception
                goto L_0x00f7
            L_0x01ac:
                okhttp3.ResponseBody r0 = r7.body()     // Catch:{ all -> 0x01a3 }
                java.io.InputStream r0 = r0.byteStream()     // Catch:{ all -> 0x01a3 }
                java.io.BufferedOutputStream r2 = java.io.BufferedOutputStream     // Catch:{ all -> 0x01a3 }
                java.io.FileOutputStream r8 = java.io.FileOutputStream     // Catch:{ all -> 0x01a3 }
                r8.<init>(r6)     // Catch:{ all -> 0x01a3 }
                r2.<init>(r8)     // Catch:{ all -> 0x01a3 }
                com.google.common.io.ByteStreams.copy((java.io.InputStream) r0, (java.io.OutputStream) r2)     // Catch:{ all -> 0x01ca }
                r2.close()     // Catch:{ all -> 0x01f3 }
                r7.close()     // Catch:{ IOException -> 0x01ef }
                r2 = r4
                goto L_0x00fa
            L_0x01ca:
                r0 = move-exception
                r2.close()     // Catch:{ all -> 0x01a3 }
                throw r0     // Catch:{ all -> 0x01a3 }
            L_0x01cf:
                r0 = move-exception
                monitor-exit(r1)
                throw r0
            L_0x01d2:
                Int r0 = r5 + 1
                r5 = r0
                goto L_0x00bc
            L_0x01d7:
                java.util.concurrent.Future<?> r0 = r10.fetchTask
                Boolean r0 = r0.isCancelled()
                if (r0 != 0) goto L_0x01ee
                java.lang.String r0 = "TextureFetchRequest: HTTP fetch unsuccessful. Trying UDP."
                com.lumiyaviewer.lumiya.Debug.Log(r0)
                com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache r0 = com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache.this
                com.lumiyaviewer.lumiya.res.executors.StartingExecutor r0 = r0.downloadExecutor
                r0.queueRequest(r10)
            L_0x01ee:
                return
            L_0x01ef:
                r0 = move-exception
                r2 = r4
                goto L_0x00f7
            L_0x01f3:
                r0 = move-exception
                r2 = r4
                goto L_0x01a5
            */
            throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache.TextureFetchRequest.run():Unit")
        }

        public Unit start() {
            SLTextureFetchRequest sLTextureFetchRequest
            SLTextureFetcher sLTextureFetcher = this.fetcher
            if (sLTextureFetcher == null) {
                completeRequest((File) null)
                return
            }
            DrawableTextureParams drawableTextureParams = (DrawableTextureParams) getParams()
            synchronized (this) {
                sLTextureFetchRequest = SLTextureFetchRequest(drawableTextureParams.uuid(), 0, drawableTextureParams.textureClass(), drawableTextureParams.avatarFaceIndex(), drawableTextureParams.avatarUUID(), this.compressedFile)
                sLTextureFetchRequest.setOnFetchComplete(this)
                this.fetchRequest = sLTextureFetchRequest
            }
            sLTextureFetcher.BeginFetch(sLTextureFetchRequest)
        }
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<DrawableTextureParams, File> CreateNewRequest(DrawableTextureParams drawableTextureParams, ResourceManager<DrawableTextureParams, File> resourceManager) {
        return TextureFetchRequest(drawableTextureParams, resourceManager, TextureCache.getInstance().getTextureCompressedFile(drawableTextureParams), this.fetcher)
    }

    public Unit RequestResource(DrawableTextureParams drawableTextureParams, ResourceConsumer resourceConsumer) {
        File textureCompressedFileOld = TextureCache.getInstance().getTextureCompressedFileOld(drawableTextureParams)
        File textureCompressedFile = TextureCache.getInstance().getTextureCompressedFile(drawableTextureParams)
        synchronized (this.lock) {
            if (!textureCompressedFileOld.exists()) {
                textureCompressedFileOld = textureCompressedFile.exists() ? textureCompressedFile : null
            }
        }
        if (textureCompressedFileOld != null) {
            resourceConsumer.OnResourceReady(textureCompressedFileOld, false)
        } else {
            super.RequestResource(drawableTextureParams, resourceConsumer)
        }
    }

    public Unit setFetcher(SLTextureFetcher sLTextureFetcher) {
        this.fetcher = sLTextureFetcher
    }

    public Unit setMaxTextureDownloads(Int i) {
        if (i > 0) {
            this.downloadExecutor.setMaxConcurrentTasks(i)
            HTTPFetchExecutor.getInstance().setCorePoolSize(i)
            HTTPFetchExecutor.getInstance().setMaximumPoolSize(i)
        }
    }
}
