package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import com.google.android.filament.EntityManager
import com.google.android.filament.LightManager
import com.google.android.filament.RenderableManager
import com.google.android.filament.Scene
import com.google.android.filament.TransformManager
import com.google.android.filament.gltfio.FilamentAsset
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

/**
 * Renders avatars inside the Filament scene graph.
 *
 * The implementation mirrors the Firestorm viewer pipeline conceptually: every avatar owns a
 * render entity (either a full glTF rig or a lightweight fallback mesh) that is driven by the
 * authoritative simulation data held in [SLObjectAvatarInfo].
 */
class FilamentAvatarRenderer(
    private val context: Context,
    private val engine: com.google.android.filament.Engine,
    private val scene: Scene,
    private val materialManager: FilamentMaterialManager,
    @Suppress("UnusedParameter") private val textureManager: FilamentTextureManager,
    private val gltfLoader: FilamentGltfLoader
) {

    private data class AvatarRenderable(
        val avatarId: UUID,
        @com.google.android.filament.Entity val entity: Int,
        val asset: FilamentAsset?,
        val mesh: FilamentAvatarMeshLoader.Mesh?,
        var lastUpdateMillis: Long,
    )

    companion object {
        private const val TAG = "FilamentAvatarRenderer"
        private const val MAX_ACTIVE_AVATARS = 128
        private const val EVICT_AFTER_MS = 10_000L
    }

    private val renderables = ConcurrentHashMap<UUID, AvatarRenderable>()
    private val meshLoader = FilamentAvatarMeshLoader(context, engine, gltfLoader)
    private var defaultAsset: FilamentAsset? = null
    private var fallbackMesh: FilamentAvatarMeshLoader.Mesh? = null

    private val transformManager: TransformManager = engine.transformManager
    private val renderableManager: RenderableManager = engine.renderableManager
    private val lightManager: LightManager = engine.lightManager
    private val entityManager: EntityManager = EntityManager.get()

    fun initialize() {
        defaultAsset = meshLoader.loadDefaultAsset()?.also { asset ->
            // Do not attach directly to the scene; each avatar will clone the hierarchy as needed.
            Log.i(TAG, "Default avatar asset ready – animations=${asset.animator?.animationCount ?: 0}")
        }
        fallbackMesh = meshLoader.fallbackMesh()
    }

    fun updateAvatar(info: SLObjectAvatarInfo) {
        val uuid = info.getId() ?: return
        val existing = renderables[uuid]
        if (existing == null) {
            ensureCapacity()
            createRenderable(uuid, info)
        } else {
            applyTransform(existing.entity, info)
            existing.lastUpdateMillis = SystemClock.elapsedRealtime()
        }
    }

    fun removeAvatar(uuid: UUID) {
        renderables.remove(uuid)?.let { renderable ->
            scene.removeEntity(renderable.entity)
            renderable.asset?.let { asset ->
                gltfLoader.removeFromScene(asset)
                gltfLoader.destroyAsset(asset)
            }
            destroyEntity(renderable.entity)
        }
    }

    fun clearAll() {
        renderables.keys.toList().forEach { removeAvatar(it) }
    }

    fun getAvatarCount(): Int = renderables.size

    fun tick(deltaSeconds: Float) {
        val now = SystemClock.elapsedRealtime()
        renderables.values.toList().forEach { renderable ->
            if (now - renderable.lastUpdateMillis > EVICT_AFTER_MS) {
                removeAvatar(renderable.avatarId)
                return@forEach
            }
            renderable.asset?.let { gltfLoader.updateAnimation(it, deltaSeconds) }
        }
    }

    fun destroy() {
        clearAll()
        meshLoader.destroy()
        defaultAsset = null
        fallbackMesh = null
    }

    private fun ensureCapacity() {
        if (renderables.size < MAX_ACTIVE_AVATARS) return
        val victim = renderables.values.minByOrNull { it.lastUpdateMillis } ?: return
        removeAvatar(victim.avatarId)
    }

    private fun createRenderable(uuid: UUID, info: SLObjectAvatarInfo) {
        val mesh = fallbackMesh ?: return

        val entity = entityManager.create()

        RenderableManager.Builder(1)
            .boundingBox(mesh.boundingBox)
            .geometry(
                0,
                RenderableManager.PrimitiveType.TRIANGLES,
                mesh.vertexBuffer,
                mesh.indexBuffer
            )
            .material(0, materialManager.getMaterial(FilamentMaterialManager.MaterialType.PRIM_BASIC).defaultInstance)
            .castShadows(true)
            .receiveShadows(true)
            .build(engine, entity)

        scene.addEntity(entity)
        applyTransform(entity, info)

        renderables[uuid] = AvatarRenderable(
            avatarId = uuid,
            entity = entity,
            asset = null,
            mesh = mesh,
            lastUpdateMillis = SystemClock.elapsedRealtime()
        )
    }

    private fun applyTransform(@com.google.android.filament.Entity entity: Int, info: SLObjectAvatarInfo) {
        val transform = FloatArray(16)
        Matrix.setIdentityM(transform, 0)

        val position = info.getAbsolutePosition()
        Matrix.translateM(transform, 0, position.x, position.y, position.z)

        info.getRotation()?.let { rotation ->
            val rotationMatrix = rotation.getMatrix()
            val result = FloatArray(16)
            Matrix.multiplyMM(result, 0, transform, 0, rotationMatrix, 0)
            System.arraycopy(result, 0, transform, 0, 16)
        }

        val scale = info.getObjectCoords().get(1)
        Matrix.scaleM(transform, 0, max(scale.x, 0.01f), max(scale.y, 0.01f), max(scale.z, 0.01f))

        val instance = transformManager.getInstance(entity)
        if (instance.isValid) {
            transformManager.setTransform(instance, transform)
        }
    }

    private fun destroyEntity(@com.google.android.filament.Entity entity: Int) {
        lightManager.destroy(entity)
        renderableManager.destroy(entity)
        transformManager.destroy(entity)
        engine.destroyEntity(entity)
        entityManager.destroy(entity)
    }

}
