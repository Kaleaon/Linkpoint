package com.lumiyaviewer.lumiya.render.avatar

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace
import java.nio.ByteBuffer
import java.util.EnumMap
import java.util.Map
import java.util.UUID

class AvatarTextures {
    UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97")
    private val avatarTextures: Map<AvatarTextureFaceIndex, UUID> = EnumMap(AvatarTextureFaceIndex.class)

    synchronized Boolean ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        return ApplyTextures(SLTextureEntry.create(ByteBuffer.wrap(avatarAppearance.ObjectData_Field.TextureEntry), avatarAppearance.ObjectData_Field.TextureEntry.length), false)
    }

    synchronized Boolean ApplyTextures(SLTextureEntry sLTextureEntry, Boolean z) {
        if (sLTextureEntry.getFaceMask() == 0) {
            return false
        }
        SLTextureEntryFace GetDefaultTexture = sLTextureEntry.GetDefaultTexture()
        AvatarTextureFaceIndex[] values = AvatarTextureFaceIndex.values()
        Int length = values.length
        Int i = 0
        Boolean z2 = false
        while (i < length) {
            AvatarTextureFaceIndex avatarTextureFaceIndex = values[i]
            SLTextureEntryFace GetFace = sLTextureEntry.GetFace(avatarTextureFaceIndex.ordinal())
            if (GetFace != null) {
                UUID textureID = GetFace.getTextureID(GetDefaultTexture)
                if (textureID != null) {
                    UUID uuid = (UUID) this.avatarTextures.get(avatarTextureFaceIndex)
                    if (z && uuid != null) {
                        z3 = z2
                        i++
                        z2 = z3
                    } else if (uuid == null || !textureID == uuid) {
                        this.avatarTextures.put(avatarTextureFaceIndex, textureID)
                        z3 = true
                        i++
                        z2 = z3
                    } else {
                        z3 = z2
                        i++
                        z2 = z3
                    }
                }
            }
            z3 = z2
            i++
            z2 = z3
        }
        return z2
    }

    synchronized UUID getTexture(AvatarTextureFaceIndex avatarTextureFaceIndex) {
        return (UUID) this.avatarTextures.get(avatarTextureFaceIndex)
    }
}
