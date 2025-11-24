package com.lumiyaviewer.lumiya.render.avatar

import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.Map
import java.util.UUID

// Stub for DrawableAvatar
class DrawableAvatar(
    store: DrawableStore?,
    agentUUID: UUID?,
    avatarObject: SLObjectAvatarInfo?,
    avatarUUID: UUID?,
    animations: Map<UUID, AnimationSequenceInfo>?
) {
    var headPosition: LLVector3 = LLVector3()
    var jointMatrixUpdated: Boolean = false
    var pelvisTranslateX: Float = 0.0f
    var pelvisTranslateY: Float = 0.0f
    var pelvisTranslateZ: Float = 0.0f

    fun UpdateShapeParams(params: AvatarShapeParams?) {
        // Stub
    }

    fun UpdateTextures(textures: AvatarTextures?) {
        // Stub
    }

    fun AnimationUpdate(info: AnimationSequenceInfo?) {
        // Stub
    }

    fun AnimationRemove(animID: UUID?) {
        // Stub
    }

    fun IsAnimationStopped(animID: UUID?): Boolean {
        return true // Stub
    }
}
