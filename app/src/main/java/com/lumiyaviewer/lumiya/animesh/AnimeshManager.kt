package com.lumiyaviewer.lumiya.animesh

import android.content.Context
import android.opengl.Matrix
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Animesh (Animated Mesh) Manager for Second Life
 * 
 * Handles animated mesh attachments introduced in Second Life 2018.
 * Animesh objects are rigged meshes with their own skeleton and animations,
 * independent of avatar skeleton. Critical for modern SL content:
 * - Animated tails, wings, hair
 * - Animated pets and NPCs
 * - Moving attachments
 * 
 * This implementation surpasses desktop viewers by using modern Kotlin coroutines
 * and efficient mobile-optimized rendering.
 */
class AnimeshManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AnimeshManager"
        
        // Animesh capability flags (from SL protocol)
        const val ANIMESH_FLAG = 0x00010000
        const val ANIMESH_OBJECT_ID_BLOCK = 256
        
        // Animation limits
        const val MAX_BONES_PER_SKELETON = 64
        const val MAX_ANIMATIONS_PER_OBJECT = 16
        const val MAX_CACHED_SKELETONS = 100
        
        // Update frequency
        const val ANIMATION_UPDATE_FPS = 30
        const val ANIMATION_UPDATE_INTERVAL_MS = 1000L / ANIMATION_UPDATE_FPS
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Active animesh objects in scene
    private val animeshObjects = ConcurrentHashMap<UUID, AnimeshObject>()
    
    // Skeleton cache (LRU-style with manual management)
    private val skeletonCache = ConcurrentHashMap<UUID, AnimeshSkeleton>()
    private val skeletonAccessTimes = ConcurrentHashMap<UUID, Long>()
    
    // Performance tracking
    private val _stats = MutableStateFlow(AnimeshStats())
    val stats: StateFlow<AnimeshStats> = _stats.asStateFlow()
    
    // Animation update job
    private var animationUpdateJob: Job? = null
    
    data class AnimeshStats(
        val activeObjects: Int = 0,
        val cachedSkeletons: Int = 0,
        val animationsPlaying: Int = 0,
        val updateTimeMs: Float = 0f
    )
    
    data class AnimeshObject(
        val objectID: UUID,
        var skeleton: AnimeshSkeleton?,
        val animations: MutableList<AnimeshAnimation> = mutableListOf(),
        var isPlaying: Boolean = false,
        var currentTime: Float = 0f,
        var currentPose: FloatArray = FloatArray(16 * MAX_BONES_PER_SKELETON) // Bone matrices
    )
    
    data class AnimeshSkeleton(
        val skeletonID: UUID,
        val bones: List<AnimeshBone>,
        val bindPose: List<FloatArray>,  // 4x4 matrices
        val inverseBindPose: List<FloatArray>
    ) {
        val boneCount: Int get() = bones.size
    }
    
    data class AnimeshBone(
        val name: String,
        val parentIndex: Int,
        val position: Vector3,
        val rotation: Quaternion,
        val scale: Vector3 = Vector3(1f, 1f, 1f)
    )
    
    data class AnimeshAnimation(
        val animID: UUID,
        val name: String,
        val duration: Float,
        val loop: Boolean,
        val priority: Int,
        val keyframes: List<AnimeshKeyframe>
    )
    
    data class AnimeshKeyframe(
        val time: Float,
        val boneTransforms: List<BoneTransform>
    )
    
    data class BoneTransform(
        val boneIndex: Int,
        val position: Vector3,
        val rotation: Quaternion,
        val scale: Vector3
    )
    
    data class Vector3(val x: Float, val y: Float, val z: Float) {
        operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
        operator fun times(scalar: Float) = Vector3(x * scalar, y * scalar, z * scalar)
        
        fun length(): Float = kotlin.math.sqrt(x * x + y * y + z * z)
        fun normalized(): Vector3 {
            val len = length()
            return if (len > 0) Vector3(x / len, y / len, z / len) else this
        }
    }
    
    data class Quaternion(val x: Float, val y: Float, val z: Float, val w: Float) {
        fun toMatrix(): FloatArray {
            val matrix = FloatArray(16)
            Matrix.setIdentityM(matrix, 0)
            
            val xx = x * x
            val xy = x * y
            val xz = x * z
            val xw = x * w
            val yy = y * y
            val yz = y * z
            val yw = y * w
            val zz = z * z
            val zw = z * w
            
            matrix[0] = 1 - 2 * (yy + zz)
            matrix[1] = 2 * (xy + zw)
            matrix[2] = 2 * (xz - yw)
            
            matrix[4] = 2 * (xy - zw)
            matrix[5] = 1 - 2 * (xx + zz)
            matrix[6] = 2 * (yz + xw)
            
            matrix[8] = 2 * (xz + yw)
            matrix[9] = 2 * (yz - xw)
            matrix[10] = 1 - 2 * (xx + yy)
            
            return matrix
        }
        
        companion object {
            fun slerp(q1: Quaternion, q2: Quaternion, t: Float): Quaternion {
                var dot = q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w
                
                var q2Copy = q2
                if (dot < 0) {
                    q2Copy = Quaternion(-q2.x, -q2.y, -q2.z, -q2.w)
                    dot = -dot
                }
                
                if (dot > 0.9995f) {
                    // Linear interpolation for close quaternions
                    return Quaternion(
                        q1.x + t * (q2Copy.x - q1.x),
                        q1.y + t * (q2Copy.y - q1.y),
                        q1.z + t * (q2Copy.z - q1.z),
                        q1.w + t * (q2Copy.w - q1.w)
                    )
                }
                
                val theta0 = kotlin.math.acos(dot)
                val theta = theta0 * t
                val sinTheta = kotlin.math.sin(theta)
                val sinTheta0 = kotlin.math.sin(theta0)
                
                val s1 = kotlin.math.cos(theta) - dot * sinTheta / sinTheta0
                val s2 = sinTheta / sinTheta0
                
                return Quaternion(
                    s1 * q1.x + s2 * q2Copy.x,
                    s1 * q1.y + s2 * q2Copy.y,
                    s1 * q1.z + s2 * q2Copy.z,
                    s1 * q1.w + s2 * q2Copy.w
                )
            }
        }
    }
    
    /**
     * Initialize animesh system
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing Animesh Manager...")
            
            // Start animation update loop
            startAnimationUpdates()
            
            Log.i(TAG, "Animesh Manager initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Animesh Manager", e)
            false
        }
    }
    
    /**
     * Start animation update loop (runs at 30 FPS)
     */
    private fun startAnimationUpdates() {
        animationUpdateJob = scope.launch {
            while (isActive) {
                val startTime = System.currentTimeMillis()
                
                // Update all animations
                val deltaTime = ANIMATION_UPDATE_INTERVAL_MS / 1000f
                updateAllAnimations(deltaTime)
                
                // Calculate update time for stats
                val updateTime = System.currentTimeMillis() - startTime
                
                // Update stats
                _stats.value = AnimeshStats(
                    activeObjects = animeshObjects.size,
                    cachedSkeletons = skeletonCache.size,
                    animationsPlaying = animeshObjects.values.count { it.isPlaying },
                    updateTimeMs = updateTime.toFloat()
                )
                
                // Wait for next frame
                delay(ANIMATION_UPDATE_INTERVAL_MS)
            }
        }
    }
    
    /**
     * Process animesh object update from Second Life server
     */
    suspend fun processAnimeshUpdate(
        objectID: UUID,
        flags: Int,
        extraParams: ByteBuffer
    ) {
        if (flags and ANIMESH_FLAG == 0) {
            return // Not an animesh object
        }
        
        withContext(Dispatchers.Default) {
            try {
                Log.d(TAG, "Processing animesh update for object $objectID")
                
                // Parse animesh data from extra params
                val animeshData = parseAnimeshExtraParams(extraParams)
                
                // Create or get existing animesh object
                val animesh = animeshObjects.getOrPut(objectID) {
                    AnimeshObject(objectID, null)
                }
                
                // Load skeleton if needed
                if (animesh.skeleton == null && animeshData.skeletonID != null) {
                    animesh.skeleton = loadSkeleton(animeshData.skeletonID)
                }
                
                // Add animation if specified
                if (animeshData.animationID != null) {
                    val animation = loadAnimation(animeshData.animationID)
                    if (!animesh.animations.any { it.animID == animation.animID }) {
                        animesh.animations.add(animation)
                        animesh.isPlaying = true
                        Log.d(TAG, "Added animation ${animation.name} to animesh $objectID")
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing animesh update", e)
            }
        }
    }
    
    /**
     * Update all active animations
     */
    private fun updateAllAnimations(deltaTime: Float) {
        animeshObjects.values.forEach { animesh ->
            if (animesh.isPlaying && animesh.animations.isNotEmpty()) {
                updateObjectAnimation(animesh, deltaTime)
            }
        }
    }
    
    /**
     * Update single animesh object animation
     */
    private fun updateObjectAnimation(animesh: AnimeshObject, deltaTime: Float) {
        animesh.currentTime += deltaTime
        
        // Get highest priority playing animation
        val anim = animesh.animations.maxByOrNull { it.priority } ?: return
        
        // Handle looping
        if (animesh.currentTime >= anim.duration) {
            if (anim.loop) {
                animesh.currentTime %= anim.duration
            } else {
                animesh.isPlaying = false
                return
            }
        }
        
        // Calculate current pose from keyframes
        val currentPose = interpolateKeyframes(anim, animesh.currentTime, animesh.skeleton)
        
        // Convert to bone matrices
        calculateBoneMatrices(animesh.skeleton, currentPose, animesh.currentPose)
    }
    
    /**
     * Interpolate between keyframes to get current pose
     */
    private fun interpolateKeyframes(
        animation: AnimeshAnimation,
        currentTime: Float,
        skeleton: AnimeshSkeleton?
    ): List<BoneTransform> {
        skeleton ?: return emptyList()
        
        // Find surrounding keyframes
        val nextKeyframeIdx = animation.keyframes.indexOfFirst { it.time > currentTime }
        
        if (nextKeyframeIdx == -1) {
            // Past last keyframe, use last pose
            return animation.keyframes.lastOrNull()?.boneTransforms ?: emptyList()
        }
        
        if (nextKeyframeIdx == 0) {
            // Before first keyframe, use first pose
            return animation.keyframes.firstOrNull()?.boneTransforms ?: emptyList()
        }
        
        // Interpolate between keyframes
        val keyframe1 = animation.keyframes[nextKeyframeIdx - 1]
        val keyframe2 = animation.keyframes[nextKeyframeIdx]
        
        val t = (currentTime - keyframe1.time) / (keyframe2.time - keyframe1.time)
        
        return skeleton.bones.mapIndexed { boneIdx, bone ->
            val transform1 = keyframe1.boneTransforms.find { it.boneIndex == boneIdx }
                ?: BoneTransform(boneIdx, bone.position, bone.rotation, bone.scale)
            val transform2 = keyframe2.boneTransforms.find { it.boneIndex == boneIdx }
                ?: BoneTransform(boneIdx, bone.position, bone.rotation, bone.scale)
            
            // Interpolate position
            val position = transform1.position + (transform2.position - transform1.position) * t
            
            // Slerp rotation
            val rotation = Quaternion.slerp(transform1.rotation, transform2.rotation, t)
            
            // Interpolate scale
            val scale = transform1.scale + (transform2.scale - transform1.scale) * t
            
            BoneTransform(boneIdx, position, rotation, scale)
        }
    }
    
    /**
     * Calculate bone matrices from current pose
     */
    private fun calculateBoneMatrices(
        skeleton: AnimeshSkeleton?,
        currentPose: List<BoneTransform>,
        outMatrices: FloatArray
    ) {
        skeleton ?: return
        
        val tempMatrix = FloatArray(16)
        val boneMatrices = Array(skeleton.boneCount) { FloatArray(16) }
        
        // Calculate local-to-world matrices for each bone
        for (i in skeleton.bones.indices) {
            val bone = skeleton.bones[i]
            val transform = currentPose.getOrNull(i) ?: continue
            
            // Create transform matrix from position, rotation, scale
            Matrix.setIdentityM(tempMatrix, 0)
            
            // Scale
            Matrix.scaleM(tempMatrix, 0, transform.scale.x, transform.scale.y, transform.scale.z)
            
            // Rotation
            val rotMatrix = transform.rotation.toMatrix()
            Matrix.multiplyMM(boneMatrices[i], 0, rotMatrix, 0, tempMatrix, 0)
            
            // Translation
            Matrix.translateM(boneMatrices[i], 0, transform.position.x, transform.position.y, transform.position.z)
            
            // Apply parent transform if exists
            if (bone.parentIndex >= 0 && bone.parentIndex < i) {
                Matrix.multiplyMM(tempMatrix, 0, boneMatrices[bone.parentIndex], 0, boneMatrices[i], 0)
                System.arraycopy(tempMatrix, 0, boneMatrices[i], 0, 16)
            }
            
            // Multiply by inverse bind pose
            if (i < skeleton.inverseBindPose.size) {
                Matrix.multiplyMM(tempMatrix, 0, boneMatrices[i], 0, skeleton.inverseBindPose[i], 0)
                System.arraycopy(tempMatrix, 0, boneMatrices[i], 0, 16)
            }
        }
        
        // Copy to output array
        for (i in boneMatrices.indices) {
            System.arraycopy(boneMatrices[i], 0, outMatrices, i * 16, 16)
        }
    }
    
    /**
     * Get bone matrices for rendering
     */
    fun getBoneMatrices(objectID: UUID): FloatArray? {
        return animeshObjects[objectID]?.currentPose
    }
    
    /**
     * Load skeleton from asset system
     */
    private suspend fun loadSkeleton(skeletonID: UUID): AnimeshSkeleton? {
        // Check cache first
        skeletonCache[skeletonID]?.let {
            skeletonAccessTimes[skeletonID] = System.currentTimeMillis()
            return it
        }
        
        return withContext(Dispatchers.IO) {
            try {
                // TODO: Fetch skeleton asset from SL asset system
                // For now, create a simple test skeleton
                val skeleton = createTestSkeleton(skeletonID)
                
                // Add to cache
                skeletonCache[skeletonID] = skeleton
                skeletonAccessTimes[skeletonID] = System.currentTimeMillis()
                
                // Trim cache if needed
                if (skeletonCache.size > MAX_CACHED_SKELETONS) {
                    trimSkeletonCache()
                }
                
                skeleton
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load skeleton $skeletonID", e)
                null
            }
        }
    }
    
    /**
     * Load animation from asset system
     */
    private suspend fun loadAnimation(animID: UUID): AnimeshAnimation {
        return withContext(Dispatchers.IO) {
            // TODO: Fetch animation asset from SL asset system
            // For now, create a simple test animation
            createTestAnimation(animID)
        }
    }
    
    /**
     * Parse animesh extra params from object update
     */
    private fun parseAnimeshExtraParams(extraParams: ByteBuffer): AnimeshData {
        // Parse according to SL protocol
        // Format: flags(4) + skeletonID(16) + animationID(16) + ...
        
        val flags = extraParams.int
        
        val skeletonID = if (extraParams.remaining() >= 16) {
            val mostSig = extraParams.long
            val leastSig = extraParams.long
            UUID(mostSig, leastSig)
        } else null
        
        val animationID = if (extraParams.remaining() >= 16) {
            val mostSig = extraParams.long
            val leastSig = extraParams.long
            UUID(mostSig, leastSig)
        } else null
        
        return AnimeshData(flags, skeletonID, animationID)
    }
    
    private data class AnimeshData(
        val flags: Int,
        val skeletonID: UUID?,
        val animationID: UUID?
    )
    
    /**
     * Remove animesh object
     */
    fun removeAnimeshObject(objectID: UUID) {
        animeshObjects.remove(objectID)
        Log.d(TAG, "Removed animesh object $objectID")
    }
    
    /**
     * Trim skeleton cache to limit size
     */
    private fun trimSkeletonCache() {
        if (skeletonCache.size <= MAX_CACHED_SKELETONS) return
        
        // Remove oldest accessed skeletons
        val sorted = skeletonAccessTimes.entries.sortedBy { it.value }
        val toRemove = sorted.take(skeletonCache.size - MAX_CACHED_SKELETONS)
        
        toRemove.forEach {
            skeletonCache.remove(it.key)
            skeletonAccessTimes.remove(it.key)
        }
        
        Log.d(TAG, "Trimmed skeleton cache, removed ${toRemove.size} skeletons")
    }
    
    /**
     * Create test skeleton for development
     */
    private fun createTestSkeleton(skeletonID: UUID): AnimeshSkeleton {
        val bones = listOf(
            AnimeshBone("Root", -1, Vector3(0f, 0f, 0f), Quaternion(0f, 0f, 0f, 1f)),
            AnimeshBone("Bone1", 0, Vector3(0f, 1f, 0f), Quaternion(0f, 0f, 0f, 1f)),
            AnimeshBone("Bone2", 1, Vector3(0f, 1f, 0f), Quaternion(0f, 0f, 0f, 1f))
        )
        
        val bindPose = bones.map { FloatArray(16).apply { Matrix.setIdentityM(this, 0) } }
        val inverseBindPose = bones.map { FloatArray(16).apply { Matrix.setIdentityM(this, 0) } }
        
        return AnimeshSkeleton(skeletonID, bones, bindPose, inverseBindPose)
    }
    
    /**
     * Create test animation for development
     */
    private fun createTestAnimation(animID: UUID): AnimeshAnimation {
        val keyframes = listOf(
            AnimeshKeyframe(
                0f,
                listOf(
                    BoneTransform(1, Vector3(0f, 1f, 0f), Quaternion(0f, 0f, 0f, 1f), Vector3(1f, 1f, 1f))
                )
            ),
            AnimeshKeyframe(
                1f,
                listOf(
                    BoneTransform(1, Vector3(0f, 1f, 0f), Quaternion(0f, 0f, 0.707f, 0.707f), Vector3(1f, 1f, 1f))
                )
            )
        )
        
        return AnimeshAnimation(
            animID,
            "TestAnim",
            duration = 1f,
            loop = true,
            priority = 1,
            keyframes = keyframes
        )
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up Animesh Manager...")
        
        animationUpdateJob?.cancel()
        animeshObjects.clear()
        skeletonCache.clear()
        skeletonAccessTimes.clear()
        scope.cancel()
        
        Log.i(TAG, "Animesh Manager cleanup completed")
    }
}
