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

    fun ApplyAvatarAnimation(AvatarAnimation avatarAnimation) {
        this.avatarVisualState.ApplyAvatarAnimation(avatarAnimation)
    }

    fun ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        this.avatarVisualState.ApplyAvatarAppearance(avatarAppearance)
    }

    fun ApplyAvatarTextures(SLTextureEntry sLTextureEntry, Boolean z) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, z)
    }

    fun ApplyAvatarVisualParams(IntArray iArr) {
        this.avatarVisualState.ApplyVisualParams(iArr)
    }

    /* access modifiers changed from: protected */
    public DrawListObjectEntry createDrawListEntry() {
        return DrawListAvatarEntry(this)
    }

    public AvatarVisualState getAvatarVisualState() {
        return this.avatarVisualState
    }

    public String getName() {
        return this.isMyAvatar ? "(my avatar)" : "(avatar)"
    }

    public Boolean isAvatar() {
        return true
    }

    public Boolean isMyAvatar() {
        return this.isMyAvatar
    }

    fun onTexturesUpdate(SLTextureEntry sLTextureEntry) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, false)
    }
}
