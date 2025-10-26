package com.lumiyaviewer.lumiya.slproto.objects

import com.lumiyaviewer.lumiya.render.avatar.AvatarVisualState
import com.lumiyaviewer.lumiya.render.spatial.DrawListAvatarEntry
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry
import java.util.UUID
import javax.annotation.Nonnull

class SLObjectAvatarInfo : SLObjectInfo {
    @Nonnull
    private AvatarVisualState avatarVisualState
    private Boolean isMyAvatar

    SLObjectAvatarInfo(UUID uuid, UUID uuid2, Boolean z) {
        this.isMyAvatar = z
        this.avatarVisualState = AvatarVisualState(uuid, this, uuid2)
    }

    Unit ApplyAvatarAnimation(AvatarAnimation avatarAnimation) {
        this.avatarVisualState.ApplyAvatarAnimation(avatarAnimation)
    }

    Unit ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        this.avatarVisualState.ApplyAvatarAppearance(avatarAppearance)
    }

    Unit ApplyAvatarTextures(SLTextureEntry sLTextureEntry, Boolean z) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, z)
    }

    Unit ApplyAvatarVisualParams(IntArray iArr) {
        this.avatarVisualState.ApplyVisualParams(iArr)
    }

    /* access modifiers changed from: protected */
    @Nonnull
    DrawListObjectEntry createDrawListEntry() {
        return DrawListAvatarEntry(this)
    }

    @Nonnull
    AvatarVisualState getAvatarVisualState() {
        return this.avatarVisualState
    }

    String getName() {
        return this.isMyAvatar ? "(my avatar)" : "(avatar)"
    }

    Boolean isAvatar() {
        return true
    }

    Boolean isMyAvatar() {
        return this.isMyAvatar
    }

    Unit onTexturesUpdate(SLTextureEntry sLTextureEntry) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, false)
    }
}
