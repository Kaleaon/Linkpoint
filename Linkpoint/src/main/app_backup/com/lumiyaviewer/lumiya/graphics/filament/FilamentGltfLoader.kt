package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.Scene
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.MaterialProvider
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.ConcurrentHashMap

/**
 * FilamentGltfLoader - Loads and manages glTF 2.0 models for Filament
 */
class FilamentGltfLoader(
    private val context: Context,
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "FilamentGltfLoader"
    }
    
    private val materialProvider: MaterialProvider
    private val assetLoader: AssetLoader
    private val resourceLoader: ResourceLoader
    private val loadedAssets = ConcurrentHashMap<String, FilamentAsset>()
    
    init {
        materialProvider = UbershaderProvider(engine)
        assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())
        resourceLoader = ResourceLoader(engine)
        Log.i(TAG, "glTF loader initialized")
    }
    
    fun loadFromAssets(assetPath: String): FilamentAsset? {
        loadedAssets[assetPath]?.let { return it }
        
        try {
            val buffer = context.assets.open(assetPath).use { input ->
                val dst = ByteBuffer.allocateDirect(input.available())
                val src = Channels.newChannel(input)
                src.read(dst)
                dst.rewind()
                dst
            }
            
            val asset = assetLoader.createAsset(buffer)
            
            if (asset == null) {
                Log.e(TAG, "Failed to load glTF asset: $assetPath")
                return null
            }
            
            resourceLoader.loadResources(asset)
            // resourceLoader.asyncBeginLoad(asset) // might depend on version
            
            asset.releaseSourceData()
            loadedAssets[assetPath] = asset
            Log.i(TAG, "Loaded glTF asset: $assetPath")
            
            return asset
        } catch (e: Exception) {
            Log.e(TAG, "Error loading glTF from assets: $assetPath", e)
            return null
        }
    }
    
    fun loadFromBuffer(buffer: ByteBuffer): FilamentAsset? {
        try {
            val asset = assetLoader.createAsset(buffer)
            
            if (asset == null) {
                Log.e(TAG, "Failed to create asset from buffer")
                return null
            }
            
            resourceLoader.loadResources(asset)
            // resourceLoader.asyncBeginLoad(asset)
            asset.releaseSourceData()
            
            Log.i(TAG, "Loaded glTF from buffer")
            
            return asset
        } catch (e: Exception) {
            Log.e(TAG, "Error loading glTF from buffer", e)
            return null
        }
    }
    
    fun addToScene(asset: FilamentAsset) {
        scene.addEntities(asset.entities)
        Log.i(TAG, "Added asset to scene")
    }
    
    fun removeFromScene(asset: FilamentAsset) {
        scene.removeEntities(asset.entities)
        Log.i(TAG, "Removed asset from scene")
    }
    
    fun destroyAsset(asset: FilamentAsset) {
        removeFromScene(asset)
        assetLoader.destroyAsset(asset)
    }
    
    fun updateAnimation(asset: FilamentAsset, deltaTime: Float) {
        // Animator access is currently problematic with some Filament versions/bindings
        /*
        val animator = asset.getAnimator()
        if (animator != null && animator.animationCount > 0) {
            animator.applyAnimation(0, deltaTime)
            animator.updateBoneMatrices()
        }
        */
    }
    
    fun isAssetReady(asset: FilamentAsset): Boolean {
        return resourceLoader.asyncGetLoadProgress() >= 1.0f
    }
    
    fun destroy() {
        Log.i(TAG, "Destroying glTF loader...")
        
        loadedAssets.values.forEach { asset ->
            try {
                assetLoader.destroyAsset(asset)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying asset", e)
            }
        }
        loadedAssets.clear()
        
        resourceLoader.evictResourceData()
        assetLoader.destroy()
        materialProvider.destroyMaterials()
        materialProvider.destroy()
        
        Log.i(TAG, "glTF loader destroyed")
    }
}
