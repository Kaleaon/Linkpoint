package com.linkpoint.res.anim

import android.content.res.AssetManager
import com.google.common.collect.ImmutableSet
import com.linkpoint.Debug
import com.linkpoint.LinkpointApp
import com.linkpoint.react.Subscription
import com.linkpoint.render.avatar.AnimationData
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.LoaderExecutor
import com.linkpoint.slproto.users.manager.assets.AssetData
import com.linkpoint.slproto.users.manager.assets.AssetKey
import com.linkpoint.slproto.users.manager.assets.AssetResponseCacher
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.Arrays
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

class AnimationCache : ResourceMemoryCache()<UUID, AnimationData> {
    private val ImmutableSet<String> assetAnimations
    /* access modifiers changed from: private */
    val AtomicReference<AssetResponseCacher> assetResponseCacher

    @JvmStatic
private class AssetLoadRequest : ResourceRequest()<UUID, AnimationData> : Runnable {
        private val String assetName

        AssetLoadRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager, String str) {
            super(uuid, resourceManager)
            this.assetName = str
        }

        fun cancelRequest() {
            LoaderExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute() {
            LoaderExecutor.getInstance().execute(this)
        }

        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
            r2.close()
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0067, code lost:
            r0 = move-exception
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0068, code lost:
            com.lumiyaviewer.lumiya.Debug.Warning(r0)
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
            r3.close()
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0074, code lost:
            r1 = move-exception
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0075, code lost:
            com.lumiyaviewer.lumiya.Debug.Warning(r1)
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0079, code lost:
            r0 = th
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x0082, code lost:
            r0 = e
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x0083, code lost:
            r2 = r3
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0063 A[SYNTHETIC, Splitter:B:23:0x0063] */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0070 A[SYNTHETIC, Splitter:B:30:0x0070] */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0079 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:5:0x0023] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        fun run() {
            /*
                r6 = this
                r2 = 0
                android.content.res.AssetManager r0 = com.lumiyaviewer.lumiya.LinkpointApp.getAssetManager()
                if (r0 == 0) goto L_0x0087
                java.lang.StringBuilder r1 = java.lang.StringBuilder     // Catch:{ IOException -> 0x005c, all -> 0x006c }
                r1.<init>()     // Catch:{ IOException -> 0x005c, all -> 0x006c }
                java.lang.String r3 = "anims/"
                java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ IOException -> 0x005c, all -> 0x006c }
                java.lang.String r3 = r6.assetName     // Catch:{ IOException -> 0x005c, all -> 0x006c }
                java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ IOException -> 0x005c, all -> 0x006c }
                java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x005c, all -> 0x006c }
                java.io.InputStream r3 = r0.open(r1)     // Catch:{ IOException -> 0x005c, all -> 0x006c }
                if (r3 == 0) goto L_0x0085
                com.lumiyaviewer.lumiya.render.avatar.AnimationData r1 = com.lumiyaviewer.lumiya.render.avatar.AnimationData     // Catch:{ IOException -> 0x007e, all -> 0x0079 }
                java.lang.Object r0 = r6.getParams()     // Catch:{ IOException -> 0x007e, all -> 0x0079 }
                java.util.UUID r0 = (java.util.UUID) r0     // Catch:{ IOException -> 0x007e, all -> 0x0079 }
                r1.<init>(r0, r3)     // Catch:{ IOException -> 0x007e, all -> 0x0079 }
                val r0: Int = r1.getPriority()     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
                r2 = 6
                if (r0 < r2) goto L_0x004e
                java.lang.String r0 = "Animation: priority %d loaded from asset %s"
                r2 = 2
                java.lang.Array<Any> r2 = java.lang.Object[r2]     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
                val r4: Int = r1.getPriority()     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
                r5 = 0
                r2[r5] = r4     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
                java.lang.String r4 = r6.assetName     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
                r5 = 1
                r2[r5] = r4     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r2)     // Catch:{ IOException -> 0x0082, all -> 0x0079 }
            L_0x004e:
                if (r3 == 0) goto L_0x0053
                r3.close()     // Catch:{ IOException -> 0x0057 }
            L_0x0053:
                r6.completeRequest(r1)
                return
            L_0x0057:
                r0 = move-exception
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                goto L_0x0053
            L_0x005c:
                r0 = move-exception
                r1 = r2
            L_0x005e:
                com.lumiyaviewer.lumiya.Debug.Warning(r0)     // Catch:{ all -> 0x007b }
                if (r2 == 0) goto L_0x0053
                r2.close()     // Catch:{ IOException -> 0x0067 }
                goto L_0x0053
            L_0x0067:
                r0 = move-exception
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                goto L_0x0053
            L_0x006c:
                r0 = move-exception
                r3 = r2
            L_0x006e:
                if (r3 == 0) goto L_0x0073
                r3.close()     // Catch:{ IOException -> 0x0074 }
            L_0x0073:
                throw r0
            L_0x0074:
                r1 = move-exception
                com.lumiyaviewer.lumiya.Debug.Warning(r1)
                goto L_0x0073
            L_0x0079:
                r0 = move-exception
                goto L_0x006e
            L_0x007b:
                r0 = move-exception
                r3 = r2
                goto L_0x006e
            L_0x007e:
                r0 = move-exception
                r1 = r2
                r2 = r3
                goto L_0x005e
            L_0x0082:
                r0 = move-exception
                r2 = r3
                goto L_0x005e
            L_0x0085:
                r1 = r2
                goto L_0x004e
            L_0x0087:
                r1 = r2
                goto L_0x0053
            */
            throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.anim.AnimationCache.AssetLoadRequest.run():Unit")
        }
    }

    private class DownloadRequest : ResourceRequest()<UUID, AnimationData> : Subscription.OnData<AssetData>, Subscription.OnError {
        private Subscription<AssetKey, AssetData> assetSubscription

        DownloadRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager) {
            super(uuid, resourceManager)
        }

        fun cancelRequest() {
            val subscription: Subscription<AssetKey, AssetData> = this.assetSubscription
            if (subscription != null) {
                subscription.unsubscribe()
            }
            super.cancelRequest()
        }

        fun completeRequest(animationData: AnimationData) {
            val subscription: Subscription<AssetKey, AssetData> = this.assetSubscription
            if (subscription != null) {
                subscription.unsubscribe()
            }
            super.completeRequest(animationData)
        }

        fun execute() {
            val assetResponseCacher: AssetResponseCacher = (AssetResponseCacher) AnimationCache.this.assetResponseCacher.get()
            if (assetResponseCacher != null) {
                this.assetSubscription = assetResponseCacher.getPool().subscribe(AssetKey.createAssetKey((UUID) null, (UUID) null, (UUID) getParams(), 20), LoaderExecutor.getInstance(), this, this)
            } else {
                completeRequest((AnimationData) null)
            }
        }

        fun onData(assetData: AssetData) {
            AnimationData animationData
            if (assetData == null || assetData.getData() == null || assetData.getStatus() != 1) {
                completeRequest((AnimationData) null)
                return
            }
            val byteArrayInputStream: ByteArrayInputStream = ByteArrayInputStream(assetData.getData())
            try {
                animationData = AnimationData((UUID) getParams(), byteArrayInputStream)
                try {
                    byteArrayInputStream.close()
                } catch (IOException e) {
                    e = e
                    Debug.Warning(e)
                    completeRequest(animationData)
                }
            } catch (IOException e2) {
                e = e2
                animationData = null
                Debug.Warning(e)
                completeRequest(animationData)
            }
            completeRequest(animationData)
        }

        fun onError(th: Throwable) {
            completeRequest((AnimationData) null)
        }
    }

    @JvmStatic
private class InstanceHolder {
        /* access modifiers changed from: private */
        const val AnimationCache Instance = AnimationCache((AnimationCache) null)

        private InstanceHolder() {
        }
    }

    private AnimationCache() {
        this.assetResponseCacher = AtomicReference<>((Object) null)
        ImmutableSet.Builder builder = ImmutableSet.builder()
        val assetManager: AssetManager = LinkpointApp.getAssetManager()
        if (assetManager != null) {
            try {
                val list: Array<String> = assetManager.list("anims")
                if (list != null) {
                    builder.addAll((Iterable) Arrays.asList(list))
                }
            } catch (IOException e) {
                Debug.Warning(e)
            }
        }
        this.assetAnimations = builder.build()
    }

    /* synthetic */ AnimationCache(AnimationCache animationCache) {
        this()
    }

    @JvmStatic
     fun getInstance(): AnimationCache {
        return InstanceHolder.Instance
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<UUID, AnimationData> CreateNewRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager) {
        val uuid2: String = uuid.toString()
        return this.assetAnimations.contains(uuid2) ? AssetLoadRequest(uuid, resourceManager, uuid2) : DownloadRequest(uuid, resourceManager)
    }

    fun setAssetResponseCacher(assetResponseCacher2: AssetResponseCacher) {
        this.assetResponseCacher.set(assetResponseCacher2)
    }
}
