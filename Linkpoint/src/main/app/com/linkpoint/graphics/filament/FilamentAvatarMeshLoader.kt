package com.linkpoint.graphics.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.Box
import com.google.android.filament.Engine
import com.google.android.filament.IndexBuffer
import com.google.android.filament.VertexBuffer
import com.google.android.filament.gltfio.FilamentAsset
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Loads avatar meshes for the Filament renderer.
 *
 * The loader prefers the modern glTF avatar shipped with the project but can build a
 * procedural fallback mesh so that avatars are always renderable.  The fallback draws
 * inspiration from the primitive stand-in Firestorm uses when a baked avatar is not
 * yet available.
 */
class FilamentAvatarMeshLoader(
    private val context: Context,
    private val engine: Engine,
    private val gltfLoader: FilamentGltfLoader
) {

    data class Mesh(
        val vertexBuffer: VertexBuffer,
        val indexBuffer: IndexBuffer,
        val boundingBox: Box
    )

    private var cachedFallback: Mesh? = null

    companion object {
        private const val TAG = "FilamentAvatarMeshLoader"
        private const val DEFAULT_GLTF = "character/avatar_default.glb"

        private const val DEFAULT_HEIGHT = 1.75f
        private const val DEFAULT_WIDTH = 0.45f
        private const val DEFAULT_DEPTH = 0.35f
    }

    /**
     * Attempts to load the default avatar asset from the APK.
     */
    fun loadDefaultAsset(): FilamentAsset? {
        return try {
            if (!assetExists(DEFAULT_GLTF)) {
                Log.w(TAG, "Default avatar asset not found: $DEFAULT_GLTF")
                return null
            }
            gltfLoader.loadFromAssets(DEFAULT_GLTF)?.also {
                Log.i(TAG, "Loaded default avatar GLB (${it.entityCount} entities)")
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to load avatar asset $DEFAULT_GLTF", t)
            null
        }
    }

    /**
     * Provides a reusable fallback mesh for avatars without a ready asset.
     */
    fun fallbackMesh(): Mesh {
        cachedFallback?.let { return it }
        val mesh = buildFallbackMesh()
        cachedFallback = mesh
        return mesh
    }

    fun destroy() {
        cachedFallback?.let {
            try {
                engine.destroyVertexBuffer(it.vertexBuffer)
                engine.destroyIndexBuffer(it.indexBuffer)
            } catch (t: Throwable) {
                Log.w(TAG, "Error destroying fallback avatar mesh", t)
            }
        }
        cachedFallback = null
    }

    private fun buildFallbackMesh(): Mesh {
        // Six-faced prism approximating a humanoid body.
        // Layout per vertex: position (3) + normal (3) + texcoord (2) = 8 floats.
        val halfWidth = DEFAULT_WIDTH * 0.5f
        val halfDepth = DEFAULT_DEPTH * 0.5f
        val torsoHeight = DEFAULT_HEIGHT * 0.55f
        val legHeight = DEFAULT_HEIGHT * 0.45f

        val yTorsoTop = legHeight + torsoHeight
        val yHeadTop = yTorsoTop + (DEFAULT_HEIGHT * 0.2f)

        val vertices = floatArrayOf(
            // Front face (torso + head)
            -halfWidth, 0f, halfDepth, 0f, 0f, 1f, 0f, 0f,
            halfWidth, 0f, halfDepth, 0f, 0f, 1f, 1f, 0f,
            halfWidth, yTorsoTop, halfDepth, 0f, 0f, 1f, 1f, 0.7f,
            -halfWidth, yTorsoTop, halfDepth, 0f, 0f, 1f, 0f, 0.7f,

            // Back face
            -halfWidth, 0f, -halfDepth, 0f, 0f, -1f, 1f, 0f,
            -halfWidth, yTorsoTop, -halfDepth, 0f, 0f, -1f, 1f, 0.7f,
            halfWidth, yTorsoTop, -halfDepth, 0f, 0f, -1f, 0f, 0.7f,
            halfWidth, 0f, -halfDepth, 0f, 0f, -1f, 0f, 0f,

            // Top face (head)
            -halfWidth, yTorsoTop, -halfDepth, 0f, 1f, 0f, 0f, 0f,
            -halfWidth, yHeadTop, -halfDepth, 0f, 1f, 0f, 0f, 0.3f,
            halfWidth, yHeadTop, -halfDepth, 0f, 1f, 0f, 1f, 0.3f,
            halfWidth, yTorsoTop, -halfDepth, 0f, 1f, 0f, 1f, 0f,

            -halfWidth, yTorsoTop, halfDepth, 0f, 1f, 0f, 0f, 0f,
            -halfWidth, yHeadTop, halfDepth, 0f, 1f, 0f, 0f, 0.3f,
            halfWidth, yHeadTop, halfDepth, 0f, 1f, 0f, 1f, 0.3f,
            halfWidth, yTorsoTop, halfDepth, 0f, 1f, 0f, 1f, 0f,

            // Bottom face (feet)
            -halfWidth * 0.6f, 0f, -halfDepth * 0.7f, 0f, -1f, 0f, 0f, 0f,
            halfWidth * 0.6f, 0f, -halfDepth * 0.7f, 0f, -1f, 0f, 1f, 0f,
            halfWidth * 0.6f, 0f, halfDepth * 0.7f, 0f, -1f, 0f, 1f, 0.5f,
            -halfWidth * 0.6f, 0f, halfDepth * 0.7f, 0f, -1f, 0f, 0f, 0.5f,

            // Left face
            -halfWidth, 0f, -halfDepth, -1f, 0f, 0f, 0f, 0f,
            -halfWidth, 0f, halfDepth, -1f, 0f, 0f, 1f, 0f,
            -halfWidth, yHeadTop, halfDepth, -1f, 0f, 0f, 1f, 1f,
            -halfWidth, yHeadTop, -halfDepth, -1f, 0f, 0f, 0f, 1f,

            // Right face
            halfWidth, 0f, -halfDepth, 1f, 0f, 0f, 1f, 0f,
            halfWidth, yHeadTop, -halfDepth, 1f, 0f, 0f, 1f, 1f,
            halfWidth, yHeadTop, halfDepth, 1f, 0f, 0f, 0f, 1f,
            halfWidth, 0f, halfDepth, 1f, 0f, 0f, 0f, 0f,
        )

        val indices = shortArrayOf(
            // Front
            0, 1, 2, 0, 2, 3,
            // Back
            4, 5, 6, 4, 6, 7,
            // Top front
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            // Bottom
            16, 17, 18, 16, 18, 19,
            // Left
            20, 21, 22, 20, 22, 23,
            // Right
            24, 25, 26, 24, 26, 27,
        )

        val strideBytes = 8 * java.lang.Float.BYTES
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertices.size / 8)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, strideBytes)
            .attribute(VertexBuffer.VertexAttribute.NORMAL, 0, VertexBuffer.AttributeType.FLOAT3, 12, strideBytes)
            .attribute(VertexBuffer.VertexAttribute.UV0, 0, VertexBuffer.AttributeType.FLOAT2, 24, strideBytes)
            .build(engine)

        val vbData = ByteBuffer.allocateDirect(vertices.size * java.lang.Float.BYTES)
            .order(ByteOrder.nativeOrder())
            .apply {
                asFloatBuffer().put(vertices)
                rewind()
            }
        vertexBuffer.setBufferAt(engine, 0, vbData)

        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        val ibData = ByteBuffer.allocateDirect(indices.size * java.lang.Short.BYTES)
            .order(ByteOrder.nativeOrder())
            .apply {
                asShortBuffer().put(indices)
                rewind()
            }
        indexBuffer.setBuffer(engine, ibData)

        val center = floatArrayOf(0f, DEFAULT_HEIGHT * 0.5f, 0f)
        val halfExtent = floatArrayOf(halfWidth, DEFAULT_HEIGHT * 0.5f, halfDepth)
        val boundingBox = Box(center, halfExtent)

        Log.i(TAG, "Created fallback avatar mesh (verts=${vertices.size / 8})")
        return Mesh(vertexBuffer, indexBuffer, boundingBox)
    }

    private fun assetExists(path: String): Boolean = runCatching {
        context.assets.open(path).use { }
    }.isSuccess
}
