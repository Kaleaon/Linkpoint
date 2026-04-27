package com.linkpoint.slproto.objects

import com.linkpoint.render.avatar.AvatarVisualState
import com.linkpoint.render.spatial.DrawListAvatarEntry
import com.linkpoint.render.spatial.DrawListObjectEntry
import com.linkpoint.slproto.messages.AvatarAnimation
import com.linkpoint.slproto.messages.AvatarAppearance
import com.linkpoint.slproto.textures.SLTextureEntry
import java.util.UUID
import javax.annotation.Nonnull

class SLObjectAvatarInfo : SLObjectInfo() {
    private val AvatarVisualState avatarVisualState
    private val Boolean isMyAvatar

    public SLObjectAvatarInfo(UUID uuid, UUID uuid2, Boolean z) {
        this.isMyAvatar = z
        this.avatarVisualState = AvatarVisualState(uuid, this, uuid2)
    }

    fun ApplyAvatarAnimation(avatarAnimation: AvatarAnimation) {
        this.avatarVisualState.ApplyAvatarAnimation(avatarAnimation)
    }

    fun ApplyAvatarAppearance(avatarAppearance: AvatarAppearance) {
        this.avatarVisualState.ApplyAvatarAppearance(avatarAppearance)
    }

    fun ApplyAvatarTextures(sLTextureEntry: SLTextureEntry, z: Boolean) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, z)
    }

    fun ApplyAvatarVisualParams(iArr: IntArray) {
        this.avatarVisualState.ApplyVisualParams(iArr)
    }

    /* access modifiers changed from: protected */
     public fun createDrawListEntry(): DrawListObjectEntry {
        return DrawListAvatarEntry(this)
    }

     public fun getAvatarVisualState(): AvatarVisualState {
        return this.avatarVisualState
    }

     public fun getName(): String {
        return this.isMyAvatar ? "(my avatar)" : "(avatar)"
    }

     public fun isAvatar(): Boolean {
        return true
    }

     public fun isMyAvatar(): Boolean {
        return this.isMyAvatar
    }

    fun onTexturesUpdate(sLTextureEntry: SLTextureEntry) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, false)
    }
}
