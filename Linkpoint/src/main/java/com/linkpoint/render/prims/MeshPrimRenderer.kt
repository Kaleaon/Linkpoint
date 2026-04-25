package com.linkpoint.render.prims

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshFace
import com.linkpoint.protocol.textures.TextureEntryParser
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Renders SL mesh-asset prims (sculptType = 5, modern uploaded mesh content).
 *
 * Owns Filament VertexBuffer / IndexBuffer pairs keyed by mesh asset UUID
 * (so multiple prims sharing a mesh asset reuse one set of GPU buffers) plus
 * per-prim per-face MaterialInstances driven by the prim's TextureEntry.
 *
 * Each face in the parsed MeshData becomes one primitive in the Filament
 * RenderableComponent so per-face TextureEntry properties (textureId, tint,
 * scale, offset, rotation) can be applied independently. Textures are
 * fetched asynchronously via the supplied [TextureBinder]; the face renders
 * with just the tint colour until the texture lands.
 *
 * Skinned (rigged) meshes: [MeshData.skinData] is captured for future GPU
 * skinning work — currently rigged meshes render in their bind pose.
 */
class MeshPrimRenderer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "MeshPrimRenderer"
        private const val DEFAULT_BOUND = 0.5f
    }

    /** GPU-side cache keyed by mesh asset UUID. */
    data class CompiledMesh(
        val vertexBuffers: List<VertexBuffer>,
        val indexBuffers: List<IndexBuffer>
    )

    /**
     * Resolves a face's textureId UUID into a Filament Texture. The supplied
     * callback is invoked exactly once per face per attach() call; it should
     * fetch the texture asynchronously and call [onLoaded] from the render
     * thread when ready (or never, if the texture fails to load).
     *
     * The MaterialInstance is owned by the renderer and re-used across
     * frames; the binder doesn't need to hold the reference.
     */
    fun interface TextureBinder {
        fun bind(face: Int, textureId: UUID, onLoaded: (Texture) -> Unit)
    }

    private val compiled = ConcurrentHashMap<UUID, CompiledMesh>()

    /**
     * Per-prim MaterialInstance ownership. Keyed by Filament entity ID so
     * shutdown() / re-attach() can dispose the previous batch cleanly.
     */
    private val perEntityMaterials = ConcurrentHashMap<Int, List<MaterialInstance>>()
    private var litMaterial: Material? = null

    fun initialize(material: Material) {
        litMaterial = material
        Log.i(TAG, "MeshPrimRenderer initialized")
    }

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
     * Attach a renderable to [entity] with one primitive per face, each
     * with its own MaterialInstance configured from [textureEntry].
     *
     * If [textureEntry] is null the prim renders with a uniform default
     * material (light grey, no texture) — caller can re-attach later when
     * the TextureEntry actually arrives.
     *
     * If [binder] is non-null, every face whose default texture isn't the
     * null UUID kicks off a fetch via the binder. The binder's `onLoaded`
     * callback is responsible for posting back to the render thread.
     */
    fun attach(
        entity: Int,
        mesh: CompiledMesh,
        textureEntry: ByteArray? = null,
        binder: TextureBinder? = null
    ) {
        val mat = litMaterial ?: return
        // Drop any prior per-face material instances for this entity so
        // a re-attach (e.g. mesh asset arrived after TextureEntry update)
        // doesn't leak GPU resources.
        perEntityMaterials.remove(entity)?.forEach {
            try { engine.destroyMaterialInstance(it) } catch (_: Exception) {}
        }

        val faceCount = mesh.vertexBuffers.size.coerceAtLeast(1)
        val perFace = textureEntry?.let { TextureEntryParser.parseFull(it, faceCount) }

        val instances = mutableListOf<MaterialInstance>()
        val builder = RenderableManager.Builder(faceCount)
            .boundingBox(Box(-DEFAULT_BOUND, -DEFAULT_BOUND, -DEFAULT_BOUND,
                DEFAULT_BOUND, DEFAULT_BOUND, DEFAULT_BOUND))
            .culling(true)
            .receiveShadows(true)
            .castShadows(true)
        for (i in 0 until faceCount) {
            val faceProps = perFace?.getOrNull(i)
            val instance = mat.createInstance().apply {
                if (faceProps != null) {
                    setParameter("baseColor",
                        faceProps.colorR, faceProps.colorG,
                        faceProps.colorB, faceProps.colorA)
                    setParameter("texScale", faceProps.scaleS, faceProps.scaleT)
                    setParameter("texOffset", faceProps.offsetS, faceProps.offsetT)
                    setParameter("texRotation", faceProps.rotation)
                } else {
                    setParameter("baseColor", 0.8f, 0.8f, 0.8f, 1f)
                    setParameter("texScale", 1f, 1f)
                    setParameter("texOffset", 0f, 0f)
                    setParameter("texRotation", 0f)
                }
                setParameter("metallic", 0f)
                setParameter("roughness", 0.5f)
                // Default to "no texture"; the binder flips this to 1.0 when
                // it sets the actual sampler.
                setParameter("hasTexture", 0f)
            }
            instances.add(instance)
            builder.geometry(
                i,
                RenderableManager.PrimitiveType.TRIANGLES,
                mesh.vertexBuffers[i.coerceAtMost(mesh.vertexBuffers.size - 1)],
                mesh.indexBuffers[i.coerceAtMost(mesh.indexBuffers.size - 1)]
            )
            builder.material(i, instance)
        }
        builder.build(engine, entity)
        perEntityMaterials[entity] = instances

        // Kick off async texture fetches for any non-null face textures.
        if (perFace != null && binder != null) {
            for (i in 0 until faceCount) {
                val faceProps = perFace[i] ?: continue
                val texId = faceProps.textureId
                if (texId == UUID(0L, 0L)) continue
                val instance = instances[i]
                binder.bind(i, texId) { tex ->
                    try {
                        instance.setParameter("baseColorMap", tex,
                            TextureSampler(
                                TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
                                TextureSampler.MagFilter.LINEAR,
                                TextureSampler.WrapMode.REPEAT
                            )
                        )
                        instance.setParameter("hasTexture", 1f)
                    } catch (e: Exception) {
                        Log.w(TAG, "bind face $i failed: ${e.message}")
                    }
                }
            }
        }
    }

    private fun uploadFace(face: MeshFace): Pair<VertexBuffer, IndexBuffer>? {
        if (face.positions.size != face.vertexCount * 3) return null
        if (face.normals.size  != face.vertexCount * 3) return null
        if (face.uvs.size      != face.vertexCount * 2) return null
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
        perEntityMaterials.values.flatten().forEach {
            try { engine.destroyMaterialInstance(it) } catch (_: Exception) {}
        }
        perEntityMaterials.clear()
    }
}
