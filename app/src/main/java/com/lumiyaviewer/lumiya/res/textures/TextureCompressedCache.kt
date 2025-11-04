package com.lumiyaviewer.lumiya.res.textures

import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Temporary stand-in for the legacy compressed texture cache. For now it tracks
 * files that have already been provided and invokes consumers immediately. A
 * full implementation (with HTTP/UDP fetch and decompression) will be restored
 * in subsequent steps.
 */
class TextureCompressedCache {

    private val completedFiles = ConcurrentHashMap<DrawableTextureParams, File>()
    @Volatile
    private var fetcher: SLTextureFetcher? = null
    @Volatile
    private var maxDownloads: Int = 4

    fun RequestResource(params: DrawableTextureParams, consumer: ResourceConsumer) {
        val cached = completedFiles[params]
        consumer.OnResourceReady(cached, false)
        // TODO: hook real fetch logic (HTTP + UDP) and populate [completedFiles].
    }

    fun CancelRequest(consumer: ResourceConsumer) {
        // Nothing to cancel in placeholder implementation.
    }

    fun setFetcher(fetcher: SLTextureFetcher?) {
        this.fetcher = fetcher
    }

    fun setMaxTextureDownloads(max: Int) {
        maxDownloads = max.coerceAtLeast(1)
    }

    fun onCacheDirChanged() {
        completedFiles.clear()
    }
}
