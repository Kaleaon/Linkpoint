package com.linkpoint.render.lumiya.drawable

import android.content.Context
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal data class AvatarMeshAsset(val vertices: FloatArray, val indices: ShortArray)

internal object AvatarMeshAssetLoader {
    private const val TAG = "AvatarMeshAssetLoader"
    private const val AVATAR_MESH_ASSET = "mesh/avatar.bin"

    fun loadDefaultAvatarMesh(context: Context): AvatarMeshAsset? {
        return try {
            val bytes = context.assets.open(AVATAR_MESH_ASSET).use { it.readBytes() }
            parseBinaryMesh(bytes)
        } catch (e: Exception) {
            Log.w(TAG, "Unable to load avatar mesh asset '$AVATAR_MESH_ASSET': ${e.message}")
            null
        }
    }

    private fun parseBinaryMesh(bytes: ByteArray): AvatarMeshAsset? {
        if (bytes.size < 8) return null
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val vertexCount = bb.int
        val indexCount = bb.int
        if (vertexCount <= 0 || indexCount <= 0) return null

        val floatsPerVertex = 16 // pos + normal + uv + weights + joints
        val vertexFloats = vertexCount * floatsPerVertex
        val vertexBytes = vertexFloats * 4
        val indexBytes = indexCount * 2
        if (bytes.size < 8 + vertexBytes + indexBytes) return null

        val vertices = FloatArray(vertexFloats)
        for (i in 0 until vertexFloats) {
            vertices[i] = bb.float
        }

        val indices = ShortArray(indexCount)
        for (i in 0 until indexCount) {
            indices[i] = bb.short
        }
        return AvatarMeshAsset(vertices, indices)
    }
}
