package com.linkpoint.render.scene.commands

import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.UUID

/**
 * Pre-processed render command ready for zero-stall Filament submission.
 * Pre-computes heavy tasks (transform matrices, shape calculations, terrain heightmaps,
 * material descriptor signatures) in parallel across worker threads.
 */
sealed class PreparedRenderCommand {
    data class PreparedUpsertPrim(
        val update: ObjectUpdateData,
        val transformMatrix: FloatArray,
        val isAvatar: Boolean,
        val descriptorKey: Long
    ) : PreparedRenderCommand() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PreparedUpsertPrim
            if (update != other.update) return false
            if (!transformMatrix.contentEquals(other.transformMatrix)) return false
            if (isAvatar != other.isAvatar) return false
            if (descriptorKey != other.descriptorKey) return false
            return true
        }

        override fun hashCode(): Int {
            var result = update.hashCode()
            result = 31 * result + transformMatrix.contentHashCode()
            result = 31 * result + isAvatar.hashCode()
            result = 31 * result + descriptorKey.hashCode()
            return result
        }
    }

    data class PreparedUpsertMesh(
        val localId: Int,
        val meshData: com.linkpoint.assets.MeshData,
        val textureEntry: ByteArray,
        val descriptorKey: Long,
        val hostAvatarId: java.util.UUID? = null
    ) : PreparedRenderCommand() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PreparedUpsertMesh
            if (localId != other.localId) return false
            if (meshData != other.meshData) return false
            if (!textureEntry.contentEquals(other.textureEntry)) return false
            if (descriptorKey != other.descriptorKey) return false
            return true
        }

        override fun hashCode(): Int {
            var result = localId
            result = 31 * result + meshData.hashCode()
            result = 31 * result + textureEntry.contentHashCode()
            result = 31 * result + descriptorKey.hashCode()
            return result
        }
    }

    data class PreparedUpdateMaterial(
        val localId: Int,
        val textureEntry: ByteArray,
        val fallbackUpdate: ObjectUpdateData?,
        val descriptorKey: Long
    ) : PreparedRenderCommand() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PreparedUpdateMaterial
            if (localId != other.localId) return false
            if (!textureEntry.contentEquals(other.textureEntry)) return false
            if (fallbackUpdate != other.fallbackUpdate) return false
            if (descriptorKey != other.descriptorKey) return false
            return true
        }

        override fun hashCode(): Int {
            var result = localId
            result = 31 * result + textureEntry.contentHashCode()
            result = 31 * result + (fallbackUpdate?.hashCode() ?: 0)
            result = 31 * result + descriptorKey.hashCode()
            return result
        }
    }

    data class PreparedRemoveEntity(
        val localId: Int,
        val fullId: UUID?
    ) : PreparedRenderCommand()

    data class PreparedSetCamera(
        val position: LLVector3,
        val target: LLVector3
    ) : PreparedRenderCommand()

    data class PreparedSetTerrainPatch(
        val patch: TerrainPatch,
        val processedHeightmap: FloatArray
    ) : PreparedRenderCommand() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PreparedSetTerrainPatch
            if (patch != other.patch) return false
            if (!processedHeightmap.contentEquals(other.processedHeightmap)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = patch.hashCode()
            result = 31 * result + processedHeightmap.contentHashCode()
            return result
        }
    }
}

/**
 * Multi-threaded parallel rendering command buffer recorder.
 * Records rendering command buffers in parallel across worker threads
 * to eliminate GPU driver stalls and main-thread processing bottlenecks.
 */
class ParallelCommandBufferRecorder {

    companion object {
        private const val PARALLEL_BATCH_THRESHOLD = 8
    }

    /**
     * Records a batch of [SceneRenderCommand] items in parallel using worker threads.
     */
    suspend fun recordBatchInParallel(commands: List<SceneRenderCommand>): List<PreparedRenderCommand> {
        if (commands.isEmpty()) return emptyList()

        if (commands.size < PARALLEL_BATCH_THRESHOLD) {
            return commands.map { recordSingleCommand(it) }
        }

        return coroutineScope {
            val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
            val chunkSize = (commands.size + cores - 1) / cores

            commands.chunked(chunkSize.coerceAtLeast(1)).map { chunk ->
                async(Dispatchers.Default) {
                    chunk.map { recordSingleCommand(it) }
                }
            }.awaitAll().flatten()
        }
    }

    /**
     * Record a single command into a [PreparedRenderCommand] on a worker thread.
     */
    fun recordSingleCommand(command: SceneRenderCommand): PreparedRenderCommand {
        return when (command) {
            is SceneRenderCommand.UpsertPrim -> {
                val matrix = computeTransformMatrix(
                    command.update.position,
                    command.update.rotation,
                    command.update.scale
                )
                val descriptorKey = computeDescriptorKey(command.update)
                PreparedRenderCommand.PreparedUpsertPrim(
                    update = command.update,
                    transformMatrix = matrix,
                    isAvatar = command.update.pcode == 47,
                    descriptorKey = descriptorKey
                )
            }
            is SceneRenderCommand.UpsertMesh -> {
                val descriptorKey = computeMeshDescriptorKey(command.textureEntry)
                PreparedRenderCommand.PreparedUpsertMesh(
                    localId = command.localId,
                    meshData = command.meshData,
                    textureEntry = command.textureEntry,
                    descriptorKey = descriptorKey,
                    hostAvatarId = command.hostAvatarId
                )
            }
            is SceneRenderCommand.UpdateMaterial -> {
                val descriptorKey = computeMeshDescriptorKey(command.textureEntry)
                PreparedRenderCommand.PreparedUpdateMaterial(
                    localId = command.localId,
                    textureEntry = command.textureEntry,
                    fallbackUpdate = command.fallbackUpdate,
                    descriptorKey = descriptorKey
                )
            }
            is SceneRenderCommand.RemoveEntity -> {
                PreparedRenderCommand.PreparedRemoveEntity(
                    localId = command.localId,
                    fullId = command.fullId
                )
            }
            is SceneRenderCommand.SetCamera -> {
                PreparedRenderCommand.PreparedSetCamera(
                    position = command.position,
                    target = command.target
                )
            }
            is SceneRenderCommand.SetTerrainPatch -> {
                val heightmap = processTerrainHeights(command.patch)
                PreparedRenderCommand.PreparedSetTerrainPatch(
                    patch = command.patch,
                    processedHeightmap = heightmap
                )
            }
        }
    }

    /**
     * Precomputes 4x4 column-major transformation matrix from position, rotation, scale.
     */
    private fun computeTransformMatrix(
        pos: LLVector3,
        rot: LLQuaternion,
        scale: LLVector3
    ): FloatArray {
        val out = FloatArray(16)
        val x = rot.x; val y = rot.y; val z = rot.z; val w = rot.w

        val x2 = x + x; val y2 = y + y; val z2 = z + z
        val xx = x * x2; val xy = x * y2; val xz = x * z2
        val yy = y * y2; val yz = y * z2; val zz = z * z2
        val wx = w * x2; val wy = w * y2; val wz = w * z2

        val sx = scale.x; val sy = scale.y; val sz = scale.z

        out[0] = (1f - (yy + zz)) * sx
        out[1] = (xy + wz) * sx
        out[2] = (xz - wy) * sx
        out[3] = 0f

        out[4] = (xy - wz) * sy
        out[5] = (1f - (xx + zz)) * sy
        out[6] = (yz + wx) * sy
        out[7] = 0f

        out[8] = (xz + wy) * sz
        out[9] = (yz - wx) * sz
        out[10] = (1f - (xx + yy)) * sz
        out[11] = 0f

        out[12] = pos.x
        out[13] = pos.y
        out[14] = pos.z
        out[15] = 1f

        return out
    }

    private fun computeDescriptorKey(update: ObjectUpdateData): Long {
        var result = update.material.toLong()
        result = 31 * result + update.textureEntry.contentHashCode()
        return result
    }

    private fun computeMeshDescriptorKey(textureEntry: ByteArray): Long {
        return textureEntry.contentHashCode().toLong()
    }

    private fun processTerrainHeights(patch: TerrainPatch): FloatArray {
        val out = FloatArray(16 * 16)
        val patchHeights = patch.heightMap
        for (i in 0 until minOf(out.size, patchHeights.size)) {
            out[i] = patchHeights[i]
        }
        return out
    }
}
