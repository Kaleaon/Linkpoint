package com.linkpoint.render.scene

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Manages the 3D scene graph for the world
 */
class SceneManager(
    private val engine: Engine,
    private val scene: Scene,
    private val context: android.content.Context? = null
) {

    companion object {
        private const val TAG = "SceneManager"

        // Articulated humanoid placeholder dimensions (meters).
        // Eight body segments (head, torso, two upper arms, two upper legs,
        // two lower legs) approximate the standard SL avatar silhouette
        // until proper skinned LL meshes land.
        private const val AVATAR_TOTAL_HEIGHT = 1.85f
        private const val AVATAR_HEAD_RADIUS = 0.13f
        private const val AVATAR_TORSO_HEIGHT = 0.62f
        private const val AVATAR_TORSO_RADIUS = 0.18f
        private const val AVATAR_LIMB_RADIUS = 0.07f
        private const val AVATAR_UPPER_ARM_LEN = 0.32f
        private const val AVATAR_LOWER_ARM_LEN = 0.28f
        private const val AVATAR_UPPER_LEG_LEN = 0.42f
        private const val AVATAR_LOWER_LEG_LEN = 0.42f
    }

    // Object tracking
    private val sceneObjects = ConcurrentHashMap<UUID, SceneObject>()
    private val avatars = ConcurrentHashMap<UUID, AvatarObject>()

    // Entity manager
    private val entityManager = EntityManager.get()

    // Current camera position
    private var cameraPosition = LLVector3(128f, 128f, 30f)
    private var cameraTarget = LLVector3(128f, 128f, 0f)

    // Optional DrawDistanceManager. When set, getVisibleObjects /
    // getVisibleAvatars consult its (live, user-tunable) draw distance
    // instead of a hard-coded 256m default. Decoupled via lambdas so the
    // render package doesn't have to take a hard dependency on the manager
    // API surface.
    @Volatile private var objectDrawDistanceProvider: (() -> Float)? = null
    @Volatile private var avatarDrawDistanceProvider: (() -> Float)? = null
    @Volatile private var visibilityStatsSink: ((visible: Int, total: Int, culled: Int) -> Unit)? = null

    /**
     * Wire a [DrawDistanceManager] (or any equivalent source) into
     * visibility queries. Pass nulls to detach.
     *
     * Lifecycle: typically called once from the app on init, before the
     * first frame is rendered, so the SceneManager already has the manager
     * in place by the time SceneDataHandler starts pushing objects.
     */
    fun setDrawDistanceProviders(
        objectDistance: (() -> Float)?,
        avatarDistance: (() -> Float)?,
        statsSink: ((visible: Int, total: Int, culled: Int) -> Unit)? = null
    ) {
        objectDrawDistanceProvider = objectDistance
        avatarDrawDistanceProvider = avatarDistance
        visibilityStatsSink = statsSink
    }

    // Avatar segment meshes (built once and shared across all avatars).
    private var avatarMaterial: MaterialInstance? = null
    private var headMesh: AvatarMesh? = null
    private var torsoMesh: AvatarMesh? = null
    private var upperArmMesh: AvatarMesh? = null
    private var lowerArmMesh: AvatarMesh? = null
    private var upperLegMesh: AvatarMesh? = null
    private var lowerLegMesh: AvatarMesh? = null

    /**
     * If non-null, the system-avatar body is rendered as a single
     * Filament entity using the actual LL `.llm` mesh data bundled in
     * `assets/character/`. The articulated capsule body is then skipped
     * for those avatars. Built lazily from setAvatarMaterial when assets
     * are available.
     */
    private var systemAvatarBodyMesh: AvatarMesh? = null
    private var systemAvatarHeadMesh: AvatarMesh? = null

    /**
     * CPU-side combined LLMesh (upper_body + lower_body) used as the base
     * for VisualParam-driven morphs. Kept alongside the GPU buffers so we
     * can compute morphed positions/normals without re-parsing the .llm
     * files on every appearance update.
     */
    private var systemAvatarBodyBaseLLMesh: com.linkpoint.avatar.LLMeshLoader.LLMesh? = null
    private var systemAvatarHeadBaseLLMesh: com.linkpoint.avatar.LLMeshLoader.LLMesh? = null

    /**
     * Per-avatar morphed mesh state. Lazy: avatars whose AvatarAppearance
     * hasn't arrived (or whose visual params are at defaults) keep using
     * the shared system body buffer.
     */
    private data class MorphedMesh(
        val bodyMesh: AvatarMesh,
        val bodyEntity: Int,
        val headMesh: AvatarMesh?,
        val headEntity: Int,
        val morphedPositions: FloatArray,
        val morphedNormals: FloatArray,
        val headMorphedPositions: FloatArray,
        val headMorphedNormals: FloatArray
    )
    private val perAvatarMorphed = ConcurrentHashMap<UUID, MorphedMesh>()

    /**
     * Provide the lit material used to render avatar placeholder geometry.
     * Should be called once after MaterialLoader.initialize() returns success.
     * Existing avatars receive the renderable retroactively.
     */
    fun setAvatarMaterial(material: Material) {
        avatarMaterial = material.createInstance()
        // Articulated capsule fallback (kept around for when the system
        // avatar .llm assets are missing or fail to parse).
        headMesh = buildSphereMesh(AVATAR_HEAD_RADIUS)
        torsoMesh = buildCapsuleMesh(AVATAR_TORSO_RADIUS, AVATAR_TORSO_HEIGHT)
        upperArmMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS, AVATAR_UPPER_ARM_LEN)
        lowerArmMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS, AVATAR_LOWER_ARM_LEN)
        upperLegMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS * 1.3f, AVATAR_UPPER_LEG_LEN)
        lowerLegMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS * 1.2f, AVATAR_LOWER_LEG_LEN)

        // Try the real LL system avatar meshes from assets/character/. We
        // bundle three: upper_body (torso + arms), lower_body (legs),
        // and head. If any of them parse OK we render that geometry
        // instead of the placeholder capsules.
        val ctx = context
        if (ctx != null) {
            val upper: com.linkpoint.avatar.LLMeshLoader.LLMesh? =
                com.linkpoint.avatar.LLMeshLoader.load(ctx, "avatar_upper_body.llm")
            val lower: com.linkpoint.avatar.LLMeshLoader.LLMesh? =
                com.linkpoint.avatar.LLMeshLoader.load(ctx, "avatar_lower_body.llm")
            val headLL: com.linkpoint.avatar.LLMeshLoader.LLMesh? =
                com.linkpoint.avatar.LLMeshLoader.load(ctx, "avatar_head.llm")
            if (upper != null && lower != null) {
                val parts: List<com.linkpoint.avatar.LLMeshLoader.LLMesh> = listOf(upper, lower)
                val combined = combineLLMeshes(parts)
                val combinedWithIndices = combined.copy(indices = combineIndices(parts))
                systemAvatarBodyBaseLLMesh = combinedWithIndices
                // Prefer the skinned upload path when the mesh has bone
                // data; falls back to the unskinned upload otherwise so
                // we still render something visible.
                systemAvatarBodyMesh = uploadSkinnedAvatarMesh(combinedWithIndices)
                    ?: uploadAvatarMesh(
                        interleaveLLMesh(combinedWithIndices),
                        combinedWithIndices.indices
                    )
                Log.i(TAG, "Loaded SL system avatar body mesh " +
                    "(${combinedWithIndices.vertexCount} verts, " +
                    "${combinedWithIndices.morphs.size} morphs, " +
                    "${combinedWithIndices.jointNames.size} joints)")
            }
            if (headLL != null) {
                systemAvatarHeadBaseLLMesh = headLL
                systemAvatarHeadMesh = uploadSkinnedAvatarMesh(headLL)
                    ?: uploadAvatarMesh(interleaveLLMesh(headLL), headLL.indices)
                Log.i(TAG, "Loaded SL system avatar head mesh " +
                    "(${headLL.vertexCount} verts, " +
                    "${headLL.morphs.size} morphs, " +
                    "${headLL.jointNames.size} joints)")
            }
        }
        Log.i(TAG, "Avatar material set; body meshes ready (system mesh=${systemAvatarBodyMesh != null})")

        // Retroactively attach renderables to any avatars that came in before
        // the material was wired up.
        avatars.values.forEach { avatar ->
            if (avatar.renderable == 0) {
                attachAvatarRenderable(avatar)
            }
        }
    }
    
    /**
     * Add or update an object in the scene
     */
    fun updateObject(
        objectId: UUID,
        localId: Int,
        position: LLVector3,
        rotation: LLQuaternion,
        scale: LLVector3,
        parentId: UUID? = null
    ) {
        val existing = sceneObjects[objectId]
        
        if (existing != null) {
            // Update existing object
            existing.position = position
            existing.rotation = rotation
            existing.scale = scale
            updateTransform(existing)
        } else {
            // Create new object
            val entity = entityManager.create()
            val obj = SceneObject(
                uuid = objectId,
                localId = localId,
                entity = entity,
                position = position,
                rotation = rotation,
                scale = scale,
                parentId = parentId
            )
            sceneObjects[objectId] = obj
            
            // Add to scene (will be renderable once mesh is loaded)
            scene.addEntity(entity)
            Log.d(TAG, "Added object: $objectId at $position")
        }
    }
    
    /**
     * Remove an object from the scene
     */
    fun removeObject(objectId: UUID) {
        sceneObjects.remove(objectId)?.let { obj ->
            scene.removeEntity(obj.entity)
            entityManager.destroy(obj.entity)
            Log.d(TAG, "Removed object: $objectId")
        }
    }
    
    /**
     * Add or update an avatar in the scene
     */
    fun updateAvatar(
        agentId: UUID,
        position: LLVector3,
        rotation: LLQuaternion,
        animations: List<UUID> = emptyList()
    ) {
        val existing = avatars[agentId]

        if (existing != null) {
            existing.position = position
            existing.rotation = rotation
            existing.animations = animations
            updateTransform(existing)
        } else {
            val entity = entityManager.create()
            val avatar = AvatarObject(
                agentId = agentId,
                entity = entity,
                position = position,
                rotation = rotation,
                animations = animations
            )
            avatars[agentId] = avatar
            attachAvatarRenderable(avatar)
            scene.addEntity(entity)
            updateTransform(avatar)
            Log.d(TAG, "Added avatar: $agentId at $position")
        }
    }

    /**
     * Build the RenderableComponents for an avatar's articulated body.
     * Each avatar gets a root entity (the existing `avatar.entity`) plus
     * 8 child entities: head, torso, two upper arms, two upper legs, two
     * lower legs. The root holds the avatar's world transform (from the
     * AvatarUpdate); each child has a fixed local offset under the root.
     *
     * Skipping the avatar (no-op) is fine if the material isn't ready yet —
     * the entity is still in the scene; child renderables get attached
     * retroactively from setAvatarMaterial().
     *
     * Real skinned skeletal animation would replace the fixed local offsets
     * with the AvatarSkeleton.Bone.worldMatrix per frame; the structure here
     * is set up to take that swap.
     */
    private fun attachAvatarRenderable(avatar: AvatarObject) {
        val material = avatarMaterial ?: return

        // Preferred path: render the actual SL system avatar mesh from the
        // bundled .llm files. Falls through to the articulated capsule
        // fallback if those failed to load (the LLM parser logs why).
        val sysBody = systemAvatarBodyMesh
        val sysHead = systemAvatarHeadMesh
        if (sysBody != null) {
            try {
                engine.transformManager.create(avatar.entity)
                val bodyJoints = systemAvatarBodyBaseLLMesh?.jointNames?.size ?: 0
                val headJoints = systemAvatarHeadBaseLLMesh?.jointNames?.size ?: 0
                attachLLMeshSegment(avatar, sysBody, material,
                    bound = 1.0f, zOffset = 0f,
                    skinningBoneCount = bodyJoints)
                if (sysHead != null) {
                    attachLLMeshSegment(avatar, sysHead, material,
                        bound = 0.4f, zOffset = 0f,
                        skinningBoneCount = headJoints)
                }
                avatar.renderable = avatar.entity
                // Mark this avatar as system-meshed so applyAvatarPose
                // doesn't try to drive the (non-existent) capsule
                // segments off bone matrices.
                avatar.bodySegmentBones.clear()
                return
            } catch (e: Exception) {
                Log.w(TAG, "System avatar mesh attach failed for ${avatar.agentId}; falling back: ${e.message}")
            }
        }

        val head = headMesh ?: return
        val torso = torsoMesh ?: return
        val upArm = upperArmMesh ?: return
        val loArm = lowerArmMesh ?: return
        val upLeg = upperLegMesh ?: return
        val loLeg = lowerLegMesh ?: return
        try {
            // Root entity: already in `avatar.entity`. Give it a transform so
            // child segments inherit the avatar's world position/rotation.
            engine.transformManager.create(avatar.entity)

            // Body segment offsets (entity-local, meters). Z is up; the avatar
            // root sits at the feet so segments stack upward from z=0.
            data class Seg(val mesh: AvatarMesh, val z: Float, val x: Float = 0f, val height: Float = 0f, val radius: Float = 0f)
            val bottomOfTorso = AVATAR_LOWER_LEG_LEN + AVATAR_UPPER_LEG_LEN
            val segments = listOf(
                Seg(head,  bottomOfTorso + AVATAR_TORSO_HEIGHT + AVATAR_HEAD_RADIUS,
                    radius = AVATAR_HEAD_RADIUS),
                Seg(torso, bottomOfTorso, height = AVATAR_TORSO_HEIGHT, radius = AVATAR_TORSO_RADIUS),
                Seg(upArm, bottomOfTorso + AVATAR_TORSO_HEIGHT - AVATAR_UPPER_ARM_LEN,
                    x = AVATAR_TORSO_RADIUS + AVATAR_LIMB_RADIUS,
                    height = AVATAR_UPPER_ARM_LEN, radius = AVATAR_LIMB_RADIUS),
                Seg(upArm, bottomOfTorso + AVATAR_TORSO_HEIGHT - AVATAR_UPPER_ARM_LEN,
                    x = -(AVATAR_TORSO_RADIUS + AVATAR_LIMB_RADIUS),
                    height = AVATAR_UPPER_ARM_LEN, radius = AVATAR_LIMB_RADIUS),
                Seg(loArm, bottomOfTorso + AVATAR_TORSO_HEIGHT - AVATAR_UPPER_ARM_LEN - AVATAR_LOWER_ARM_LEN,
                    x = AVATAR_TORSO_RADIUS + AVATAR_LIMB_RADIUS,
                    height = AVATAR_LOWER_ARM_LEN, radius = AVATAR_LIMB_RADIUS),
                Seg(loArm, bottomOfTorso + AVATAR_TORSO_HEIGHT - AVATAR_UPPER_ARM_LEN - AVATAR_LOWER_ARM_LEN,
                    x = -(AVATAR_TORSO_RADIUS + AVATAR_LIMB_RADIUS),
                    height = AVATAR_LOWER_ARM_LEN, radius = AVATAR_LIMB_RADIUS),
                Seg(upLeg, AVATAR_LOWER_LEG_LEN, x = AVATAR_LIMB_RADIUS,
                    height = AVATAR_UPPER_LEG_LEN, radius = AVATAR_LIMB_RADIUS * 1.3f),
                Seg(upLeg, AVATAR_LOWER_LEG_LEN, x = -AVATAR_LIMB_RADIUS,
                    height = AVATAR_UPPER_LEG_LEN, radius = AVATAR_LIMB_RADIUS * 1.3f),
                Seg(loLeg, 0f, x = AVATAR_LIMB_RADIUS,
                    height = AVATAR_LOWER_LEG_LEN, radius = AVATAR_LIMB_RADIUS * 1.2f),
                Seg(loLeg, 0f, x = -AVATAR_LIMB_RADIUS,
                    height = AVATAR_LOWER_LEG_LEN, radius = AVATAR_LIMB_RADIUS * 1.2f)
            )

            for (seg in segments) {
                val child = entityManager.create()
                val r = if (seg.radius > 0f) seg.radius else 0.2f
                val h = if (seg.height > 0f) seg.height else (seg.radius * 2f)
                RenderableManager.Builder(1)
                    .boundingBox(Box(-r, -r, 0f, r, r, h.coerceAtLeast(r * 2f)))
                    .geometry(0, RenderableManager.PrimitiveType.TRIANGLES,
                        seg.mesh.vertexBuffer, seg.mesh.indexBuffer)
                    .material(0, material)
                    .culling(true)
                    .receiveShadows(true)
                    .castShadows(true)
                    .build(engine, child)

                val ti = engine.transformManager.create(child)
                val tm = engine.transformManager
                val rootInstance = tm.getInstance(avatar.entity)
                if (rootInstance != 0 && ti != 0) {
                    tm.setParent(ti, rootInstance)
                }
                // Local offset: x sideways, z height (Z up).
                val localTransform = floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    seg.x, 0f, seg.z, 1f
                )
                if (ti != 0) tm.setTransform(ti, localTransform)

                scene.addEntity(child)
                avatar.bodySegmentEntities.add(child)
            }
            // Order matches the segments list above; each entry binds the
            // corresponding body segment to a skeleton bone so
            // applyAvatarPose() can drive segment transforms from animation.
            avatar.bodySegmentBones.addAll(listOf(
                "mHead", "mTorso",
                "mShoulderLeft", "mShoulderRight",
                "mElbowLeft", "mElbowRight",
                "mHipLeft", "mHipRight",
                "mKneeLeft", "mKneeRight"
            ))
            avatar.renderable = avatar.entity // mark as built
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach articulated avatar for ${avatar.agentId}", e)
        }
    }

    /**
     * Build a sphere mesh of the given radius. Same vertex layout
     * (POSITION+TANGENTS+UV0) as PrimRenderer so it shares the lit material.
     */
    /**
     * Attach the supplied AvatarMesh as a child entity of the avatar root,
     * inheriting the avatar's world transform via TransformManager parenting.
     * Used for the LL system-avatar `.llm` paths (one entity per body part)
     * so we don't have to merge head + body into a single buffer.
     */
    private fun attachLLMeshSegment(
        avatar: AvatarObject,
        mesh: AvatarMesh,
        material: MaterialInstance,
        bound: Float,
        zOffset: Float,
        skinningBoneCount: Int = 0
    ) {
        val child = entityManager.create()
        val builder = RenderableManager.Builder(1)
            .boundingBox(Box(-bound, -bound, -bound, bound, bound, bound))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES,
                mesh.vertexBuffer, mesh.indexBuffer)
            .material(0, material)
            .culling(true)
            .receiveShadows(true)
            .castShadows(true)
        // Enable Filament's GPU skinning path when the mesh has bone data.
        // Filament will use the BONE_INDICES + BONE_WEIGHTS attributes on
        // the VertexBuffer (declared at slot 1 in uploadSkinnedAvatarMesh)
        // and our setBonesAsMatrices() per-frame call to compute final
        // vertex positions on the GPU.
        if (skinningBoneCount > 0) {
            try {
                builder.skinning(skinningBoneCount)
            } catch (e: Exception) {
                Log.w(TAG, "skinning(${skinningBoneCount}) builder call rejected: ${e.message}")
            }
        }
        builder.build(engine, child)

        val tm = engine.transformManager
        val ti = tm.create(child)
        val rootInstance = tm.getInstance(avatar.entity)
        if (rootInstance != 0 && ti != 0) {
            tm.setParent(ti, rootInstance)
        }
        if (zOffset != 0f && ti != 0) {
            val xform = floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, zOffset, 1f
            )
            tm.setTransform(ti, xform)
        }

        scene.addEntity(child)
        avatar.bodySegmentEntities.add(child)
    }

    private fun buildSphereMesh(radius: Float): AvatarMesh {
        val rings = 12
        val segments = 18
        val verts = mutableListOf<Float>()
        val idx = mutableListOf<Short>()
        for (ring in 0..rings) {
            val phi = -PI / 2.0 + PI * ring / rings
            val cp = cos(phi).toFloat()
            val sp = sin(phi).toFloat()
            for (seg in 0..segments) {
                val theta = 2.0 * PI * seg / segments
                val ct = cos(theta).toFloat()
                val st = sin(theta).toFloat()
                val nx = cp * ct; val ny = cp * st; val nz = sp
                val px = nx * radius; val py = ny * radius
                val pz = nz * radius + radius
                verts += floatArrayOf(
                    px, py, pz, nx, ny, nz,
                    seg.toFloat() / segments, ring.toFloat() / rings
                ).toList()
            }
        }
        val verticesPerRing = segments + 1
        for (r in 0 until rings) {
            for (s in 0 until segments) {
                val i0 = (r * verticesPerRing + s).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = ((r + 1) * verticesPerRing + s).toShort()
                val i3 = (i2 + 1).toShort()
                idx += listOf(i0, i2, i1, i1, i2, i3)
            }
        }
        return uploadAvatarMesh(verts.toFloatArray(), idx.toShortArray())
    }

    /**
     * Build a capsule (cylinder + two hemispherical caps) of the given
     * radius and height. Capsule's bottom hemisphere centre sits at z=0,
     * cylinder spans z=0..height, top hemisphere on top.
     */
    private fun buildCapsuleMesh(radius: Float, height: Float): AvatarMesh {
        val rings = 8
        val segments = 14
        val verts = mutableListOf<Float>()
        val idx = mutableListOf<Short>()
        // Lower hemisphere
        for (ring in 0..rings / 2) {
            val phi = -PI / 2.0 + PI * ring / rings
            val cp = cos(phi).toFloat(); val sp = sin(phi).toFloat()
            for (seg in 0..segments) {
                val theta = 2.0 * PI * seg / segments
                val ct = cos(theta).toFloat(); val st = sin(theta).toFloat()
                val nx = cp * ct; val ny = cp * st; val nz = sp
                val px = nx * radius; val py = ny * radius; val pz = nz * radius
                verts += floatArrayOf(
                    px, py, pz, nx, ny, nz,
                    seg.toFloat() / segments, ring.toFloat() / rings * 0.5f
                ).toList()
            }
        }
        // Upper hemisphere offset by `height`
        for (ring in 0..rings / 2) {
            val phi = PI * ring / rings
            val cp = cos(phi).toFloat(); val sp = sin(phi).toFloat()
            for (seg in 0..segments) {
                val theta = 2.0 * PI * seg / segments
                val ct = cos(theta).toFloat(); val st = sin(theta).toFloat()
                val nx = cp * ct; val ny = cp * st; val nz = sp
                val px = nx * radius; val py = ny * radius
                val pz = nz * radius + height
                verts += floatArrayOf(
                    px, py, pz, nx, ny, nz,
                    seg.toFloat() / segments, 0.5f + ring.toFloat() / rings * 0.5f
                ).toList()
            }
        }
        val ringsTotal = rings + 1
        val verticesPerRing = segments + 1
        for (r in 0 until ringsTotal - 1) {
            for (s in 0 until segments) {
                val i0 = (r * verticesPerRing + s).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = ((r + 1) * verticesPerRing + s).toShort()
                val i3 = (i2 + 1).toShort()
                idx += listOf(i0, i2, i1, i1, i2, i3)
            }
        }
        return uploadAvatarMesh(verts.toFloatArray(), idx.toShortArray())
    }

    /**
     * Concatenate several LLMesh-loaded meshes into a single combined vertex
     * list. Returned array is laid out [pos pos pos normal normal normal uv uv]
     * per vertex, ready to be uploaded by uploadAvatarMesh().
     *
     * Index lists are concatenated separately by combineIndices() — they need
     * to be offset by the cumulative vertex count of preceding meshes.
     */
    private fun combineLLMeshes(meshes: List<com.linkpoint.avatar.LLMeshLoader.LLMesh>): com.linkpoint.avatar.LLMeshLoader.LLMesh {
        if (meshes.size == 1) return meshes[0]
        val totalVerts = meshes.sumOf { it.vertexCount }
        val pos = FloatArray(totalVerts * 3)
        val nor = FloatArray(totalVerts * 3)
        val uvs = FloatArray(totalVerts * 2)
        var v = 0
        for (m in meshes) {
            System.arraycopy(m.positions, 0, pos, v * 3, m.vertexCount * 3)
            System.arraycopy(m.normals,   0, nor, v * 3, m.vertexCount * 3)
            System.arraycopy(m.uvs,       0, uvs, v * 2, m.vertexCount * 2)
            v += m.vertexCount
        }

        // Merge morph targets — vertex indices in the second+ mesh's morphs
        // need to be offset by the cumulative vertex count of preceding
        // meshes so they reference the right verts in the combined buffer.
        val mergedMorphs = HashMap<String, com.linkpoint.avatar.LLMeshLoader.MorphTarget>()
        var vOffset = 0
        for (m in meshes) {
            for ((name, morph) in m.morphs) {
                if (vOffset == 0) {
                    mergedMorphs[name] = morph
                } else {
                    val shifted = IntArray(morph.vertexIndices.size)
                    for (i in shifted.indices) shifted[i] = morph.vertexIndices[i] + vOffset
                    val existing = mergedMorphs[name]
                    if (existing == null) {
                        mergedMorphs[name] = morph.copy(vertexIndices = shifted)
                    } else {
                        // Two source meshes both define the same morph;
                        // concatenate their sparse delta lists so a single
                        // VisualParam drives both halves.
                        val newIdx = IntArray(existing.vertexIndices.size + shifted.size)
                        System.arraycopy(existing.vertexIndices, 0, newIdx, 0, existing.vertexIndices.size)
                        System.arraycopy(shifted, 0, newIdx, existing.vertexIndices.size, shifted.size)
                        val newCoord = FloatArray(existing.coordDeltas.size + morph.coordDeltas.size)
                        System.arraycopy(existing.coordDeltas, 0, newCoord, 0, existing.coordDeltas.size)
                        System.arraycopy(morph.coordDeltas, 0, newCoord, existing.coordDeltas.size, morph.coordDeltas.size)
                        val newNormal = FloatArray(existing.normalDeltas.size + morph.normalDeltas.size)
                        System.arraycopy(existing.normalDeltas, 0, newNormal, 0, existing.normalDeltas.size)
                        System.arraycopy(morph.normalDeltas, 0, newNormal, existing.normalDeltas.size, morph.normalDeltas.size)
                        val newUv = FloatArray(existing.uvDeltas.size + morph.uvDeltas.size)
                        System.arraycopy(existing.uvDeltas, 0, newUv, 0, existing.uvDeltas.size)
                        System.arraycopy(morph.uvDeltas, 0, newUv, existing.uvDeltas.size, morph.uvDeltas.size)
                        mergedMorphs[name] = com.linkpoint.avatar.LLMeshLoader.MorphTarget(
                            name = name, vertexIndices = newIdx,
                            coordDeltas = newCoord, normalDeltas = newNormal,
                            uvDeltas = newUv
                        )
                    }
                }
            }
            vOffset += m.vertexCount
        }

        return com.linkpoint.avatar.LLMeshLoader.LLMesh(
            name = "combined", positions = pos, normals = nor, uvs = uvs,
            indices = ShortArray(0),
            morphs = mergedMorphs
        )
    }

    private fun combineIndices(meshes: List<com.linkpoint.avatar.LLMeshLoader.LLMesh>): ShortArray {
        val total = meshes.sumOf { it.indexCount }
        val out = ShortArray(total)
        var p = 0
        var vOff = 0
        for (m in meshes) {
            for (i in 0 until m.indexCount) {
                val idx = (m.indices[i].toInt() and 0xFFFF) + vOff
                out[p + i] = idx.toShort()
            }
            p += m.indexCount
            vOff += m.vertexCount
        }
        return out
    }

    /** Build the interleaved POS+NORMAL+UV float buffer Filament expects. */
    private fun interleaveLLMesh(m: com.linkpoint.avatar.LLMeshLoader.LLMesh): FloatArray {
        val out = FloatArray(m.vertexCount * 8)
        for (i in 0 until m.vertexCount) {
            out[i * 8]     = m.positions[i * 3]
            out[i * 8 + 1] = m.positions[i * 3 + 1]
            out[i * 8 + 2] = m.positions[i * 3 + 2]
            out[i * 8 + 3] = m.normals[i * 3]
            out[i * 8 + 4] = m.normals[i * 3 + 1]
            out[i * 8 + 5] = m.normals[i * 3 + 2]
            out[i * 8 + 6] = m.uvs[i * 2]
            out[i * 8 + 7] = m.uvs[i * 2 + 1]
        }
        return out
    }

    /**
     * Build a skinned VertexBuffer with BONE_INDICES (UBYTE4) and
     * BONE_WEIGHTS (FLOAT4) in a second buffer slot, alongside the
     * interleaved POS+NORMAL+UV in slot 0. Filament's renderer uses the
     * bone attributes when the RenderableComponent has skinning enabled,
     * transforming each vertex by sum(weight_i * boneMatrix_i).
     *
     * Returns null if the mesh has no skin data — caller should fall back
     * to the unskinned uploadAvatarMesh() path.
     */
    private fun uploadSkinnedAvatarMesh(
        m: com.linkpoint.avatar.LLMeshLoader.LLMesh
    ): AvatarMesh? {
        val boneIndices = m.deriveBoneIndices() ?: return null
        val boneWeights = m.deriveBoneWeights() ?: return null
        val tangents = com.linkpoint.render.TangentBuilder.build(
            m.positions, m.normals, m.uvs, m.indices
        )
        val vertexCount = m.vertexCount
        // POS(3) + TANGENTS(4 quaternion) + UV0(2) = 9 floats per vertex.
        val stride0 = 9 * 4
        val stride1 = 4 + 16 // UBYTE4 indices + FLOAT4 weights

        val vBytes0 = ByteBuffer.allocateDirect(vertexCount * stride0).order(ByteOrder.nativeOrder())
        for (i in 0 until vertexCount) {
            vBytes0.putFloat(m.positions[i * 3])
            vBytes0.putFloat(m.positions[i * 3 + 1])
            vBytes0.putFloat(m.positions[i * 3 + 2])
            vBytes0.putFloat(tangents[i * 4])
            vBytes0.putFloat(tangents[i * 4 + 1])
            vBytes0.putFloat(tangents[i * 4 + 2])
            vBytes0.putFloat(tangents[i * 4 + 3])
            vBytes0.putFloat(m.uvs[i * 2])
            vBytes0.putFloat(m.uvs[i * 2 + 1])
        }
        vBytes0.flip()

        val vBytes1 = ByteBuffer.allocateDirect(vertexCount * stride1).order(ByteOrder.nativeOrder())
        for (i in 0 until vertexCount) {
            vBytes1.put(boneIndices[i * 4])
            vBytes1.put(boneIndices[i * 4 + 1])
            vBytes1.put(boneIndices[i * 4 + 2])
            vBytes1.put(boneIndices[i * 4 + 3])
            vBytes1.putFloat(boneWeights[i * 4])
            vBytes1.putFloat(boneWeights[i * 4 + 1])
            vBytes1.putFloat(boneWeights[i * 4 + 2])
            vBytes1.putFloat(boneWeights[i * 4 + 3])
        }
        vBytes1.flip()

        val vb = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(2)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride0)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT4, 12, stride0)
            .attribute(VertexAttribute.UV0,      0, AttributeType.FLOAT2, 28, stride0)
            .attribute(VertexAttribute.BONE_INDICES, 1, AttributeType.UBYTE4, 0, stride1)
            .attribute(VertexAttribute.BONE_WEIGHTS, 1, AttributeType.FLOAT4, 4, stride1)
            .normalized(VertexAttribute.BONE_INDICES, false)
            .build(engine)
        vb.setBufferAt(engine, 0, vBytes0)
        vb.setBufferAt(engine, 1, vBytes1)

        val iBytes = ByteBuffer.allocateDirect(m.indices.size * 2).order(ByteOrder.nativeOrder())
        for (i in m.indices.indices) iBytes.putShort(m.indices[i])
        iBytes.flip()
        val ib = IndexBuffer.Builder()
            .indexCount(m.indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        ib.setBuffer(engine, iBytes)

        return AvatarMesh(vb, ib)
    }

    private fun uploadAvatarMesh(verts: FloatArray, idx: ShortArray): AvatarMesh {
        val stride = 8 * 4
        val vertexCount = verts.size / 8
        val vertexBytes = ByteBuffer.allocateDirect(verts.size * 4).order(ByteOrder.nativeOrder())
        verts.forEach { vertexBytes.putFloat(it) }
        vertexBytes.flip()

        val indexBytes = ByteBuffer.allocateDirect(idx.size * 2).order(ByteOrder.nativeOrder())
        idx.forEach { indexBytes.putShort(it) }
        indexBytes.flip()

        val vb = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT3, 12, stride)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 24, stride)
            .build(engine)
        vb.setBufferAt(engine, 0, vertexBytes)

        val ib = IndexBuffer.Builder()
            .indexCount(idx.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        ib.setBuffer(engine, indexBytes)

        return AvatarMesh(vb, ib)
    }
    
    /**
     * Remove an avatar from the scene
     */
    /**
     * Drive each body segment's local transform from the avatar's skeleton.
     * Call this every frame for the local agent (and for nearby avatars,
     * once we wire AvatarAnimator into a per-frame tick) so bone
     * rotations from the animator translate into visible motion.
     *
     * No-op if the segment-to-bone bindings list is empty (e.g. avatar
     * created before the material was ready).
     */
    fun applyAvatarPose(agentId: UUID, skeleton: com.linkpoint.avatar.AvatarSkeleton) {
        val avatar = avatars[agentId] ?: return
        if (avatar.bodySegmentEntities.size != avatar.bodySegmentBones.size) return
        val tm = engine.transformManager
        for (i in avatar.bodySegmentEntities.indices) {
            val boneName = avatar.bodySegmentBones[i]
            val bone = skeleton.getBone(boneName) ?: continue
            val ti = tm.getInstance(avatar.bodySegmentEntities[i])
            if (ti == 0) continue
            tm.setTransform(ti, bone.worldMatrix)
        }
    }

    /**
     * Push the avatar's skinning matrices (per-bone `worldMatrix *
     * inverseBindMatrix`) onto each system-mesh segment that was attached
     * with skinning enabled. Filament reads them via the renderable's
     * BONE_INDICES + BONE_WEIGHTS vertex attributes and computes final
     * vertex positions on the GPU each frame.
     *
     * Bone names in the LLM joint list (e.g. "mPelvis") map to the
     * skeleton's [AvatarSkeleton.getBone] entries by name, so a vertex
     * weighted to LL joint index N picks up the matrix for jointNames[N].
     *
     * No-op if the avatar isn't tracked, or none of its segments have
     * skinning enabled (e.g. the articulated capsule fallback).
     */
    fun applyAvatarSkinning(agentId: UUID, skeleton: com.linkpoint.avatar.AvatarSkeleton) {
        val avatar = avatars[agentId] ?: return
        if (avatar.bodySegmentEntities.isEmpty()) return
        val rm = engine.renderableManager

        val bodyMesh = systemAvatarBodyBaseLLMesh
        val headMesh = systemAvatarHeadBaseLLMesh
        // Body segment is index 0, head segment is index 1 (set up in
        // attachAvatarRenderable when the system mesh path takes over).
        if (bodyMesh != null && bodyMesh.jointNames.isNotEmpty() &&
            avatar.bodySegmentEntities.isNotEmpty()
        ) {
            applySkinningToEntity(rm, avatar.bodySegmentEntities[0],
                skeleton, bodyMesh.jointNames)
        }
        if (headMesh != null && headMesh.jointNames.isNotEmpty() &&
            avatar.bodySegmentEntities.size > 1
        ) {
            applySkinningToEntity(rm, avatar.bodySegmentEntities[1],
                skeleton, headMesh.jointNames)
        }
    }

    private fun applySkinningToEntity(
        rm: RenderableManager,
        entity: Int,
        skeleton: com.linkpoint.avatar.AvatarSkeleton,
        jointNames: List<String>
    ) {
        val instance = rm.getInstance(entity)
        if (instance == 0) return
        // Build a flat float[16 * boneCount] of skinning matrices in the
        // LL joint order (matches the BONE_INDICES values in the mesh).
        val boneCount = jointNames.size
        val matrices = FloatArray(boneCount * 16)
        for (i in 0 until boneCount) {
            val bone = skeleton.getBone(jointNames[i])
            val src = bone?.skinningMatrix
            if (src != null && src.size == 16) {
                System.arraycopy(src, 0, matrices, i * 16, 16)
            } else {
                // Identity for missing bones — safe fallback (vertex
                // doesn't move) instead of a NaN that would explode the
                // mesh.
                identityInto(matrices, i * 16)
            }
        }
        try {
            rm.setBonesAsMatrices(instance, java.nio.FloatBuffer.wrap(matrices), boneCount, 0)
        } catch (e: Exception) {
            Log.v(TAG, "setBonesAsMatrices failed for $entity: ${e.message}")
        }
    }

    private fun identityInto(out: FloatArray, offset: Int) {
        for (i in 0 until 16) out[offset + i] = 0f
        out[offset] = 1f
        out[offset + 5] = 1f
        out[offset + 10] = 1f
        out[offset + 15] = 1f
    }

    /**
     * Set the avatar's overall height multiplier, applied as a uniform Z
     * scale on the root transform. Driven from AvatarAppearance VisualParam
     * 33 (avatar height). 1.0 = LL default; the slider's 0..1 maps to a
     * roughly 0.85..1.20 multiplier to match the LL viewer's scaling.
     */
    /**
     * Apply the full VisualParams blob from AvatarAppearance to the
     * avatar's morphable system mesh. Re-uploads positions + normals to
     * the avatar's body and head VertexBuffers so the morphs are visible
     * immediately.
     *
     * No-op if the system avatar mesh isn't loaded, the avatar isn't
     * tracked, or [VisualParamLoader] hasn't been populated yet.
     */
    fun applyAvatarMorphs(
        agentId: UUID,
        visualParams: ByteArray,
        params: List<com.linkpoint.avatar.VisualParamLoader.VisualParam>
    ) {
        if (visualParams.isEmpty() || params.isEmpty()) return
        val avatar = avatars[agentId] ?: return
        val baseBody = systemAvatarBodyBaseLLMesh ?: return
        val baseBodyMesh = systemAvatarBodyMesh ?: return

        // Lazily create per-avatar morphed buffers on first appearance
        // update. Avatars at default appearance never need their own
        // buffers — they share the system mesh.
        val morphed = perAvatarMorphed.getOrPut(agentId) {
            MorphedMesh(
                bodyMesh = baseBodyMesh,
                bodyEntity = avatar.bodySegmentEntities.getOrNull(0) ?: 0,
                headMesh = systemAvatarHeadMesh,
                headEntity = avatar.bodySegmentEntities.getOrNull(1) ?: 0,
                morphedPositions = FloatArray(baseBody.positions.size),
                morphedNormals = FloatArray(baseBody.normals.size),
                headMorphedPositions = FloatArray(systemAvatarHeadBaseLLMesh?.positions?.size ?: 0),
                headMorphedNormals = FloatArray(systemAvatarHeadBaseLLMesh?.normals?.size ?: 0)
            )
        }

        // Apply morphs to the body mesh and re-upload.
        com.linkpoint.avatar.MorphApplier.apply(
            base = baseBody,
            visualParams = visualParams,
            params = params,
            outPositions = morphed.morphedPositions,
            outNormals = morphed.morphedNormals
        )
        reuploadInterleavedPositions(
            mesh = baseBodyMesh,
            positions = morphed.morphedPositions,
            normals = morphed.morphedNormals,
            uvs = baseBody.uvs,
            indices = baseBody.indices
        )

        // And the head, if we have one and an entity is bound.
        val baseHead = systemAvatarHeadBaseLLMesh
        val headMesh = systemAvatarHeadMesh
        if (baseHead != null && headMesh != null && morphed.headMorphedPositions.isNotEmpty()) {
            com.linkpoint.avatar.MorphApplier.apply(
                base = baseHead,
                visualParams = visualParams,
                params = params,
                outPositions = morphed.headMorphedPositions,
                outNormals = morphed.headMorphedNormals
            )
            reuploadInterleavedPositions(
                mesh = headMesh,
                positions = morphed.headMorphedPositions,
                normals = morphed.headMorphedNormals,
                uvs = baseHead.uvs,
                indices = baseHead.indices
            )
        }
    }

    /**
     * Re-upload an interleaved POS + TANGENTS(quat) + UV0 vertex stream to
     * an existing Filament VertexBuffer in place. Used by morph application
     * — mesh topology (vertex count, index list) doesn't change, but the
     * morphed positions shift normals and therefore the tangent frame
     * subtly. We rebuild the full TBN quaternion per morph application so
     * normal-mapped lighting stays correct as visual params interpolate.
     *
     * Caller supplies the morphed [positions] / [normals]; we invoke
     * [TangentBuilder.build] against the original [indices] (topology is
     * stable across morphs) and pack the result alongside [uvs].
     */
    private fun reuploadInterleavedPositions(
        mesh: AvatarMesh,
        positions: FloatArray,
        normals: FloatArray,
        uvs: FloatArray,
        indices: ShortArray
    ) {
        val n = positions.size / 3
        // Recompute tangents against the morphed positions/normals — without
        // this the normal-mapped portions of normalMap-using materials would
        // shade against stale tangents and look "off" as the user adjusts
        // shape sliders.
        val tangents = com.linkpoint.render.TangentBuilder.build(positions, normals, uvs, indices)
        // POS(3) + TANGENTS(4 quat) + UV0(2) = 9 floats per vertex.
        val stride = 9
        val data = ByteBuffer.allocateDirect(n * stride * 4).order(ByteOrder.nativeOrder())
        for (i in 0 until n) {
            data.putFloat(positions[i * 3])
            data.putFloat(positions[i * 3 + 1])
            data.putFloat(positions[i * 3 + 2])
            data.putFloat(tangents[i * 4])
            data.putFloat(tangents[i * 4 + 1])
            data.putFloat(tangents[i * 4 + 2])
            data.putFloat(tangents[i * 4 + 3])
            data.putFloat(uvs[i * 2])
            data.putFloat(uvs[i * 2 + 1])
        }
        data.flip()
        try {
            mesh.vertexBuffer.setBufferAt(engine, 0, data)
        } catch (e: Exception) {
            Log.w(TAG, "morph re-upload failed: ${e.message}")
        }
    }

    /**
     * Apply a baseColor tint to every body-segment material instance for
     * the given avatar. Driven by the wearable="skin" colour-palette
     * VisualParams in AvatarAppearance (see LinkpointApp.blendSkinColorParams).
     *
     * The tint is multiplied with the lit material's baseColorMap sample,
     * so for system avatars (no per-face texture yet) the avatar picks up
     * the skin colour directly; for mesh attachments using BoM the tint
     * is layered with the texture sample.
     */
    fun setAvatarSkinTint(agentId: UUID, r: Float, g: Float, b: Float, a: Float = 1f) {
        val avatar = avatars[agentId] ?: return
        val rm = engine.renderableManager
        for (entity in avatar.bodySegmentEntities) {
            val instance = rm.getInstance(entity)
            if (instance == 0) continue
            val primCount = rm.getPrimitiveCount(instance)
            for (p in 0 until primCount) {
                val mat = rm.getMaterialInstanceAt(instance, p) ?: continue
                try { mat.setParameter("baseColor", r, g, b, a) } catch (_: Exception) {}
            }
        }
    }

    fun setAvatarHeightScale(agentId: UUID, scale: Float) {
        val avatar = avatars[agentId] ?: return
        avatar.heightScale = scale.coerceIn(0.5f, 2.0f)
        updateTransform(avatar)
    }

    fun removeAvatar(agentId: UUID) {
        avatars.remove(agentId)?.let { avatar ->
            avatar.bodySegmentEntities.forEach { child ->
                scene.removeEntity(child)
                entityManager.destroy(child)
            }
            avatar.bodySegmentEntities.clear()
            scene.removeEntity(avatar.entity)
            entityManager.destroy(avatar.entity)
            Log.d(TAG, "Removed avatar: $agentId")
        }
    }
    
    /**
     * Set object renderable (mesh loaded)
     */
    fun setObjectRenderable(objectId: UUID, renderable: Int) {
        sceneObjects[objectId]?.let { obj ->
            obj.renderable = renderable
            val transformManager = engine.transformManager
            val instance = transformManager.getInstance(obj.entity)
            if (instance == 0) {
                transformManager.create(obj.entity)
            }
            updateTransform(obj)
        }
    }
    
    /**
     * Update camera position and target
     */
    fun updateCamera(position: LLVector3, target: LLVector3) {
        cameraPosition = position
        cameraTarget = target
    }
    
    /**
     * Get visible objects within range of camera. If [maxDistance] is not
     * supplied, falls back to the wired [DrawDistanceManager] (via
     * [setDrawDistanceProviders]) and ultimately to the historic 256m
     * default when no provider is attached.
     */
    fun getVisibleObjects(maxDistance: Float? = null): List<SceneObject> {
        val effective = maxDistance
            ?: objectDrawDistanceProvider?.invoke()
            ?: 256f
        val all = sceneObjects.values
        val visible = all.filter { obj ->
            obj.position.distance(cameraPosition) <= effective
        }.sortedBy { it.position.distance(cameraPosition) }
        visibilityStatsSink?.invoke(visible.size, all.size, all.size - visible.size)
        return visible
    }

    /**
     * Get visible avatars within range. Same provider precedence as
     * [getVisibleObjects]; the avatar provider is independent so the
     * caller can apply [DrawDistanceManager.AVATAR_DISTANCE_MULTIPLIER]
     * to keep nearby avatars visible at lower draw distances.
     */
    fun getVisibleAvatars(maxDistance: Float? = null): List<AvatarObject> {
        val effective = maxDistance
            ?: avatarDrawDistanceProvider?.invoke()
            ?: 256f
        return avatars.values.filter { avatar ->
            avatar.position.distance(cameraPosition) <= effective
        }.sortedBy { it.position.distance(cameraPosition) }
    }
    
    /**
     * Find object by local ID
     */
    fun findByLocalId(localId: Int): SceneObject? {
        return sceneObjects.values.find { it.localId == localId }
    }
    
    /**
     * Get object by UUID
     */
    fun getObject(objectId: UUID): SceneObject? = sceneObjects[objectId]
    
    /**
     * Get avatar by UUID
     */
    fun getAvatar(agentId: UUID): AvatarObject? = avatars[agentId]
    
    /**
     * Get all objects
     */
    fun getAllObjects(): Collection<SceneObject> = sceneObjects.values
    
    /**
     * Get all avatars
     */
    fun getAllAvatars(): Collection<AvatarObject> = avatars.values
    
    /**
     * Clear all objects and avatars
     */
    fun clear() {
        sceneObjects.values.forEach { obj ->
            scene.removeEntity(obj.entity)
            entityManager.destroy(obj.entity)
        }
        sceneObjects.clear()
        
        avatars.values.forEach { avatar ->
            avatar.bodySegmentEntities.forEach { child ->
                scene.removeEntity(child)
                entityManager.destroy(child)
            }
            avatar.bodySegmentEntities.clear()
            scene.removeEntity(avatar.entity)
            entityManager.destroy(avatar.entity)
        }
        avatars.clear()
        
        Log.i(TAG, "Scene cleared")
    }
    
    private fun updateTransform(obj: SceneObject) {
        val transformManager = engine.transformManager
        val instance = transformManager.getInstance(obj.entity)
        if (instance != 0) {
            val matrix = FloatArray(16)
            buildTransformMatrix(obj.position, obj.rotation, obj.scale, matrix)
            transformManager.setTransform(instance, matrix)
        }
    }
    
    private fun updateTransform(avatar: AvatarObject) {
        val transformManager = engine.transformManager
        val instance = transformManager.getInstance(avatar.entity)
        if (instance != 0) {
            val matrix = FloatArray(16)
            // Apply heightScale uniformly so VisualParam 33 (height) scales
            // the avatar without rebuilding the body meshes.
            buildTransformMatrix(
                avatar.position,
                avatar.rotation,
                LLVector3(avatar.heightScale, avatar.heightScale, avatar.heightScale),
                matrix
            )
            transformManager.setTransform(instance, matrix)
        }
    }
    
    private fun buildTransformMatrix(
        position: LLVector3,
        rotation: LLQuaternion,
        scale: LLVector3,
        out: FloatArray
    ) {
        // Build rotation matrix from quaternion
        val x = rotation.x
        val y = rotation.y
        val z = rotation.z
        val w = rotation.w
        
        val x2 = x + x
        val y2 = y + y
        val z2 = z + z
        val xx = x * x2
        val xy = x * y2
        val xz = x * z2
        val yy = y * y2
        val yz = y * z2
        val zz = z * z2
        val wx = w * x2
        val wy = w * y2
        val wz = w * z2
        
        out[0] = (1 - (yy + zz)) * scale.x
        out[1] = (xy + wz) * scale.x
        out[2] = (xz - wy) * scale.x
        out[3] = 0f
        
        out[4] = (xy - wz) * scale.y
        out[5] = (1 - (xx + zz)) * scale.y
        out[6] = (yz + wx) * scale.y
        out[7] = 0f
        
        out[8] = (xz + wy) * scale.z
        out[9] = (yz - wx) * scale.z
        out[10] = (1 - (xx + yy)) * scale.z
        out[11] = 0f
        
        out[12] = position.x
        out[13] = position.y
        out[14] = position.z
        out[15] = 1f
    }
}

/**
 * Represents an object in the scene
 */
data class SceneObject(
    val uuid: UUID,
    val localId: Int,
    val entity: Int,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var parentId: UUID? = null,
    var renderable: Int = 0,
    var textureIds: List<UUID> = emptyList(),
    var isPhantom: Boolean = false,
    var isTemporary: Boolean = false
)

/**
 * Represents an avatar in the scene
 */
data class AvatarObject(
    val agentId: UUID,
    val entity: Int,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var animations: List<UUID> = emptyList(),
    var displayName: String = "",
    var isTyping: Boolean = false,
    var isSitting: Boolean = false,
    var renderable: Int = 0,
    /**
     * Per-avatar child entities for the articulated body segments
     * (head/torso/arms/legs). Tracked here so removeAvatar() can detach
     * and destroy them along with the root entity.
     */
    val bodySegmentEntities: MutableList<Int> = mutableListOf(),
    /**
     * Skeletal bone name each body segment is bound to, in the same order
     * as bodySegmentEntities. Used by applyAvatarPose() to drive the
     * segment's local transform from the bone's world matrix each frame.
     * Empty if the avatar has no skeleton wired up.
     */
    val bodySegmentBones: MutableList<String> = mutableListOf(),
    /**
     * Optional uniform Z scale applied to the avatar's root transform.
     * Drives "height" visual param 33 from AvatarAppearance: 0..1 maps to
     * a 0.85x..1.20x range to roughly match the LL viewer's scaling.
     */
    var heightScale: Float = 1f
)

internal data class AvatarMesh(
    val vertexBuffer: VertexBuffer,
    val indexBuffer: IndexBuffer
)
