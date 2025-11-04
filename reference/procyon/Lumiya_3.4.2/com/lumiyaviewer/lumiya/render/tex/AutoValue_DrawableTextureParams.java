// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.tex;

import javax.annotation.Nullable;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;

final class AutoValue_DrawableTextureParams extends DrawableTextureParams
{
    private final AvatarTextureFaceIndex avatarFaceIndex;
    private final UUID avatarUUID;
    private final TextureClass textureClass;
    private final UUID uuid;
    
    AutoValue_DrawableTextureParams(final UUID uuid, final TextureClass textureClass, @Nullable final AvatarTextureFaceIndex avatarFaceIndex, @Nullable final UUID avatarUUID) {
        if (uuid == null) {
            throw new NullPointerException("Null uuid");
        }
        this.uuid = uuid;
        if (textureClass == null) {
            throw new NullPointerException("Null textureClass");
        }
        this.textureClass = textureClass;
        this.avatarFaceIndex = avatarFaceIndex;
        this.avatarUUID = avatarUUID;
    }
    
    @Nullable
    @Override
    public AvatarTextureFaceIndex avatarFaceIndex() {
        return this.avatarFaceIndex;
    }
    
    @Nullable
    @Override
    public UUID avatarUUID() {
        return this.avatarUUID;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = true;
        if (o == this) {
            return true;
        }
        if (o instanceof DrawableTextureParams) {
            final DrawableTextureParams drawableTextureParams = (DrawableTextureParams)o;
            if (!this.uuid.equals(drawableTextureParams.uuid()) || !this.textureClass.equals(drawableTextureParams.textureClass())) {
                return false;
            }
            if (this.avatarFaceIndex == null) {
                if (drawableTextureParams.avatarFaceIndex() != null) {
                    return false;
                }
            }
            else if (!this.avatarFaceIndex.equals(drawableTextureParams.avatarFaceIndex())) {
                return false;
            }
            if (this.avatarUUID == null) {
                if (drawableTextureParams.avatarUUID() != null) {
                    equals = false;
                }
            }
            else {
                equals = this.avatarUUID.equals(drawableTextureParams.avatarUUID());
            }
            return equals;
            equals = false;
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        final int hashCode2 = this.uuid.hashCode();
        final int hashCode3 = this.textureClass.hashCode();
        int hashCode4;
        if (this.avatarFaceIndex == null) {
            hashCode4 = 0;
        }
        else {
            hashCode4 = this.avatarFaceIndex.hashCode();
        }
        if (this.avatarUUID != null) {
            hashCode = this.avatarUUID.hashCode();
        }
        return (hashCode4 ^ ((hashCode2 ^ 0xF4243) * 1000003 ^ hashCode3) * 1000003) * 1000003 ^ hashCode;
    }
    
    @Override
    public TextureClass textureClass() {
        return this.textureClass;
    }
    
    @Override
    public String toString() {
        return "DrawableTextureParams{uuid=" + this.uuid + ", " + "textureClass=" + this.textureClass + ", " + "avatarFaceIndex=" + this.avatarFaceIndex + ", " + "avatarUUID=" + this.avatarUUID + "}";
    }
    
    @Override
    public UUID uuid() {
        return this.uuid;
    }
}
