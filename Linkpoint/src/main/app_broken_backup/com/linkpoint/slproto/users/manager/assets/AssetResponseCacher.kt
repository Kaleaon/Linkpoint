package com.linkpoint.slproto.users.manager.assets

import com.linkpoint.Debug
import com.linkpoint.dao.CachedAsset
import com.linkpoint.dao.CachedAssetDao
import com.linkpoint.dao.DaoSession
import com.linkpoint.react.RateLimitRequestHandler
import com.linkpoint.react.Refreshable
import com.linkpoint.react.RequestProcessor
import com.linkpoint.react.RequestSource
import com.linkpoint.react.Subscribable
import com.linkpoint.react.SubscriptionPool
import java.util.concurrent.Executor
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class AssetResponseCacher : Refreshable<AssetKey> {
    /* access modifiers changed from: private */
    CachedAssetDao cachedAssetDao
    private SubscriptionPool<AssetKey, AssetData> pool = SubscriptionPool<>()
    private RateLimitRequestHandler<AssetKey, AssetData> requestHandler

    AssetResponseCacher(DaoSession daoSession, Executor executor) {
        this.cachedAssetDao = daoSession.getCachedAssetDao()
        this.pool.setCacheInvalidateHandler($Lambda$9LOU8pkPwNYFJNwesblYMTVNE0(this), executor)
        this.requestHandler = RateLimitRequestHandler<>(RequestProcessor<AssetKey, AssetData, AssetData>(this.pool, executor) {
            /* access modifiers changed from: protected */
            fun isRequestComplete(@NonNull AssetKey assetKey, AssetData assetData): Boolean {
                CachedAsset cachedAsset = (AssetResponseCacher as CachedAsset).this.cachedAssetDao.load(assetKey.toString())
                if (cachedAsset != null) {
                    return !cachedAsset.getMustRevalidate()
                }
                return false
            }

            /* access modifiers changed from: protected */
            @Nullable
            fun processRequest(@NonNull AssetKey assetKey): AssetData {
                CachedAsset cachedAsset = (AssetResponseCacher as CachedAsset).this.cachedAssetDao.load(assetKey.toString())
                if (cachedAsset != null) {
                    AssetData assetData = AssetData(cachedAsset.getStatus(), cachedAsset.getData())
                    Debug.Printf("AssetCache: returning cached response for key %s", assetKey)
                    return assetData
                }
                Debug.Printf("AssetCache: no cached data for key %s", assetKey)
                return null
            }

            /* access modifiers changed from: protected */
            fun processResult(@NonNull AssetKey assetKey, AssetData assetData): AssetData {
                Debug.Printf("AssetCache: saving cached data for key %s", assetKey.toString())
                if (assetData != null) {
                    AssetResponseCacher.this.cachedAssetDao.insertOrReplace(CachedAsset(assetKey.toString(), assetData.getStatus(), assetData.getData(), false))
                }
                return assetData
            }
    }

    fun getPool(): Subscribable<AssetKey, AssetData> {
        return this.pool
    }

    fun getRequestSource(): RequestSource<AssetKey, AssetData> {
        return this.requestHandler
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_assets_AssetResponseCacher_872  reason: not valid java name */
    /* synthetic */ Unit m378lambda$com_lumiyaviewer_lumiya_slproto_users_manager_assets_AssetResponseCacher_872(AssetKey assetKey) {
        CachedAsset cachedAsset = (this as CachedAsset).cachedAssetDao.load(assetKey.toString())
        if (cachedAsset != null) {
            cachedAsset.setMustRevalidate(true)
            this.cachedAssetDao.update(cachedAsset)
        }
    }

    fun requestUpdate(AssetKey assetKey)  {
        this.pool.requestUpdate(assetKey)
    }
}
