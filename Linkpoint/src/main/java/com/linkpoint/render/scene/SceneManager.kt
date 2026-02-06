package com.linkpoint.render.scene

import android.util.Log
import com.google.android.filament.*
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages the 3D scene graph for the world
 */
class SceneManager(private val engine: Engine, private val scene: Scene) {
    
    companion object {
        private const val TAG = "SceneManager"
    }
    
    // Object tracking
    private val sceneObjects = ConcurrentHashMap<UUID, SceneObject>()
    private val avatars = ConcurrentHashMap<UUID, AvatarObject>()
    
    // Entity manager
    private val entityManager = EntityManager.get()
    
    // Current camera position
    private var cameraPosition = LLVector3(128f, 128f, 30f)
    private var cameraTarget = LLVector3(128f, 128f, 0f)
    
    /**
     * Add or update an object in the scene
     */
    fun updateObject(
        objectId: UUID,
        localId: Int,
        position: LLVector3,
        rotation: LLQuaternion,
        scale: LLVector3,
        parentId: UUID? = null
    ) {
        val existing = sceneObjects[objectId]
        
        if (existing != null) {
            // Update existing object
            existing.position = position
            existing.rotation = rotation
            existing.scale = scale
            updateTransform(existing)
        } else {
            // Create new object
            val entity = entityManager.create()
            val obj = SceneObject(
                uuid = objectId,
                localId = localId,
                entity = entity,
                position = position,
                rotation = rotation,
                scale = scale,
                parentId = parentId
            )
            sceneObjects[objectId] = obj
            
            // Add to scene (will be renderable once mesh is loaded)
            scene.addEntity(entity)
            Log.d(TAG, "Added object: $objectId at $position")
        }
    }
    
    /**
     * Remove an object from the scene
     */
    fun removeObject(objectId: UUID) {
        sceneObjects.remove(objectId)?.let { obj ->
            scene.removeEntity(obj.entity)
            entityManager.destroy(obj.entity)
            Log.d(TAG, "Removed object: $objectId")
        }
    }
    
    /**
     * Add or update an avatar in the scene
     */
    fun updateAvatar(
        agentId: UUID,
        position: LLVector3,
        rotation: LLQuaternion,
        animations: List<UUID> = emptyList()
    ) {
        val existing = avatars[agentId]
        
        if (existing != null) {
            existing.position = position
            existing.rotation = rotation
            existing.animations = animations
            updateTransform(existing)
        } else {
            val entity = entityManager.create()
            val avatar = AvatarObject(
                agentId = agentId,
                entity = entity,
                position = position,
                rotation = rotation,
                animations = animations
            )
            avatars[agentId] = avatar
            scene.addEntity(entity)
            Log.d(TAG, "Added avatar: $agentId at $position")
        }
    }
    
    /**
     * Remove an avatar from the scene
     */
    fun removeAvatar(agentId: UUID) {
        avatars.remove(agentId)?.let { avatar ->
            scene.removeEntity(avatar.entity)
            entityManager.destroy(avatar.entity)
            Log.d(TAG, "Removed avatar: $agentId")
        }
    }
    
    /**
     * Set object renderable (mesh loaded)
     */
    fun setObjectRenderable(objectId: UUID, renderable: Int) {
        sceneObjects[objectId]?.let { obj ->
            obj.renderable = renderable
            val transformManager = engine.transformManager
            val instance = transformManager.getInstance(obj.entity)
            if (instance == 0) {
                transformManager.create(obj.entity)
            }
            updateTransform(obj)
        }
    }
    
    /**
     * Update camera position and target
     */
    fun updateCamera(position: LLVector3, target: LLVector3) {
        cameraPosition = position
        cameraTarget = target
    }
    
    /**
     * Get visible objects within range of camera
     */
    fun getVisibleObjects(maxDistance: Float = 256f): List<SceneObject> {
        return sceneObjects.values.filter { obj ->
            obj.position.distance(cameraPosition) <= maxDistance
        }.sortedBy { it.position.distance(cameraPosition) }
    }
    
    /**
     * Get visible avatars within range
     */
    fun getVisibleAvatars(maxDistance: Float = 256f): List<AvatarObject> {
        return avatars.values.filter { avatar ->
            avatar.position.distance(cameraPosition) <= maxDistance
        }.sortedBy { it.position.distance(cameraPosition) }
    }
    
    /**
     * Find object by local ID
     */
    fun findByLocalId(localId: Int): SceneObject? {
        return sceneObjects.values.find { it.localId == localId }
    }
    
    /**
     * Get object by UUID
     */
    fun getObject(objectId: UUID): SceneObject? = sceneObjects[objectId]
    
    /**
     * Get avatar by UUID
     */
    fun getAvatar(agentId: UUID): AvatarObject? = avatars[agentId]
    
    /**
     * Get all objects
     */
    fun getAllObjects(): Collection<SceneObject> = sceneObjects.values
    
    /**
     * Get all avatars
     */
    fun getAllAvatars(): Collection<AvatarObject> = avatars.values
    
    /**
     * Clear all objects and avatars
     */
    fun clear() {
        sceneObjects.values.forEach { obj ->
            scene.removeEntity(obj.entity)
            entityManager.destroy(obj.entity)
        }
        sceneObjects.clear()
        
        avatars.values.forEach { avatar ->
            scene.removeEntity(avatar.entity)
            entityManager.destroy(avatar.entity)
        }
        avatars.clear()
        
        Log.i(TAG, "Scene cleared")
    }
    
    private fun updateTransform(obj: SceneObject) {
        val transformManager = engine.transformManager
        val instance = transformManager.getInstance(obj.entity)
        if (instance != 0) {
            val matrix = FloatArray(16)
            buildTransformMatrix(obj.position, obj.rotation, obj.scale, matrix)
            transformManager.setTransform(instance, matrix)
        }
    }
    
    private fun updateTransform(avatar: AvatarObject) {
        val transformManager = engine.transformManager
        val instance = transformManager.getInstance(avatar.entity)
        if (instance != 0) {
            val matrix = FloatArray(16)
            buildTransformMatrix(avatar.position, avatar.rotation, LLVector3.one(), matrix)
            transformManager.setTransform(instance, matrix)
        }
    }
    
    private fun buildTransformMatrix(
        position: LLVector3,
        rotation: LLQuaternion,
        scale: LLVector3,
        out: FloatArray
    ) {
        // Build rotation matrix from quaternion
        val x = rotation.x
        val y = rotation.y
        val z = rotation.z
        val w = rotation.w
        
        val x2 = x + x
        val y2 = y + y
        val z2 = z + z
        val xx = x * x2
        val xy = x * y2
        val xz = x * z2
        val yy = y * y2
        val yz = y * z2
        val zz = z * z2
        val wx = w * x2
        val wy = w * y2
        val wz = w * z2
        
        out[0] = (1 - (yy + zz)) * scale.x
        out[1] = (xy + wz) * scale.x
        out[2] = (xz - wy) * scale.x
        out[3] = 0f
        
        out[4] = (xy - wz) * scale.y
        out[5] = (1 - (xx + zz)) * scale.y
        out[6] = (yz + wx) * scale.y
        out[7] = 0f
        
        out[8] = (xz + wy) * scale.z
        out[9] = (yz - wx) * scale.z
        out[10] = (1 - (xx + yy)) * scale.z
        out[11] = 0f
        
        out[12] = position.x
        out[13] = position.y
        out[14] = position.z
        out[15] = 1f
    }
}

/**
 * Represents an object in the scene
 */
data class SceneObject(
    val uuid: UUID,
    val localId: Int,
    val entity: Int,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var parentId: UUID? = null,
    var renderable: Int = 0,
    var textureIds: List<UUID> = emptyList(),
    var isPhantom: Boolean = false,
    var isTemporary: Boolean = false
)

/**
 * Represents an avatar in the scene
 */
data class AvatarObject(
    val agentId: UUID,
    val entity: Int,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var animations: List<UUID> = emptyList(),
    var displayName: String = "",
    var isTyping: Boolean = false,
    var isSitting: Boolean = false
)
