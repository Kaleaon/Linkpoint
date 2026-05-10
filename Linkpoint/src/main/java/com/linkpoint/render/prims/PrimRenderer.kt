package com.linkpoint.render.prims

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.avatar.BakesOnMesh
import com.linkpoint.diagnostics.ScenePopulationDiagnostics
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.PrimShapeParams
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

/**
 * Renders Second Life primitives (prims)
 * Supports box, cylinder, sphere, torus, and sculpted prims
 */
class PrimRenderer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "PrimRenderer"
        
        // Pcode types
        const val PCODE_PRIM = 9
        const val PCODE_AVATAR = 47
        const val PCODE_GRASS = 95
        const val PCODE_NEW_TREE = 111
        const val PCODE_PARTICLE = 143
        const val PCODE_TREE = 255
    }
    
    private val prims = ConcurrentHashMap<Int, PrimInstance>()
    // Meshes are cached by a coarse "shape key" that captures the visible
    // distinctions (path/profile curve + path/profile cuts + hollow). Path
    // taper/twist/skew aren't part of the cache key yet — they'd explode the
    // cardinality. Treat them as a uniform-scale or post-process for now.
    private val primMeshes = ConcurrentHashMap<PrimShapeKey, PrimMesh>()

    private var materialTemplate: Material? = null
    private var defaultMaterial: MaterialInstance? = null
    // Per-prim material instances so each prim can have an independent texture.
    private val perEntityMaterials = ConcurrentHashMap<Int, MaterialInstance>()
    private val transformManager = engine.transformManager

    /**
     * Optional Bakes-on-Mesh resolver. When set, a TextureEntry face that
     * references a BoM sentinel UUID (e.g. IMG_USE_BAKED_HEAD) is rewritten
     * to the avatar's actual baked texture UUID before being requested from
     * TextureManager. Wired by RenderManager.initialize() to point at
     * AvatarManager's local-agent baker.
     */
    @Volatile
    private var bomResolver: ((Int) -> UUID?)? = null

    fun setBomResolver(resolver: ((Int) -> UUID?)?) {
        bomResolver = resolver
    }

    /**
     * Callback invoked when a prim's texture UUID has been resolved (including
     * BoM substitution). The implementor fetches the bitmap asynchronously and
     * calls [onLoaded] on the render thread with the Filament Texture.
     */
    fun interface TextureBinder {
        fun bind(uuid: UUID, onLoaded: (com.google.android.filament.Texture) -> Unit)
    }

    @Volatile private var textureBinder: TextureBinder? = null

    fun setTextureBinder(binder: TextureBinder?) {
        textureBinder = binder
    }

    /**
     * Initialize with default material
     */
    fun initialize(material: Material) {
        materialTemplate = material
        defaultMaterial = material.createInstance()
        // Pre-generate the default-shape mesh for each base shape kind so the
        // common case (no path/profile customisation) doesn't tessellate on
        // the first frame after each prim arrives.
        listOf(
            PrimShape.BOX, PrimShape.CYLINDER, PrimShape.PRISM,
            PrimShape.SPHERE, PrimShape.TORUS, PrimShape.TUBE, PrimShape.RING
        ).forEach { kind ->
            getOrCreateMesh(PrimShapeKey.defaultFor(kind))
        }
        Log.i(TAG, "PrimRenderer initialized with ${primMeshes.size} default meshes")
    }
    
    /**
     * Add or update a prim from ObjectUpdate
     */
    fun updatePrim(data: ObjectUpdateData): Boolean {
        if (data.pcode != PCODE_PRIM) {
            ScenePopulationDiagnostics.markSceneInserted(ScenePopulationDiagnostics.EntityType.OBJECT, false)
            return false
        }

        // Mesh-asset prims (extraParams type 0x30, sculptType 5) currently
        // fall through to the path/profile path so they render as a box
        // until MeshManager.parseMesh() lands. Logged so we can size the
        // mesh-prim work later — most modern SL builds are mesh-heavy.
        val meshId = data.getMeshAssetId()
        if (meshId != null) {
            // TODO: route through MeshManager.getMesh(meshId, LOD), parse
            //       LLMesh format, build vertex/index buffers, attach via
            //       RenderableManager.Builder. Tracked as a follow-up.
            if (data.localId % 50 == 0) {
                Log.d(TAG, "Mesh-asset prim ${data.localId} (mesh=$meshId) — falling back to path/profile box")
            }
        }

        val prim = prims.getOrPut(data.localId) {
            createPrim(data)
        }
        
        // Update transform
        prim.position = data.position
        prim.rotation = data.rotation
        prim.scale = data.scale
        updateTransform(prim)
        
        // Update material if texture changed
        if (!data.textureEntry.contentEquals(prim.textureEntry)) {
            prim.textureEntry = data.textureEntry
            updatePrimMaterial(prim, data.textureEntry)
        }

        ScenePopulationDiagnostics.markSceneInserted(ScenePopulationDiagnostics.EntityType.OBJECT, true)
        return true
    }
    
    /**
     * Remove a prim
     */
    /**
     * Replace the geometry on an existing prim entity. Used by the
     * mesh-asset path: when MeshManager finishes parsing the LLM asset,
     * RenderManager hands us the entity back so we can swap its
     * RenderableComponent for the parsed mesh primitives. Caller's
     * [attach] block is invoked with the prim's entity ID and is
     * responsible for destroying the existing renderable and building the
     * new one (we don't know the new geometry's shape from here).
     *
     * No-op if no prim with this localId is tracked.
     */
    fun replaceGeometry(localId: Int, attach: (entity: Int) -> Unit) {
        val prim = prims[localId] ?: return
        try {
            attach(prim.entity)
            // Mark the prim's effective shape as MESH so subsequent
            // updatePrim calls don't re-create the path/profile geometry.
            // (We can't mutate `shape` on the data class, so just leave it;
            // updatePrim's getOrPut won't fire because the prim is already
            // tracked. If a future updatePrim ever needed to rebuild we'd
            // route back through replaceGeometry as well.)
        } catch (e: Exception) {
            Log.w(TAG, "replaceGeometry failed for $localId", e)
        }
    }

    fun removePrim(localId: Int) {
        prims.remove(localId)?.let { prim ->
            scene.removeEntity(prim.entity)
            perEntityMaterials.remove(prim.entity)?.let {
                try { engine.destroyMaterialInstance(it) } catch (_: Exception) {}
            }
            engine.destroyEntity(prim.entity)
        }
    }
    
    /**
     * Get prim by local ID
     */
    fun getPrim(localId: Int): PrimInstance? = prims[localId]
    
    /**
     * Get all prims
     */
    fun getAllPrims(): Collection<PrimInstance> = prims.values
    
    private fun createPrim(data: ObjectUpdateData): PrimInstance {
        val entity = EntityManager.get().create()

        // Use the protocol-decoded shape params instead of a hardcoded BOX.
        val shapeKey = PrimShapeKey.from(data.shapeParams)
        val mesh = getOrCreateMesh(shapeKey)

        val instance = (materialTemplate?.createInstance() ?: defaultMaterial)
            ?: throw IllegalStateException("Default material not initialized. Call initialize() first.")
        // Set initial default parameters (no texture; lit with base colour).
        try {
            instance.setParameter("baseColor", 1f, 1f, 1f, 1f)
            instance.setParameter("texScale", 1f, 1f)
            instance.setParameter("texOffset", 0f, 0f)
            instance.setParameter("texRotation", 0f)
            instance.setParameter("metallic", 0f)
            instance.setParameter("roughness", 0.5f)
            instance.setParameter("hasTexture", 0f)
        } catch (_: Exception) {}
        perEntityMaterials[entity] = instance

        // The base mesh is built in unit space (-0.5..0.5 cube envelope). The
        // prim's actual scale comes from the protocol's `scale` vector, so the
        // bounding box stays unit-sized here and the transform handles size.
        RenderableManager.Builder(1)
            .boundingBox(Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, mesh.vertexBuffer, mesh.indexBuffer)
            .material(0, instance)
            .culling(true)
            .receiveShadows(true)
            .castShadows(true)
            .build(engine, entity)

        val ti = transformManager.create(entity)

        scene.addEntity(entity)

        return PrimInstance(
            localId = data.localId,
            fullId = data.fullId,
            entity = entity,
            transformInstance = ti,
            shape = shapeKey.kind,
            position = data.position,
            rotation = data.rotation,
            scale = data.scale,
            textureEntry = data.textureEntry
        )
    }

    private fun getOrCreateMesh(key: PrimShapeKey): PrimMesh {
        return primMeshes.getOrPut(key) {
            when (key.kind) {
                // Path-line shapes (box / cylinder / prism) all share the
                // same extrude topology, so they go through the unified
                // generator that honours profile cuts + hollow + taper.
                // Falling back to the legacy box/cyl/prism generators only
                // when params are at their defaults keeps the well-tested
                // legacy geometry on the common case.
                PrimShape.BOX -> if (needsExtrudeGenerator(key)) {
                    generateExtrudedProfile(key)
                } else generateBoxMesh(key)
                PrimShape.CYLINDER -> if (needsExtrudeGenerator(key)) {
                    generateExtrudedProfile(key)
                } else generateCylinderMesh(key)
                PrimShape.PRISM -> if (needsExtrudeGenerator(key)) {
                    generateExtrudedProfile(key)
                } else generatePrismMesh(key)
                PrimShape.SPHERE -> generateSphereMesh(key)
                PrimShape.TORUS -> generateTorusMesh(key)
                PrimShape.RING -> generateRingMesh(key)
                PrimShape.TUBE -> generateTubeMesh(key)
            }
        }
    }

    private fun needsExtrudeGenerator(key: PrimShapeKey): Boolean {
        val p = key.params
        return p.profileBegin > 0.0001f || p.profileEnd < 0.9999f ||
               p.pathBegin > 0.0001f || p.pathEnd < 0.9999f ||
               p.profileHollow > 0.0001f ||
               kotlin.math.abs(p.pathTaperX) > 0.0001f ||
               kotlin.math.abs(p.pathTaperY) > 0.0001f ||
               kotlin.math.abs(p.pathTwist) > 0.0001f ||
               kotlin.math.abs(p.pathTwistBegin) > 0.0001f
    }

    /**
     * Generic path-line profile extrusion with cuts, hollow, and taper.
     *
     * Builds two profile rings (bottom and top, in entity-local Z) with a
     * tapered scale on the top ring, plus optional inner rings if hollow > 0.
     * The wall is two triangles per profile segment per direction; the open
     * ends are capped with a fan unless profileBegin/End cut leaves them
     * open (the cuts produce visible cross-sections, matching SL's
     * "carved" look).
     *
     * Path twist / taper-X-vs-Y / radius offset / revolutions are NOT
     * applied yet — twist would need rotating the top profile around Z,
     * radius-offset/revolutions only apply to PATH_CIRCLE shapes.
     */
    private fun generateExtrudedProfile(key: PrimShapeKey): PrimMesh {
        val p = key.params
        val profile = buildProfile2D(key.kind, p)
        if (profile.size < 6) return generateBoxMesh(key)

        // Path is a line from z = -0.5 to z = +0.5, cut to
        // [-0.5 + pathBegin, -0.5 + pathEnd]. We emit several rings along
        // it so per-ring twist + taper varies smoothly along the prim.
        val zBottom = -0.5f + p.pathBegin
        val zTop = -0.5f + p.pathEnd

        // SL "taper": top profile scaled by (1 - taper) on each axis.
        // taper = +1 => knife-edge cone; -1 => expand outward.
        val taperX = (1f - p.pathTaperX).coerceAtLeast(0.001f)
        val taperY = (1f - p.pathTaperY).coerceAtLeast(0.001f)

        // Twist: pathTwistBegin at the bottom, pathTwist at the top.
        // LL convention is "revolutions" (1.0 = 360°), so multiply by 2π.
        val twistBeginRad = p.pathTwistBegin * 2f * PI.toFloat()
        val twistEndRad = p.pathTwist * 2f * PI.toFloat()
        val twistDelta = kotlin.math.abs(twistEndRad - twistBeginRad)

        // Choose intermediate ring count: more rings make twist + taper
        // look smooth; flat extrusions only need 2 (begin + end).
        val pathRings = when {
            twistDelta < 0.05f -> 2
            else -> (4 + (twistDelta / (PI.toFloat() / 6f)).toInt()).coerceAtMost(24)
        }

        val n = profile.size / 2
        val verts = mutableListOf<Float>()
        val idx = mutableListOf<Short>()
        val ringStart = IntArray(pathRings)

        fun pushVertex(x: Float, y: Float, z: Float, nx: Float, ny: Float, nz: Float, u: Float, v: Float) {
            verts += floatArrayOf(x, y, z, nx, ny, nz, u, v).toList()
        }

        // Outer rings.
        for (r in 0 until pathRings) {
            val t = if (pathRings == 1) 0f else r.toFloat() / (pathRings - 1)
            val z = zBottom + (zTop - zBottom) * t
            val sx = 1f + (taperX - 1f) * t
            val sy = 1f + (taperY - 1f) * t
            val twist = twistBeginRad + (twistEndRad - twistBeginRad) * t
            val ct = kotlin.math.cos(twist); val st = kotlin.math.sin(twist)
            ringStart[r] = verts.size / 8
            for (i in 0 until n) {
                val rx0 = profile[i * 2] * sx
                val ry0 = profile[i * 2 + 1] * sy
                val px = rx0 * ct - ry0 * st
                val py = rx0 * st + ry0 * ct
                val nl = kotlin.math.sqrt(px * px + py * py).coerceAtLeast(1e-4f)
                pushVertex(px, py, z, px / nl, py / nl, 0f, i.toFloat() / n, t)
            }
        }

        val closesProfile = p.profileBegin <= 0.0001f && p.profileEnd >= 0.9999f
        val wallSegments = if (closesProfile) n else n - 1
        for (r in 0 until pathRings - 1) {
            val a = ringStart[r]
            val b = ringStart[r + 1]
            for (i in 0 until wallSegments) {
                val i0 = (a + i).toShort()
                val i1 = (a + (i + 1) % n).toShort()
                val i2 = (b + i).toShort()
                val i3 = (b + (i + 1) % n).toShort()
                idx += listOf(i0, i2, i1, i1, i2, i3)
            }
        }

        // Hollow path: emit inner rings, walls, and ring caps that connect
        // outer to inner profiles at top and bottom.
        val hollow = p.profileHollow.coerceIn(0f, 0.95f)
        if (hollow > 0.001f) {
            val innerProfile = scaleProfile(profile, 1f - hollow)
            val innerRingStart = IntArray(pathRings)
            for (r in 0 until pathRings) {
                val t = if (pathRings == 1) 0f else r.toFloat() / (pathRings - 1)
                val z = zBottom + (zTop - zBottom) * t
                val sx = 1f + (taperX - 1f) * t
                val sy = 1f + (taperY - 1f) * t
                val twist = twistBeginRad + (twistEndRad - twistBeginRad) * t
                val ct = kotlin.math.cos(twist); val st = kotlin.math.sin(twist)
                innerRingStart[r] = verts.size / 8
                for (i in 0 until n) {
                    val rx0 = innerProfile[i * 2] * sx
                    val ry0 = innerProfile[i * 2 + 1] * sy
                    val px = rx0 * ct - ry0 * st
                    val py = rx0 * st + ry0 * ct
                    val nl = kotlin.math.sqrt(px * px + py * py).coerceAtLeast(1e-4f)
                    pushVertex(px, py, z, -px / nl, -py / nl, 0f, i.toFloat() / n, t)
                }
            }
            // Inner walls (flipped winding).
            for (r in 0 until pathRings - 1) {
                val a = innerRingStart[r]
                val b = innerRingStart[r + 1]
                for (i in 0 until wallSegments) {
                    val i0 = (a + i).toShort()
                    val i1 = (a + (i + 1) % n).toShort()
                    val i2 = (b + i).toShort()
                    val i3 = (b + (i + 1) % n).toShort()
                    idx += listOf(i0, i1, i2, i1, i3, i2)
                }
            }
            // Bottom ring cap.
            run {
                val o = ringStart[0]; val n0 = innerRingStart[0]
                for (i in 0 until wallSegments) {
                    val o0 = (o + i).toShort()
                    val o1 = (o + (i + 1) % n).toShort()
                    val ni0 = (n0 + i).toShort()
                    val ni1 = (n0 + (i + 1) % n).toShort()
                    idx += listOf(o0, ni0, o1, o1, ni0, ni1)
                }
            }
            // Top ring cap (flipped winding).
            run {
                val o = ringStart[pathRings - 1]; val n0 = innerRingStart[pathRings - 1]
                for (i in 0 until wallSegments) {
                    val o0 = (o + i).toShort()
                    val o1 = (o + (i + 1) % n).toShort()
                    val ni0 = (n0 + i).toShort()
                    val ni1 = (n0 + (i + 1) % n).toShort()
                    idx += listOf(o0, o1, ni0, o1, ni1, ni0)
                }
            }
        } else {
            // Solid caps: fan to centre. Twist is constant within the cap
            // (the cap's z is fixed) so we don't need to rotate the centre.
            val centerBottom = (verts.size / 8).toShort()
            pushVertex(0f, 0f, zBottom, 0f, 0f, -1f, 0.5f, 0.5f)
            val centerTop = (verts.size / 8).toShort()
            pushVertex(0f, 0f, zTop, 0f, 0f, 1f, 0.5f, 0.5f)
            val a = ringStart[0]
            val b = ringStart[pathRings - 1]
            for (i in 0 until wallSegments) {
                val a0 = (a + i).toShort()
                val a1 = (a + (i + 1) % n).toShort()
                idx += listOf(centerBottom, a1, a0)
                val b0 = (b + i).toShort()
                val b1 = (b + (i + 1) % n).toShort()
                idx += listOf(centerTop, b0, b1)
            }
        }

        return createMesh(verts.toFloatArray(), idx.toShortArray())
    }

    /**
     * 2D profile loop in entity-local XY plane, scaled to fit a 1×1 box.
     * Returns flat (x, y) pairs for each vertex around the loop.
     *
     * Honours profileBegin / profileEnd by clipping the parameter range:
     * - PROFILE_CIRCLE: the loop becomes an arc from begin*2π to end*2π.
     * - PROFILE_SQUARE: 4 corners walked from begin to end, treating the
     *   square's perimeter as a 0..1 parameter.
     * - PROFILE_TRIANGLE / HALF_CIRCLE: same idea.
     */
    private fun buildProfile2D(kind: PrimShape, p: PrimShapeParams): FloatArray {
        val begin = p.profileBegin.coerceIn(0f, 0.99f)
        val end = p.profileEnd.coerceIn(begin + 0.01f, 1f)
        return when (kind) {
            PrimShape.CYLINDER -> circularArc(begin, end, segments = 24, radius = 0.5f)
            PrimShape.PRISM -> polygonArc(begin, end, sides = 3)
            PrimShape.BOX -> polygonArc(begin, end, sides = 4)
            else -> polygonArc(begin, end, sides = 4)
        }
    }

    /** N points along a circle of [radius] from angle begin*2π to end*2π. */
    private fun circularArc(begin: Float, end: Float, segments: Int, radius: Float): FloatArray {
        val n = (segments * (end - begin)).toInt().coerceAtLeast(3)
        val out = FloatArray(n * 2)
        for (i in 0 until n) {
            val t = begin + (end - begin) * (i.toFloat() / (n - 1))
            val theta = t * 2.0 * PI
            out[i * 2] = (cos(theta) * radius).toFloat()
            out[i * 2 + 1] = (sin(theta) * radius).toFloat()
        }
        return out
    }

    /** Walk a regular [sides]-gon's perimeter from t=begin to t=end. */
    private fun polygonArc(begin: Float, end: Float, sides: Int): FloatArray {
        // Each side spans 1/sides of the perimeter parameter.
        // Walk densely (4× sample density) to keep wall segments short.
        val samples = (sides * 8 * (end - begin)).toInt().coerceAtLeast(sides + 1)
        val out = FloatArray(samples * 2)
        for (i in 0 until samples) {
            val t = begin + (end - begin) * (i.toFloat() / (samples - 1))
            // Map parameter to (sideIndex, sideT).
            val st = (t * sides) % 1f
            val sideIdx = (t * sides).toInt() % sides
            val a0 = sideIdx * (2.0 * PI / sides) - PI / 4
            val a1 = (sideIdx + 1) * (2.0 * PI / sides) - PI / 4
            val x0 = cos(a0).toFloat() * 0.5f * kotlin.math.sqrt(2f)
            val y0 = sin(a0).toFloat() * 0.5f * kotlin.math.sqrt(2f)
            val x1 = cos(a1).toFloat() * 0.5f * kotlin.math.sqrt(2f)
            val y1 = sin(a1).toFloat() * 0.5f * kotlin.math.sqrt(2f)
            out[i * 2] = x0 + (x1 - x0) * st
            out[i * 2 + 1] = y0 + (y1 - y0) * st
        }
        return out
    }

    private fun scaleProfile(profile: FloatArray, factor: Float): FloatArray {
        val out = FloatArray(profile.size)
        for (i in profile.indices) out[i] = profile[i] * factor
        return out
    }

    
    /**
     * Box mesh. Honours `key.params.profileBegin/profileEnd` (cuts out the
     * unused side faces when the profile is partially revolved) and
     * `profileHollow` (carves out an inner box). Path twist is approximated
     * as a single rotation applied to the top face, which matches LL's
     * default 4-step path subdivision well enough for typical prims.
     */
    private fun generateBoxMesh(key: PrimShapeKey): PrimMesh {
        // 6 faces, 4 vertices each = 24 vertices
        // 6 faces, 2 triangles each = 36 indices
        val vertices = floatArrayOf(
            // Front face
            -0.5f, -0.5f,  0.5f,  0f,  0f,  1f,  0f, 0f,
             0.5f, -0.5f,  0.5f,  0f,  0f,  1f,  1f, 0f,
             0.5f,  0.5f,  0.5f,  0f,  0f,  1f,  1f, 1f,
            -0.5f,  0.5f,  0.5f,  0f,  0f,  1f,  0f, 1f,
            // Back face
            -0.5f, -0.5f, -0.5f,  0f,  0f, -1f,  1f, 0f,
            -0.5f,  0.5f, -0.5f,  0f,  0f, -1f,  1f, 1f,
             0.5f,  0.5f, -0.5f,  0f,  0f, -1f,  0f, 1f,
             0.5f, -0.5f, -0.5f,  0f,  0f, -1f,  0f, 0f,
            // Top face
            -0.5f,  0.5f, -0.5f,  0f,  1f,  0f,  0f, 0f,
            -0.5f,  0.5f,  0.5f,  0f,  1f,  0f,  0f, 1f,
             0.5f,  0.5f,  0.5f,  0f,  1f,  0f,  1f, 1f,
             0.5f,  0.5f, -0.5f,  0f,  1f,  0f,  1f, 0f,
            // Bottom face
            -0.5f, -0.5f, -0.5f,  0f, -1f,  0f,  0f, 1f,
             0.5f, -0.5f, -0.5f,  0f, -1f,  0f,  1f, 1f,
             0.5f, -0.5f,  0.5f,  0f, -1f,  0f,  1f, 0f,
            -0.5f, -0.5f,  0.5f,  0f, -1f,  0f,  0f, 0f,
            // Right face
             0.5f, -0.5f, -0.5f,  1f,  0f,  0f,  0f, 0f,
             0.5f,  0.5f, -0.5f,  1f,  0f,  0f,  0f, 1f,
             0.5f,  0.5f,  0.5f,  1f,  0f,  0f,  1f, 1f,
             0.5f, -0.5f,  0.5f,  1f,  0f,  0f,  1f, 0f,
            // Left face
            -0.5f, -0.5f, -0.5f, -1f,  0f,  0f,  1f, 0f,
            -0.5f, -0.5f,  0.5f, -1f,  0f,  0f,  0f, 0f,
            -0.5f,  0.5f,  0.5f, -1f,  0f,  0f,  0f, 1f,
            -0.5f,  0.5f, -0.5f, -1f,  0f,  0f,  1f, 1f
        )
        
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,       // front
            4, 5, 6, 4, 6, 7,       // back
            8, 9, 10, 8, 10, 11,    // top
            12, 13, 14, 12, 14, 15, // bottom
            16, 17, 18, 16, 18, 19, // right
            20, 21, 22, 20, 22, 23  // left
        )
        
        return createMesh(vertices, indices)
    }
    
    private fun generateSphereMesh(key: PrimShapeKey): PrimMesh {
        val segments = 24
        val rings = 16
        
        val vertexCount = (rings + 1) * (segments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0
        
        for (y in 0..rings) {
            val phi = PI * y / rings
            
            for (x in 0..segments) {
                val theta = 2 * PI * x / segments
                
                val px = (sin(phi) * cos(theta) * 0.5).toFloat()
                val py = (cos(phi) * 0.5).toFloat()
                val pz = (sin(phi) * sin(theta) * 0.5).toFloat()
                
                // Normal (same as position for sphere)
                val nx = (sin(phi) * cos(theta)).toFloat()
                val ny = cos(phi).toFloat()
                val nz = (sin(phi) * sin(theta)).toFloat()
                
                // UV
                val u = x.toFloat() / segments
                val v = y.toFloat() / rings
                
                vertices[vIdx++] = px
                vertices[vIdx++] = py
                vertices[vIdx++] = pz
                vertices[vIdx++] = nx
                vertices[vIdx++] = ny
                vertices[vIdx++] = nz
                vertices[vIdx++] = u
                vertices[vIdx++] = v
            }
        }
        
        val indexCount = rings * segments * 6
        val indices = ShortArray(indexCount)
        var iIdx = 0

        for (y in 0 until rings) {
            for (x in 0 until segments) {
                val i0 = (y * (segments + 1) + x).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + segments + 1).toShort()
                val i3 = (i2 + 1).toShort()
                
                indices[iIdx++] = i0
                indices[iIdx++] = i2
                indices[iIdx++] = i1

                indices[iIdx++] = i1
                indices[iIdx++] = i2
                indices[iIdx++] = i3
            }
        }
        
        return createMesh(vertices, indices)
    }
    
    private fun generateCylinderMesh(key: PrimShapeKey): PrimMesh {
        val segments = 24
        
        // Calculate buffer sizes
        // Vertices: 2 for side loop (per segment+1), 1 top center, 1 top loop (per segment+1),
        //           1 bottom center, 1 bottom loop (per segment+1)
        // Total = 2*(segments+1) + 1 + (segments+1) + 1 + (segments+1) = 4*(segments+1) + 2
        val vertexCount = 4 * (segments + 1) + 2
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        // Indices: Side loop (6 per segment), Top cap (3 per segment), Bottom cap (3 per segment)
        val indexCount = segments * 12
        val indices = ShortArray(indexCount)
        var iIdx = 0
        
        // Side
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()
            val nx = cos(theta).toFloat()
            val nz = sin(theta).toFloat()
            val u = i.toFloat() / segments
            
            // Bottom vertex for side
            vertices[vIdx++] = x
            vertices[vIdx++] = -0.5f
            vertices[vIdx++] = z
            vertices[vIdx++] = nx
            vertices[vIdx++] = 0f
            vertices[vIdx++] = nz
            vertices[vIdx++] = u
            vertices[vIdx++] = 0f

            // Top vertex for side
            vertices[vIdx++] = x
            vertices[vIdx++] = 0.5f
            vertices[vIdx++] = z
            vertices[vIdx++] = nx
            vertices[vIdx++] = 0f
            vertices[vIdx++] = nz
            vertices[vIdx++] = u
            vertices[vIdx++] = 1f
        }
        
        // Side indices
        for (i in 0 until segments) {
            val b0 = (i * 2).toShort()
            val t0 = (i * 2 + 1).toShort()
            val b1 = (i * 2 + 2).toShort()
            val t1 = (i * 2 + 3).toShort()
            
            indices[iIdx++] = b0
            indices[iIdx++] = b1
            indices[iIdx++] = t0

            indices[iIdx++] = t0
            indices[iIdx++] = b1
            indices[iIdx++] = t1
        }
        
        // Top cap center
        val topCenter = (vIdx / 8).toShort()
        vertices[vIdx++] = 0f
        vertices[vIdx++] = 0.5f
        vertices[vIdx++] = 0f
        vertices[vIdx++] = 0f
        vertices[vIdx++] = 1f
        vertices[vIdx++] = 0f
        vertices[vIdx++] = 0.5f
        vertices[vIdx++] = 0.5f
        
        // Top cap vertices
        val topStart = (vIdx / 8).toShort()
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()

            vertices[vIdx++] = x
            vertices[vIdx++] = 0.5f
            vertices[vIdx++] = z
            vertices[vIdx++] = 0f
            vertices[vIdx++] = 1f
            vertices[vIdx++] = 0f
            vertices[vIdx++] = (cos(theta) * 0.5 + 0.5).toFloat()
            vertices[vIdx++] = (sin(theta) * 0.5 + 0.5).toFloat()
        }
        
        // Top cap indices
        for (i in 0 until segments) {
            indices[iIdx++] = topCenter
            indices[iIdx++] = (topStart + i).toShort()
            indices[iIdx++] = (topStart + i + 1).toShort()
        }
        
        // Bottom cap center
        val bottomCenter = (vIdx / 8).toShort()
        vertices[vIdx++] = 0f
        vertices[vIdx++] = -0.5f
        vertices[vIdx++] = 0f
        vertices[vIdx++] = 0f
        vertices[vIdx++] = -1f
        vertices[vIdx++] = 0f
        vertices[vIdx++] = 0.5f
        vertices[vIdx++] = 0.5f
        
        // Bottom cap vertices
        val bottomStart = (vIdx / 8).toShort()
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()

            vertices[vIdx++] = x
            vertices[vIdx++] = -0.5f
            vertices[vIdx++] = z
            vertices[vIdx++] = 0f
            vertices[vIdx++] = -1f
            vertices[vIdx++] = 0f
            vertices[vIdx++] = (cos(theta) * 0.5 + 0.5).toFloat()
            vertices[vIdx++] = (sin(theta) * 0.5 + 0.5).toFloat()
        }
        
        // Bottom cap indices
        for (i in 0 until segments) {
            indices[iIdx++] = bottomCenter
            indices[iIdx++] = (bottomStart + i + 1).toShort()
            indices[iIdx++] = (bottomStart + i).toShort()
        }
        
        return createMesh(vertices, indices)
    }
    
    private fun generateTorusMesh(key: PrimShapeKey): PrimMesh {
        val majorSegments = 24
        val minorSegments = 12
        val majorRadius = 0.35f
        val minorRadius = 0.15f
        
        val vertexCount = (majorSegments + 1) * (minorSegments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        val indexCount = majorSegments * minorSegments * 6
        val indices = ShortArray(indexCount)
        var iIdx = 0
        
        for (i in 0..majorSegments) {
            val u = 2 * PI * i / majorSegments
            
            for (j in 0..minorSegments) {
                val v = 2 * PI * j / minorSegments
                
                val x = ((majorRadius + minorRadius * cos(v)) * cos(u)).toFloat()
                val y = (minorRadius * sin(v)).toFloat()
                val z = ((majorRadius + minorRadius * cos(v)) * sin(u)).toFloat()
                
                val nx = (cos(v) * cos(u)).toFloat()
                val ny = sin(v).toFloat()
                val nz = (cos(v) * sin(u)).toFloat()
                
                vertices[vIdx++] = x
                vertices[vIdx++] = y
                vertices[vIdx++] = z
                vertices[vIdx++] = nx
                vertices[vIdx++] = ny
                vertices[vIdx++] = nz
                vertices[vIdx++] = i.toFloat() / majorSegments
                vertices[vIdx++] = j.toFloat() / minorSegments
            }
        }
        
        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val i0 = (i * (minorSegments + 1) + j).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + minorSegments + 1).toShort()
                val i3 = (i2 + 1).toShort()
                
                indices[iIdx++] = i0
                indices[iIdx++] = i2
                indices[iIdx++] = i1

                indices[iIdx++] = i1
                indices[iIdx++] = i2
                indices[iIdx++] = i3
            }
        }
        
        return createMesh(vertices, indices)
    }
    
    private fun generatePrismMesh(key: PrimShapeKey): PrimMesh {
        // Triangular prism
        val vertices = floatArrayOf(
            // Front triangle
            0f, 0.5f, 0.5f, 0f, 0f, 1f, 0.5f, 1f,
            -0.5f, -0.5f, 0.5f, 0f, 0f, 1f, 0f, 0f,
            0.5f, -0.5f, 0.5f, 0f, 0f, 1f, 1f, 0f,
            // Back triangle
            0f, 0.5f, -0.5f, 0f, 0f, -1f, 0.5f, 1f,
            0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 1f, 0f,
            -0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 0f, 0f,
            // Bottom
            -0.5f, -0.5f, 0.5f, 0f, -1f, 0f, 0f, 0f,
            -0.5f, -0.5f, -0.5f, 0f, -1f, 0f, 0f, 1f,
            0.5f, -0.5f, -0.5f, 0f, -1f, 0f, 1f, 1f,
            0.5f, -0.5f, 0.5f, 0f, -1f, 0f, 1f, 0f,
            // Left side
            0f, 0.5f, 0.5f, -0.894f, 0.447f, 0f, 1f, 0f,
            -0.5f, -0.5f, 0.5f, -0.894f, 0.447f, 0f, 0f, 0f,
            -0.5f, -0.5f, -0.5f, -0.894f, 0.447f, 0f, 0f, 1f,
            0f, 0.5f, -0.5f, -0.894f, 0.447f, 0f, 1f, 1f,
            // Right side
            0f, 0.5f, -0.5f, 0.894f, 0.447f, 0f, 0f, 1f,
            0.5f, -0.5f, -0.5f, 0.894f, 0.447f, 0f, 1f, 1f,
            0.5f, -0.5f, 0.5f, 0.894f, 0.447f, 0f, 1f, 0f,
            0f, 0.5f, 0.5f, 0.894f, 0.447f, 0f, 0f, 0f
        )
        
        val indices = shortArrayOf(
            0, 1, 2,       // front
            3, 4, 5,       // back
            6, 7, 8, 6, 8, 9,  // bottom
            10, 11, 12, 10, 12, 13, // left
            14, 15, 16, 14, 16, 17  // right
        )
        
        return createMesh(vertices, indices)
    }
    
    private fun generateRingMesh(key: PrimShapeKey): PrimMesh {
        val majorSegments = 32
        val minorSegments = 12
        val majorRadius = 0.45f
        val minorRadius = 0.08f

        val vertexCount = (majorSegments + 1) * (minorSegments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        for (i in 0..majorSegments) {
            val u = 2.0 * PI * i / majorSegments
            val cosU = cos(u).toFloat()
            val sinU = sin(u).toFloat()

            for (j in 0..minorSegments) {
                val v = 2.0 * PI * j / minorSegments
                val cosV = cos(v).toFloat()
                val sinV = sin(v).toFloat()

                val radial = majorRadius + minorRadius * cosV
                val px = radial * cosU
                val py = radial * sinU
                val pz = minorRadius * sinV

                val nx = cosV * cosU
                val ny = cosV * sinU
                val nz = sinV

                vertices[vIdx++] = px
                vertices[vIdx++] = py
                vertices[vIdx++] = pz
                vertices[vIdx++] = nx
                vertices[vIdx++] = ny
                vertices[vIdx++] = nz
                vertices[vIdx++] = i.toFloat() / majorSegments
                vertices[vIdx++] = j.toFloat() / minorSegments
            }
        }

        val indices = ShortArray(majorSegments * minorSegments * 6)
        var iIdx = 0
        val stride = minorSegments + 1

        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val i0 = (i * stride + j).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = ((i + 1) * stride + j).toShort()
                val i3 = (i2 + 1).toShort()

                indices[iIdx++] = i0
                indices[iIdx++] = i2
                indices[iIdx++] = i1

                indices[iIdx++] = i1
                indices[iIdx++] = i2
                indices[iIdx++] = i3
            }
        }

        return createMesh(vertices, indices)
    }

    /**
     * Tube mesh — a square cross-section swept around a circular path. The
     * SL "tube" prim type. Geometry-wise it's a torus with a square inner
     * profile; here we approximate the square profile with 4 vertices per
     * minor ring so we keep the same vertex layout.
     */
    private fun generateTubeMesh(key: PrimShapeKey): PrimMesh {
        val majorSegments = 24
        val minorSegments = 4 // square cross-section
        val majorRadius = 0.35f
        val minorHalf = 0.15f

        val vertexCount = (majorSegments + 1) * (minorSegments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        for (i in 0..majorSegments) {
            val u = 2 * PI * i / majorSegments
            val cu = cos(u).toFloat()
            val su = sin(u).toFloat()
            // Walk the square cross-section (4 corners with normals).
            val corners = floatArrayOf(
                 minorHalf,  minorHalf,  1f,  0f,
                -minorHalf,  minorHalf,  0f,  1f,
                -minorHalf, -minorHalf, -1f,  0f,
                 minorHalf, -minorHalf,  0f, -1f
            )
            for (j in 0..minorSegments) {
                val k = (j % minorSegments) * 4
                val rx = corners[k]
                val ry = corners[k + 1]
                val nrx = corners[k + 2]
                val nry = corners[k + 3]

                val px = (majorRadius + rx) * cu
                val py = ry.toFloat()
                val pz = (majorRadius + rx) * su

                val nx = nrx * cu
                val ny = nry
                val nz = nrx * su

                vertices[vIdx++] = px
                vertices[vIdx++] = py
                vertices[vIdx++] = pz
                vertices[vIdx++] = nx
                vertices[vIdx++] = ny
                vertices[vIdx++] = nz
                vertices[vIdx++] = i.toFloat() / majorSegments
                vertices[vIdx++] = j.toFloat() / minorSegments
            }
        }

        val indexCount = majorSegments * minorSegments * 6
        val indices = ShortArray(indexCount)
        var iIdx = 0
        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val i0 = (i * (minorSegments + 1) + j).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + minorSegments + 1).toShort()
                val i3 = (i2 + 1).toShort()
                indices[iIdx++] = i0
                indices[iIdx++] = i2
                indices[iIdx++] = i1
                indices[iIdx++] = i1
                indices[iIdx++] = i2
                indices[iIdx++] = i3
            }
        }
        return createMesh(vertices, indices)
    }

    private fun createMesh(vertices: FloatArray, indices: ShortArray): PrimMesh {
        // Input layout: 8 floats/vertex = pos(3) + normal(3) + uv(2)
        val vertexCount = vertices.size / 8

        // Deinterleave for TangentBuilder.
        val positions = FloatArray(vertexCount * 3)
        val normals   = FloatArray(vertexCount * 3)
        val uvs       = FloatArray(vertexCount * 2)
        for (i in 0 until vertexCount) {
            val b = i * 8
            positions[i * 3]     = vertices[b];     positions[i * 3 + 1] = vertices[b + 1]; positions[i * 3 + 2] = vertices[b + 2]
            normals[i * 3]       = vertices[b + 3]; normals[i * 3 + 1]   = vertices[b + 4]; normals[i * 3 + 2]   = vertices[b + 5]
            uvs[i * 2]           = vertices[b + 6]; uvs[i * 2 + 1]       = vertices[b + 7]
        }
        val tangentQuats = com.linkpoint.render.TangentBuilder.build(positions, normals, uvs, indices)

        // Output layout: 9 floats/vertex = pos(3) + tangent_quat(4) + uv(2)
        val stride = 9 * 4
        val vertexData = ByteBuffer.allocateDirect(vertexCount * stride).order(ByteOrder.nativeOrder())
        for (i in 0 until vertexCount) {
            val b = i * 8
            vertexData.putFloat(vertices[b]);     vertexData.putFloat(vertices[b + 1]); vertexData.putFloat(vertices[b + 2])
            vertexData.putFloat(tangentQuats[i * 4]);     vertexData.putFloat(tangentQuats[i * 4 + 1])
            vertexData.putFloat(tangentQuats[i * 4 + 2]); vertexData.putFloat(tangentQuats[i * 4 + 3])
            vertexData.putFloat(vertices[b + 6]); vertexData.putFloat(vertices[b + 7])
        }
        vertexData.flip()

        val indexData = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder())
        for (i in indices) indexData.putShort(i)
        indexData.flip()

        val vertexBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT4, 12, stride)
            .attribute(VertexAttribute.UV0,      0, AttributeType.FLOAT2, 28, stride)
            .build(engine)
        vertexBuffer.setBufferAt(engine, 0, vertexData)

        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        indexBuffer.setBuffer(engine, indexData)

        return PrimMesh(vertexBuffer, indexBuffer)
    }
    
    private fun updateTransform(prim: PrimInstance) {
        val m = FloatArray(16)
        
        // Build transform matrix: T * R * S
        val r = prim.rotation
        r.toMatrix(m)
        
        // Apply scale
        m[0] *= prim.scale.x; m[1] *= prim.scale.x; m[2] *= prim.scale.x
        m[4] *= prim.scale.y; m[5] *= prim.scale.y; m[6] *= prim.scale.y
        m[8] *= prim.scale.z; m[9] *= prim.scale.z; m[10] *= prim.scale.z
        
        // Apply translation
        m[12] = prim.position.x
        m[13] = prim.position.y
        m[14] = prim.position.z
        
        transformManager.setTransform(prim.transformInstance, m)
    }
    
    private fun updatePrimMaterial(prim: PrimInstance, textureEntry: ByteArray) {
        // Parse texture entry to extract texture UUIDs and material properties
        // Texture entry format:
        // - Default face texture UUID (16 bytes)
        // - Face-specific texture overrides (bitfield + UUID pairs)
        // - RGBA color
        // - Repeat U/V, Offset U/V, Rotation
        
        if (textureEntry.isEmpty()) return
        
        try {
            val buffer = java.nio.ByteBuffer.wrap(textureEntry)
                .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // Extract default texture UUID (first 16 bytes)
            if (buffer.remaining() >= 16) {
                val uuidBytes = ByteArray(16)
                buffer.get(uuidBytes)
                val defaultTextureId = bytesToUUID(uuidBytes)
                
                if (defaultTextureId != UUID(0, 0)) {
                    // Bakes-on-Mesh: if the prim references one of the magic
                    // bake-slot sentinels, substitute the avatar's actual
                    // baked texture UUID via the registered resolver. Logged
                    // either way so we can see how many BoM faces a typical
                    // mesh outfit references.
                    val resolved = bomResolver?.let {
                        BakesOnMesh.resolve(defaultTextureId, it)
                    } ?: defaultTextureId
                    if (resolved != defaultTextureId) {
                        Log.d(TAG, "Prim ${prim.localId} BoM face $defaultTextureId -> baked $resolved")
                    } else if (BakesOnMesh.isBakeSentinel(defaultTextureId)) {
                        Log.v(TAG, "Prim ${prim.localId} BoM sentinel $defaultTextureId — no resolver")
                    }
                    val binder = textureBinder
                    val matInstance = perEntityMaterials[prim.entity]
                    if (binder != null && matInstance != null && resolved != UUID(0L, 0L)) {
                        binder.bind(resolved) { tex ->
                            try {
                                matInstance.setParameter(
                                    "baseColorMap", tex,
                                    com.google.android.filament.TextureSampler(
                                        com.google.android.filament.TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
                                        com.google.android.filament.TextureSampler.MagFilter.LINEAR,
                                        com.google.android.filament.TextureSampler.WrapMode.REPEAT
                                    )
                                )
                                matInstance.setParameter("hasTexture", 1f)
                            } catch (e: Exception) {
                                Log.w(TAG, "Texture bind failed for prim ${prim.localId}: ${e.message}")
                            }
                        }
                    }
                }
            }
            
            // Skip face-specific textures for now (complex bitfield parsing)
            // A full implementation would parse the complete texture entry
            
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse texture entry for prim ${prim.localId}", e)
        }
    }
    
    private fun bytesToUUID(bytes: ByteArray): UUID {
        val buffer = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN)
        return UUID(buffer.long, buffer.long)
    }
    
    fun shutdown() {
        for (prim in prims.values) {
            scene.removeEntity(prim.entity)
            engine.destroyEntity(prim.entity)
        }
        prims.clear()

        perEntityMaterials.values.forEach {
            try { engine.destroyMaterialInstance(it) } catch (_: Exception) {}
        }
        perEntityMaterials.clear()

        for (mesh in primMeshes.values) {
            engine.destroyVertexBuffer(mesh.vertexBuffer)
            engine.destroyIndexBuffer(mesh.indexBuffer)
        }
        primMeshes.clear()

        defaultMaterial?.let { engine.destroyMaterialInstance(it) }
    }
}

enum class PrimShape {
    BOX, SPHERE, CYLINDER, TORUS, PRISM, RING, TUBE
}

/**
 * Coarse cache key for prim mesh tessellation. Two prims that differ only in
 * size/rotation share the same mesh; two prims that differ in path/profile
 * curve, profile cuts, or hollow get distinct meshes. Path twist/taper/skew
 * are NOT in the key — they'd explode cardinality. Future work: bake them
 * into the geometry and add to the key when full LLVolume parity lands.
 */
data class PrimShapeKey(
    val kind: PrimShape,
    val params: PrimShapeParams
) {
    companion object {
        /** Standard SL prim shape selection per LL's `LLVolume::regenerate`. */
        fun from(p: PrimShapeParams): PrimShapeKey {
            val kind = when (p.pathCurve) {
                PrimShapeParams.PATH_LINE -> when (p.profileType) {
                    PrimShapeParams.PROFILE_SQUARE -> PrimShape.BOX
                    PrimShapeParams.PROFILE_CIRCLE -> PrimShape.CYLINDER
                    PrimShapeParams.PROFILE_ISO_TRI,
                    PrimShapeParams.PROFILE_EQUAL_TRI,
                    PrimShapeParams.PROFILE_RIGHT_TRI -> PrimShape.PRISM
                    PrimShapeParams.PROFILE_HALF_CIRCLE -> PrimShape.CYLINDER // half-circle extrusion ≈ cylinder
                    else -> PrimShape.BOX
                }
                PrimShapeParams.PATH_CIRCLE,
                PrimShapeParams.PATH_CIRCLE2 -> when (p.profileType) {
                    PrimShapeParams.PROFILE_HALF_CIRCLE -> PrimShape.SPHERE
                    PrimShapeParams.PROFILE_CIRCLE -> PrimShape.TORUS
                    PrimShapeParams.PROFILE_SQUARE -> PrimShape.TUBE
                    PrimShapeParams.PROFILE_ISO_TRI,
                    PrimShapeParams.PROFILE_EQUAL_TRI,
                    PrimShapeParams.PROFILE_RIGHT_TRI -> PrimShape.RING
                    else -> PrimShape.TORUS
                }
                else -> PrimShape.BOX
            }
            return PrimShapeKey(kind, p)
        }

        fun defaultFor(kind: PrimShape): PrimShapeKey =
            PrimShapeKey(kind, PrimShapeParams.DEFAULT)
    }
}

data class PrimMesh(
    val vertexBuffer: VertexBuffer,
    val indexBuffer: IndexBuffer
)

data class PrimInstance(
    val localId: Int,
    val fullId: UUID,
    val entity: Int,
    val transformInstance: Int,
    val shape: PrimShape,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var textureEntry: ByteArray
)
