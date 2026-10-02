package com.linkpoint.render.scene.commands

import android.util.Log
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.render.RenderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FilamentRenderCommandConsumer(
    private val renderManager: RenderManager,
    private val stream: RenderCommandStream,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "FilamentCmdConsumer"
        private const val REGION_SIZE = 256
        private const val READY_POLL_INTERVAL_MS = 100L
        private const val PENDING_CAPACITY = 4096
    }

    private val recorder = ParallelCommandBufferRecorder()
    private val terrainHeightmap = FloatArray(REGION_SIZE * REGION_SIZE)
    private var consumeJob: Job? = null
    private var readyWatcherJob: Job? = null

    /**
     * Commands that landed before [RenderManager.isReady] returned true.
     * Filament's prim renderer is constructed asynchronously after engine
     * init, and any UpsertPrim that beats it would fall out of
     * [RenderManager.updatePrim] at the `primRenderer ?: return false`
     * guard, silently dropping the simulator's initial scene burst.
     * Mirrors the buffer in [Gles3RenderCommandConsumer]; oldest commands
     * are evicted first since newer ObjectUpdates supersede older ones for
     * the same prim.
     */
    private val pendingCommands = ArrayDeque<SceneRenderCommand>()
    private val pendingLock = Any()

    fun start() {
        if (consumeJob != null) return
        consumeJob = scope.launch {
            stream.commands.collect { command ->
                val prepared = recorder.recordSingleCommand(command)
                renderManager.dispatcher.post(Runnable {
                    if (!renderManager.isReady()) {
                        bufferPending(command)
                    } else {
                        applyPreparedCommand(prepared)
                    }
                })
            }
        }
        // Drain any commands buffered while the prim renderer was still
        // initialising. Polling is acceptable here — Filament init takes
        // sub-second on warm caches and the watcher exits as soon as the
        // first ready check passes. Without this, buffered commands sit
        // forever because nothing else triggers a flush.
        readyWatcherJob = scope.launch {
            while (isActive && !renderManager.isReady()) {
                delay(READY_POLL_INTERVAL_MS)
            }
            renderManager.dispatcher.post(Runnable { drainPending() })
        }
    }

    private fun bufferPending(command: SceneRenderCommand) {
        synchronized(pendingLock) {
            if (pendingCommands.size >= PENDING_CAPACITY) {
                pendingCommands.removeFirst()
            }
            pendingCommands.addLast(command)
        }
    }

    private fun drainPending() {
        val drained = synchronized(pendingLock) {
            val copy = pendingCommands.toList()
            pendingCommands.clear()
            copy
        }
        if (drained.isEmpty()) return
        Log.i(TAG, "Replaying ${drained.size} pending render commands after primRenderer ready")
        for (command in drained) {
            val prepared = recorder.recordSingleCommand(command)
            applyPreparedCommand(prepared)
        }
    }

    private fun applyPreparedCommand(prepared: PreparedRenderCommand) {
        try {
            when (prepared) {
                is PreparedRenderCommand.PreparedUpsertPrim -> {
                    if (prepared.isAvatar) {
                        renderManager.getSceneManager()?.updateAvatar(
                            agentId = prepared.update.fullId,
                            position = prepared.update.position,
                            rotation = prepared.update.rotation
                        )
                    } else {
                        renderManager.updatePrim(prepared.update)
                    }
                }
                is PreparedRenderCommand.PreparedUpsertMesh -> {
                    renderManager.attachMeshAsset(
                        prepared.localId,
                        prepared.meshData,
                        prepared.textureEntry,
                        binder = null
                    )
                }
                is PreparedRenderCommand.PreparedUpdateMaterial -> {
                    prepared.fallbackUpdate?.let { renderManager.updatePrim(it) }
                }
                is PreparedRenderCommand.PreparedRemoveEntity -> {
                    renderManager.removePrim(prepared.localId)
                    prepared.fullId?.let { renderManager.getSceneManager()?.removeObject(it) }
                }
                is PreparedRenderCommand.PreparedSetCamera -> {
                    renderManager.cameraController.setAgentPosition(prepared.position)
                }
                is PreparedRenderCommand.PreparedSetTerrainPatch -> {
                    applyPatch(prepared.patch)
                    renderManager.getTerrainRenderer()?.setHeightmap(toRendererHeightmap())
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to apply prepared command $prepared: ${e.message}")
        }
    }

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
