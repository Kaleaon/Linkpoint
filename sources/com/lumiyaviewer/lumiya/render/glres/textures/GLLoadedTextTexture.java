// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.render.RenderContext;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.textures:
//            GLLoadedTexture

public class GLLoadedTextTexture extends GLLoadedTexture
{

    public final float baselineOffset;

    public GLLoadedTextTexture(RenderContext rendercontext, Bitmap bitmap, float f)
    {
        super(rendercontext, bitmap);
        baselineOffset = f;
    }
}
