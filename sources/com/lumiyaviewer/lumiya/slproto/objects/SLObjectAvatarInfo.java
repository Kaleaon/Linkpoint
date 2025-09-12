// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.lumiyaviewer.lumiya.render.avatar.AvatarVisualState;
import com.lumiyaviewer.lumiya.render.spatial.DrawListAvatarEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            SLObjectInfo

public class SLObjectAvatarInfo extends SLObjectInfo
{

    private final AvatarVisualState avatarVisualState;
    private final boolean isMyAvatar;

    public SLObjectAvatarInfo(UUID uuid, UUID uuid1, boolean flag)
    {
        isMyAvatar = flag;
        avatarVisualState = new AvatarVisualState(uuid, this, uuid1);
    }

    public void ApplyAvatarAnimation(AvatarAnimation avataranimation)
    {
        avatarVisualState.ApplyAvatarAnimation(avataranimation);
    }

    public void ApplyAvatarAppearance(AvatarAppearance avatarappearance)
    {
        avatarVisualState.ApplyAvatarAppearance(avatarappearance);
    }

    public void ApplyAvatarTextures(SLTextureEntry sltextureentry, boolean flag)
    {
        avatarVisualState.ApplyTextures(sltextureentry, flag);
    }

    public void ApplyAvatarVisualParams(int ai[])
    {
        avatarVisualState.ApplyVisualParams(ai);
    }

    protected DrawListObjectEntry createDrawListEntry()
    {
        return new DrawListAvatarEntry(this);
    }

    public AvatarVisualState getAvatarVisualState()
    {
        return avatarVisualState;
    }

    public String getName()
    {
        if (isMyAvatar)
        {
            return "(my avatar)";
        } else
        {
            return "(avatar)";
        }
    }

    public boolean isAvatar()
    {
        return true;
    }

    public boolean isMyAvatar()
    {
        return isMyAvatar;
    }

    public void onTexturesUpdate(SLTextureEntry sltextureentry)
    {
        avatarVisualState.ApplyTextures(sltextureentry, false);
    }
}
