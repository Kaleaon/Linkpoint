package com.linkpoint.render.avatar

import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import com.linkpoint.slproto.messages.AvatarAppearance
import com.linkpoint.slproto.textures.SLTextureEntry
import com.linkpoint.slproto.textures.SLTextureEntryFace
import java.nio.ByteBuffer
import java.util.EnumMap
import java.util.Map
import java.util.UUID

class AvatarTextures {
    const val UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97")
    private val Map<AvatarTextureFaceIndex, UUID> avatarTextures = EnumMap(AvatarTextureFaceIndex.class)

    public synchronized Boolean ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        return ApplyTextures(SLTextureEntry.create(ByteBuffer.wrap(avatarAppearance.ObjectData_Field.TextureEntry), avatarAppearance.ObjectData_Field.TextureEntry.length), false)
    }

    public synchronized Boolean ApplyTextures(SLTextureEntry sLTextureEntry, Boolean z) {
        if (sLTextureEntry.getFaceMask() == 0) {
            return false
        }
        val GetDefaultTexture: SLTextureEntryFace = sLTextureEntry.GetDefaultTexture()
        val values: Array<AvatarTextureFaceIndex> = AvatarTextureFaceIndex.values()
        val length: Int = values.length
        val i: Int = 0
        val z2: Boolean = false
        while (i < length) {
            val avatarTextureFaceIndex: AvatarTextureFaceIndex = values[i]
            val GetFace: SLTextureEntryFace = sLTextureEntry.GetFace(avatarTextureFaceIndex.ordinal())
            if (GetFace != null) {
                val textureID: UUID = GetFace.getTextureID(GetDefaultTexture)
                if (textureID != null) {
                    val uuid: UUID = (UUID) this.avatarTextures.get(avatarTextureFaceIndex)
                    if (z && uuid != null) {
                        z3 = z2
                        i++
                        z2 = z3
                    } else if (uuid == null || !textureID.equals(uuid)) {
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

    public synchronized UUID getTexture(AvatarTextureFaceIndex avatarTextureFaceIndex) {
        return (UUID) this.avatarTextures.get(avatarTextureFaceIndex)
    }
}
