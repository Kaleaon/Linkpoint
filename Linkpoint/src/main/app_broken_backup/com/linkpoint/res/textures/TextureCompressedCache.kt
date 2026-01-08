package com.linkpoint.res.textures

import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceRequest
import com.linkpoint.slproto.modules.texfetcher.SLTextureFetcher
import com.linkpoint.res.executors.HTTPFetchExecutor
import com.linkpoint.GlobalOptions
import com.linkpoint.Debug
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

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
    
    private val downloadExecutor: ExecutorService = Executors.newFixedThreadPool(4)
    private val activeDownloads = AtomicInteger(0)
    private val pendingRequests = ConcurrentHashMap<DrawableTextureParams, MutableList<ResourceConsumer>>()

    fun RequestResource(params: DrawableTextureParams, consumer: ResourceConsumer) {
        // Check if already cached
        val cached = completedFiles[params]
        if (cached != null) {
            consumer.OnResourceReady(cached, false)
            return
        }

        // Check if download is already in progress
        synchronized(pendingRequests) {
            val existingConsumers = pendingRequests[params]
            if (existingConsumers != null) {
                // Add to waiting list for this download
                existingConsumers.add(consumer)
                return
            } else {
                // Start new download
                pendingRequests[params] = mutableListOf(consumer)
            }
        }

        // Check download limit
        if (activeDownloads.get() >= maxDownloads) {
            Debug.Printf("TextureCompressedCache: Max downloads reached, queuing request")
            return
        }

        // Start download
        startTextureDownload(params)
    }

    private fun startTextureDownload(params: DrawableTextureParams) {
        activeDownloads.incrementAndGet()
        
        downloadExecutor.submit {
            try {
                val textureFile = fetchTextureViaHTTP(params)
                if (textureFile != null) {
                    // Cache the file
                    completedFiles[params] = textureFile
                    
                    // Notify all waiting consumers
                    val consumers = synchronized(pendingRequests) {
                        pendingRequests.remove(params) ?: emptyList()
                    }
                    
                    for (consumer in consumers) {
                        consumer.OnResourceReady(textureFile, true)
                    }
                } else {
                    // Try UDP fallback
                    val udpFile = fetchTextureViaUDP(params)
                    if (udpFile != null) {
                        completedFiles[params] = udpFile
                        
                        val consumers = synchronized(pendingRequests) {
                            pendingRequests.remove(params) ?: emptyList()
                        }
                        
                        for (consumer in consumers) {
                            consumer.OnResourceReady(udpFile, true)
                        }
                    } else {
                        // Notify failure
                        val consumers = synchronized(pendingRequests) {
                            pendingRequests.remove(params) ?: emptyList()
                        }
                        
                        for (consumer in consumers) {
                            consumer.OnResourceReady(null, true)
                        }
                    }
                }
            } catch (e: Exception) {
                Debug.Printf("TextureCompressedCache: Error downloading texture %s: %s", 
                    params.textureId, e.message)
                    
                // Notify failure
                val consumers = synchronized(pendingRequests) {
                    pendingRequests.remove(params) ?: emptyList()
                }
                
                for (consumer in consumers) {
                    consumer.OnResourceReady(null, true)
                }
            } finally {
                activeDownloads.decrementAndGet()
            }
        }
    }

    private fun fetchTextureViaHTTP(params: DrawableTextureParams): File? {
        return try {
            fetcher?.fetchTextureToFile(params.textureId)
        } catch (e: Exception) {
            Debug.Printf("TextureCompressedCache: HTTP fetch failed: %s", e.message)
            null
        }
    }

    private fun fetchTextureViaUDP(params: DrawableTextureParams): File? {
        return try {
            // UDP fallback for texture fetching
            // This would use the SL UDP protocol for texture transfer
            fetcher?.fetchTextureViaUDP(params.textureId)
        } catch (e: Exception) {
            Debug.Printf("TextureCompressedCache: UDP fetch failed: %s", e.message)
            null
        }
    }

    fun CancelRequest(consumer: ResourceConsumer) {
        // Remove consumer from pending requests
        synchronized(pendingRequests) {
            for ((_, consumers) in pendingRequests) {
                consumers.remove(consumer)
            }
        }
    }

    fun setFetcher(fetcher: SLTextureFetcher?) {
        this.fetcher = fetcher
    }

    fun setMaxTextureDownloads(max: Int) {
        maxDownloads = max.coerceAtLeast(1)
    }

    fun onCacheDirChanged() {
        completedFiles.clear()
        synchronized(pendingRequests) {
            pendingRequests.clear()
        }
    }

    fun shutdown() {
        downloadExecutor.shutdown()
    }
}
