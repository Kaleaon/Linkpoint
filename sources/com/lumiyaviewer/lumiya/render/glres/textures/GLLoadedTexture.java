// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.textures:
//            GLResourceTexture

public class GLLoadedTexture extends GLResourceTexture
{

    private final boolean hasAlphaLayer;
    private final int height;
    private final int width;

    GLLoadedTexture(RenderContext rendercontext, Bitmap bitmap)
    {
        super(rendercontext.glResourceManager, bitmap.getHeight() * bitmap.getRowBytes());
        if (rendercontext.hasGL20)
        {
            GLES20.glBindTexture(3553, handle);
        } else
        {
            GLES10.glBindTexture(3553, handle);
        }
        hasAlphaLayer = bitmap.hasAlpha();
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        rendercontext.KeepTexture(bitmap);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        if (rendercontext.hasGL20)
        {
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10242, 10497);
            GLES20.glTexParameteri(3553, 10243, 10497);
        } else
        {
            GLES10.glTexParameterf(3553, 10240, 9728F);
            GLES10.glTexParameterf(3553, 10241, 9728F);
            GLES10.glTexParameterf(3553, 10242, 10497F);
            GLES10.glTexParameterf(3553, 10243, 10497F);
        }
        if (rendercontext.hasGL20)
        {
            GLES20.glBindTexture(3553, 0);
            return;
        } else
        {
            GLES10.glBindTexture(3553, 0);
            return;
        }
    }

    GLLoadedTexture(RenderContext rendercontext, OpenJPEG openjpeg)
    {
        super(rendercontext.glResourceManager, openjpeg.getLoadedSize());
        if (rendercontext.hasGL20)
        {
            GLES20.glBindTexture(3553, handle);
        } else
        {
            GLES10.glBindTexture(3553, handle);
        }
        hasAlphaLayer = openjpeg.hasAlphaLayer();
        width = openjpeg.getWidth();
        height = openjpeg.getHeight();
        rendercontext.KeepTexture(openjpeg);
        if (rendercontext.hasGL30)
        {
            openjpeg.SetAsImmutableTexture();
        } else
        {
            openjpeg.SetAsTexture();
        }
        if (rendercontext.hasGL20)
        {
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10242, 10497);
            GLES20.glTexParameteri(3553, 10243, 10497);
        } else
        {
            GLES10.glTexParameterf(3553, 10240, 9728F);
            GLES10.glTexParameterf(3553, 10241, 9728F);
            GLES10.glTexParameterf(3553, 10242, 10497F);
            GLES10.glTexParameterf(3553, 10243, 10497F);
        }
        if (rendercontext.hasGL20)
        {
            GLES20.glBindTexture(3553, 0);
            return;
        } else
        {
            GLES10.glBindTexture(3553, 0);
            return;
        }
    }

    public static GLLoadedTexture loadFromAssets(RenderContext rendercontext, Context context, String s)
    {
        try
        {
            context = context.getAssets().open(s);
            s = BitmapFactory.decodeStream(context);
            context.close();
        }
        // Misplaced declaration of an exception variable
        catch (RenderContext rendercontext)
        {
            Debug.Warning(rendercontext);
            return null;
        }
        if (s == null)
        {
            break MISSING_BLOCK_LABEL_34;
        }
        rendercontext = new GLLoadedTexture(rendercontext, s);
        return rendercontext;
        return null;
    }

    public final void GLDraw()
    {
        GLES10.glBindTexture(3553, handle);
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public boolean hasAlphaLayer()
    {
        return hasAlphaLayer;
    }
}
