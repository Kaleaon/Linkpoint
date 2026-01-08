package com.lumiyaviewer.lumiya.render.tex

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import com.lumiyaviewer.lumiya.render.tex.TextureClass // Explicit import
import java.util.UUID

data class DrawableTextureParams(
    val textureId: UUID = UUID(0, 0),
    val textureClass: TextureClass = TextureClass.Unknown,
    val isAvatarTexture: Boolean = false
) {
    companion object {
        @JvmStatic
        fun create(uuid: UUID, textureClass: TextureClass): DrawableTextureParams {
            return DrawableTextureParams(textureId = uuid, textureClass = textureClass)
        }
        
        @JvmStatic
        fun create(uuid: UUID?, faceIndex: AvatarTextureFaceIndex, avatarUUID: UUID): DrawableTextureParams {
            return DrawableTextureParams(textureId = uuid ?: UUID(0,0), isAvatarTexture = true)
        }
    }
}
