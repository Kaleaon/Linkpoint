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

public class AssetResponseCacher implements Refreshable<AssetKey> {
    /* access modifiers changed from: private */
    public final CachedAssetDao cachedAssetDao;
    private final SubscriptionPool<AssetKey, AssetData> pool = new SubscriptionPool<>();
    private final RateLimitRequestHandler<AssetKey, AssetData> requestHandler;

    public AssetResponseCacher(DaoSession daoSession, Executor executor) {
        this.cachedAssetDao = daoSession.getCachedAssetDao();
        this.pool.setCacheInvalidateHandler(new $Lambda$9LOU8pkPwNYFJNwesblYMTVNE0(this), executor);
        this.requestHandler = new RateLimitRequestHandler<>(new RequestProcessor<AssetKey, AssetData, AssetData>(this.pool, executor) {
            /* access modifiers changed from: protected */
            public boolean isRequestComplete(@Nonnull AssetKey assetKey, AssetData assetData) {
                CachedAsset cachedAsset = (CachedAsset) AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString());
                if (cachedAsset != null) {
                    return !cachedAsset.getMustRevalidate();
                }
                return false;
            }

            /* access modifiers changed from: protected */
            @Nullable
            public AssetData processRequest(@Nonnull AssetKey assetKey) {
                CachedAsset cachedAsset = (CachedAsset) AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString());
                if (cachedAsset != null) {
                    AssetData assetData = new AssetData(cachedAsset.getStatus(), cachedAsset.getData());
                    Debug.Printf("AssetCache: returning cached response for key %s", assetKey);
                    return assetData;
                }
                Debug.Printf("AssetCache: no cached data for key %s", assetKey);
                return null;
            }

            /* access modifiers changed from: protected */
            public AssetData processResult(@Nonnull AssetKey assetKey, AssetData assetData) {
                Debug.Printf("AssetCache: saving cached data for key %s", assetKey.toString());
                if (assetData != null) {
                    AssetResponseCacher.this.cachedAssetDao.insertOrReplace(new CachedAsset(assetKey.toString(), assetData.getStatus(), assetData.getData(), false));
                }
                return assetData;
            }
    }

    public Subscribable<AssetKey, AssetData> getPool() {
        return this.pool;
    }

    public RequestSource<AssetKey, AssetData> getRequestSource() {
        return this.requestHandler;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_assets_AssetResponseCacher_872  reason: not valid java name */
    public /* synthetic */ void m378lambda$com_lumiyaviewer_lumiya_slproto_users_manager_assets_AssetResponseCacher_872(AssetKey assetKey) {
        CachedAsset cachedAsset = (CachedAsset) this.cachedAssetDao.load(assetKey.toString());
        if (cachedAsset != null) {
            cachedAsset.setMustRevalidate(true);
            this.cachedAssetDao.update(cachedAsset);
        }
    }

    public void requestUpdate(AssetKey assetKey) {
        this.pool.requestUpdate(assetKey);
    }
}
