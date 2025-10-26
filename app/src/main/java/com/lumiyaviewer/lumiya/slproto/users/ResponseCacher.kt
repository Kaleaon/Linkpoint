package com.lumiyaviewer.lumiya.slproto.users

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.CachedResponse
import com.lumiyaviewer.lumiya.dao.CachedResponseDao
import com.lumiyaviewer.lumiya.dao.DaoSession
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler
import com.lumiyaviewer.lumiya.react.Refreshable
import com.lumiyaviewer.lumiya.react.RequestProcessor
import com.lumiyaviewer.lumiya.react.RequestSource
import com.lumiyaviewer.lumiya.react.Subscribable
import com.lumiyaviewer.lumiya.react.SubscriptionPool
import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class ResponseCacher<KeyType, MessageType> : Refreshable<KeyType> {
    private Executor cacheExecutor
    /* access modifiers changed from: private */
    CachedResponseDao cachedresponseDao
    private String keyPrefix
    private SubscriptionPool<KeyType, MessageType> pool = SubscriptionPool<>()
    private RateLimitRequestHandler<KeyType, MessageType> requestHandler

    ResponseCacher(DaoSession daoSession, Executor executor, String str) {
        this.cacheExecutor = executor
        this.cachedresponseDao = daoSession.getCachedResponseDao()
        this.keyPrefix = str
        this.pool.setCacheInvalidateHandler($Lambda$Tb8OaRVtYXRJ6N6ca7skHX1PNws(this), executor)
        this.requestHandler = RateLimitRequestHandler<>(RequestProcessor<KeyType, MessageType, MessageType>(this.pool, executor) {
            /* access modifiers changed from: protected */
            Boolean isRequestComplete(@Nonnull KeyType keytype, MessageType messagetype) {
                CachedResponse cachedResponse = (CachedResponse) ResponseCacher.this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keytype))
                if (cachedResponse != null) {
                    return !cachedResponse.getMustRevalidate()
                }
                return false
            }

            /* access modifiers changed from: protected */
            @Nullable
            MessageType processRequest(@Nonnull KeyType keytype) {
                CachedResponse cachedResponse = (CachedResponse) ResponseCacher.this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keytype))
                if (cachedResponse == null || cachedResponse.getData() == null) {
                    return null
                }
                MessageType loadCached = ResponseCacher.this.loadCached(cachedResponse.getData())
                Debug.Printf("%s: returning cached response for key %s (%s)", str, keytype.toString(), loadCached)
                return loadCached
            }

            /* access modifiers changed from: protected */
            MessageType processResult(@Nonnull KeyType keytype, MessageType messagetype) {
                Debug.Printf("%s: saving cached data for key %s", str, keytype.toString())
                if (messagetype != null) {
                    ResponseCacher.this.cachedresponseDao.insertOrReplace(CachedResponse(ResponseCacher.this.getKeyString(keytype), ResponseCacher.this.storeCached(messagetype), false))
                }
                return messagetype
            }
    }

    /* access modifiers changed from: private */
    String getKeyString(@Nonnull KeyType keytype) {
        return this.keyPrefix + ":" + keytype.toString()
    }

    Subscribable<KeyType, MessageType> getPool() {
        return this.pool
    }

    RequestSource<KeyType, MessageType> getRequestSource() {
        return this.requestHandler
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_ResponseCacher_1058  reason: not valid java name */
    /* synthetic */ Unit m267lambda$com_lumiyaviewer_lumiya_slproto_users_ResponseCacher_1058(Any obj) {
        CachedResponse cachedResponse = (CachedResponse) this.cachedresponseDao.load(getKeyString(obj))
        if (cachedResponse != null) {
            cachedResponse.setMustRevalidate(true)
            this.cachedresponseDao.update(cachedResponse)
        }
    }

    /* access modifiers changed from: protected */
    abstract MessageType loadCached(ByteArray bArr)

    Unit requestUpdate(KeyType keytype) {
        this.pool.requestUpdate(keytype)
    }

    /* access modifiers changed from: protected */
    abstract ByteArray storeCached(@Nonnull MessageType messagetype)
}
