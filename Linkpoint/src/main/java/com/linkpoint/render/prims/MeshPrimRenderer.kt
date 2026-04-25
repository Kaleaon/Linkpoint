package com.linkpoint.render.prims

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshFace
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Renders SL mesh-asset prims (sculptType = 5, i.e. modern uploaded mesh
 * content). Owns Filament VertexBuffer/IndexBuffer pairs keyed by mesh asset
 * UUID so multiple prims that share a mesh asset (very common for furniture,
 * vehicles, hairstyles) reuse one set of GPU buffers.
 *
 * The flow is:
 *   1. PrimRenderer detects an ObjectUpdate with extraParams pointing at a
 *      mesh asset (LLOctet type 0x30, sculptType 5).
 *   2. PrimRenderer asks this renderer for a renderable for (mesh UUID, lod).
 *   3. If we have GPU buffers for that mesh, we attach them to the prim's
 *      entity directly. If not, we kick off MeshManager.getMesh() and the
 *      caller falls back to the path/profile shape until the mesh lands.
 *
 * Every face in the parsed MeshData becomes one primitive in the
 * RenderableComponent so per-face TextureEntry can be applied later (each
 * face gets its own MaterialInstance once textured BoM/face wiring lands).
 *
 * Skinned (rigged) mesh: skinData is captured for future use (GPU skinning is
 * a follow-up); for now rigged meshes render in their bind pose.
 */
class MeshPrimRenderer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "MeshPrimRenderer"
        // Mesh prims have a unit-cube envelope by convention; the scale on
        // the prim transform handles real-world size.
        private const val DEFAULT_BOUND = 0.5f
    }

    /** GPU-side cache keyed by mesh asset UUID. */
    data class CompiledMesh(
        val vertexBuffers: List<VertexBuffer>,
        val indexBuffers: List<IndexBuffer>
    )

    private val compiled = ConcurrentHashMap<UUID, CompiledMesh>()
    private var defaultMaterial: MaterialInstance? = null

    fun initialize(material: Material) {
        defaultMaterial = material.createInstance()
        Log.i(TAG, "MeshPrimRenderer initialized")
    }

    /**
     * Build (or look up) Filament buffers for a parsed MeshData. Must run on
     * the render thread. Returns null if the mesh has no usable faces.
     */
    fun getOrCompile(data: MeshData): CompiledMesh? {
        compiled[data.meshId]?.let { return it }
        if (data.faces.isEmpty()) return null
        val vbs = mutableListOf<VertexBuffer>()
        val ibs = mutableListOf<IndexBuffer>()
        try {
            for (face in data.faces) {
                if (face.vertexCount == 0 || face.indexCount == 0) continue
                val (vb, ib) = uploadFace(face) ?: continue
                vbs.add(vb)
                ibs.add(ib)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to compile mesh ${data.meshId}", e)
            return null
        }
        if (vbs.isEmpty()) return null
        val cm = CompiledMesh(vbs, ibs)
        compiled[data.meshId] = cm
        return cm
    }

    /**
     * Attach a renderable component to [entity] using the compiled buffers.
     * One primitive per face — this matches LL's convention where each face
     * is independently texturable. We use the same default lit material for
     * every primitive for now; per-face material binding (BoM, TextureEntry
     * face overrides) is a follow-up.
     */
    fun attach(entity: Int, mesh: CompiledMesh) {
        val material = defaultMaterial ?: return
        val primitives = mesh.vertexBuffers.size.coerceAtLeast(1)
        val builder = RenderableManager.Builder(primitives)
            .boundingBox(Box(-DEFAULT_BOUND, -DEFAULT_BOUND, -DEFAULT_BOUND,
                DEFAULT_BOUND, DEFAULT_BOUND, DEFAULT_BOUND))
            .culling(true)
            .receiveShadows(true)
            .castShadows(true)
        for (i in mesh.vertexBuffers.indices) {
            builder.geometry(
                i,
                RenderableManager.PrimitiveType.TRIANGLES,
                mesh.vertexBuffers[i],
                mesh.indexBuffers[i]
            )
            builder.material(i, material)
        }
        builder.build(engine, entity)
    }

    private fun uploadFace(face: MeshFace): Pair<VertexBuffer, IndexBuffer>? {
        if (face.positions.size != face.vertexCount * 3) return null
        if (face.normals.size  != face.vertexCount * 3) return null
        if (face.uvs.size      != face.vertexCount * 2) return null
        // Interleave POS(3) + NORMAL(3) + UV0(2) to match the lit material's
        // expected layout (same as PrimRenderer / TerrainRenderer).
        val stride = 8 * 4
        val vBytes = ByteBuffer.allocateDirect(face.vertexCount * stride)
            .order(ByteOrder.nativeOrder())
        for (i in 0 until face.vertexCount) {
            vBytes.putFloat(face.positions[i * 3])
            vBytes.putFloat(face.positions[i * 3 + 1])
            vBytes.putFloat(face.positions[i * 3 + 2])
            vBytes.putFloat(face.normals[i * 3])
            vBytes.putFloat(face.normals[i * 3 + 1])
            vBytes.putFloat(face.normals[i * 3 + 2])
            vBytes.putFloat(face.uvs[i * 2])
            vBytes.putFloat(face.uvs[i * 2 + 1])
        }
        vBytes.flip()
        val vb = VertexBuffer.Builder()
            .vertexCount(face.vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, stride)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, stride)
            .build(engine)
        vb.setBufferAt(engine, 0, vBytes)

        val iBytes = ByteBuffer.allocateDirect(face.indexCount * 2)
            .order(ByteOrder.nativeOrder())
        for (i in 0 until face.indexCount) iBytes.putShort(face.indices[i])
        iBytes.flip()
        val ib = IndexBuffer.Builder()
            .indexCount(face.indexCount)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        ib.setBuffer(engine, iBytes)

        return vb to ib
    }

    fun shutdown() {
        compiled.values.forEach { mesh ->
            mesh.vertexBuffers.forEach { engine.destroyVertexBuffer(it) }
            mesh.indexBuffers.forEach { engine.destroyIndexBuffer(it) }
        }
        compiled.clear()
        defaultMaterial?.let { engine.destroyMaterialInstance(it) }
    }
}
