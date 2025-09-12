// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class AvatarTextures
{

    public static final UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    private final Map avatarTextures = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex);

    public AvatarTextures()
    {
    }

    public boolean ApplyAvatarAppearance(AvatarAppearance avatarappearance)
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = ApplyTextures(SLTextureEntry.create(ByteBuffer.wrap(avatarappearance.ObjectData_Field.TextureEntry), avatarappearance.ObjectData_Field.TextureEntry.length), false);
        this;
        JVM INSTR monitorexit ;
        return flag;
        avatarappearance;
        throw avatarappearance;
    }

    public boolean ApplyTextures(SLTextureEntry sltextureentry, boolean flag)
    {
        this;
        JVM INSTR monitorenter ;
        int i = sltextureentry.getFaceMask();
        if (i != 0)
        {
            break MISSING_BLOCK_LABEL_17;
        }
        this;
        JVM INSTR monitorexit ;
        return false;
        SLTextureEntryFace sltextureentryface;
        AvatarTextureFaceIndex aavatartexturefaceindex[];
        int j;
        sltextureentryface = sltextureentry.GetDefaultTexture();
        aavatartexturefaceindex = AvatarTextureFaceIndex.values();
        j = aavatartexturefaceindex.length;
        boolean flag1;
        i = 0;
        flag1 = false;
_L2:
        AvatarTextureFaceIndex avatartexturefaceindex;
        if (i >= j)
        {
            break MISSING_BLOCK_LABEL_148;
        }
        avatartexturefaceindex = aavatartexturefaceindex[i];
        Object obj = sltextureentry.GetFace(avatartexturefaceindex.ordinal());
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_157;
        }
        obj = ((SLTextureEntryFace) (obj)).getTextureID(sltextureentryface);
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_157;
        }
        UUID uuid = (UUID)avatarTextures.get(avatartexturefaceindex);
        if (flag && uuid != null)
        {
            break MISSING_BLOCK_LABEL_157;
        }
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_127;
        }
        if (((UUID) (obj)).equals(uuid))
        {
            break MISSING_BLOCK_LABEL_157;
        }
        avatarTextures.put(avatartexturefaceindex, obj);
        flag1 = true;
        break MISSING_BLOCK_LABEL_157;
        return flag1;
        sltextureentry;
        throw sltextureentry;
        i++;
        if (true) goto _L2; else goto _L1
_L1:
    }

    public UUID getTexture(AvatarTextureFaceIndex avatartexturefaceindex)
    {
        this;
        JVM INSTR monitorenter ;
        avatartexturefaceindex = (UUID)avatarTextures.get(avatartexturefaceindex);
        this;
        JVM INSTR monitorexit ;
        return avatartexturefaceindex;
        avatartexturefaceindex;
        throw avatartexturefaceindex;
    }

}
