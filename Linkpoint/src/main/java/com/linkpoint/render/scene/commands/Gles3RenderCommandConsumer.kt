package com.linkpoint.render.scene.commands

import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.render.lumiya.core.RenderEngineProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Gles3RenderCommandConsumer(
    private val stream: RenderCommandStream,
    private val scope: CoroutineScope
) {
    private var engineProvider: RenderEngineProvider? = null
    private var consumeJob: Job? = null
    private val terrainHeightmap = FloatArray(256 * 256)

    fun bindEngine(provider: RenderEngineProvider?) {
        engineProvider = provider
    }

    fun start() {
        if (consumeJob != null) return
        consumeJob = scope.launch {
            stream.commands.collect { command ->
                val engine = engineProvider ?: return@collect
                when (command) {
                    is SceneRenderCommand.UpsertPrim -> {
                        val p = command.update.position
                        engine.addObject(command.update.localId.toLong(), p.x, p.y, p.z)
                    }
                    is SceneRenderCommand.UpsertMesh -> Unit
                    is SceneRenderCommand.UpdateMaterial -> Unit
                    is SceneRenderCommand.RemoveEntity -> engine.removeObject(command.localId.toLong())
                    is SceneRenderCommand.SetCamera -> {
                        val p = command.position
                        val t = command.target
                        engine.setCameraPosition(p.x, p.y, p.z)
                        engine.setCameraTarget(t.x, t.y, t.z)
                    }
                    is SceneRenderCommand.SetTerrainPatch -> {
                        applyPatch(command.patch)
                        engine.updateTerrain(toRendererHeightmap(), 257, 257)
                    }
                }
            }
        }
    }

    private fun applyPatch(patch: TerrainPatch) {
        val baseX = patch.x * 16
        val baseY = patch.y * 16
        for (y in 0 until 16) {
            val gy = baseY + y
            if (gy >= 256) continue
            for (x in 0 until 16) {
                val gx = baseX + x
                if (gx >= 256) continue
                terrainHeightmap[gy * 256 + gx] = patch.heightMap[y * 16 + x]
            }
        }
    }

    private fun toRendererHeightmap(): FloatArray {
        val out = FloatArray(257 * 257)
        for (y in 0..256) {
            for (x in 0..256) {
                val sx = x.coerceIn(0, 255)
                val sy = y.coerceIn(0, 255)
                out[y * 257 + x] = terrainHeightmap[sy * 256 + sx]
            }
        }
        return out
    }
}
