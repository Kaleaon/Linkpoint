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

        public void run() {
            InputStream inputStream = null;
            try {
                // Get asset manager
                android.content.res.AssetManager assetManager = com.lumiyaviewer.lumiya.LumiyaApp.getAssetManager();
                if (assetManager == null) {
                    completeRequest(null);
                    return;
                }
                
                // Build animation file path and open it
                String animationPath = "anims/" + this.assetName;
                inputStream = assetManager.open(animationPath);
                
                if (inputStream != null) {
                    // Create animation data from the stream
                    AnimationData animData = new com.lumiyaviewer.lumiya.render.avatar.AnimationData(
                        (UUID) getParams(), inputStream);
                    
                    // Log debug info for high priority animations (priority >= 6)
                    if (animData.getPriority() >= 6) {
                        Debug.Printf("Animation: priority %d loaded from asset %s", 
                            animData.getPriority(), this.assetName);
                    }
                    
                    // Complete the request with the loaded animation data
                    completeRequest(animData);
                } else {
                    completeRequest(null);
                }
                
            } catch (IOException e) {
                Debug.Warning(e);
                completeRequest(null);
            } finally {
                // Clean up resources
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Debug.Warning(e);
                    }
                }
            }
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
