package com.lumiyaviewer.lumiya.animesh

import java.nio.ByteBuffer
import java.util.*

/**
 * Animesh Data Structure
 * Represents animated mesh attachments in Second Life (2018+)
 * 
 * Animesh allows mesh attachments to have their own skeleton and animations,
 * enabling animated tails, wings, pets, hair, and other attachments.
 */
data class AnimeshData(
    val attachmentId: UUID,
    val objectId: UUID,
    val skeleton: AnimeshSkeleton,
    val activeAnimations: MutableList<AnimeshAnimation> = mutableListOf(),
    val isPlaying: Boolean = true,
    val priority: Int = 0
) {
    companion object {
        const val ANIMESH_FLAG = 0x80000  // Animesh object flag from SL protocol
        const val MAX_BONES = 256         // Maximum bones in animesh skeleton
        const val MAX_ANIMATIONS = 32     // Maximum concurrent animations
    }
    
    /**
     * Update animation state
     */
    fun updateAnimations(deltaTime: Float) {
        if (!isPlaying) return
        
        activeAnimations.forEach { animation ->
            animation.update(deltaTime)
        }
        
        // Remove finished animations
        activeAnimations.removeIf { it.isFinished() && !it.loop }
    }
    
    /**
     * Add animation to animesh
     */
    fun addAnimation(animation: AnimeshAnimation) {
        if (activeAnimations.size >= MAX_ANIMATIONS) {
            // Remove lowest priority animation
            activeAnimations.sortBy { it.priority }
            activeAnimations.removeAt(0)
        }
        activeAnimations.add(animation)
        activeAnimations.sortByDescending { it.priority }
    }
    
    /**
     * Stop all animations
     */
    fun stopAllAnimations() {
        activeAnimations.clear()
    }
}

/**
 * Animesh Skeleton
 * Bone hierarchy for animesh attachments
 */
data class AnimeshSkeleton(
    val bones: List<AnimeshBone>,
    val rootBoneId: Int = 0
) {
    /**
     * Get bone by ID
     */
    fun getBone(boneId: Int): AnimeshBone? {
        return bones.find { it.boneId == boneId }
    }
    
    /**
     * Get bone hierarchy
     */
    fun getBoneHierarchy(boneId: Int): List<AnimeshBone> {
        val hierarchy = mutableListOf<AnimeshBone>()
        var currentBone = getBone(boneId)
        
        while (currentBone != null) {
            hierarchy.add(currentBone)
            currentBone = if (currentBone.parentId >= 0) {
                getBone(currentBone.parentId)
            } else null
        }
        
        return hierarchy.reversed()
    }
    
    companion object {
        /**
         * Parse skeleton from LLSD binary data
         */
        fun fromLLSD(data: ByteBuffer): AnimeshSkeleton {
            val boneCount = data.getInt()
            val bones = mutableListOf<AnimeshBone>()
            
            for (i in 0 until boneCount) {
                val boneId = data.getInt()
                val parentId = data.getInt()
                val name = readString(data)
                
                // Position
                val posX = data.getFloat()
                val posY = data.getFloat()
                val posZ = data.getFloat()
                
                // Rotation (quaternion)
                val rotX = data.getFloat()
                val rotY = data.getFloat()
                val rotZ = data.getFloat()
                val rotW = data.getFloat()
                
                bones.add(AnimeshBone(
                    boneId = boneId,
                    parentId = parentId,
                    name = name,
                    position = Vector3(posX, posY, posZ),
                    rotation = Quaternion(rotX, rotY, rotZ, rotW)
                ))
            }
            
            return AnimeshSkeleton(bones)
        }
        
        private fun readString(buffer: ByteBuffer): String {
            val length = buffer.getInt()
            val bytes = ByteArray(length)
            buffer.get(bytes)
            return String(bytes)
        }
    }
}

/**
 * Individual bone in animesh skeleton
 */
data class AnimeshBone(
    val boneId: Int,
    val parentId: Int,
    val name: String,
    val position: Vector3,
    val rotation: Quaternion,
    var currentPosition: Vector3 = position.copy(),
    var currentRotation: Quaternion = rotation.copy()
) {
    /**
     * Apply animation transform
     */
    fun applyTransform(posOffset: Vector3, rotOffset: Quaternion) {
        currentPosition = position + posOffset
        currentRotation = rotation * rotOffset
    }
    
    /**
     * Reset to bind pose
     */
    fun resetToBindPose() {
        currentPosition = position.copy()
        currentRotation = rotation.copy()
    }
}

/**
 * Animesh Animation
 */
data class AnimeshAnimation(
    val animationId: UUID,
    val name: String,
    val duration: Float,
    val loop: Boolean = true,
    val priority: Int = 0,
    val keyframes: List<AnimationKeyframe> = emptyList(),
    var currentTime: Float = 0f
) {
    /**
     * Update animation time
     */
    fun update(deltaTime: Float) {
        currentTime += deltaTime
        
        if (loop && currentTime >= duration) {
            currentTime = currentTime % duration
        }
    }
    
    /**
     * Check if animation finished
     */
    fun isFinished(): Boolean {
        return !loop && currentTime >= duration
    }
    
    /**
     * Get bone transform at current time
     */
    fun getBoneTransform(boneId: Int): Pair<Vector3, Quaternion>? {
        val boneKeyframes = keyframes.filter { it.boneId == boneId }
        if (boneKeyframes.isEmpty()) return null
        
        // Find surrounding keyframes
        val prevKeyframe = boneKeyframes.lastOrNull { it.time <= currentTime }
        val nextKeyframe = boneKeyframes.firstOrNull { it.time > currentTime }
        
        return when {
            prevKeyframe == null -> nextKeyframe?.let { 
                Pair(it.position, it.rotation) 
            }
            nextKeyframe == null -> Pair(prevKeyframe.position, prevKeyframe.rotation)
            else -> {
                // Interpolate between keyframes
                val t = (currentTime - prevKeyframe.time) / (nextKeyframe.time - prevKeyframe.time)
                val position = Vector3.lerp(prevKeyframe.position, nextKeyframe.position, t)
                val rotation = Quaternion.slerp(prevKeyframe.rotation, nextKeyframe.rotation, t)
                Pair(position, rotation)
            }
        }
    }
}

/**
 * Animation keyframe
 */
data class AnimationKeyframe(
    val boneId: Int,
    val time: Float,
    val position: Vector3,
    val rotation: Quaternion
)

/**
 * 3D Vector
 */
data class Vector3(
    val x: Float,
    val y: Float,
    val z: Float
) {
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vector3(x * scalar, y * scalar, z * scalar)
    
    fun copy() = Vector3(x, y, z)
    
    companion object {
        fun lerp(a: Vector3, b: Vector3, t: Float): Vector3 {
            return Vector3(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
            )
        }
    }
}

/**
 * Quaternion for rotations
 */
data class Quaternion(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
) {
    operator fun times(other: Quaternion): Quaternion {
        return Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y + y * other.w + z * other.x - x * other.z,
            w * other.z + z * other.w + x * other.y - y * other.x,
            w * other.w - x * other.x - y * other.y - z * other.z
        )
    }
    
    fun copy() = Quaternion(x, y, z, w)
    
    companion object {
        fun slerp(a: Quaternion, b: Quaternion, t: Float): Quaternion {
            var cosHalfTheta = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z
            
            if (cosHalfTheta < 0) {
                cosHalfTheta = -cosHalfTheta
            }
            
            if (cosHalfTheta >= 1.0f) {
                return a.copy()
            }
            
            val halfTheta = kotlin.math.acos(cosHalfTheta)
            val sinHalfTheta = kotlin.math.sqrt(1.0f - cosHalfTheta * cosHalfTheta)
            
            if (kotlin.math.abs(sinHalfTheta) < 0.001f) {
                return Quaternion(
                    (a.x + b.x) * 0.5f,
                    (a.y + b.y) * 0.5f,
                    (a.z + b.z) * 0.5f,
                    (a.w + b.w) * 0.5f
                )
            }
            
            val ratioA = kotlin.math.sin((1 - t) * halfTheta) / sinHalfTheta
            val ratioB = kotlin.math.sin(t * halfTheta) / sinHalfTheta
            
            return Quaternion(
                (a.x * ratioA + b.x * ratioB),
                (a.y * ratioA + b.y * ratioB),
                (a.z * ratioA + b.z * ratioB),
                (a.w * ratioA + b.w * ratioB)
            )
        }
    }
}