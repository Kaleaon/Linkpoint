package com.linkpoint.slproto.objects

import com.linkpoint.render.avatar.AvatarVisualState
import com.linkpoint.render.spatial.DrawListAvatarEntry
import com.linkpoint.render.spatial.DrawListObjectEntry
import com.linkpoint.slproto.messages.AvatarAnimation
import com.linkpoint.slproto.messages.AvatarAppearance
import com.linkpoint.slproto.textures.SLTextureEntry
import java.util.UUID
import androidx.annotation.NonNull

class SLObjectAvatarInfo : SLObjectInfo {
    @NonNull
    private AvatarVisualState avatarVisualState
    private Boolean isMyAvatar

    SLObjectAvatarInfo(UUID uuid, UUID uuid2, Boolean z) {
        this.isMyAvatar = z
        this.avatarVisualState = AvatarVisualState(uuid, this, uuid2)
    }

    fun ApplyAvatarAnimation(AvatarAnimation avatarAnimation)  {
        this.avatarVisualState.ApplyAvatarAnimation(avatarAnimation)
    }

    fun ApplyAvatarAppearance(AvatarAppearance avatarAppearance)  {
        this.avatarVisualState.ApplyAvatarAppearance(avatarAppearance)
    }

    fun ApplyAvatarTextures(SLTextureEntry sLTextureEntry, Boolean z)  {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, z)
    }

    fun ApplyAvatarVisualParams(IntArray iArr)  {
        this.avatarVisualState.ApplyVisualParams(iArr)
    }

    /* access modifiers changed from: protected */
    @NonNull
    fun createDrawListEntry(): DrawListObjectEntry {
        return DrawListAvatarEntry(this)
    }

    @NonNull
    fun getAvatarVisualState(): AvatarVisualState {
        return this.avatarVisualState
    }

    fun getName(): String {
        return this.isMyAvatar ? "(my avatar)" : "(avatar)"
    }

    fun isAvatar(): Boolean {
        return true
    }

    fun isMyAvatar(): Boolean {
        return this.isMyAvatar
    }

    fun onTexturesUpdate(SLTextureEntry sLTextureEntry)  {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, false)
    }
}
