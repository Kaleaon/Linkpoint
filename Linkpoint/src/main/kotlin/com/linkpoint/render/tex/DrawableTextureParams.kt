package com.linkpoint.render.tex

import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import java.io.File
import java.util.UUID
import javax.annotation.Nullable

abstract class DrawableTextureParams {
    @JvmStatic
     fun create(uuid: UUID, textureClass: TextureClass): DrawableTextureParams {
        return AutoValue_DrawableTextureParams(uuid, textureClass, null, null)
    }

    @JvmStatic
     fun create(uuid: UUID, avatarTextureFaceIndex: AvatarTextureFaceIndex, uuid2: UUID): DrawableTextureParams {
        return AutoValue_DrawableTextureParams(uuid, TextureClass.Baked, avatarTextureFaceIndex, uuid2)
    }

    public abstract AvatarTextureFaceIndex avatarFaceIndex()

    public abstract UUID avatarUUID()

    val File getTextureRawPath(File file, Boolean z) {
        val hashCode: Int = uuid().hashCode()
        val i: Int = ((hashCode >> 24) ^ (((hashCode >> 8) ^ hashCode) ^ (hashCode >> 16))) & 255
        val storePath: String = textureClass().getStorePath()
        if (textureClass() == TextureClass.Prim && z) {
            storePath = storePath + "-hq"
        }
        return File(file, String.format("%s-raw/%02x/%s.raw", Array<Any>{storePath, Integer.valueOf(i), uuid().toString()}))
    }

    public abstract TextureClass textureClass()

    public abstract UUID uuid()
}
