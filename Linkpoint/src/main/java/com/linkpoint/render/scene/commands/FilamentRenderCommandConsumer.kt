package com.linkpoint.render.scene.commands

import android.util.Log
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.render.RenderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FilamentRenderCommandConsumer(
    private val renderManager: RenderManager,
    private val stream: RenderCommandStream,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "FilamentCmdConsumer"
        private const val REGION_SIZE = 256
    }

    private val terrainHeightmap = FloatArray(REGION_SIZE * REGION_SIZE)
    private var consumeJob: Job? = null

    fun start() {
        if (consumeJob != null) return
        consumeJob = scope.launch {
            stream.commands.collect { command ->
                renderManager.dispatcher.post(Runnable {
                    try {
                        when (command) {
                            is SceneRenderCommand.UpsertPrim -> {
                                if (command.update.pcode == 47) {
                                    renderManager.getSceneManager()?.updateAvatar(
                                        agentId = command.update.fullId,
                                        position = command.update.position,
                                        rotation = command.update.rotation
                                    )
                                } else {
                                    renderManager.updatePrim(command.update)
                                }
                            }
                            is SceneRenderCommand.UpsertMesh -> {
                                renderManager.attachMeshAsset(
                                    command.localId,
                                    command.meshData,
                                    command.textureEntry,
                                    binder = null
                                )
                            }
                            is SceneRenderCommand.UpdateMaterial -> {
                                command.fallbackUpdate?.let { renderManager.updatePrim(it) }
                            }
                            is SceneRenderCommand.RemoveEntity -> {
                                renderManager.removePrim(command.localId)
                                command.fullId?.let { renderManager.getSceneManager()?.removeObject(it) }
                            }
                            is SceneRenderCommand.SetCamera -> {
                                renderManager.cameraController.setAgentPosition(command.position)
                            }
                            is SceneRenderCommand.SetTerrainPatch -> {
                                applyPatch(command.patch)
                                renderManager.getTerrainRenderer()?.setHeightmap(toRendererHeightmap())
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to apply command $command: ${e.message}")
                    }
                })
            }
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
