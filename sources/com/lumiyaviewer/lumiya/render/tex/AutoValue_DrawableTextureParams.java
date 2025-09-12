// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.tex;

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.render.tex:
//            DrawableTextureParams, TextureClass

final class AutoValue_DrawableTextureParams extends DrawableTextureParams
{

    private final AvatarTextureFaceIndex avatarFaceIndex;
    private final UUID avatarUUID;
    private final TextureClass textureClass;
    private final UUID uuid;

    AutoValue_DrawableTextureParams(UUID uuid1, TextureClass textureclass, AvatarTextureFaceIndex avatartexturefaceindex, UUID uuid2)
    {
        if (uuid1 == null)
        {
            throw new NullPointerException("Null uuid");
        }
        uuid = uuid1;
        if (textureclass == null)
        {
            throw new NullPointerException("Null textureClass");
        } else
        {
            textureClass = textureclass;
            avatarFaceIndex = avatartexturefaceindex;
            avatarUUID = uuid2;
            return;
        }
    }

    public AvatarTextureFaceIndex avatarFaceIndex()
    {
        return avatarFaceIndex;
    }

    public UUID avatarUUID()
    {
        return avatarUUID;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof DrawableTextureParams)
        {
            obj = (DrawableTextureParams)obj;
            if (uuid.equals(((DrawableTextureParams) (obj)).uuid()) && textureClass.equals(((DrawableTextureParams) (obj)).textureClass()) && (avatarFaceIndex != null ? avatarFaceIndex.equals(((DrawableTextureParams) (obj)).avatarFaceIndex()) : ((DrawableTextureParams) (obj)).avatarFaceIndex() == null))
            {
                if (avatarUUID == null)
                {
                    return ((DrawableTextureParams) (obj)).avatarUUID() == null;
                } else
                {
                    return avatarUUID.equals(((DrawableTextureParams) (obj)).avatarUUID());
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int j = 0;
        int k = uuid.hashCode();
        int l = textureClass.hashCode();
        int i;
        if (avatarFaceIndex == null)
        {
            i = 0;
        } else
        {
            i = avatarFaceIndex.hashCode();
        }
        if (avatarUUID != null)
        {
            j = avatarUUID.hashCode();
        }
        return (i ^ ((k ^ 0xf4243) * 0xf4243 ^ l) * 0xf4243) * 0xf4243 ^ j;
    }

    public TextureClass textureClass()
    {
        return textureClass;
    }

    public String toString()
    {
        return (new StringBuilder()).append("DrawableTextureParams{uuid=").append(uuid).append(", ").append("textureClass=").append(textureClass).append(", ").append("avatarFaceIndex=").append(avatarFaceIndex).append(", ").append("avatarUUID=").append(avatarUUID).append("}").toString();
    }

    public UUID uuid()
    {
        return uuid;
    }
}
