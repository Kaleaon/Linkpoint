package com.lumiyaviewer.lumiya.res.anim;

import android.content.res.AssetManager;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.Subscription.OnData;
import com.lumiyaviewer.lumiya.react.Subscription.OnError;
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
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationCache extends ResourceMemoryCache<UUID, AnimationData> {
    private final ImmutableSet<String> assetAnimations;
    private final AtomicReference<AssetResponseCacher> assetResponseCacher;

    private static class AssetLoadRequest extends ResourceRequest<UUID, AnimationData> implements Runnable {
        private final String assetName;

        AssetLoadRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager, String str) {
            super(uuid, resourceManager);
            this.assetName = str;
        }

        public void cancelRequest() {
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute() {
            LoaderExecutor.getInstance().execute(this);
        }

        /* DevToolsApp WARNING: Removed duplicated region for block: B:30:0x0070 A:{SYNTHETIC, Splitter: B:30:0x0070} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:30:0x0070 A:{SYNTHETIC, Splitter: B:30:0x0070} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:23:0x0063 A:{SYNTHETIC, Splitter: B:23:0x0063} */
        /* DevToolsApp WARNING: Removed duplicated region for block: B:35:0x0079 A:{ExcHandler: all (th java.lang.Throwable), Splitter: B:5:0x0023} */
        /* DevToolsApp WARNING: Failed to process nested try/catch */
        /* DevToolsApp WARNING: Missing block: B:24:?, code:
            r2.close();
     */
        /* DevToolsApp WARNING: Missing block: B:25:0x0067, code:
            r0 = move-exception;
     */
        /* DevToolsApp WARNING: Missing block: B:26:0x0068, code:
            com.lumiyaviewer.lumiya.Debug.Warning(r0);
     */
        /* DevToolsApp WARNING: Missing block: B:31:?, code:
            r3.close();
     */
        /* DevToolsApp WARNING: Missing block: B:33:0x0074, code:
            r1 = move-exception;
     */
        /* DevToolsApp WARNING: Missing block: B:34:0x0075, code:
            com.lumiyaviewer.lumiya.Debug.Warning(r1);
     */
        /* DevToolsApp WARNING: Missing block: B:35:0x0079, code:
            r0 = th;
     */
        /* DevToolsApp WARNING: Missing block: B:40:0x0082, code:
            r0 = e;
     */
        /* DevToolsApp WARNING: Missing block: B:41:0x0083, code:
            r2 = r3;
     */
        public void run() {
            /*
            r6 = this;
            r2 = 0;
            r0 = com.lumiyaviewer.lumiya.LumiyaApp.getAssetManager();
            if (r0 == 0) goto L_0x0087;
        L_0x0007:
            r1 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x005c, all -> 0x006c }
            r1.<init>();	 Catch:{ IOException -> 0x005c, all -> 0x006c }
            r3 = "anims/";
            r1 = r1.append(r3);	 Catch:{ IOException -> 0x005c, all -> 0x006c }
            r3 = r6.assetName;	 Catch:{ IOException -> 0x005c, all -> 0x006c }
            r1 = r1.append(r3);	 Catch:{ IOException -> 0x005c, all -> 0x006c }
            r1 = r1.toString();	 Catch:{ IOException -> 0x005c, all -> 0x006c }
            r3 = r0.open(r1);	 Catch:{ IOException -> 0x005c, all -> 0x006c }
            if (r3 == 0) goto L_0x0085;
        L_0x0023:
            r1 = new com.lumiyaviewer.lumiya.render.avatar.AnimationData;	 Catch:{ IOException -> 0x007e, all -> 0x0079 }
            r0 = r6.getParams();	 Catch:{ IOException -> 0x007e, all -> 0x0079 }
            r0 = (java.util.UUID) r0;	 Catch:{ IOException -> 0x007e, all -> 0x0079 }
            r1.<init>(r0, r3);	 Catch:{ IOException -> 0x007e, all -> 0x0079 }
            r0 = r1.getPriority();	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
            r2 = 6;
            if (r0 < r2) goto L_0x004e;
        L_0x0035:
            r0 = "Animation: priority %d loaded from asset %s";
            r2 = 2;
            r2 = new java.lang.Object[r2];	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
            r4 = r1.getPriority();	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
            r4 = java.lang.Integer.valueOf(r4);	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
            r5 = 0;
            r2[r5] = r4;	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
            r4 = r6.assetName;	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
            r5 = 1;
            r2[r5] = r4;	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r2);	 Catch:{ IOException -> 0x0082, all -> 0x0079 }
        L_0x004e:
            if (r3 == 0) goto L_0x0053;
        L_0x0050:
            r3.close();	 Catch:{ IOException -> 0x0057 }
        L_0x0053:
            r6.completeRequest(r1);
            return;
        L_0x0057:
            r0 = move-exception;
            com.lumiyaviewer.lumiya.Debug.Warning(r0);
            goto L_0x0053;
        L_0x005c:
            r0 = move-exception;
            r1 = r2;
        L_0x005e:
            com.lumiyaviewer.lumiya.Debug.Warning(r0);	 Catch:{ all -> 0x007b }
            if (r2 == 0) goto L_0x0053;
        L_0x0063:
            r2.close();	 Catch:{ IOException -> 0x0067 }
            goto L_0x0053;
        L_0x0067:
            r0 = move-exception;
            com.lumiyaviewer.lumiya.Debug.Warning(r0);
            goto L_0x0053;
        L_0x006c:
            r0 = move-exception;
            r3 = r2;
        L_0x006e:
            if (r3 == 0) goto L_0x0073;
        L_0x0070:
            r3.close();	 Catch:{ IOException -> 0x0074 }
        L_0x0073:
            throw r0;
        L_0x0074:
            r1 = move-exception;
            com.lumiyaviewer.lumiya.Debug.Warning(r1);
            goto L_0x0073;
        L_0x0079:
            r0 = move-exception;
            goto L_0x006e;
        L_0x007b:
            r0 = move-exception;
            r3 = r2;
            goto L_0x006e;
        L_0x007e:
            r0 = move-exception;
            r1 = r2;
            r2 = r3;
            goto L_0x005e;
        L_0x0082:
            r0 = move-exception;
            r2 = r3;
            goto L_0x005e;
        L_0x0085:
            r1 = r2;
            goto L_0x004e;
        L_0x0087:
            r1 = r2;
            goto L_0x0053;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.anim.AnimationCache.AssetLoadRequest.run():void");
        }
    }

    private class DownloadRequest extends ResourceRequest<UUID, AnimationData> implements OnData<AssetData>, OnError {
        private Subscription<AssetKey, AssetData> assetSubscription;

        DownloadRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager) {
            super(uuid, resourceManager);
        }

        public void cancelRequest() {
            Subscription subscription = this.assetSubscription;
            if (subscription != null) {
                subscription.unsubscribe();
            }
            super.cancelRequest();
        }

        public void completeRequest(AnimationData animationData) {
            Subscription subscription = this.assetSubscription;
            if (subscription != null) {
                subscription.unsubscribe();
            }
            super.completeRequest(animationData);
        }

        public void execute() {
            AssetResponseCacher assetResponseCacher = (AssetResponseCacher) AnimationCache.this.assetResponseCacher.get();
            if (assetResponseCacher != null) {
                this.assetSubscription = assetResponseCacher.getPool().subscribe(AssetKey.createAssetKey(null, null, (UUID) getParams(), 20), LoaderExecutor.getInstance(), this, this);
            } else {
                completeRequest(null);
            }
        }

        public void onData(AssetData assetData) {
            AnimationData animationData;
            Throwable e;
            if (assetData == null || assetData.getData() == null || assetData.getStatus() != 1) {
                completeRequest(null);
                return;
            }
            InputStream byteArrayInputStream = new ByteArrayInputStream(assetData.getData());
            try {
                animationData = new AnimationData((UUID) getParams(), byteArrayInputStream);
                try {
                    byteArrayInputStream.close();
                } catch (IOException e2) {
                    e = e2;
                    Debug.Warning(e);
                    completeRequest(animationData);
                }
            } catch (IOException e3) {
                e = e3;
                animationData = null;
                Debug.Warning(e);
                completeRequest(animationData);
            }
            completeRequest(animationData);
        }

        public void onError(Throwable th) {
            completeRequest(null);
        }
    }

    private static class InstanceHolder {
        private static final AnimationCache Instance = new AnimationCache();

        private InstanceHolder() {
        }
    }

    private AnimationCache() {
        this.assetResponseCacher = new AtomicReference(null);
        Builder builder = ImmutableSet.builder();
        AssetManager assetManager = LumiyaApp.getAssetManager();
        if (assetManager != null) {
            try {
                String[] list = assetManager.list("anims");
                if (list != null) {
                    builder.addAll(Arrays.asList(list));
                }
            } catch (Throwable e) {
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

    protected ResourceRequest<UUID, AnimationData> CreateNewRequest(UUID uuid, ResourceManager<UUID, AnimationData> resourceManager) {
        String uuid2 = uuid.toString();
        return this.assetAnimations.contains(uuid2) ? new AssetLoadRequest(uuid, resourceManager, uuid2) : new DownloadRequest(uuid, resourceManager);
    }

    public void setAssetResponseCacher(AssetResponseCacher assetResponseCacher) {
        this.assetResponseCacher.set(assetResponseCacher);
    }
}
