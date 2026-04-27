package com.linkpoint.render.scene.commands

import com.linkpoint.assets.MeshData
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.protocol.types.LLVector3
import java.util.UUID

sealed interface SceneRenderCommand {
    data class UpsertPrim(val update: ObjectUpdateData) : SceneRenderCommand
    data class UpsertMesh(
        val localId: Int,
        val meshData: MeshData,
        val textureEntry: ByteArray
    ) : SceneRenderCommand
    data class UpdateMaterial(
        val localId: Int,
        val textureEntry: ByteArray,
        val fallbackUpdate: ObjectUpdateData? = null
    ) : SceneRenderCommand
    data class RemoveEntity(
        val localId: Int,
        val fullId: UUID? = null
    ) : SceneRenderCommand
    data class SetCamera(
        val position: LLVector3,
        val target: LLVector3
    ) : SceneRenderCommand
    data class SetTerrainPatch(val patch: TerrainPatch) : SceneRenderCommand
}
