// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.tex;

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import java.io.File;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.render.tex:
//            AutoValue_DrawableTextureParams, TextureClass

public abstract class DrawableTextureParams
{

    public DrawableTextureParams()
    {
    }

    public static DrawableTextureParams create(UUID uuid1, TextureClass textureclass)
    {
        return new AutoValue_DrawableTextureParams(uuid1, textureclass, null, null);
    }

    public static DrawableTextureParams create(UUID uuid1, AvatarTextureFaceIndex avatartexturefaceindex, UUID uuid2)
    {
        return new AutoValue_DrawableTextureParams(uuid1, TextureClass.Baked, avatartexturefaceindex, uuid2);
    }

    public abstract AvatarTextureFaceIndex avatarFaceIndex();

    public abstract UUID avatarUUID();

    public final File getTextureRawPath(File file, boolean flag)
    {
        int i = uuid().hashCode();
        String s1 = textureClass().getStorePath();
        String s = s1;
        if (textureClass() == TextureClass.Prim)
        {
            s = s1;
            if (flag)
            {
                s = (new StringBuilder()).append(s1).append("-hq").toString();
            }
        }
        return new File(file, String.format("%s-raw/%02x/%s.raw", new Object[] {
            s, Integer.valueOf((i >> 24 ^ (i >> 8 ^ i ^ i >> 16)) & 0xff), uuid().toString()
        }));
    }

    public abstract TextureClass textureClass();

    public abstract UUID uuid();
}
