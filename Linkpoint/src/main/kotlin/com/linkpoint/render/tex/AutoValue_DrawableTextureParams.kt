package com.linkpoint.render.tex

import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import java.util.UUID
import javax.annotation.Nullable

final class AutoValue_DrawableTextureParams : DrawableTextureParams() {
    private val AvatarTextureFaceIndex avatarFaceIndex
    private val UUID avatarUUID
    private val TextureClass textureClass
    private val UUID uuid

    AutoValue_DrawableTextureParams(UUID uuid, TextureClass textureClass, AvatarTextureFaceIndex avatarTextureFaceIndex, UUID uuid2) {
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

    public AvatarTextureFaceIndex avatarFaceIndex() {
        return this.avatarFaceIndex
    }

    public UUID avatarUUID() {
        return this.avatarUUID
    }

    public Boolean equals(Object obj) {
        Boolean z = true
        if (obj == this) {
            return true
        }
        if (!(obj instanceof DrawableTextureParams)) {
            return false
        }
        DrawableTextureParams drawableTextureParams = (DrawableTextureParams) obj
        if (!this.uuid.equals(drawableTextureParams.uuid()) || !this.textureClass.equals(drawableTextureParams.textureClass()) || (this.avatarFaceIndex != null ? this.avatarFaceIndex.equals(drawableTextureParams.avatarFaceIndex()) : drawableTextureParams.avatarFaceIndex() == null)) {
            z = false
        } else if (this.avatarUUID != null) {
            z = this.avatarUUID.equals(drawableTextureParams.avatarUUID())
        } else if (drawableTextureParams.avatarUUID() != null) {
            z = false
        }
        return z
    }

    public Int hashCode() {
        Int i = 0
        Int hashCode = ((this.avatarFaceIndex == null ? 0 : this.avatarFaceIndex.hashCode()) ^ ((((this.uuid.hashCode() ^ 1000003) * 1000003) ^ this.textureClass.hashCode()) * 1000003)) * 1000003
        if (this.avatarUUID != null) {
            i = this.avatarUUID.hashCode()
        }
        return hashCode ^ i
    }

    public TextureClass textureClass() {
        return this.textureClass
    }

    public String toString() {
        return "DrawableTextureParams{uuid=" + this.uuid + ", " + "textureClass=" + this.textureClass + ", " + "avatarFaceIndex=" + this.avatarFaceIndex + ", " + "avatarUUID=" + this.avatarUUID + "}"
    }

    public UUID uuid() {
        return this.uuid
    }
}
