package com.linkpoint.animesh

import android.util.Log
import com.linkpoint.assets.AnimationManager
import com.linkpoint.assets.MeshManager
import com.linkpoint.avatar.AvatarAnimator
import com.linkpoint.avatar.AvatarSkeleton
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Animesh Manager - Handles animated mesh objects in Second Life.
 * 
 * Animesh (animated mesh) was introduced in 2018 and allows mesh objects
 * to have their own skeleton and play animations independently.
 * 
 * This is different from rigged mesh attachments which use the avatar's skeleton.
 * Animesh objects have their own skeleton and can be:
 * - Rezzed in-world as NPCs/pets
 * - Attached to avatars as animated attachments (tails, wings, etc.)
 * 
 * Reference: https://wiki.secondlife.com/wiki/Animesh
 */
class AnimeshManager(
    private val meshManager: MeshManager,
    private val animationManager: AnimationManager
) {
    companion object {
        private const val TAG = "AnimeshManager"
        
        // Object flags for animesh detection
        const val FLAG_ANIMESH = 0x00080000 // Animesh flag in ObjectUpdate
        const val FLAG_ANIMATED_MESH = 0x00100000
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Animesh objects in the scene
    private val animeshObjects = ConcurrentHashMap<UUID, AnimeshObject>()
    
    /**
     * Check if an object is animesh based on its flags.
     */
    fun isAnimesh(objectFlags: Int): Boolean {
        return (objectFlags and FLAG_ANIMESH) != 0 || (objectFlags and FLAG_ANIMATED_MESH) != 0
    }
    
    /**
     * Create or update an animesh object.
     */
    fun updateAnimeshObject(
        objectId: UUID,
        localId: Int,
        position: LLVector3,
        rotation: LLQuaternion,
        scale: LLVector3,
        meshAssetId: UUID?,
        animationAssetId: UUID?
    ) {
        val animesh = animeshObjects.getOrPut(objectId) {
            createAnimeshObject(objectId, localId)
        }
        
        animesh.position = position
        animesh.rotation = rotation
        animesh.scale = scale
        animesh.lastUpdate = System.currentTimeMillis()
        
        // Load mesh if changed
        if (meshAssetId != null && animesh.meshAssetId != meshAssetId) {
            animesh.meshAssetId = meshAssetId
            scope.launch {
                loadAnimeshMesh(animesh, meshAssetId)
            }
        }
        
        // Start animation if provided
        if (animationAssetId != null) {
            scope.launch {
                playAnimeshAnimation(animesh, animationAssetId)
            }
        }
    }
    
    /**
     * Handle object update that may be animesh.
     */
    fun handleObjectUpdate(update: ObjectUpdateData) {
        if (isAnimesh(update.updateFlags)) {
            Log.d(TAG, "Animesh object detected: ${update.fullId}")
            updateAnimeshObject(
                objectId = update.fullId,
                localId = update.localId,
                position = update.position,
                rotation = update.rotation,
                scale = update.scale,
                meshAssetId = null, // TODO: Extract mesh ID from update data
                animationAssetId = null // Animation comes separately
            )
        }
    }
    
    /**
     * Handle animation update for animesh.
     */
    fun handleAnimeshAnimation(objectId: UUID, animationId: UUID, start: Boolean) {
        val animesh = animeshObjects[objectId] ?: return
        
        if (start) {
            scope.launch {
                playAnimeshAnimation(animesh, animationId)
            }
        } else {
            animesh.animator?.stopAnimation(animationId)
        }
    }
    
    /**
     * Create a new animesh object.
     */
    private fun createAnimeshObject(objectId: UUID, localId: Int): AnimeshObject {
        // Create skeleton for animesh (same as avatar skeleton)
        val skeleton = AvatarSkeleton(null)
        val animator = AvatarAnimator(skeleton, animationManager)
        
        return AnimeshObject(
            objectId = objectId,
            localId = localId,
            skeleton = skeleton,
            animator = animator
        )
    }
    
    /**
     * Load the mesh asset for an animesh object.
     */
    private suspend fun loadAnimeshMesh(animesh: AnimeshObject, meshAssetId: UUID) {
        try {
            val meshData = meshManager.getMesh(meshAssetId)
            if (meshData != null) {
                animesh.meshData = meshData
                // Parse skinning data from mesh
                if (meshData.hasSkinData) {
                    parseSkinningData(animesh, meshData)
                }
                Log.d(TAG, "Loaded mesh for animesh ${animesh.objectId}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load animesh mesh", e)
        }
    }
    
    /**
     * Play an animation on an animesh object.
     */
    private suspend fun playAnimeshAnimation(animesh: AnimeshObject, animationId: UUID) {
        try {
            animesh.animator?.startAnimation(animationId, loop = true)
            Log.d(TAG, "Playing animation $animationId on animesh ${animesh.objectId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play animesh animation", e)
        }
    }
    
    /**
     * Parse skinning/rigging data from mesh.
     */
    private fun parseSkinningData(animesh: AnimeshObject, meshData: com.linkpoint.assets.MeshData) {
        // Mesh skin data contains:
        // - Joint names (bones)
        // - Bind matrices
        // - Vertex weights
        // This is used to animate the mesh vertices based on skeleton pose
        
        if (meshData.skinJointNames.isNotEmpty()) {
            animesh.skeleton?.let { skeleton ->
                for (jointName in meshData.skinJointNames) {
                    // Map mesh joints to skeleton bones
                    skeleton.getBone(jointName)
                }
            }
        }
    }
    
    /**
     * Update all animesh objects (called every frame).
     */
    fun update(deltaTime: Float) {
        for (animesh in animeshObjects.values) {
            animesh.animator?.update(deltaTime)
            animesh.skeleton?.updateBoneMatrices()
        }
    }
    
    /**
     * Get an animesh object by ID.
     */
    fun getAnimesh(objectId: UUID): AnimeshObject? = animeshObjects[objectId]
    
    /**
     * Get all animesh objects.
     */
    fun getAllAnimesh(): Collection<AnimeshObject> = animeshObjects.values
    
    /**
     * Remove an animesh object.
     */
    fun removeAnimesh(objectId: UUID) {
        animeshObjects.remove(objectId)?.let { animesh ->
            animesh.animator?.stopAll()
        }
    }
    
    fun shutdown() {
        scope.cancel()
        animeshObjects.values.forEach { it.animator?.stopAll() }
        animeshObjects.clear()
    }
}

/**
 * Represents an animesh object in the scene.
 */
data class AnimeshObject(
    val objectId: UUID,
    val localId: Int,
    val skeleton: AvatarSkeleton?,
    val animator: AvatarAnimator?
) {
    var position: LLVector3 = LLVector3.zero()
    var rotation: LLQuaternion = LLQuaternion.identity()
    var scale: LLVector3 = LLVector3(1f, 1f, 1f)
    var meshAssetId: UUID? = null
    var meshData: com.linkpoint.assets.MeshData? = null
    var lastUpdate: Long = 0
    
    // Current animations playing on this animesh
    val playingAnimations = mutableSetOf<UUID>()
}
