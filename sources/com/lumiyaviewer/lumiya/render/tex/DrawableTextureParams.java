package com.lumiyaviewer.lumiya.render.tex;

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import java.io.File;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class DrawableTextureParams {
    public static DrawableTextureParams create(UUID uuid, TextureClass textureClass) {
        return new AutoValue_DrawableTextureParams(uuid, textureClass, (AvatarTextureFaceIndex) null, (UUID) null);
    }

    public static DrawableTextureParams create(UUID uuid, @Nullable AvatarTextureFaceIndex avatarTextureFaceIndex, @Nullable UUID uuid2) {
        return new AutoValue_DrawableTextureParams(uuid, TextureClass.Baked, avatarTextureFaceIndex, uuid2);
    }

    @Nullable
    public abstract AvatarTextureFaceIndex avatarFaceIndex();

    @Nullable
    public abstract UUID avatarUUID();

    public final File getTextureRawPath(File file, boolean z) {
        int hashCode = uuid().hashCode();
        int i = ((hashCode >> 24) ^ (((hashCode >> 8) ^ hashCode) ^ (hashCode >> 16))) & 255;
        String storePath = textureClass().getStorePath();
        if (textureClass() == TextureClass.Prim && z) {
            storePath = storePath + "-hq";
        }
        return new File(file, String.format("%s-raw/%02x/%s.raw", new Object[]{storePath, Integer.valueOf(i), uuid().toString()}));
    }

    public abstract TextureClass textureClass();

    public abstract UUID uuid();
}
