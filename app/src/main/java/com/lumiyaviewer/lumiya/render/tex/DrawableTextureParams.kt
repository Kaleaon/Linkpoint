package com.lumiyaviewer.lumiya.render.tex

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import java.io.File
import java.util.UUID
import javax.annotation.Nullable

abstract class DrawableTextureParams {
    fun create(uuid: UUID, textureClass: TextureClass): DrawableTextureParams {
        return AutoValue_DrawableTextureParams(uuid, textureClass, null, null)
    }

    fun create(uuid: UUID, avatarTextureFaceIndex: AvatarTextureFaceIndex, uuid2: UUID): DrawableTextureParams {
        return AutoValue_DrawableTextureParams(uuid, TextureClass.Baked, avatarTextureFaceIndex, uuid2)
    }

    @Nullable
    abstract fun avatarFaceIndex(): AvatarTextureFaceIndex

    @Nullable
    abstract fun avatarUUID(): UUID

    File getTextureRawPath(File file, boolean z) {
        int hashCode = uuid().hashCode()
        int i = ((hashCode >> 24) ^ (((hashCode >> 8) ^ hashCode) ^ (hashCode >> 16))) & 255
        String storePath = textureClass().getStorePath()
        if (textureClass() == TextureClass.Prim && z) {
            storePath = storePath + "-hq"
        }
        return File(file, String.format("%s-raw/%02x/%s.raw", new Array<Any>{storePath, Integer.valueOf(i), uuid().toString()}))
    }

    abstract fun textureClass(): TextureClass

    abstract fun uuid(): UUID
}
