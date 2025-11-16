package com.linkpoint.animesh

import java.nio.ByteBuffer
import java.util.UUID

/**
 * Immutable math helpers for the lightweight animesh system.
 */
data class Vector3(val x: Float, val y: Float, val z: Float) {
    fun lerp(other: Vector3, alpha: Float): Vector3 =
        Vector3(
            x = x + ((other.x - x) * alpha),
            y = y + ((other.y - y) * alpha),
            z = z + ((other.z - z) * alpha)
        )
}

data class Quaternion(val x: Float, val y: Float, val z: Float, val w: Float) {
    fun normalized(): Quaternion {
        val magnitude = kotlin.math.sqrt((x * x) + (y * y) + (z * z) + (w * w))
        if (magnitude == 0f) return this
        return Quaternion(x / magnitude, y / magnitude, z / magnitude, w / magnitude)
    }
}

data class AnimeshBone(
    val id: Int,
    val parentId: Int,
    val name: String,
    val position: Vector3,
    val rotation: Quaternion
)

data class AnimeshSkeleton(val bones: List<AnimeshBone>) {
    val boneById: Map<Int, AnimeshBone> = bones.associateBy { it.id }

    init {
        require(bones.isNotEmpty()) { "Skeleton must contain at least one bone" }
    }
}

data class AnimeshAnimation(
    val animationId: UUID,
    val name: String,
    val duration: Float,
    val loop: Boolean,
    val priority: Int = 0
)

data class ActiveAnimation(
    val animation: AnimeshAnimation,
    val startedAtMillis: Long
)

data class AnimeshInstance(
    val objectId: UUID,
    val attachmentId: UUID,
    val skeleton: AnimeshSkeleton,
    val activeAnimations: MutableList<ActiveAnimation> = mutableListOf()
)

data class AnimeshStats(
    val activeAnimeshCount: Int,
    val cachedAnimationCount: Int
)

/**
 * Utility responsible for parsing the binary skeleton payload defined by the
 * historic Lumiya viewer.
 */
internal object SkeletonParser {
    fun parse(buffer: ByteBuffer): AnimeshSkeleton {
        val orderedBuffer = buffer.slice().order(buffer.order())

        if (orderedBuffer.remaining() < Int.SIZE_BYTES) {
            error("Skeleton buffer is empty")
        }

        val boneCount = orderedBuffer.int
        require(boneCount in 1..2_048) {
            "Bone count $boneCount is outside supported range"
        }

        val bones = mutableListOf<AnimeshBone>()
        repeat(boneCount) {
            bones += readBone(orderedBuffer)
        }
        return AnimeshSkeleton(bones)
    }

    private fun readBone(buffer: ByteBuffer): AnimeshBone {
        fun readString(): String {
            val length = buffer.int
            require(length in 0..4_096) { "Invalid bone name length: $length" }
            val bytes = ByteArray(length)
            buffer.get(bytes)
            return bytes.toString(Charsets.UTF_8)
        }

        fun readVector3() = Vector3(buffer.float, buffer.float, buffer.float)
        fun readQuaternion() = Quaternion(buffer.float, buffer.float, buffer.float, buffer.float)

        val id = buffer.int
        val parentId = buffer.int
        val name = readString()
        val position = readVector3()
        val rotation = readQuaternion().normalized()

        return AnimeshBone(
            id = id,
            parentId = parentId,
            name = name,
            position = position,
            rotation = rotation
        )
    }
}
