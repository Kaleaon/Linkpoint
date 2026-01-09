package com.linkpoint.render.tex

import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import java.io.File
import java.util.Locale
import java.util.UUID

abstract class DrawableTextureParams {

    abstract fun uuid(): UUID
    abstract fun textureClass(): TextureClass
    abstract fun avatarFaceIndex(): AvatarTextureFaceIndex?
    abstract fun avatarUUID(): UUID?

    fun getTextureRawPath(parent: File, highQuality: Boolean): File {
        val hash = uuid().hashCode()
        val directoryId = (hash shr 24 xor (hash shr 8 xor hash xor (hash shr 16))) and 0xFF

        var storePath = textureClass().storePath
        if (textureClass() == TextureClass.Prim && highQuality) {
            storePath += "-hq"
        }

        val relative = String.format(
            Locale.US,
            "%s-raw/%02x/%s.raw",
            storePath,
            directoryId,
            uuid().toString(),
        )
        return File(parent, relative)
    }

    companion object {
        fun create(uuid: UUID, textureClass: TextureClass): DrawableTextureParams =
            AutoValue_DrawableTextureParams(uuid, textureClass, null, null)

        fun create(
            uuid: UUID,
            avatarTextureFaceIndex: AvatarTextureFaceIndex?,
            avatarUUID: UUID?,
        ): DrawableTextureParams =
            AutoValue_DrawableTextureParams(uuid, TextureClass.Baked, avatarTextureFaceIndex, avatarUUID)
    }
}
