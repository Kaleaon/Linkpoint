package com.lumiyaviewer.lumiya.render.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo

class DrawableAvatarCache {
    private val cache: Cache<SLObjectInfo, DrawableAvatar> = CacheBuilder.newBuilder()
        .weakValues()
        .build()

    fun getIfPresent(info: SLObjectInfo): DrawableAvatar? {
        return cache.getIfPresent(info)
    }
    
    fun put(info: SLObjectInfo, avatar: DrawableAvatar) {
        cache.put(info, avatar)
    }
    
    fun invalidate(info: SLObjectInfo) {
        cache.invalidate(info)
    }
}
