// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.lumiyaviewer.lumiya.render.spatial.DrawListAvatarEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.render.avatar.AvatarVisualState;

public class SLObjectAvatarInfo extends SLObjectInfo
{
    @Nonnull
    private final AvatarVisualState avatarVisualState;
    private final boolean isMyAvatar;
    
    public SLObjectAvatarInfo(final UUID uuid, final UUID uuid2, final boolean isMyAvatar) {
        this.isMyAvatar = isMyAvatar;
        this.avatarVisualState = new AvatarVisualState(uuid, this, uuid2);
    }
    
    public void ApplyAvatarAnimation(final AvatarAnimation avatarAnimation) {
        this.avatarVisualState.ApplyAvatarAnimation(avatarAnimation);
    }
    
    public void ApplyAvatarAppearance(final AvatarAppearance avatarAppearance) {
        this.avatarVisualState.ApplyAvatarAppearance(avatarAppearance);
    }
    
    public void ApplyAvatarTextures(final SLTextureEntry slTextureEntry, final boolean b) {
        this.avatarVisualState.ApplyTextures(slTextureEntry, b);
    }
    
    public void ApplyAvatarVisualParams(final int[] array) {
        this.avatarVisualState.ApplyVisualParams(array);
    }
    
    @Nonnull
    @Override
    protected DrawListObjectEntry createDrawListEntry() {
        return new DrawListAvatarEntry(this);
    }
    
    @Nonnull
    public AvatarVisualState getAvatarVisualState() {
        return this.avatarVisualState;
    }
    
    @Override
    public String getName() {
        if (this.isMyAvatar) {
            return "(my avatar)";
        }
        return "(avatar)";
    }
    
    @Override
    public boolean isAvatar() {
        return true;
    }
    
    public boolean isMyAvatar() {
        return this.isMyAvatar;
    }
    
    public void onTexturesUpdate(final SLTextureEntry slTextureEntry) {
        this.avatarVisualState.ApplyTextures(slTextureEntry, false);
    }
}
