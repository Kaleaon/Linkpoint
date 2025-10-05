package com.linkpoint.animesh

import android.util.Log
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Animesh Manager
 * Manages all animesh attachments and their animations
 * Implements Second Life Animesh protocol (2018+)
 */
class AnimeshManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    companion object {
        private const val TAG = "AnimeshManager"
        private const val UPDATE_INTERVAL_MS = 16L // ~60 FPS
    }
    
    // Active animesh objects
    private val animeshObjects = ConcurrentHashMap<UUID, AnimeshData>()
    
    // Animation cache
    private val animationCache = ConcurrentHashMap<UUID, AnimeshAnimation>()
    
    // Update job
    private var updateJob: Job? = null
    private var isRunning = false
    
    /**
     * Start animesh manager
     */
    fun start() {
        if (isRunning) return
        
        isRunning = true
        updateJob = scope.launch {
            var lastTime = System.currentTimeMillis()
            
            while (isActive && isRunning) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = (currentTime - lastTime) / 1000f
                lastTime = currentTime
                
                updateAllAnimesh(deltaTime)
                
                delay(UPDATE_INTERVAL_MS)
            }
        }
        
        Log.i(TAG, "Animesh manager started")
    }
    
    /**
     * Stop animesh manager
     */
    fun stop() {
        isRunning = false
        updateJob?.cancel()
        updateJob = null
        Log.i(TAG, "Animesh manager stopped")
    }
    
    /**
     * Update all animesh objects
     */
    private fun updateAllAnimesh(deltaTime: Float) {
        animeshObjects.values.forEach { animesh ->
            try {
                animesh.updateAnimations(deltaTime)
                
                // Apply animations to skeleton
                applyAnimationsToSkeleton(animesh)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error updating animesh ${animesh.objectId}", e)
            }
        }
    }
    
    /**
     * Apply animations to skeleton
     */
    private fun applyAnimationsToSkeleton(animesh: AnimeshData) {
        // Reset bones to bind pose
        animesh.skeleton.bones.forEach { it.resetToBindPose() }
        
        // Apply each active animation
        animesh.activeAnimations.forEach { animation ->
            animesh.skeleton.bones.forEach { bone ->
                animation.getBoneTransform(bone.boneId)?.let { (pos, rot) ->
                    bone.applyTransform(pos, rot)
                }
            }
        }
    }
    
    /**
     * Register animesh object
     */
    fun registerAnimesh(objectId: UUID, attachmentId: UUID, skeletonData: ByteBuffer) {
        try {
            val skeleton = AnimeshSkeleton.fromLLSD(skeletonData)
            val animesh = AnimeshData(
                attachmentId = attachmentId,
                objectId = objectId,
                skeleton = skeleton
            )
            
            animeshObjects[objectId] = animesh
            Log.i(TAG, "Registered animesh object: $objectId with ${skeleton.bones.size} bones")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register animesh object: $objectId", e)
        }
    }
    
    /**
     * Unregister animesh object
     */
    fun unregisterAnimesh(objectId: UUID) {
        animeshObjects.remove(objectId)
        Log.i(TAG, "Unregistered animesh object: $objectId")
    }
    
    /**
     * Add animation to animesh object
     */
    fun addAnimation(objectId: UUID, animationId: UUID, startNow: Boolean = true) {
        val animesh = animeshObjects[objectId]
        if (animesh == null) {
            Log.w(TAG, "Animesh object not found: $objectId")
            return
        }
        
        val animation = animationCache[animationId]
        if (animation == null) {
            Log.w(TAG, "Animation not found: $animationId")
            return
        }
        
        animesh.addAnimation(animation.copy(currentTime = 0f))
        Log.d(TAG, "Added animation $animationId to animesh $objectId")
    }
    
    /**
     * Remove animation from animesh object
     */
    fun removeAnimation(objectId: UUID, animationId: UUID) {
        val animesh = animeshObjects[objectId] ?: return
        animesh.activeAnimations.removeIf { it.animationId == animationId }
        Log.d(TAG, "Removed animation $animationId from animesh $objectId")
    }
    
    /**
     * Stop all animations on animesh object
     */
    fun stopAllAnimations(objectId: UUID) {
        animeshObjects[objectId]?.stopAllAnimations()
        Log.d(TAG, "Stopped all animations on animesh $objectId")
    }
    
    /**
     * Cache animation data
     */
    fun cacheAnimation(animationId: UUID, animation: AnimeshAnimation) {
        animationCache[animationId] = animation
        Log.d(TAG, "Cached animation: $animationId (${animation.name})")
    }
    
    /**
     * Load animation from asset data
     */
    suspend fun loadAnimation(animationId: UUID, assetData: ByteBuffer): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val animation = parseAnimationAsset(animationId, assetData)
                cacheAnimation(animationId, animation)
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load animation: $animationId", e)
                false
            }
        }
    }
    
    /**
     * Parse animation asset data
     */
    private fun parseAnimationAsset(animationId: UUID, data: ByteBuffer): AnimeshAnimation {
        // Parse BVH or SL animation format
        val version = data.getShort()
        val subVersion = data.getShort()
        val priority = data.getInt()
        val duration = data.getFloat()
        
        // Read emote name
        val nameLength = data.getInt()
        val nameBytes = ByteArray(nameLength)
        data.get(nameBytes)
        val name = String(nameBytes)
        
        // Read loop info
        val loopInPoint = data.getFloat()
        val loopOutPoint = data.getFloat()
        val loop = data.getInt() != 0
        
        // Read ease in/out
        val easeInDuration = data.getFloat()
        val easeOutDuration = data.getFloat()
        
        // Read hand pose
        val handPose = data.getInt()
        
        // Read number of joints
        val numJoints = data.getInt()
        
        val keyframes = mutableListOf<AnimationKeyframe>()
        
        // Read joint data
        for (i in 0 until numJoints) {
            // Read joint name
            val jointNameLength = data.getInt()
            val jointNameBytes = ByteArray(jointNameLength)
            data.get(jointNameBytes)
            
            // Read priority
            val jointPriority = data.getInt()
            
            // Read rotation keyframes
            val numRotKeys = data.getInt()
            for (k in 0 until numRotKeys) {
                val time = data.getShort().toFloat() / 65535f * duration
                val rotX = data.getShort().toFloat() / 32767f
                val rotY = data.getShort().toFloat() / 32767f
                val rotZ = data.getShort().toFloat() / 32767f
                
                keyframes.add(AnimationKeyframe(
                    boneId = i,
                    time = time,
                    position = Vector3(0f, 0f, 0f),
                    rotation = Quaternion(rotX, rotY, rotZ, 
                        kotlin.math.sqrt(1f - rotX*rotX - rotY*rotY - rotZ*rotZ))
                ))
            }
            
            // Read position keyframes
            val numPosKeys = data.getInt()
            for (k in 0 until numPosKeys) {
                val time = data.getShort().toFloat() / 65535f * duration
                val posX = data.getShort().toFloat() / 32767f
                val posY = data.getShort().toFloat() / 32767f
                val posZ = data.getShort().toFloat() / 32767f
                
                // Find or create keyframe at this time
                val existingKeyframe = keyframes.find { 
                    it.boneId == i && kotlin.math.abs(it.time - time) < 0.001f 
                }
                
                if (existingKeyframe != null) {
                    keyframes.remove(existingKeyframe)
                    keyframes.add(existingKeyframe.copy(position = Vector3(posX, posY, posZ)))
                } else {
                    keyframes.add(AnimationKeyframe(
                        boneId = i,
                        time = time,
                        position = Vector3(posX, posY, posZ),
                        rotation = Quaternion(0f, 0f, 0f, 1f)
                    ))
                }
            }
        }
        
        return AnimeshAnimation(
            animationId = animationId,
            name = name,
            duration = duration,
            loop = loop,
            priority = priority,
            keyframes = keyframes.sortedBy { it.time }
        )
    }
    
    /**
     * Get animesh data
     */
    fun getAnimesh(objectId: UUID): AnimeshData? {
        return animeshObjects[objectId]
    }
    
    /**
     * Get all animesh objects
     */
    fun getAllAnimesh(): List<AnimeshData> {
        return animeshObjects.values.toList()
    }
    
    /**
     * Get animation from cache
     */
    fun getAnimation(animationId: UUID): AnimeshAnimation? {
        return animationCache[animationId]
    }
    
    /**
     * Clear all data
     */
    fun clear() {
        animeshObjects.clear()
        animationCache.clear()
        Log.i(TAG, "Cleared all animesh data")
    }
    
    /**
     * Cleanup
     */
    fun cleanup() {
        stop()
        clear()
        scope.cancel()
        Log.i(TAG, "Animesh manager cleaned up")
    }
    
    /**
     * Get statistics
     */
    fun getStats(): AnimeshStats {
        return AnimeshStats(
            activeAnimeshCount = animeshObjects.size,
            cachedAnimationCount = animationCache.size,
            totalActiveAnimations = animeshObjects.values.sumOf { it.activeAnimations.size },
            isRunning = isRunning
        )
    }
}

/**
 * Animesh statistics
 */
data class AnimeshStats(
    val activeAnimeshCount: Int,
    val cachedAnimationCount: Int,
    val totalActiveAnimations: Int,
    val isRunning: Boolean
)