package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.CachedAsset;
import com.lumiyaviewer.lumiya.dao.CachedAssetDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.react.Refreshable;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class AssetResponseCacher implements Refreshable<AssetKey> {
    private final CachedAssetDao cachedAssetDao;
    private final SubscriptionPool<AssetKey, AssetData> pool = new SubscriptionPool<>();
    private final RateLimitRequestHandler<AssetKey, AssetData> requestHandler;

    public AssetResponseCacher(DaoSession daoSession, Executor executor) {
        this.cachedAssetDao = daoSession.getCachedAssetDao();
        this.pool.setCacheInvalidateHandler(new Refreshable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.assets.-$Lambda$9LOU8pkPwNY-FJNwesblYMTVNE0
            private final /* synthetic */ void $m$0(Object obj) {
                ((AssetResponseCacher) this).m388x50f99f72((AssetKey) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Refreshable
            public final void requestUpdate(Object obj) {
                $m$0(obj);
            }
        }, executor);
        this.requestHandler = new RateLimitRequestHandler<>(new RequestProcessor<AssetKey, AssetData, AssetData>(this.pool, executor) { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.lumiyaviewer.lumiya.react.RequestProcessor
            public boolean isRequestComplete(@Nonnull AssetKey assetKey, AssetData assetData) {
                if (AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString()) != null) {
                    return !r0.getMustRevalidate();
                }
                return false;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.lumiyaviewer.lumiya.react.RequestProcessor
            @Nullable
            public AssetData processRequest(@Nonnull AssetKey assetKey) {
                CachedAsset load = AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString());
                if (load == null) {
                    Debug.Printf("AssetCache: no cached data for key %s", assetKey);
                    return null;
                }
                AssetData assetData = new AssetData(load.getStatus(), load.getData());
                Debug.Printf("AssetCache: returning cached response for key %s", assetKey);
                return assetData;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.lumiyaviewer.lumiya.react.RequestProcessor
            public AssetData processResult(@Nonnull AssetKey assetKey, AssetData assetData) {
                Debug.Printf("AssetCache: saving cached data for key %s", assetKey.toString());
                if (assetData != null) {
                    AssetResponseCacher.this.cachedAssetDao.insertOrReplace(new CachedAsset(assetKey.toString(), assetData.getStatus(), assetData.getData(), false));
                }
                return assetData;
            }
        });
    }

    public Subscribable<AssetKey, AssetData> getPool() {
        return this.pool;
    }

    public RequestSource<AssetKey, AssetData> getRequestSource() {
        return this.requestHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_assets_AssetResponseCacher_872, reason: not valid java name */
    public /* synthetic */ void m388x50f99f72(AssetKey assetKey) {
        CachedAsset load = this.cachedAssetDao.load(assetKey.toString());
        if (load != null) {
            load.setMustRevalidate(true);
            this.cachedAssetDao.update(load);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.Refreshable
    public void requestUpdate(AssetKey assetKey) {
        this.pool.requestUpdate(assetKey);
    }
}
