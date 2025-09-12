// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.texfetcher;

import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.render.tex.TexturePriority;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.io.File;
import java.util.UUID;

public class SLTextureFetchRequest
    implements HasPriority
{
    public static interface TextureFetchCompleteListener
    {

        public abstract void OnTextureFetchComplete(SLTextureFetchRequest sltexturefetchrequest);
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues[];
    public AvatarTextureFaceIndex avatarFaceIndex;
    public UUID avatarUUID;
    public final File destFile;
    TextureFetchCompleteListener onFetchComplete;
    public File outputFile;
    public TextureClass textureClass;
    public UUID textureID;
    public int textureLayer;
    private int visibleRangeCategory;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues;
        }
        int ai[] = new int[TextureClass.values().length];
        try
        {
            ai[TextureClass.Asset.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[TextureClass.Baked.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[TextureClass.Prim.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[TextureClass.Sculpt.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[TextureClass.Terrain.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues = ai;
        return ai;
    }

    public SLTextureFetchRequest(UUID uuid, int i, TextureClass textureclass, AvatarTextureFaceIndex avatartexturefaceindex, UUID uuid1, File file)
    {
        onFetchComplete = null;
        textureID = uuid;
        textureLayer = i;
        textureClass = textureclass;
        avatarFaceIndex = avatartexturefaceindex;
        avatarUUID = uuid1;
        outputFile = null;
        visibleRangeCategory = -1;
        destFile = file;
    }

    public static int getPriorityForClass(TextureClass textureclass, int i)
    {
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_render_2D_tex_2D_TextureClassSwitchesValues()[textureclass.ordinal()])
        {
        default:
            return TexturePriority.Lowest.ordinal();

        case 1: // '\001'
            return TexturePriority.Asset.ordinal();

        case 2: // '\002'
            return TexturePriority.PrimVisibleClose.ordinal();

        case 3: // '\003'
            switch (i)
            {
            default:
                return TexturePriority.PrimVisibleFar.ordinal();

            case -1: 
                return TexturePriority.PrimInvisible.ordinal();

            case 0: // '\0'
                return TexturePriority.PrimVisibleClose.ordinal();

            case 1: // '\001'
                return TexturePriority.PrimVisibleMedium.ordinal();
            }

        case 4: // '\004'
            return TexturePriority.Sculpt.ordinal();

        case 5: // '\005'
            return TexturePriority.Terrain.ordinal();
        }
    }

    public int getPriority()
    {
        return getPriorityForClass(textureClass, visibleRangeCategory);
    }

    public void setOnFetchComplete(TextureFetchCompleteListener texturefetchcompletelistener)
    {
        onFetchComplete = texturefetchcompletelistener;
    }
}
