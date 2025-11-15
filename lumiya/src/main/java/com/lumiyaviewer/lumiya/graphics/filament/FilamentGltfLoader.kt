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
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * FilamentGltfLoader - Loads and manages glTF 2.0 models for Filament
 * 
 * Supports:
 * - Loading glTF/GLB files from assets or network
 * - Mesh objects
 * - Avatar attachments
 * - Animations
 * - PBR materials
 */
class FilamentGltfLoader(
    private val context: Context,
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "FilamentGltfLoader"
    }
    
    // Material provider for glTF materials
    private val materialProvider: MaterialProvider
    
    // Asset loader
    private val assetLoader: AssetLoader
    
    // Resource loader for loading textures/buffers
    private val resourceLoader: ResourceLoader
    
    // Loaded assets cache
    private val loadedAssets = ConcurrentHashMap<String, FilamentAsset>()
    
    init {
        // Create material provider with ubershader
        materialProvider = UbershaderProvider(engine)
        
        // Create asset loader
        assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())
        
        // Create resource loader
        resourceLoader = ResourceLoader(engine)
        
        Log.i(TAG, "glTF loader initialized")
    }
    
    /**
     * Load a glTF model from assets
     */
    fun loadFromAssets(assetPath: String): FilamentAsset? {
        // Check cache
        loadedAssets[assetPath]?.let { return it }
        
        try {
            // Read asset file
            val buffer = context.assets.open(assetPath).use { input ->
                val dst = ByteBuffer.allocateDirect(input.available())
                val src = Channels.newChannel(input)
                src.read(dst)
                dst.rewind()
                dst
            }
            
            // Create asset
            val asset = if (assetPath.endsWith(".glb")) {
                assetLoader.createAssetFromBinary(buffer)
            } else {
                assetLoader.createAssetFromJson(buffer)
            }
            
            if (asset == null) {
                Log.e(TAG, "Failed to load glTF asset: $assetPath")
                return null
            }
            
            // Load resources (textures, buffers)
            resourceLoader.loadResources(asset)
            resourceLoader.asyncBeginLoad(asset)
            
            // Release source data (not needed anymore)
            asset.releaseSourceData()
            
            // Cache it
            loadedAssets[assetPath] = asset
            
            Log.i(TAG, "Loaded glTF asset: $assetPath (${asset.entityCount} entities)")
            
            return asset
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading glTF from assets: $assetPath", e)
            return null
        }
    }
    
    /**
     * Load a glTF model from a byte buffer (e.g., downloaded from network)
     */
    fun loadFromBuffer(buffer: ByteBuffer, isGlb: Boolean = true): FilamentAsset? {
        try {
            val asset = if (isGlb) {
                assetLoader.createAssetFromBinary(buffer)
            } else {
                assetLoader.createAssetFromJson(buffer)
            }
            
            if (asset == null) {
                Log.e(TAG, "Failed to create asset from buffer")
                return null
            }
            
            // Load resources
            resourceLoader.loadResources(asset)
            resourceLoader.asyncBeginLoad(asset)
            asset.releaseSourceData()
            
            Log.i(TAG, "Loaded glTF from buffer (${asset.entityCount} entities)")
            
            return asset
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading glTF from buffer", e)
            return null
        }
    }
    
    /**
     * Add a loaded asset to the scene
     */
    fun addToScene(asset: FilamentAsset) {
        scene.addEntities(asset.entities)
        Log.i(TAG, "Added asset to scene (${asset.entityCount} entities)")
    }
    
    /**
     * Remove an asset from the scene
     */
    fun removeFromScene(asset: FilamentAsset) {
        scene.removeEntities(asset.entities)
        Log.i(TAG, "Removed asset from scene")
    }
    
    /**
     * Destroy an asset and free its resources
     */
    fun destroyAsset(asset: FilamentAsset) {
        removeFromScene(asset)
        assetLoader.destroyAsset(asset)
    }
    
    /**
     * Update animation for an asset
     */
    fun updateAnimation(asset: FilamentAsset, deltaTime: Float) {
        val animator = asset.animator
        if (animator != null && animator.animationCount > 0) {
            animator.applyAnimation(0, deltaTime)
            animator.updateBoneMatrices()
        }
    }
    
    /**
     * Check if resource loading is done
     */
    fun isAssetReady(asset: FilamentAsset): Boolean {
        return resourceLoader.asyncGetLoadProgress() >= 1.0f
    }
    
    /**
     * Cleanup loader resources
     */
    fun destroy() {
        Log.i(TAG, "Destroying glTF loader...")
        
        // Destroy all loaded assets
        loadedAssets.values.forEach { asset ->
            try {
                assetLoader.destroyAsset(asset)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying asset", e)
            }
        }
        loadedAssets.clear()
        
        // Cleanup loaders
        resourceLoader.evictResourceData()
        assetLoader.destroy()
        materialProvider.destroyMaterials()
        materialProvider.destroy()
        
        Log.i(TAG, "glTF loader destroyed")
    }
}
