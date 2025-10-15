package com.lumiyaviewer.lumiya.render.tex

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import java.util.UUID
import javax.annotation.Nullable

class AutoValue_DrawableTextureParams : DrawableTextureParams {
    private AvatarTextureFaceIndex avatarFaceIndex
    private UUID avatarUUID
    private TextureClass textureClass
    private UUID uuid

    AutoValue_DrawableTextureParams(UUID uuid, TextureClass textureClass, @Nullable AvatarTextureFaceIndex avatarTextureFaceIndex, @Nullable UUID uuid2) {
        if (uuid == null) {
            throw NullPointerException("Null uuid")
        }
        this.uuid = uuid
        if (textureClass == null) {
            throw NullPointerException("Null textureClass")
        }
        this.textureClass = textureClass
        this.avatarFaceIndex = avatarTextureFaceIndex
        this.avatarUUID = uuid2
    }

    @Nullable
    fun avatarFaceIndex(): AvatarTextureFaceIndex {
        return this.avatarFaceIndex
    }

    @Nullable
    fun avatarUUID(): UUID {
        return this.avatarUUID
    }

    fun equals(obj: Any): Boolean {
        Boolean z = true
        if (obj == this) {
            return true
        }
        if (!(obj instanceof DrawableTextureParams)) {
            return false
        }
        DrawableTextureParams drawableTextureParams = (DrawableTextureParams) obj
        if (!this.uuid == drawableTextureParams.uuid() || !this.textureClass == drawableTextureParams.textureClass() || (this.avatarFaceIndex != null ? this.avatarFaceIndex == drawableTextureParams.avatarFaceIndex() : drawableTextureParams.avatarFaceIndex() == null)) {
            z = false
        } else if (this.avatarUUID != null) {
            z = this.avatarUUID == drawableTextureParams.avatarUUID()
        } else if (drawableTextureParams.avatarUUID() != null) {
            z = false
        }
        return z
    }

    fun hashCode(): Int {
        Int i = 0
        Int hashCode = ((this.avatarFaceIndex == null ? 0 : this.avatarFaceIndex.hashCode()) ^ ((((this.uuid.hashCode() ^ 1000003) * 1000003) ^ this.textureClass.hashCode()) * 1000003)) * 1000003
        if (this.avatarUUID != null) {
            i = this.avatarUUID.hashCode()
        }
        return hashCode ^ i
    }

    fun textureClass(): TextureClass {
        return this.textureClass
    }

    fun toString(): String {
        return "DrawableTextureParams{uuid=" + this.uuid + ", " + "textureClass=" + this.textureClass + ", " + "avatarFaceIndex=" + this.avatarFaceIndex + ", " + "avatarUUID=" + this.avatarUUID + "}"
    }

    fun uuid(): UUID {
        return this.uuid
    }
}
