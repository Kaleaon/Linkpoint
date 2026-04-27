// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.react.Subscribable;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.CachedAsset;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.dao.CachedAssetDao;
import com.lumiyaviewer.lumiya.react.Refreshable;

public class AssetResponseCacher implements Refreshable<AssetKey>
{
    private final CachedAssetDao cachedAssetDao;
    private final SubscriptionPool<AssetKey, AssetData> pool;
    private final RateLimitRequestHandler<AssetKey, AssetData> requestHandler;
    
    public AssetResponseCacher(final DaoSession daoSession, final Executor executor) {
        this.pool = new SubscriptionPool<AssetKey, AssetData>();
        this.cachedAssetDao = daoSession.getCachedAssetDao();
        this.pool.setCacheInvalidateHandler(new _$Lambda$9LOU8pkPwNY_FJNwesblYMTVNE0(this), executor);
        this.requestHandler = new RateLimitRequestHandler<AssetKey, AssetData>(new RequestProcessor<AssetKey, AssetData, AssetData>(this.pool, executor) {
            @Override
            protected boolean isRequestComplete(@Nonnull final AssetKey assetKey, final AssetData assetData) {
                final CachedAsset cachedAsset = AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString());
                return cachedAsset != null && (cachedAsset.getMustRevalidate() ^ true);
            }
            
            @Nullable
            @Override
            protected AssetData processRequest(@Nonnull final AssetKey assetKey) {
                final CachedAsset cachedAsset = AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString());
                if (cachedAsset != null) {
                    final AssetData assetData = new AssetData(cachedAsset.getStatus(), cachedAsset.getData());
                    Debug.Printf("AssetCache: returning cached response for key %s", assetKey);
                    return assetData;
                }
                Debug.Printf("AssetCache: no cached data for key %s", assetKey);
                return null;
            }
            
            @Override
            protected AssetData processResult(@Nonnull final AssetKey assetKey, final AssetData assetData) {
                Debug.Printf("AssetCache: saving cached data for key %s", assetKey.toString());
                if (assetData != null) {
                    ((AbstractDao<CachedAsset, K>)AssetResponseCacher.this.cachedAssetDao).insertOrReplace(new CachedAsset(assetKey.toString(), assetData.getStatus(), assetData.getData(), false));
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
    
    @Override
    public void requestUpdate(final AssetKey assetKey) {
        this.pool.requestUpdate(assetKey);
    }
}
