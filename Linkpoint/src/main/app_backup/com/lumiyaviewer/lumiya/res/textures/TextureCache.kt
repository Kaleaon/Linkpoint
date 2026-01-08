package com.lumiyaviewer.lumiya.res.textures

import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.res.executors.StartingExecutor
import com.lumiyaviewer.lumiya.res.executors.WeakExecutor
import com.lumiyaviewer.lumiya.res.executors.HTTPFetchExecutor
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean

object TextureCache : ResourceMemoryCache<DrawableTextureParams, OpenJPEG>(com.lumiyaviewer.lumiya.memory.MemoryManager()) {

    private val lock = Any()
    private var baseDir: File? = null
    private var textureTempDir: File? = null

    private val decompressExecutor: ExecutorService = WeakExecutor("TextureDecompressor", 1)
    private val downloadGate = StartingExecutor()
    private val compressedCache = TextureCompressedCache()
    private val lowMemory = AtomicBoolean(false)

    fun getInstance(): TextureCache = this

    override fun CreateNewRequest(
        params: DrawableTextureParams,
        manager: ResourceManager<DrawableTextureParams, OpenJPEG>
    ): ResourceRequest<DrawableTextureParams, OpenJPEG> {
        return object : ResourceRequest<DrawableTextureParams, OpenJPEG>(params, manager) {
            private var pendingTask: Future<*>? = null

            override fun execute() {
                pendingTask = decompressExecutor.submit {
                    try {
                        val textureData = decompressTexture(params)
                        completeRequest(textureData)
                    } catch (e: Exception) {
                        com.lumiyaviewer.lumiya.Debug.Printf("TextureCache: Decompression error: %s", e.message)
                        completeRequest(null)
                    }
                }
            }

            override fun cancelRequest() {
                pendingTask?.cancel(true)
            }
        }
    }

    override fun RequestResource(params: DrawableTextureParams, consumer: ResourceConsumer) {
        super.RequestResource(params, consumer)
    }

    override fun CancelRequest(consumer: ResourceConsumer) {
        super.CancelRequest(consumer)
    }

    fun getTextureCompressedCache(): TextureCompressedCache = compressedCache

    fun getDecompressorExecutor(): ExecutorService = decompressExecutor

    fun setFetcher(fetcher: SLTextureFetcher?) {
        compressedCache.setFetcher(fetcher)
    }

    fun setMaxTextureDownloads(count: Int) {
        val safeCount = count.coerceAtLeast(1)
        downloadGate.setMaxConcurrentTasks(safeCount)
        compressedCache.setMaxTextureDownloads(safeCount)
        HTTPFetchExecutor.getInstance().setCorePoolSize(safeCount)
        HTTPFetchExecutor.getInstance().setMaximumPoolSize(safeCount)
    }

    fun setTextureMemoryState(low: Boolean) {
        lowMemory.set(low)
        if (low) {
            downloadGate.pause()
        } else {
            downloadGate.unpause()
        }
    }

    fun getTextureCompressedFile(params: DrawableTextureParams): File {
        // Stub for now as uuid() property access might vary
        val cache = textureTempDir() ?: GlobalOptions.getInstance().getCacheDir("textures")
        // val uuid = params.uuid // Assuming property access
        // Implementation depends on DrawableTextureParams
        return File(cache, "dummy.jp2")
    }

    fun getResourceFile(params: DrawableTextureParams, highQuality: Boolean): File {
        val base = baseDir() ?: GlobalOptions.getInstance().getCacheDir("tex2")
        // Stub
        return File(base, "dummy.raw")
    }

    fun onCacheDirChanged() {
        synchronized(lock) {
            baseDir = null
            textureTempDir = null
        }
        compressedCache.onCacheDirChanged()
    }

    private fun baseDir(): File? {
        synchronized(lock) {
            if (baseDir == null) {
                baseDir = GlobalOptions.getInstance().getCacheDir("tex2")
            }
            return baseDir
        }
    }

    private fun textureTempDir(): File? {
        synchronized(lock) {
            if (textureTempDir == null) {
                textureTempDir = GlobalOptions.getInstance().getCacheDir("textures")
            }
            return textureTempDir
        }
    }

    private fun decompressTexture(params: DrawableTextureParams): OpenJPEG? {
        return null // Stub
    }
}
