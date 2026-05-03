package com.linkpoint.render.scene.commands

import android.graphics.Bitmap
import android.util.Log
import com.linkpoint.assets.TextureFormatPolicy
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.protocol.textures.TextureEntryParser
import com.linkpoint.render.lumiya.core.LumiyaRenderer
import com.linkpoint.render.lumiya.core.RenderEngineProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Consumes [SceneRenderCommand]s from the protocol layer and applies them
 * to the Lumiya OpenGL ES 3 backend.
 *
 * Mirrors [FilamentRenderCommandConsumer]. The Filament path lives on a
 * private dispatcher; the GL path lives on the GLSurfaceView's render
 * thread, so commands that touch GL state are routed through
 * [glThreadExecutor].
 *
 * **Texture flow.** UpdateMaterial commands carry a raw TextureEntry blob.
 * We parse out the default texture UUID, hand it to [textureFetcher] (an
 * async TextureManager fetch), then post the resulting bitmap onto the
 * GL thread for upload + binding via [LumiyaRenderer.uploadTextureForPrim].
 */
class Gles3RenderCommandConsumer(
    private val stream: RenderCommandStream,
    private val scope: CoroutineScope
) {

    companion object {
        private const val TAG = "GlesCmdConsumer"
        private const val REGION_SIZE = 256
    }

    /** Hook to fetch a decoded bitmap by SL asset UUID. */
    fun interface TextureFetcher {
        /** Asynchronously fetch [textureId]; deliver bitmap (or null) to [onResolved]. */
        fun fetch(textureId: UUID, onResolved: (Bitmap?) -> Unit)
    }

    private var engineProvider: RenderEngineProvider? = null
    private var lumiya: LumiyaRenderer? = null
    private var glThreadExecutor: (Runnable) -> Unit = { it.run() }
    private var textureFetcher: TextureFetcher? = null
    private var consumeJob: Job? = null

    private val terrainHeightmap = FloatArray(REGION_SIZE * REGION_SIZE)

    /**
     * Commands that arrived before the GL engine was bound. The simulator
     * sends the bulk of the scene (ObjectUpdate, LayerData, terrain) in a
     * single burst right after RegionHandshake; subsequent traffic is
     * mostly deltas (ImprovedTerseObjectUpdate, KillObject) that assume
     * the viewer already holds the full state. If the burst arrives before
     * `WorldViewActivity` has built its `LumiyaGLSurfaceView` and called
     * `bindEngine`, every command would be silently dropped at the
     * `engineProvider ?: return` guard in [dispatch] and the scene would
     * stay empty for the rest of the session — the symptom captured in
     * the 2026-05-03 debug report (Total Objects: 0, Live Textures: 0
     * despite a connected circuit).
     *
     * Buffer is bounded to keep a stuck pre-bind state from growing
     * unbounded; oldest commands are evicted first, which is the right
     * behaviour because newer ObjectUpdates supersede older ones for the
     * same prim.
     */
    private val pendingCommands = ArrayDeque<SceneRenderCommand>()
    private val pendingLock = Any()
    private val pendingCapacity = 4096

    /**
     * Bind the active GL engine. [glThreadExecutor] must marshal a Runnable
     * onto the GL render thread (typically `lumiyaGlSurfaceView::queueEvent`).
     * Pass [textureFetcher] to enable real material binding; without it,
     * UpdateMaterial commands silently no-op.
     */
    fun bindEngine(
        provider: RenderEngineProvider?,
        glThreadExecutor: (Runnable) -> Unit = { it.run() },
        textureFetcher: TextureFetcher? = null
    ) {
        val wasUnbound = this.engineProvider == null
        this.engineProvider = provider
        this.lumiya = provider as? LumiyaRenderer
        this.glThreadExecutor = glThreadExecutor
        this.textureFetcher = textureFetcher

        if (provider != null && wasUnbound) {
            replayPendingCommands()
        }
    }

    /** Backwards-compatible single-argument bind for callers that don't yet wire texture fetch. */
    fun bindEngine(provider: RenderEngineProvider?) {
        bindEngine(provider, { it.run() }, null)
    }

    private fun replayPendingCommands() {
        val drained = synchronized(pendingLock) {
            val copy = pendingCommands.toList()
            pendingCommands.clear()
            copy
        }
        if (drained.isEmpty()) return
        Log.i(TAG, "Replaying ${drained.size} pending render commands after engine bind")
        for (command in drained) {
            try {
                dispatch(command)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to replay $command: ${e.message}")
            }
        }
    }

    fun start() {
        if (consumeJob != null) return
        consumeJob = scope.launch {
            stream.commands.collect { command ->
                try {
                    dispatch(command)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to apply command $command: ${e.message}")
                }
            }
        }
    }

    // =====================================================================
    // Dispatch
    // =====================================================================

    private fun dispatch(command: SceneRenderCommand) {
        val engine = engineProvider ?: run {
            bufferPending(command)
            return
        }
        when (command) {
            is SceneRenderCommand.UpsertPrim -> handleUpsertPrim(engine, command)
            is SceneRenderCommand.UpsertMesh -> handleUpsertMesh(engine, command)
            is SceneRenderCommand.UpdateMaterial -> handleUpdateMaterial(engine, command)
            is SceneRenderCommand.RemoveEntity -> handleRemove(engine, command)
            is SceneRenderCommand.SetCamera -> handleCamera(engine, command)
            is SceneRenderCommand.SetTerrainPatch -> handleTerrainPatch(engine, command)
        }
    }

    private fun handleUpsertPrim(engine: RenderEngineProvider, cmd: SceneRenderCommand.UpsertPrim) {
        val update = cmd.update
        val p = update.position
        val s = update.scale
        runOnGl {
            // pcode 47 == LL_PCODE_LEGACY_AVATAR. Route avatars to the
            // avatar store; regular prims go to the prim store with
            // full shape + scale + texture metadata.
            if (update.pcode == 47) {
                lumiya?.upsertAvatar(update.fullId, p.x, p.y, p.z)
            } else {
                lumiya?.upsertPrim(
                    id = update.localId.toLong(),
                    posX = p.x, posY = p.y, posZ = p.z,
                    scaleX = s.x, scaleY = s.y, scaleZ = s.z,
                    rotation = quatToMatrix(update.rotation),
                    shapeParams = update.shapeParams,
                    textureEntry = update.textureEntry
                ) ?: engine.addObject(update.localId.toLong(), p.x, p.y, p.z)
                // Kick off async texture fetches for every face referencing
                // a real (non-default) UUID so prims pick up their textures
                // as soon as the asset cache delivers them.
                tryBindFaceTextures(update.localId.toLong(), update.textureEntry)
            }
        }
    }

    private fun quatToMatrix(rot: com.linkpoint.protocol.types.LLQuaternion): FloatArray {
        // Convert LL quaternion (x,y,z,w) to a column-major 4x4 rotation matrix.
        val m = FloatArray(16)
        val xx = rot.x * rot.x; val yy = rot.y * rot.y; val zz = rot.z * rot.z
        val xy = rot.x * rot.y; val xz = rot.x * rot.z; val yz = rot.y * rot.z
        val wx = rot.w * rot.x; val wy = rot.w * rot.y; val wz = rot.w * rot.z
        m[0] = 1f - 2f * (yy + zz); m[1] = 2f * (xy + wz);    m[2] = 2f * (xz - wy);    m[3] = 0f
        m[4] = 2f * (xy - wz);      m[5] = 1f - 2f * (xx + zz); m[6] = 2f * (yz + wx);  m[7] = 0f
        m[8] = 2f * (xz + wy);      m[9] = 2f * (yz - wx);    m[10] = 1f - 2f * (xx + yy); m[11] = 0f
        m[12] = 0f; m[13] = 0f; m[14] = 0f; m[15] = 1f
        return m
    }

    private fun handleUpsertMesh(engine: RenderEngineProvider, cmd: SceneRenderCommand.UpsertMesh) {
        // Compile + attach the mesh on the GL thread. We don't have the
        // ObjectUpdate position here (the producer only forwards the mesh
        // localId + data + texture entry), so we keep whatever transform
        // the prim already has from its UpsertPrim — the mesh attaches
        // *into* the existing prim slot. Then kick off per-face texture
        // fetches.
        val lumiyaRef = lumiya
        if (lumiyaRef != null) {
            runOnGl {
                // Best-effort: pull the prim's current position out of
                // the snapshot, fall back to origin if it's brand new.
                val (pos, scale) = currentPositionScale(cmd.localId.toLong())
                lumiyaRef.upsertMeshPrim(
                    id = cmd.localId.toLong(),
                    posX = pos[0], posY = pos[1], posZ = pos[2],
                    scaleX = scale[0], scaleY = scale[1], scaleZ = scale[2],
                    rotation = null,
                    meshData = cmd.meshData,
                    textureEntry = cmd.textureEntry
                )
                tryBindFaceTextures(cmd.localId.toLong(), cmd.textureEntry)
            }
        } else {
            runOnGl { tryBindFaceTextures(cmd.localId.toLong(), cmd.textureEntry) }
        }
    }

    private fun currentPositionScale(primId: Long): Pair<FloatArray, FloatArray> {
        val pos = floatArrayOf(0f, 0f, 0f)
        val scale = floatArrayOf(1f, 1f, 1f)
        // We don't currently expose the prim's current transform back
        // to the consumer; landing on a sane default is fine because
        // a follow-up UpsertPrim will overwrite the matrix. This branch
        // exists so we don't lose the mesh-arrival event entirely.
        return pos to scale
    }

    private fun handleUpdateMaterial(engine: RenderEngineProvider, cmd: SceneRenderCommand.UpdateMaterial) {
        // Re-bind the default face texture on material change. If a
        // fallback ObjectUpdate is attached we also re-upsert the prim
        // (mirrors the Filament path) so a material update can repair a
        // prim that was missed by the initial UpsertPrim.
        cmd.fallbackUpdate?.let { fallback ->
            val p = fallback.position
            runOnGl { engine.addObject(fallback.localId.toLong(), p.x, p.y, p.z) }
        }
        runOnGl { tryBindDefaultTexture(cmd.localId.toLong(), cmd.textureEntry) }
    }

    private fun handleRemove(engine: RenderEngineProvider, cmd: SceneRenderCommand.RemoveEntity) {
        runOnGl {
            engine.removeObject(cmd.localId.toLong())
            cmd.fullId?.let { lumiya?.removeAvatar(it) }
        }
    }

    private fun handleCamera(engine: RenderEngineProvider, cmd: SceneRenderCommand.SetCamera) {
        val p = cmd.position
        val t = cmd.target
        runOnGl {
            engine.setCameraPosition(p.x, p.y, p.z)
            engine.setCameraTarget(t.x, t.y, t.z)
        }
    }

    private fun handleTerrainPatch(engine: RenderEngineProvider, cmd: SceneRenderCommand.SetTerrainPatch) {
        applyPatch(cmd.patch)
        val heightmap = toRendererHeightmap()
        runOnGl { engine.updateTerrain(heightmap, 257, 257) }
    }

    private fun bufferPending(command: SceneRenderCommand) {
        synchronized(pendingLock) {
            if (pendingCommands.size >= pendingCapacity) {
                pendingCommands.removeFirst()
            }
            pendingCommands.addLast(command)
        }
    }

    // =====================================================================
    // Texture binding
    // =====================================================================

    /**
     * Fetch every distinct texture UUID referenced by [textureEntry]
     * and, on success, queue a GL-thread upload + bind for the matching
     * faces of [primId]. Faces sharing the same UUID share a single
     * upload via the GLTextureCache.
     */
    private fun tryBindFaceTextures(primId: Long, textureEntry: ByteArray) {
        val fetcher = textureFetcher ?: return
        val lumiyaRef = lumiya ?: return
        if (textureEntry.isEmpty()) return

        val ids = try {
            TextureEntryParser.extractTextureIds(textureEntry)
        } catch (t: Throwable) {
            Log.v(TAG, "TextureEntry parse failed for prim=$primId: ${t.message}")
            return
        }
        for (id in ids) {
            if (!TextureEntryParser.shouldDownload(id)) continue
            fetcher.fetch(id) { bitmap ->
                if (bitmap == null) return@fetch
                runOnGl {
                    lumiyaRef.uploadTextureForPrim(
                        primId,
                        id,
                        bitmap,
                        TextureFormatPolicy.TextureSemantic.ALBEDO
                    )
                }
            }
        }
    }

    /** Backwards-compat alias for callers that still want the legacy name. */
    private fun tryBindDefaultTexture(primId: Long, textureEntry: ByteArray) =
        tryBindFaceTextures(primId, textureEntry)

    // =====================================================================
    // GL thread marshalling
    // =====================================================================

    private fun runOnGl(block: () -> Unit) {
        glThreadExecutor.invoke(Runnable { block() })
    }

    // =====================================================================
    // Terrain helpers
    // =====================================================================

    private fun applyPatch(patch: TerrainPatch) {
        val baseX = patch.x * 16
        val baseY = patch.y * 16
        for (y in 0 until 16) {
            val gy = baseY + y
            if (gy >= REGION_SIZE) continue
            for (x in 0 until 16) {
                val gx = baseX + x
                if (gx >= REGION_SIZE) continue
                terrainHeightmap[gy * REGION_SIZE + gx] = patch.heightMap[y * 16 + x]
            }
        }
    }

    private fun toRendererHeightmap(): FloatArray {
        val out = FloatArray(257 * 257)
        for (y in 0..256) {
            for (x in 0..256) {
                val sx = x.coerceIn(0, 255)
                val sy = y.coerceIn(0, 255)
                out[y * 257 + x] = terrainHeightmap[sy * REGION_SIZE + sx]
            }
        }
        return out
    }
}
