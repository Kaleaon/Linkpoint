// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.baker:
//            BakeProcess

private class textureReady
    implements ResourceConsumer
{

    private final com.lumiyaviewer.lumiya.slproto.assets.extureID texture;
    private volatile OpenJPEG textureData;
    private volatile boolean textureReady;
    final BakeProcess this$0;

    static OpenJPEG _2D_get0(textureReady textureready)
    {
        return textureready.textureData;
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof OpenJPEG)
        {
            textureData = (OpenJPEG)obj;
        }
        textureReady = true;
        BakeProcess._2D_wrap0(BakeProcess.this);
    }

    protected com.lumiyaviewer.lumiya.slproto.assets.his._cls0 getTexture()
    {
        return texture;
    }

    OpenJPEG getTextureData()
    {
        return textureData;
    }

    boolean getTextureReady()
    {
        return textureReady;
    }

    void requestData()
    {
        TextureCache.getInstance().RequestResource(DrawableTextureParams.create(texture.extureID, TextureClass.Asset), this);
    }

    (com.lumiyaviewer.lumiya.slproto.assets. )
    {
        this$0 = BakeProcess.this;
        super();
        texture = ;
        textureReady = false;
    }
}
