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
        this.engineProvider = provider
        this.lumiya = provider as? LumiyaRenderer
        this.glThreadExecutor = glThreadExecutor
        this.textureFetcher = textureFetcher
    }

    /** Backwards-compatible single-argument bind for callers that don't yet wire texture fetch. */
    fun bindEngine(provider: RenderEngineProvider?) {
        bindEngine(provider, { it.run() }, null)
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
        val engine = engineProvider ?: return
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
        runOnGl {
            // pcode 47 == LL_PCODE_LEGACY_AVATAR. Route avatars to the
            // avatar store; regular prims go to the prim store.
            if (update.pcode == 47) {
                lumiya?.upsertAvatar(update.fullId, p.x, p.y, p.z)
            } else {
                engine.addObject(update.localId.toLong(), p.x, p.y, p.z)
                // Kick off a texture fetch for the default face so the
                // prim doesn't ship as flat-shaded grey on first frame.
                tryBindDefaultTexture(update.localId.toLong(), update.textureEntry)
            }
        }
    }

    private fun handleUpsertMesh(engine: RenderEngineProvider, cmd: SceneRenderCommand.UpsertMesh) {
        // Mesh geometry compilation in the Lumiya backend is not yet
        // wired — this lands as a basic prim placeholder so the asset
        // arrival isn't silently lost. Texture binding still applies.
        runOnGl {
            tryBindDefaultTexture(cmd.localId.toLong(), cmd.textureEntry)
        }
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

    // =====================================================================
    // Texture binding
    // =====================================================================

    /**
     * Pull the default texture UUID out of [textureEntry], fetch the
     * bitmap via [textureFetcher], and on success queue a GL-thread
     * upload + bind for [primId].
     */
    private fun tryBindDefaultTexture(primId: Long, textureEntry: ByteArray) {
        val fetcher = textureFetcher ?: return
        val lumiyaRef = lumiya ?: return
        if (textureEntry.isEmpty()) return

        val ids = try {
            TextureEntryParser.extractTextureIds(textureEntry)
        } catch (t: Throwable) {
            Log.v(TAG, "TextureEntry parse failed for prim=$primId: ${t.message}")
            return
        }
        // Use the first downloadable UUID we find. Faces beyond the
        // default share its handle until per-face material binding lands.
        val defaultId = ids.firstOrNull { TextureEntryParser.shouldDownload(it) } ?: return

        fetcher.fetch(defaultId) { bitmap ->
            if (bitmap == null) return@fetch
            runOnGl {
                lumiyaRef.uploadTextureForPrim(
                    primId,
                    defaultId,
                    bitmap,
                    TextureFormatPolicy.TextureSemantic.ALBEDO
                )
            }
        }
    }

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
