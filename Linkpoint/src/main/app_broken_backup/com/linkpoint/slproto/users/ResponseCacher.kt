package com.linkpoint.slproto.users

import com.linkpoint.Debug
import com.linkpoint.dao.CachedResponse
import com.linkpoint.dao.CachedResponseDao
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
            fun isRequestComplete(@NonNull KeyType keytype, MessageType messagetype): Boolean {
                CachedResponse cachedResponse = (ResponseCacher as CachedResponse).this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keytype))
                if (cachedResponse != null) {
                    return !cachedResponse.getMustRevalidate()
                }
                return false
            }

            /* access modifiers changed from: protected */
            @Nullable
            fun processRequest(@NonNull KeyType keytype): MessageType {
                CachedResponse cachedResponse = (ResponseCacher as CachedResponse).this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keytype))
                if (cachedResponse == null || cachedResponse.getData() == null) {
                    return null
                }
                MessageType loadCached = ResponseCacher.this.loadCached(cachedResponse.getData())
                Debug.Printf("%s: returning cached response for key %s (%s)", str, keytype.toString(), loadCached)
                return loadCached
            }

            /* access modifiers changed from: protected */
            fun processResult(@NonNull KeyType keytype, MessageType messagetype): MessageType {
                Debug.Printf("%s: saving cached data for key %s", str, keytype.toString())
                if (messagetype != null) {
                    ResponseCacher.this.cachedresponseDao.insertOrReplace(CachedResponse(ResponseCacher.this.getKeyString(keytype), ResponseCacher.this.storeCached(messagetype), false))
                }
                return messagetype
            }
    }

    /* access modifiers changed from: private */
    fun getKeyString(@NonNull KeyType keytype): String {
        return this.keyPrefix + ":" + keytype.toString()
    }

    fun getPool(): Subscribable<KeyType, MessageType> {
        return this.pool
    }

    fun getRequestSource(): RequestSource<KeyType, MessageType> {
        return this.requestHandler
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_ResponseCacher_1058  reason: not valid java name */
    /* synthetic */ Unit m267lambda$com_lumiyaviewer_lumiya_slproto_users_ResponseCacher_1058(Any obj) {
        CachedResponse cachedResponse = (this as CachedResponse).cachedresponseDao.load(getKeyString(obj))
        if (cachedResponse != null) {
            cachedResponse.setMustRevalidate(true)
            this.cachedresponseDao.update(cachedResponse)
        }
    }

    /* access modifiers changed from: protected */
    abstract MessageType loadCached(ByteArray bArr)

    fun requestUpdate(KeyType keytype): Unit {
        this.pool.requestUpdate(keytype)
    }

    /* access modifiers changed from: protected */
    abstract ByteArray storeCached(@NonNull MessageType messagetype)
}
