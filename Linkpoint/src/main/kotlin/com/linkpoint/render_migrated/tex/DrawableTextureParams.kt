package com.linkpoint.render.tex

import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import java.io.File
import java.util.UUID
import javax.annotation.Nullable

abstract class DrawableTextureParams {
    @JvmStatic
    DrawableTextureParams create(UUID uuid, TextureClass textureClass) {
        return AutoValue_DrawableTextureParams(uuid, textureClass, null, null)
    }

    @JvmStatic
    DrawableTextureParams create(UUID uuid, AvatarTextureFaceIndex avatarTextureFaceIndex, UUID uuid2) {
        return AutoValue_DrawableTextureParams(uuid, TextureClass.Baked, avatarTextureFaceIndex, uuid2)
    }

    public abstract AvatarTextureFaceIndex avatarFaceIndex()

    public abstract UUID avatarUUID()

    val File getTextureRawPath(File file, Boolean z) {
        Int hashCode = uuid().hashCode()
        Int i = ((hashCode >> 24) ^ (((hashCode >> 8) ^ hashCode) ^ (hashCode >> 16))) & 255
        String storePath = textureClass().getStorePath()
        if (textureClass() == TextureClass.Prim && z) {
            storePath = storePath + "-hq"
        }
        return File(file, String.format("%s-raw/%02x/%s.raw", Object[]{storePath, Integer.valueOf(i), uuid().toString()}))
    }

    public abstract TextureClass textureClass()

    public abstract UUID uuid()
}
