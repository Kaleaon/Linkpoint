package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class AvatarTextures {
    public static final UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    private final Map<AvatarTextureFaceIndex, UUID> avatarTextures = new EnumMap(AvatarTextureFaceIndex.class);

    public synchronized boolean ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        return ApplyTextures(SLTextureEntry.create(ByteBuffer.wrap(avatarAppearance.ObjectData_Field.TextureEntry), avatarAppearance.ObjectData_Field.TextureEntry.length), false);
    }

    public synchronized boolean ApplyTextures(SLTextureEntry sLTextureEntry, boolean z) {
        boolean z2;
        UUID textureID;
        if (sLTextureEntry.getFaceMask() == 0) {
            return false;
        }
        SLTextureEntryFace GetDefaultTexture = sLTextureEntry.GetDefaultTexture();
        AvatarTextureFaceIndex[] values = AvatarTextureFaceIndex.values();
        int length = values.length;
        int i = 0;
        boolean z3 = false;
        while (i < length) {
            AvatarTextureFaceIndex avatarTextureFaceIndex = values[i];
            SLTextureEntryFace GetFace = sLTextureEntry.GetFace(avatarTextureFaceIndex.ordinal());
            if (GetFace == null || (textureID = GetFace.getTextureID(GetDefaultTexture)) == null) {
                z2 = z3;
            } else {
                UUID uuid = this.avatarTextures.get(avatarTextureFaceIndex);
                if (z && uuid != null) {
                    z2 = z3;
                } else if (uuid == null || !textureID.equals(uuid)) {
                    this.avatarTextures.put(avatarTextureFaceIndex, textureID);
                    z2 = true;
                } else {
                    z2 = z3;
                }
            }
            i++;
            z3 = z2;
        }
        return z3;
    }

    public synchronized UUID getTexture(AvatarTextureFaceIndex avatarTextureFaceIndex) {
        return this.avatarTextures.get(avatarTextureFaceIndex);
    }
}
