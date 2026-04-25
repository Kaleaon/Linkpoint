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

        // Avatar placeholder dimensions (humanoid capsule approximation, in meters).
        // Real skinned meshes are a separate, much larger task; until then this
        // makes avatars visible at the right height/footprint.
        private const val AVATAR_PLACEHOLDER_HEIGHT = 1.85f
        private const val AVATAR_PLACEHOLDER_RADIUS = 0.35f
    }

    // Object tracking
    private val sceneObjects = ConcurrentHashMap<UUID, SceneObject>()
    private val avatars = ConcurrentHashMap<UUID, AvatarObject>()

    // Entity manager
    private val entityManager = EntityManager.get()

    // Current camera position
    private var cameraPosition = LLVector3(128f, 128f, 30f)
    private var cameraTarget = LLVector3(128f, 128f, 0f)

    // Avatar placeholder rendering. Set via setAvatarMaterial() once the
    // MaterialLoader has compiled the lit material; until then avatars are
    // tracked but invisible (same behaviour as the previous code).
    private var avatarMaterial: MaterialInstance? = null
    private var avatarMesh: AvatarMesh? = null

    /**
     * Provide the lit material used to render avatar placeholder geometry.
     * Should be called once after MaterialLoader.initialize() returns success.
     * Existing avatars receive the renderable retroactively.
     */
    fun setAvatarMaterial(material: Material) {
        avatarMaterial = material.createInstance()
        avatarMesh = buildAvatarPlaceholderMesh()
        Log.i(TAG, "Avatar material set; placeholder mesh ready")

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
     * Build the RenderableComponent for an avatar entity using the shared
     * placeholder capsule mesh and lit material. Called from updateAvatar()
     * for new avatars, and from setAvatarMaterial() for any avatars that
     * arrived before the material was ready.
     *
     * If the avatar material hasn't been wired up yet (MaterialLoader still
     * compiling, or compilation failed), this is a no-op — the entity is
     * still added to the scene but won't render until the material lands.
     * Real skinned-mesh avatar rendering is a separate follow-up.
     */
    private fun attachAvatarRenderable(avatar: AvatarObject) {
        val mesh = avatarMesh ?: return
        val material = avatarMaterial ?: return
        try {
            RenderableManager.Builder(1)
                .boundingBox(
                    Box(
                        -AVATAR_PLACEHOLDER_RADIUS,
                        -AVATAR_PLACEHOLDER_RADIUS,
                        0f,
                        AVATAR_PLACEHOLDER_RADIUS,
                        AVATAR_PLACEHOLDER_RADIUS,
                        AVATAR_PLACEHOLDER_HEIGHT
                    )
                )
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, mesh.vertexBuffer, mesh.indexBuffer)
                .material(0, material)
                .culling(true)
                .receiveShadows(true)
                .castShadows(true)
                .build(engine, avatar.entity)
            avatar.renderable = avatar.entity
            engine.transformManager.create(avatar.entity)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach avatar renderable for ${avatar.agentId}", e)
        }
    }

    /**
     * Build a single shared capsule-ish mesh used as the placeholder body for
     * every avatar. The geometry is positioned with feet at z=0 and head at
     * z=AVATAR_PLACEHOLDER_HEIGHT in entity-local space, so the avatar's
     * world-space transform from the protocol layer can be applied directly.
     *
     * Capsule = lower hemisphere + cylinder + upper hemisphere. We re-use the
     * same vertex layout (FLOAT3 position, FLOAT3 normal, FLOAT2 UV) as
     * PrimRenderer so it consumes the same lit material.
     */
    private fun buildAvatarPlaceholderMesh(): AvatarMesh {
        val rings = 12
        val segments = 16
        val radius = AVATAR_PLACEHOLDER_RADIUS
        val cylinderHeight = (AVATAR_PLACEHOLDER_HEIGHT - 2f * radius).coerceAtLeast(0.01f)

        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        fun pushVertex(x: Float, y: Float, z: Float, nx: Float, ny: Float, nz: Float, u: Float, v: Float) {
            vertices.add(x); vertices.add(y); vertices.add(z)
            vertices.add(nx); vertices.add(ny); vertices.add(nz)
            vertices.add(u); vertices.add(v)
        }

        // Lower hemisphere: from y = -PI/2 to 0 (capsule bottom), centered at z=radius
        var rowStart = 0
        for (ring in 0..rings / 2) {
            val phi = -PI / 2.0 + PI * ring / rings
            val cp = cos(phi).toFloat()
            val sp = sin(phi).toFloat()
            for (seg in 0..segments) {
                val theta = 2.0 * PI * seg / segments
                val ct = cos(theta).toFloat()
                val st = sin(theta).toFloat()
                val nx = cp * ct
                val ny = cp * st
                val nz = sp
                val px = nx * radius
                val py = ny * radius
                val pz = nz * radius + radius
                pushVertex(px, py, pz, nx, ny, nz, seg.toFloat() / segments, ring.toFloat() / rings * 0.5f)
            }
        }
        // Upper hemisphere: from 0 to PI/2, centered at z = radius + cylinderHeight
        for (ring in 0..rings / 2) {
            val phi = PI * ring / rings
            val cp = cos(phi).toFloat()
            val sp = sin(phi).toFloat()
            for (seg in 0..segments) {
                val theta = 2.0 * PI * seg / segments
                val ct = cos(theta).toFloat()
                val st = sin(theta).toFloat()
                val nx = cp * ct
                val ny = cp * st
                val nz = sp
                val px = nx * radius
                val py = ny * radius
                val pz = nz * radius + radius + cylinderHeight
                pushVertex(px, py, pz, nx, ny, nz, seg.toFloat() / segments, 0.5f + ring.toFloat() / rings * 0.5f)
            }
        }

        // Build indices spanning both hemispheres + the cylindrical seam between them.
        val ringsTotal = rings + 1 // (rings/2 + 1) bottom + (rings/2 + 1) top - 0 (no shared row)
        val verticesPerRing = segments + 1
        for (r in 0 until ringsTotal - 1) {
            for (s in 0 until segments) {
                val i0 = (r * verticesPerRing + s).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = ((r + 1) * verticesPerRing + s).toShort()
                val i3 = (i2 + 1).toShort()
                indices.add(i0); indices.add(i2); indices.add(i1)
                indices.add(i1); indices.add(i2); indices.add(i3)
            }
        }

        return uploadAvatarMesh(vertices.toFloatArray(), indices.toShortArray())
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
    var renderable: Int = 0
)

internal data class AvatarMesh(
    val vertexBuffer: VertexBuffer,
    val indexBuffer: IndexBuffer
)
