package com.lumiyaviewer.lumiya.render.avatar

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry
import java.nio.ByteBuffer
import java.util.EnumMap
import java.util.UUID

class AvatarTextures {
    val DEFAULT_AVATAR_TEXTURE: UUID = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97")
    private val avatarTextures: MutableMap<AvatarTextureFaceIndex, UUID> = EnumMap(AvatarTextureFaceIndex::class.java)

    @Synchronized
    fun ApplyAvatarAppearance(avatarAppearance: AvatarAppearance): Boolean {
        return ApplyTextures(
            SLTextureEntry.create(
                ByteBuffer.wrap(avatarAppearance.ObjectData_Field.TextureEntry),
                avatarAppearance.ObjectData_Field.TextureEntry.size
            ),
            false
        )
    }

    @Synchronized
    fun ApplyTextures(slTextureEntry: SLTextureEntry?, force: Boolean): Boolean {
        if (slTextureEntry == null || slTextureEntry.getFaceMask() == 0) {
            return false
        }
        
        val defaultTexture = slTextureEntry.GetDefaultTexture()
        var changed = false
        
        for (faceIndex in AvatarTextureFaceIndex.values()) {
            val slFace = slTextureEntry.GetFace(faceIndex.ordinal)
            if (slFace != null) {
                val textureID = slFace.getTextureID(defaultTexture)
                if (textureID != null) {
                    val currentID = avatarTextures[faceIndex]
                    
                    if (force && currentID != null) {
                        // Logic from decompiled code seemed to do nothing if force is true and uuid != null?
                        // "if (z && uuid != null) { ... i++ ... }"
                        // But typically force means apply anyway?
                        // Let's follow the logic: if (z && uuid != null) -> no change recorded?
                        // Wait, logic was:
                        /*
                        if (z && uuid != null) {
                            z3 = z2 // z2 is changed flag
                        } else if (uuid == null || !textureID == uuid) {
                            this.avatarTextures.put(avatarTextureFaceIndex, textureID)
                            z3 = true
                        }
                        */
                        // So if force is true and we already have a texture, we DON'T update? That seems weird for "ApplyTextures".
                        // Maybe 'z' was 'onlyIfMissing'?
                        // Let's assume 'force' means update even if same? No.
                        // If 'z' is true, we skip update if existing?
                        // Let's assume standard behavior: update if different.
                        
                        if (currentID == null || textureID != currentID) {
                             // If 'z' (force?) is true, and we have one, maybe we skip?
                             // The decompiled code:
                             // if (z && uuid != null) { ... no update ... }
                             // else if (uuid == null || !textureID.equals(uuid)) { update }
                             
                             // This implies z means "only apply if missing" (if uuid != null, do nothing).
                             
                             if (!force || currentID == null) {
                                 if (currentID == null || textureID != currentID) {
                                     avatarTextures[faceIndex] = textureID
                                     changed = true
                                 }
                             }
                        }
                    } else {
                        // If not forcing (or current is null), check difference
                         if (currentID == null || textureID != currentID) {
                             avatarTextures[faceIndex] = textureID
                             changed = true
                         }
                    }
                }
            }
        }
        return changed
    }

    @Synchronized
    fun getTexture(faceIndex: AvatarTextureFaceIndex): UUID? {
        return avatarTextures[faceIndex]
    }
}
