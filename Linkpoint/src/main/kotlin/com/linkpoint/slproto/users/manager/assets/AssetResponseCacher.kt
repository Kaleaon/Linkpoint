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
import javax.annotation.Nonnull
import javax.annotation.Nullable

class AssetResponseCacher : Refreshable<AssetKey> {
    /* access modifiers changed from: private */
    val CachedAssetDao cachedAssetDao
    private val SubscriptionPool<AssetKey, AssetData> pool = SubscriptionPool<>()
    private val RateLimitRequestHandler<AssetKey, AssetData> requestHandler

    public AssetResponseCacher(DaoSession daoSession, Executor executor) {
        this.cachedAssetDao = daoSession.getCachedAssetDao()
        this.pool.setCacheInvalidateHandler($Lambda$9LOU8pkPwNYFJNwesblYMTVNE0(this), executor)
        this.requestHandler = RateLimitRequestHandler<>(RequestProcessor<AssetKey, AssetData, AssetData>(this.pool, executor) {
            /* access modifiers changed from: protected */
             public fun isRequestComplete(assetKey: AssetKey, assetData: AssetData): Boolean {
                val cachedAsset: CachedAsset = (CachedAsset) AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString())
                if (cachedAsset != null) {
                    return !cachedAsset.getMustRevalidate()
                }
                return false
            }

            /* access modifiers changed from: protected */
             public fun processRequest(assetKey: AssetKey): AssetData {
                val cachedAsset: CachedAsset = (CachedAsset) AssetResponseCacher.this.cachedAssetDao.load(assetKey.toString())
                if (cachedAsset != null) {
                    val assetData: AssetData = AssetData(cachedAsset.getStatus(), cachedAsset.getData())
                    Debug.Printf("AssetCache: returning cached response for key %s", assetKey)
                    return assetData
                }
                Debug.Printf("AssetCache: no cached data for key %s", assetKey)
                return null
            }

            /* access modifiers changed from: protected */
             public fun processResult(assetKey: AssetKey, assetData: AssetData): AssetData {
                Debug.Printf("AssetCache: saving cached data for key %s", assetKey.toString())
                if (assetData != null) {
                    AssetResponseCacher.this.cachedAssetDao.insertOrReplace(CachedAsset(assetKey.toString(), assetData.getStatus(), assetData.getData(), false))
                }
                return assetData
            }
    }

    public Subscribable<AssetKey, AssetData> getPool() {
        return this.pool
    }

    public RequestSource<AssetKey, AssetData> getRequestSource() {
        return this.requestHandler
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_assets_AssetResponseCacher_872  reason: not valid java name */
    public /* synthetic */ Unit m378lambda$com_lumiyaviewer_lumiya_slproto_users_manager_assets_AssetResponseCacher_872(AssetKey assetKey) {
        val cachedAsset: CachedAsset = (CachedAsset) this.cachedAssetDao.load(assetKey.toString())
        if (cachedAsset != null) {
            cachedAsset.setMustRevalidate(true)
            this.cachedAssetDao.update(cachedAsset)
        }
    }

    fun requestUpdate(assetKey: AssetKey) {
        this.pool.requestUpdate(assetKey)
    }
}
