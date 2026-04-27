// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.tex;

import java.io.File;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import java.util.UUID;

public abstract class DrawableTextureParams
{
    public static DrawableTextureParams create(final UUID uuid, final TextureClass textureClass) {
        return new AutoValue_DrawableTextureParams(uuid, textureClass, null, null);
    }
    
    public static DrawableTextureParams create(final UUID uuid, @Nullable final AvatarTextureFaceIndex avatarTextureFaceIndex, @Nullable final UUID uuid2) {
        return new AutoValue_DrawableTextureParams(uuid, TextureClass.Baked, avatarTextureFaceIndex, uuid2);
    }
    
    @Nullable
    public abstract AvatarTextureFaceIndex avatarFaceIndex();
    
    @Nullable
    public abstract UUID avatarUUID();
    
    public final File getTextureRawPath(final File parent, final boolean b) {
        final int hashCode = this.uuid().hashCode();
        String s;
        final String str = s = this.textureClass().getStorePath();
        if (this.textureClass() == TextureClass.Prim) {
            s = str;
            if (b) {
                s = str + "-hq";
            }
        }
        return new File(parent, String.format("%s-raw/%02x/%s.raw", s, (hashCode >> 24 ^ (hashCode >> 8 ^ hashCode ^ hashCode >> 16)) & 0xFF, this.uuid().toString()));
    }
    
    public abstract TextureClass textureClass();
    
    public abstract UUID uuid();
}
