package com.lumiyaviewer.lumiya.res.textures

import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.render.tex.TextureClass
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.res.executors.StartingExecutor
import com.lumiyaviewer.lumiya.res.executors.WeakExecutor
import com.lumiyaviewer.lumiya.res.executors.HTTPFetchExecutor
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher
import java.io.File
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Simplified placeholder implementation of the original texture cache.
 *
 * The goal is to restore compilation while we rebuild the full feature set.
 * The current implementation provides directory management and request wiring,
 * but defers actual decompression/fetch logic to follow-up iterations.
 */
object TextureCache : ResourceMemoryCache<DrawableTextureParams, OpenJPEG>() {

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
        manager: ResourceManager<DrawableTextureParams, OpenJPEG>,
    ): ResourceRequest<DrawableTextureParams, OpenJPEG> {
        return object : ResourceRequest<DrawableTextureParams, OpenJPEG>(params, manager) {
            private var pendingTask: Future<*>? = null

            override fun execute() {
                pendingTask = decompressExecutor.submit {
                    // TODO: hook real decompression; for now emit null to unblock requesters
                    completeRequest(null)
                }
            }

            override fun cancelRequest() {
                pendingTask?.cancel(true)
            }
        }
    }

    fun RequestResource(params: DrawableTextureParams, consumer: ResourceConsumer) {
        super.RequestResource(params, consumer)
    }

    fun CancelRequest(consumer: ResourceConsumer?) {
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
        val cache = textureTempDir() ?: GlobalOptions.getInstance().getCacheDir("textures")
        val uuid = params.uuid()
        val hash = uuid.hashCode()
        val dir = String.format("%02x", (hash shr 24 xor (hash shr 8 xor hash xor (hash shr 16))) and 0xFF)
        val path = File(cache, "$dir/${uuid}.jp2")
        path.parentFile?.mkdirs()
        return path
    }

    fun getTextureCompressedFileOld(params: DrawableTextureParams): File {
        val cache = textureTempDir() ?: GlobalOptions.getInstance().getCacheDir("textures")
        val file = File(cache, params.uuid().toString() + ".jp2")
        file.parentFile?.mkdirs()
        return file
    }

    fun getResourceFile(params: DrawableTextureParams, highQuality: Boolean): File {
        val base = baseDir() ?: GlobalOptions.getInstance().getCacheDir("tex2")
        return params.getTextureRawPath(base, highQuality)
    }

    fun getBitmapsBaseDir(): File = GlobalOptions.getInstance().getCacheDir("bitmaps")

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
}
