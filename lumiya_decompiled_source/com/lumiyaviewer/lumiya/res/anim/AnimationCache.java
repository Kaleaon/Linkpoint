package com.lumiyaviewer.lumiya.res.anim;

import android.content.res.AssetManager;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.render.avatar.AnimationData;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class AnimationCache extends ResourceMemoryCache<UUID, AnimationData> {
    private final ImmutableSet<String> assetAnimations;
    private final AtomicReference<AssetResponseCacher> assetResponseCacher;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AssetLoadRequest extends ResourceRequest<UUID, AnimationData> implements Runnable {
        private final String assetName;

        AssetLoadRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager, String str) {
            super(uuid, resourceManager);
            this.assetName = str;
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            LoaderExecutor.getInstance().execute(this);
        }

        /* JADX WARN: Removed duplicated region for block: B:45:0x0070 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                r6 = this;
                r2 = 0
                android.content.res.AssetManager r0 = com.lumiyaviewer.lumiya.LumiyaApp.getAssetManager()
                if (r0 == 0) goto L87
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.io.IOException -> L5c java.lang.Throwable -> L6c
                r1.<init>()     // Catch: java.io.IOException -> L5c java.lang.Throwable -> L6c
                java.lang.String r3 = "anims/"
                java.lang.StringBuilder r1 = r1.append(r3)     // Catch: java.io.IOException -> L5c java.lang.Throwable -> L6c
                java.lang.String r3 = r6.assetName     // Catch: java.io.IOException -> L5c java.lang.Throwable -> L6c
                java.lang.StringBuilder r1 = r1.append(r3)     // Catch: java.io.IOException -> L5c java.lang.Throwable -> L6c
                java.lang.String r1 = r1.toString()     // Catch: java.io.IOException -> L5c java.lang.Throwable -> L6c
                java.io.InputStream r3 = r0.open(r1)     // Catch: java.io.IOException -> L5c java.lang.Throwable -> L6c
                if (r3 == 0) goto L85
                com.lumiyaviewer.lumiya.render.avatar.AnimationData r1 = new com.lumiyaviewer.lumiya.render.avatar.AnimationData     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L7e
                java.lang.Object r0 = r6.getParams()     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L7e
                java.util.UUID r0 = (java.util.UUID) r0     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L7e
                r1.<init>(r0, r3)     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L7e
                int r0 = r1.getPriority()     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
                r2 = 6
                if (r0 < r2) goto L4e
                java.lang.String r0 = "Animation: priority %d loaded from asset %s"
                r2 = 2
                java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
                int r4 = r1.getPriority()     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
                r5 = 0
                r2[r5] = r4     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
                java.lang.String r4 = r6.assetName     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
                r5 = 1
                r2[r5] = r4     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
                com.lumiyaviewer.lumiya.Debug.Printf(r0, r2)     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L82
            L4e:
                if (r3 == 0) goto L53
                r3.close()     // Catch: java.io.IOException -> L57
            L53:
                r6.completeRequest(r1)
                return
            L57:
                r0 = move-exception
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                goto L53
            L5c:
                r0 = move-exception
                r1 = r2
            L5e:
                com.lumiyaviewer.lumiya.Debug.Warning(r0)     // Catch: java.lang.Throwable -> L7b
                if (r2 == 0) goto L53
                r2.close()     // Catch: java.io.IOException -> L67
                goto L53
            L67:
                r0 = move-exception
                com.lumiyaviewer.lumiya.Debug.Warning(r0)
                goto L53
            L6c:
                r0 = move-exception
                r3 = r2
            L6e:
                if (r3 == 0) goto L73
                r3.close()     // Catch: java.io.IOException -> L74
            L73:
                throw r0
            L74:
                r1 = move-exception
                com.lumiyaviewer.lumiya.Debug.Warning(r1)
                goto L73
            L79:
                r0 = move-exception
                goto L6e
            L7b:
                r0 = move-exception
                r3 = r2
                goto L6e
            L7e:
                r0 = move-exception
                r1 = r2
                r2 = r3
                goto L5e
            L82:
                r0 = move-exception
                r2 = r3
                goto L5e
            L85:
                r1 = r2
                goto L4e
            L87:
                r1 = r2
                goto L53
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.anim.AnimationCache.AssetLoadRequest.run():void");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DownloadRequest extends ResourceRequest<UUID, AnimationData> implements Subscription.OnData<AssetData>, Subscription.OnError {
        private Subscription<AssetKey, AssetData> assetSubscription;

        DownloadRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager) {
            super(uuid, resourceManager);
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            Subscription<AssetKey, AssetData> subscription = this.assetSubscription;
            if (subscription != null) {
                subscription.unsubscribe();
            }
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void completeRequest(AnimationData animationData) {
            Subscription<AssetKey, AssetData> subscription = this.assetSubscription;
            if (subscription != null) {
                subscription.unsubscribe();
            }
            super.completeRequest((DownloadRequest) animationData);
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            AssetResponseCacher assetResponseCacher = (AssetResponseCacher) AnimationCache.this.assetResponseCacher.get();
            if (assetResponseCacher != null) {
                this.assetSubscription = assetResponseCacher.getPool().subscribe(AssetKey.createAssetKey(null, null, getParams(), 20), LoaderExecutor.getInstance(), this, this);
            } else {
                completeRequest((AnimationData) null);
            }
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public void onData(AssetData assetData) {
            AnimationData animationData;
            if (assetData == null || assetData.getData() == null || assetData.getStatus() != 1) {
                completeRequest((AnimationData) null);
                return;
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(assetData.getData());
            try {
                animationData = new AnimationData(getParams(), byteArrayInputStream);
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e = e;
                    Debug.Warning(e);
                    completeRequest(animationData);
                }
            } catch (IOException e2) {
                e = e2;
                animationData = null;
            }
            completeRequest(animationData);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnError
        public void onError(Throwable th) {
            completeRequest((AnimationData) null);
        }
    }

    /* loaded from: classes.dex */
    private static class InstanceHolder {
        private static final AnimationCache Instance = new AnimationCache(null);

        private InstanceHolder() {
        }
    }

    private AnimationCache() {
        this.assetResponseCacher = new AtomicReference<>(null);
        ImmutableSet.Builder builder = ImmutableSet.builder();
        AssetManager assetManager = LumiyaApp.getAssetManager();
        if (assetManager != null) {
            try {
                String[] list = assetManager.list("anims");
                if (list != null) {
                    builder.addAll((Iterable) Arrays.asList(list));
                }
            } catch (IOException e) {
                Debug.Warning(e);
            }
        }
        this.assetAnimations = builder.build();
    }

    /* synthetic */ AnimationCache(AnimationCache animationCache) {
        this();
    }

    public static AnimationCache getInstance() {
        return InstanceHolder.Instance;
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    protected /* bridge */ /* synthetic */ ResourceRequest CreateNewRequest(Object obj, ResourceManager resourceManager) {
        return CreateNewRequest((UUID) obj, (ResourceManager<UUID, AnimationData>) resourceManager);
    }

    protected ResourceRequest<UUID, AnimationData> CreateNewRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager) {
        String uuid2 = uuid.toString();
        return this.assetAnimations.contains(uuid2) ? new AssetLoadRequest(uuid, resourceManager, uuid2) : new DownloadRequest(uuid, resourceManager);
    }

    public void setAssetResponseCacher(AssetResponseCacher assetResponseCacher) {
        this.assetResponseCacher.set(assetResponseCacher);
    }
}
