package com.linkpoint.render

import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.scenery.SceneObject
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.channels.Channel
import java.util.UUID

sealed class RenderableUpdate {
    data class PrimUpdate(val data: ObjectUpdateData) : RenderableUpdate()
    data class SceneObjectUpdate(val sceneObject: SceneObject) : RenderableUpdate()
    data class TerrainUpdate(val heightmap: FloatArray, val width: Int, val depth: Int) : RenderableUpdate()
    data class AvatarUpdate(
        val agentId: UUID,
        val position: LLVector3,
        val rotation: LLQuaternion,
        val animations: List<UUID> = emptyList()
    ) : RenderableUpdate()
    data class RemoveObject(val objectId: UUID) : RenderableUpdate()
    object ClearScene : RenderableUpdate()
}

class RenderQueue {
    private val channel = Channel<RenderableUpdate>(Channel.UNLIMITED)

    fun enqueue(update: RenderableUpdate): Boolean {
        return channel.trySend(update).isSuccess
    }

    fun drain(maxUpdates: Int = Int.MAX_VALUE): List<RenderableUpdate> {
        if (maxUpdates <= 0) return emptyList()
        val updates = mutableListOf<RenderableUpdate>()
        while (updates.size < maxUpdates) {
            val result = channel.tryReceive()
            if (result.isSuccess) {
                updates.add(result.getOrThrow())
            } else {
                break
            }
        }
        return updates
    }
}
