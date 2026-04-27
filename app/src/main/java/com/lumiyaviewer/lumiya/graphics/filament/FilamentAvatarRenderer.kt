package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.gltfio.FilamentAsset
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * FilamentAvatarRenderer - Renders avatars using Filament
 * 
 * Handles:
 * - Avatar mesh rendering
 * - Avatar animations
 * - BakesOnMesh textures
 * - Avatar attachments
 * - Nametags
 */
class FilamentAvatarRenderer(
    private val context: Context,
    private val engine: Engine,
    private val scene: Scene,
    private val materialManager: FilamentMaterialManager,
    private val textureManager: FilamentTextureManager,
    private val gltfLoader: FilamentGltfLoader
) {
    companion object {
        private const val TAG = "FilamentAvatarRenderer"
        private const val MAX_AVATARS = 100
    }
    
    /**
     * Avatar entity data
     */
    private data class AvatarEntity(
        @Entity val rootEntity: Int,
        val vertexBuffer: VertexBuffer?,
        val indexBuffer: IndexBuffer?,
        val asset: FilamentAsset?,
        val avatarInfo: SLObjectAvatarInfo,
        var lastUpdate: Long = 0
    )
    
    // Avatar entities (UUID -> avatar data)
    private val avatars = ConcurrentHashMap<UUID, AvatarEntity>()
    
    // Avatar mesh loader
    private lateinit var avatarMeshLoader: FilamentAvatarMeshLoader
    
    // Default avatar mesh (loaded once, used for all avatars)
    private var defaultAvatarMesh: FilamentAvatarMeshLoader.AvatarMesh? = null
    
    /**
     * Initialize avatar renderer
     */
    fun initialize() {
        // Create avatar mesh loader
        avatarMeshLoader = FilamentAvatarMeshLoader(context, engine, gltfLoader)
        
        // Load default avatar mesh
        try {
            defaultAvatarMesh = avatarMeshLoader.loadDefaultAvatar()
            Log.i(TAG, "Default avatar mesh loaded")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load default avatar mesh", e)
        }
        
        Log.i(TAG, "Avatar renderer initialized")
    }
    
    /**
     * Create or update an avatar
     */
    fun updateAvatar(avatarInfo: SLObjectAvatarInfo) {
        val uuid = avatarInfo.uuid ?: return
        
        val existing = avatars[uuid]
        
        if (existing == null) {
            // Create new avatar
            createAvatar(avatarInfo)
        } else {
            // Update existing avatar
            updateAvatarTransform(existing, avatarInfo)
        }
    }
    
    /**
     * Create a new avatar entity
     */
    private fun createAvatar(avatarInfo: SLObjectAvatarInfo) {
        try {
            val uuid = avatarInfo.uuid ?: return
            
            // Get default avatar mesh
            val avatarMesh = defaultAvatarMesh
            if (avatarMesh == null) {
                Log.w(TAG, "No avatar mesh available")
                return
            }
            
            // Create entity
            @Entity val entity = EntityManager.get().create()
            
            // Get position
            val position = avatarInfo.getPosition() ?: LLVector3(128f, 128f, 25f)
            
            // Get material
            val material = materialManager.getMaterial(
                FilamentMaterialManager.MaterialType.PRIM_BASIC
            )
            
            // Build renderable with avatar mesh
            RenderableManager.Builder(1)
                .boundingBox(Box(
                    -0.5f, 0f, -0.5f,
                    0.5f, 2.0f, 0.5f
                ))
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES,
                    avatarMesh.vertexBuffer, avatarMesh.indexBuffer)
                .material(0, material.defaultInstance)
                .castShadows(true)
                .receiveShadows(true)
                .build(engine, entity)
            
            // Set transform
            val transform = FloatArray(16)
            android.opengl.Matrix.setIdentityM(transform, 0)
            android.opengl.Matrix.translateM(transform, 0, position.x, position.y, position.z)
            
            val tcm = engine.transformManager
            tcm.setTransform(tcm.getInstance(entity), transform)
            
            // Add to scene
            scene.addEntity(entity)
            
            // Store avatar data (don't own the buffers, they're shared)
            avatars[uuid] = AvatarEntity(entity, null, null, null, avatarInfo,
                System.currentTimeMillis())
            
            Log.d(TAG, "Created avatar $uuid with humanoid mesh at ($position)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating avatar", e)
        }
    }
    
    /**
     * Update avatar transform (position, rotation)
     */
    private fun updateAvatarTransform(avatarEntity: AvatarEntity, avatarInfo: SLObjectAvatarInfo) {
        try {
            val position = avatarInfo.getPosition() ?: return
            val rotation = avatarInfo.getRotation()
            
            // Update transform
            val transform = FloatArray(16)
            android.opengl.Matrix.setIdentityM(transform, 0)
            android.opengl.Matrix.translateM(transform, 0, position.x, position.y, position.z)
            
            // Apply rotation if available
            if (rotation != null) {
                // TODO: Convert quaternion to matrix rotation
            }
            
            val tcm = engine.transformManager
            val instance = tcm.getInstance(avatarEntity.rootEntity)
            if (instance.isValid) {
                tcm.setTransform(instance, transform)
            }
            
            avatarEntity.lastUpdate = System.currentTimeMillis()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating avatar transform", e)
        }
    }
    
    /**
     * Remove an avatar from the scene
     */
    fun removeAvatar(uuid: UUID) {
        val avatar = avatars.remove(uuid) ?: return
        
        try {
            // Remove from scene
            scene.removeEntity(avatar.rootEntity)
            
            // Destroy asset if exists
            avatar.asset?.let { gltfLoader.destroyAsset(it) }
            
            // Destroy buffers if owned by this avatar
            avatar.vertexBuffer?.let { engine.destroyVertexBuffer(it) }
            avatar.indexBuffer?.let { engine.destroyIndexBuffer(it) }
            
            // Destroy entity
            engine.destroyEntity(avatar.rootEntity)
            EntityManager.get().destroy(avatar.rootEntity)
            
            Log.d(TAG, "Removed avatar $uuid")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error removing avatar", e)
        }
    }
    
    /**
     * Clear all avatars
     */
    fun clearAll() {
        Log.i(TAG, "Clearing all avatars...")
        
        avatars.keys.toList().forEach { uuid ->
            removeAvatar(uuid)
        }
        
        Log.i(TAG, "All avatars cleared")
    }
    
    /**
     * Get avatar count
     */
    fun getAvatarCount(): Int = avatars.size
    
    /**
     * Cleanup avatar renderer
     */
    fun destroy() {
        clearAll()
        
        // Destroy avatar mesh loader
        if (::avatarMeshLoader.isInitialized) {
            avatarMeshLoader.destroy()
        }
        
        defaultAvatarMesh = null
        
        Log.i(TAG, "Avatar renderer destroyed")
    }
}
