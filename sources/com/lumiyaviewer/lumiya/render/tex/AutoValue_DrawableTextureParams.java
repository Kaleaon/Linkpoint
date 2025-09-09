package com.lumiyaviewer.lumiya.render.tex;

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import java.util.UUID;
import javax.annotation.Nullable;

final class AutoValue_DrawableTextureParams extends DrawableTextureParams {
    private final AvatarTextureFaceIndex avatarFaceIndex;
    private final UUID avatarUUID;
    private final TextureClass textureClass;
    private final UUID uuid;

    AutoValue_DrawableTextureParams(UUID uuid2, TextureClass textureClass2, @Nullable AvatarTextureFaceIndex avatarTextureFaceIndex, @Nullable UUID uuid3) {
        if (uuid2 == null) {
            throw new NullPointerException("Null uuid");
        }
        this.uuid = uuid2;
        if (textureClass2 == null) {
            throw new NullPointerException("Null textureClass");
        }
        this.textureClass = textureClass2;
        this.avatarFaceIndex = avatarTextureFaceIndex;
        this.avatarUUID = uuid3;
    }

    @Nullable
    public AvatarTextureFaceIndex avatarFaceIndex() {
        return this.avatarFaceIndex;
    }

    @Nullable
    public UUID avatarUUID() {
        return this.avatarUUID;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DrawableTextureParams)) {
            return false;
        }
        DrawableTextureParams drawableTextureParams = (DrawableTextureParams) obj;
        if (!this.uuid.equals(drawableTextureParams.uuid()) || !this.textureClass.equals(drawableTextureParams.textureClass()) || (this.avatarFaceIndex != null ? !this.avatarFaceIndex.equals(drawableTextureParams.avatarFaceIndex()) : drawableTextureParams.avatarFaceIndex() != null)) {
            return false;
        }
        return this.avatarUUID == null ? drawableTextureParams.avatarUUID() == null : this.avatarUUID.equals(drawableTextureParams.avatarUUID());
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.avatarFaceIndex == null ? 0 : this.avatarFaceIndex.hashCode()) ^ ((((this.uuid.hashCode() ^ 1000003) * 1000003) ^ this.textureClass.hashCode()) * 1000003)) * 1000003;
        if (this.avatarUUID != null) {
            i = this.avatarUUID.hashCode();
        }
        return hashCode ^ i;
    }

    public TextureClass textureClass() {
        return this.textureClass;
    }

    public String toString() {
        return "DrawableTextureParams{uuid=" + this.uuid + ", " + "textureClass=" + this.textureClass + ", " + "avatarFaceIndex=" + this.avatarFaceIndex + ", " + "avatarUUID=" + this.avatarUUID + "}";
    }

    public UUID uuid() {
        return this.uuid;
    }
}
