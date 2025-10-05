package com.linkpoint.slproto.objects;

import com.linkpoint.render.avatar.AvatarVisualState;
import com.linkpoint.render.spatial.DrawListAvatarEntry;
import com.linkpoint.render.spatial.DrawListObjectEntry;
import com.linkpoint.slproto.messages.AvatarAnimation;
import com.linkpoint.slproto.messages.AvatarAppearance;
import com.linkpoint.slproto.textures.SLTextureEntry;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SLObjectAvatarInfo extends SLObjectInfo {
    @Nonnull
    private final AvatarVisualState avatarVisualState;
    private final boolean isMyAvatar;

    public SLObjectAvatarInfo(UUID uuid, UUID uuid2, boolean z) {
        this.isMyAvatar = z;
        this.avatarVisualState = new AvatarVisualState(uuid, this, uuid2);
    }

    public void ApplyAvatarAnimation(AvatarAnimation avatarAnimation) {
        this.avatarVisualState.ApplyAvatarAnimation(avatarAnimation);
    }

    public void ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        this.avatarVisualState.ApplyAvatarAppearance(avatarAppearance);
    }

    public void ApplyAvatarTextures(SLTextureEntry sLTextureEntry, boolean z) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, z);
    }

    public void ApplyAvatarVisualParams(int[] iArr) {
        this.avatarVisualState.ApplyVisualParams(iArr);
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public DrawListObjectEntry createDrawListEntry() {
        return new DrawListAvatarEntry(this);
    }

    @Nonnull
    public AvatarVisualState getAvatarVisualState() {
        return this.avatarVisualState;
    }

    public String getName() {
        return this.isMyAvatar ? "(my avatar)" : "(avatar)";
    }

    public boolean isAvatar() {
        return true;
    }

    public boolean isMyAvatar() {
        return this.isMyAvatar;
    }

    public void onTexturesUpdate(SLTextureEntry sLTextureEntry) {
        this.avatarVisualState.ApplyTextures(sLTextureEntry, false);
    }
}
