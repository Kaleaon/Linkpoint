package com.linkpoint.graphics.filament

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.gltfio.FilamentAsset
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.types.LLVector3
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
        val asset: FilamentAsset?,
        val avatarInfo: SLObjectAvatarInfo,
        var lastUpdate: Long = 0
    )
    
    // Avatar entities (UUID -> avatar data)
    private val avatars = ConcurrentHashMap<UUID, AvatarEntity>()
    
    // Default avatar mesh (loaded once, instanced for each avatar)
    private var defaultAvatarAsset: FilamentAsset? = null
    
    /**
     * Initialize avatar renderer
     */
    fun initialize() {
        // TODO: Load default avatar mesh from assets
        // For now, avatars will use simple geometry
        
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
            
            // For now, create a simple placeholder
            // TODO: Load actual avatar mesh (glTF) based on avatar shape
            @Entity val entity = EntityManager.get().create()
            
            // Create simple capsule/cube for avatar placeholder
            val position = avatarInfo.getPosition() ?: LLVector3(128f, 128f, 25f)
            
            // Set transform
            val transform = FloatArray(16)
            android.opengl.Matrix.setIdentityM(transform, 0)
            android.opengl.Matrix.translateM(transform, 0, position.x, position.y, position.z)
            
            val tcm = engine.transformManager
            tcm.setTransform(tcm.getInstance(entity), transform)
            
            // Add to scene
            scene.addEntity(entity)
            
            // Store avatar data
            avatars[uuid] = AvatarEntity(entity, null, avatarInfo, System.currentTimeMillis())
            
            Log.d(TAG, "Created avatar $uuid at ($position)")
            
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
        
        // Destroy default avatar asset
        defaultAvatarAsset?.let { gltfLoader.destroyAsset(it) }
        defaultAvatarAsset = null
        
        Log.i(TAG, "Avatar renderer destroyed")
    }
}
