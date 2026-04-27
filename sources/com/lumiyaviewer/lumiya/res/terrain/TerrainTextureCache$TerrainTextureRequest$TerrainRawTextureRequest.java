// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.res.terrain:
//            TerrainTextureCache

private class layer
    implements ResourceConsumer
{

    private final int layer;
    final layer this$1;

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof OpenJPEG)
        {
            layer.this.layer(layer, (OpenJPEG)obj);
        } else
        if (obj == null)
        {
            layer.this.layer(layer, null);
            return;
        }
    }

    public (UUID uuid, int i)
    {
        this$1 = this._cls1.this;
        super();
        layer = i;
        TextureCache.getInstance().RequestResource(DrawableTextureParams.create(uuid, TextureClass.Terrain), this);
    }
}
