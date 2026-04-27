// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import java.util.EnumMap;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import java.util.Map;
import java.util.UUID;

public class AvatarTextures
{
    public static final UUID DEFAULT_AVATAR_TEXTURE;
    private final Map<AvatarTextureFaceIndex, UUID> avatarTextures;
    
    static {
        DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    }
    
    public AvatarTextures() {
        this.avatarTextures = new EnumMap<AvatarTextureFaceIndex, UUID>(AvatarTextureFaceIndex.class);
    }
    
    public boolean ApplyAvatarAppearance(final AvatarAppearance avatarAppearance) {
        synchronized (this) {
            return this.ApplyTextures(SLTextureEntry.create(ByteBuffer.wrap(avatarAppearance.ObjectData_Field.TextureEntry), avatarAppearance.ObjectData_Field.TextureEntry.length), false);
        }
    }
    
    public boolean ApplyTextures(final SLTextureEntry slTextureEntry, final boolean b) {
        while (true) {
            while (true) {
                Label_0157: {
                    synchronized (this) {
                        int i = slTextureEntry.getFaceMask();
                        if (i == 0) {
                            return false;
                        }
                        final SLTextureEntryFace getDefaultTexture = slTextureEntry.GetDefaultTexture();
                        final AvatarTextureFaceIndex[] values = AvatarTextureFaceIndex.values();
                        final int length = values.length;
                        i = 0;
                        boolean b2 = false;
                        while (i < length) {
                            final AvatarTextureFaceIndex avatarTextureFaceIndex = values[i];
                            final SLTextureEntryFace getFace = slTextureEntry.GetFace(avatarTextureFaceIndex.ordinal());
                            if (getFace == null) {
                                break Label_0157;
                            }
                            final UUID textureID = getFace.getTextureID(getDefaultTexture);
                            if (textureID == null) {
                                break Label_0157;
                            }
                            final UUID obj = this.avatarTextures.get(avatarTextureFaceIndex);
                            if (!b || obj == null) {
                                if (obj == null || !textureID.equals(obj)) {
                                    this.avatarTextures.put(avatarTextureFaceIndex, textureID);
                                    b2 = true;
                                }
                            }
                            ++i;
                        }
                        return b2;
                    }
                }
                continue;
            }
        }
    }
    
    public UUID getTexture(final AvatarTextureFaceIndex avatarTextureFaceIndex) {
        synchronized (this) {
            return this.avatarTextures.get(avatarTextureFaceIndex);
        }
    }
}
