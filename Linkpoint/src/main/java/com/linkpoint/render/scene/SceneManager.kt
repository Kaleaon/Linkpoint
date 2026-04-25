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
class SceneManager(private val engine: Engine, private val scene: Scene) {

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

    // Avatar segment meshes (built once and shared across all avatars).
    private var avatarMaterial: MaterialInstance? = null
    private var headMesh: AvatarMesh? = null
    private var torsoMesh: AvatarMesh? = null
    private var upperArmMesh: AvatarMesh? = null
    private var lowerArmMesh: AvatarMesh? = null
    private var upperLegMesh: AvatarMesh? = null
    private var lowerLegMesh: AvatarMesh? = null

    /**
     * Provide the lit material used to render avatar placeholder geometry.
     * Should be called once after MaterialLoader.initialize() returns success.
     * Existing avatars receive the renderable retroactively.
     */
    fun setAvatarMaterial(material: Material) {
        avatarMaterial = material.createInstance()
        headMesh = buildSphereMesh(AVATAR_HEAD_RADIUS)
        torsoMesh = buildCapsuleMesh(AVATAR_TORSO_RADIUS, AVATAR_TORSO_HEIGHT)
        upperArmMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS, AVATAR_UPPER_ARM_LEN)
        lowerArmMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS, AVATAR_LOWER_ARM_LEN)
        upperLegMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS * 1.3f, AVATAR_UPPER_LEG_LEN)
        lowerLegMesh = buildCapsuleMesh(AVATAR_LIMB_RADIUS * 1.2f, AVATAR_LOWER_LEG_LEN)
        Log.i(TAG, "Avatar material set; articulated body meshes ready")

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
            avatar.renderable = avatar.entity // mark as built
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach articulated avatar for ${avatar.agentId}", e)
        }
    }

    /**
     * Build a sphere mesh of the given radius. Same vertex layout
     * (POSITION+TANGENTS+UV0) as PrimRenderer so it shares the lit material.
     */
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
     * Get visible objects within range of camera
     */
    fun getVisibleObjects(maxDistance: Float = 256f): List<SceneObject> {
        return sceneObjects.values.filter { obj ->
            obj.position.distance(cameraPosition) <= maxDistance
        }.sortedBy { it.position.distance(cameraPosition) }
    }
    
    /**
     * Get visible avatars within range
     */
    fun getVisibleAvatars(maxDistance: Float = 256f): List<AvatarObject> {
        return avatars.values.filter { avatar ->
            avatar.position.distance(cameraPosition) <= maxDistance
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
            buildTransformMatrix(avatar.position, avatar.rotation, LLVector3.one(), matrix)
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
    val bodySegmentEntities: MutableList<Int> = mutableListOf()
)

internal data class AvatarMesh(
    val vertexBuffer: VertexBuffer,
    val indexBuffer: IndexBuffer
)
