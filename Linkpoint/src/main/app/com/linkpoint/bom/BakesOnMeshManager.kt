package com.linkpoint.bom

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Minimal-yet-functional Bakes on Mesh implementation. The manager keeps
 * track of each avatar's texture layers, performs deterministic "bakes" on a
 * background coroutine, and exposes a simple cache for the renderer/tests.
 */
class BakesOnMeshManager(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val bakeDelayMs: Long = 50L
) {

    private data class AvatarBakeState(
        val layers: MutableList<BomTextureLayer> = CopyOnWriteArrayList(),
        val bakedTextures: MutableMap<BomBakeType, BomBakedTexture> = ConcurrentHashMap()
    )

    private val avatars = ConcurrentHashMap<UUID, AvatarBakeState>()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    fun registerAvatar(avatarId: UUID) {
        avatars.computeIfAbsent(avatarId) { AvatarBakeState() }
    }

    fun unregisterAvatar(avatarId: UUID) {
        avatars.remove(avatarId)
    }

    fun addTextureLayer(avatarId: UUID, layer: BomTextureLayer) {
        val state = avatars[avatarId] ?: error("Avatar $avatarId not registered")
        state.layers.removeAll { it.layerId == layer.layerId }
        state.layers.add(layer)
        scope.launch {
            bakeLayer(avatarId, layer.bakeType)
        }
    }

    fun getBakedTexture(avatarId: UUID, bakeType: BomBakeType): BomBakedTexture? =
        avatars[avatarId]?.bakedTextures?.get(bakeType)

    fun getAllBakedTextures(avatarId: UUID): Map<BomBakeType, BomBakedTexture> =
        avatars[avatarId]?.bakedTextures?.toMap() ?: emptyMap()

    fun cleanup() {
        avatars.clear()
        scope.cancel("BakesOnMeshManager disposed")
    }

    private suspend fun bakeLayer(avatarId: UUID, bakeType: BomBakeType) {
        val state = avatars[avatarId] ?: return
        delay(bakeDelayMs)
        val layer = state.layers.lastOrNull { it.bakeType == bakeType } ?: return
        state.bakedTextures[bakeType] = BomBakedTexture(
            bakeType = bakeType,
            textureId = layer.textureId,
            bakedAtMillis = System.currentTimeMillis()
        )
    }
}
