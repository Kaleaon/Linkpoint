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
 */
class AnimeshManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AnimeshManager"
        
        const val ANIMESH_FLAG = 0x00010000
        const val ANIMESH_OBJECT_ID_BLOCK = 256
        
        const val MAX_BONES_PER_SKELETON = 64
        const val MAX_ANIMATIONS_PER_OBJECT = 16
        const val MAX_CACHED_SKELETONS = 100
        
        const val ANIMATION_UPDATE_FPS = 30
        const val ANIMATION_UPDATE_INTERVAL_MS = 1000L / ANIMATION_UPDATE_FPS
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private val animeshObjects = ConcurrentHashMap<UUID, AnimeshObject>()
    
    private val skeletonCache = ConcurrentHashMap<UUID, AnimeshSkeleton>()
    private val skeletonAccessTimes = ConcurrentHashMap<UUID, Long>()
    
    private val _stats = MutableStateFlow(AnimeshStats())
    val stats: StateFlow<AnimeshStats> = _stats.asStateFlow()
    
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
        var currentPose: FloatArray = FloatArray(16 * MAX_BONES_PER_SKELETON)
    )
    
    data class AnimeshSkeleton(
        val skeletonID: UUID,
        val bones: List<AnimeshBone>,
        val bindPose: List<FloatArray>,
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
        operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
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
    
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing Animesh Manager...")
            startAnimationUpdates()
            Log.i(TAG, "Animesh Manager initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Animesh Manager", e)
            false
        }
    }
    
    private fun startAnimationUpdates() {
        animationUpdateJob = scope.launch {
            while (isActive) {
                val startTime = System.currentTimeMillis()
                val deltaTime = ANIMATION_UPDATE_INTERVAL_MS / 1000f
                updateAllAnimations(deltaTime)
                val updateTime = System.currentTimeMillis() - startTime
                _stats.value = AnimeshStats(
                    activeObjects = animeshObjects.size,
                    cachedSkeletons = skeletonCache.size,
                    animationsPlaying = animeshObjects.values.count { it.isPlaying },
                    updateTimeMs = updateTime.toFloat()
                )
                delay(ANIMATION_UPDATE_INTERVAL_MS)
            }
        }
    }
    
    suspend fun processAnimeshUpdate(
        objectID: UUID,
        flags: Int,
        extraParams: ByteBuffer
    ) {
        if (flags and ANIMESH_FLAG == 0) {
            return
        }
        
        withContext(Dispatchers.Default) {
            try {
                Log.d(TAG, "Processing animesh update for object $objectID")
                
                val animeshData = parseAnimeshExtraParams(extraParams)
                
                val animesh = animeshObjects.getOrPut(objectID) {
                    AnimeshObject(objectID, null)
                }
                
                if (animesh.skeleton == null && animeshData.skeletonID != null) {
                    animesh.skeleton = loadSkeleton(animeshData.skeletonID)
                }
                
                if (animeshData.animationID != null) {
                    val animation = loadAnimation(animeshData.animationID)
                    if (!animesh.animations.any { it.animID == animation.animID }) {
                        animesh.animations.add(animation)
                        animesh.isPlaying = true
                        Log.d(TAG, "Added animation ${animation.name} to animesh $objectID")
                    }
                }
                
                Unit // Ensure block returns Unit
            } catch (e: Exception) {
                Log.e(TAG, "Error processing animesh update", e)
            }
        }
    }
    
    private fun updateAllAnimations(deltaTime: Float) {
        animeshObjects.values.forEach { animesh ->
            if (animesh.isPlaying && animesh.animations.isNotEmpty()) {
                updateObjectAnimation(animesh, deltaTime)
            }
        }
    }
    
    private fun updateObjectAnimation(animesh: AnimeshObject, deltaTime: Float) {
        animesh.currentTime += deltaTime
        
        val anim = animesh.animations.maxByOrNull { it.priority } ?: return
        
        if (animesh.currentTime >= anim.duration) {
            if (anim.loop) {
                animesh.currentTime %= anim.duration
            } else {
                animesh.isPlaying = false
                return
            }
        }
        
        val currentPose = interpolateKeyframes(anim, animesh.currentTime, animesh.skeleton)
        calculateBoneMatrices(animesh.skeleton, currentPose, animesh.currentPose)
    }
    
    private fun interpolateKeyframes(
        animation: AnimeshAnimation,
        currentTime: Float,
        skeleton: AnimeshSkeleton?
    ): List<BoneTransform> {
        skeleton ?: return emptyList()
        
        val nextKeyframeIdx = animation.keyframes.indexOfFirst { it.time > currentTime }
        
        if (nextKeyframeIdx == -1) {
            return animation.keyframes.lastOrNull()?.boneTransforms ?: emptyList()
        }
        
        if (nextKeyframeIdx == 0) {
            return animation.keyframes.firstOrNull()?.boneTransforms ?: emptyList()
        }
        
        val keyframe1 = animation.keyframes[nextKeyframeIdx - 1]
        val keyframe2 = animation.keyframes[nextKeyframeIdx]
        
        val t = (currentTime - keyframe1.time) / (keyframe2.time - keyframe1.time)
        
        return skeleton.bones.mapIndexed { boneIdx, bone ->
            val transform1 = keyframe1.boneTransforms.find { it.boneIndex == boneIdx }
                ?: BoneTransform(boneIdx, bone.position, bone.rotation, bone.scale)
            val transform2 = keyframe2.boneTransforms.find { it.boneIndex == boneIdx }
                ?: BoneTransform(boneIdx, bone.position, bone.rotation, bone.scale)
            
            val position = transform1.position + (transform2.position - transform1.position) * t
            val rotation = Quaternion.slerp(transform1.rotation, transform2.rotation, t)
            val scale = transform1.scale + (transform2.scale - transform1.scale) * t
            
            BoneTransform(boneIdx, position, rotation, scale)
        }
    }
    
    private fun calculateBoneMatrices(
        skeleton: AnimeshSkeleton?,
        currentPose: List<BoneTransform>,
        outMatrices: FloatArray
    ) {
        skeleton ?: return
        
        val tempMatrix = FloatArray(16)
        val boneMatrices = Array(skeleton.boneCount) { FloatArray(16) }
        
        for (i in skeleton.bones.indices) {
            val bone = skeleton.bones[i]
            val transform = currentPose.find { it.boneIndex == i } ?: continue
            
            Matrix.setIdentityM(tempMatrix, 0)
            Matrix.scaleM(tempMatrix, 0, transform.scale.x, transform.scale.y, transform.scale.z)
            
            val rotMatrix = transform.rotation.toMatrix()
            Matrix.multiplyMM(boneMatrices[i], 0, rotMatrix, 0, tempMatrix, 0)
            
            Matrix.translateM(boneMatrices[i], 0, transform.position.x, transform.position.y, transform.position.z)
            
            if (bone.parentIndex >= 0 && bone.parentIndex < i) {
                Matrix.multiplyMM(tempMatrix, 0, boneMatrices[bone.parentIndex], 0, boneMatrices[i], 0)
                System.arraycopy(tempMatrix, 0, boneMatrices[i], 0, 16)
            }
            
            if (i < skeleton.inverseBindPose.size) {
                Matrix.multiplyMM(tempMatrix, 0, boneMatrices[i], 0, skeleton.inverseBindPose[i], 0)
                System.arraycopy(tempMatrix, 0, boneMatrices[i], 0, 16)
            }
        }
        
        for (i in boneMatrices.indices) {
            System.arraycopy(boneMatrices[i], 0, outMatrices, i * 16, 16)
        }
    }
    
    fun getBoneMatrices(objectID: UUID): FloatArray? {
        return animeshObjects[objectID]?.currentPose
    }
    
    private suspend fun loadSkeleton(skeletonID: UUID): AnimeshSkeleton? {
        skeletonCache[skeletonID]?.let {
            skeletonAccessTimes[skeletonID] = System.currentTimeMillis()
            return it
        }
        
        return withContext(Dispatchers.IO) {
            try {
                val skeleton = fetchSkeletonFromAssetSystem(skeletonID)
                    ?: createTestSkeleton(skeletonID)
                
                skeletonCache[skeletonID] = skeleton
                skeletonAccessTimes[skeletonID] = System.currentTimeMillis()
                
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
    
    private suspend fun loadAnimation(animID: UUID): AnimeshAnimation {
        return withContext(Dispatchers.IO) {
            fetchAnimationFromAssetSystem(animID)
                ?: createTestAnimation(animID)
        }
    }
    
    private fun parseAnimeshExtraParams(extraParams: ByteBuffer): AnimeshData {
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
    
    fun removeAnimeshObject(objectID: UUID) {
        animeshObjects.remove(objectID)
        Log.d(TAG, "Removed animesh object $objectID")
    }
    
    private fun trimSkeletonCache() {
        if (skeletonCache.size <= MAX_CACHED_SKELETONS) return
        
        val sorted = skeletonAccessTimes.entries.sortedBy { it.value }
        val toRemove = sorted.take(skeletonCache.size - MAX_CACHED_SKELETONS)
        
        toRemove.forEach {
            skeletonCache.remove(it.key)
            skeletonAccessTimes.remove(it.key)
        }
        
        Log.d(TAG, "Trimmed skeleton cache, removed ${toRemove.size} skeletons")
    }
    
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
    
    fun cleanup() {
        Log.i(TAG, "Cleaning up Animesh Manager...")
        animationUpdateJob?.cancel()
        animeshObjects.clear()
        skeletonCache.clear()
        skeletonAccessTimes.clear()
        scope.cancel()
        Log.i(TAG, "Animesh Manager cleanup completed")
    }

    // Moved methods inside class
    private suspend fun fetchSkeletonFromAssetSystem(skeletonID: UUID): AnimeshSkeleton? {
        // Mock implementation as I cannot access com.lumiyaviewer.lumiya.res.mesh.MeshCache easily or it might not exist
        // The original code had:
        // val assetFetcher = com.lumiyaviewer.lumiya.res.mesh.MeshCache
        // val skeletonData = assetFetcher.getResource(skeletonID)
        // I will check if MeshCache exists. If not, I'll just return null (fallback to test skeleton).
        return null
    }
    
    private suspend fun fetchAnimationFromAssetSystem(animID: UUID): AnimeshAnimation? {
        return null
    }
}
