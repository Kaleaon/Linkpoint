package com.lumiyaviewer.lumiya.res.anim

import com.google.common.collect.ImmutableSet
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.render.avatar.AnimationData
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

/**
 * Cache for animation data
 * Loads animations from assets or downloads them
 */
object AnimationCache : ResourceMemoryCache<UUID, AnimationData>(MemoryManager.getInstance()) {
    
    private val assetAnimations: ImmutableSet<String>
    private val assetResponseCacher = AtomicReference<AssetResponseCacher?>()

    /**
     * Request to load animation from asset bundle
     */
    private class AssetLoadRequest(
        uuid: UUID,
        manager: ResourceManager<UUID, AnimationData>,
        private val assetName: String
    ) : ResourceRequest<UUID, AnimationData>(uuid, manager), Runnable {

        override fun cancelRequest() {
            LoaderExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        override fun execute() {
            LoaderExecutor.getInstance().execute(this)
        }

        override fun run() {
            val assetManager = LumiyaApp.getAssetManager() ?: run {
                completeRequest(null)
                return
            }
            
            var animData: AnimationData? = null
            try {
                val path = "anims/$assetName"
                assetManager.open(path)?.use { inputStream ->
                    animData = AnimationData(getParams() as UUID, inputStream)
                    
                    animData?.let { data ->
                        if (data.getPriority() >= 6) {
                            Debug.Printf(
                                "Animation: priority %d loaded from asset %s",
                                data.getPriority(),
                                assetName
                            )
                        }
                    }
                }
            } catch (e: IOException) {
                Debug.Warning(e)
            }
            
            completeRequest(animData)
        }
    }

    /**
     * Request to download animation from asset server
     */
    private class DownloadRequest(
        uuid: UUID,
        manager: ResourceManager<UUID, AnimationData>
    ) : ResourceRequest<UUID, AnimationData>(uuid, manager),
        Subscription.OnData<AssetData>,
        Subscription.OnError {
        
        private var assetSubscription: Subscription<AssetKey, AssetData>? = null

        override fun cancelRequest() {
            assetSubscription?.unsubscribe()
            super.cancelRequest()
        }

        override fun completeRequest(resource: AnimationData?) {
            assetSubscription?.unsubscribe()
            super.completeRequest(resource)
        }

        override fun execute() {
            val cacher = AnimationCache.assetResponseCacher.get()
            if (cacher != null) {
                val key = AssetKey.createAssetKey(
                    null,
                    null,
                    getParams() as UUID,
                    20 // Animation asset type
                )
                assetSubscription = cacher.getPool().subscribe(
                    key,
                    LoaderExecutor.getInstance(),
                    this,
                    this
                )
            } else {
                completeRequest(null)
            }
        }

        override fun onData(assetData: AssetData?) {
            if (assetData?.getData() == null || assetData.getStatus() != 1) {
                completeRequest(null)
                return
            }
            
            var animData: AnimationData? = null
            try {
                ByteArrayInputStream(assetData.getData()).use { inputStream ->
                    animData = AnimationData(getParams() as UUID, inputStream)
                }
            } catch (e: IOException) {
                Debug.Warning(e)
            }
            
            completeRequest(animData)
        }

        override fun onError(throwable: Throwable) {
            completeRequest(null)
        }
    }

    init {
        // Build set of available asset animations
        val builder = ImmutableSet.builder<String>()
        val assetManager = LumiyaApp.getAssetManager()
        
        if (assetManager != null) {
            try {
                val animFiles = assetManager.list("anims")
                if (animFiles != null) {
                    builder.addAll(animFiles.toList())
                }
            } catch (e: IOException) {
                Debug.Warning(e)
            }
        }
        
        assetAnimations = builder.build()
    }

    /**
     * Get singleton instance
     */
    fun getInstance(): AnimationCache = this

    /**
     * Create new animation load request
     */
    override fun CreateNewRequest(
        params: UUID,
        manager: ResourceManager<UUID, AnimationData>
    ): ResourceRequest<UUID, AnimationData> {
        val uuidString = params.toString()
        
        return if (assetAnimations.contains(uuidString)) {
            AssetLoadRequest(params, manager, uuidString)
        } else {
            DownloadRequest(params, manager)
        }
    }

    /**
     * Set the asset response cacher for downloading animations
     */
    fun setAssetResponseCacher(cacher: AssetResponseCacher) {
        assetResponseCacher.set(cacher)
    }
    
    /**
     * Estimate memory size of animation data
     */
    override fun estimateSize(resource: AnimationData): Long {
        // Rough estimate: 1KB per animation
        return 1024L
    }
}
