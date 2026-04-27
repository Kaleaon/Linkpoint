// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.Quad;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTextTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextTextureCache;
import com.lumiyaviewer.lumiya.render.shaders.QuadProgram;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams;

public class DrawableHoverText
    implements ResourceConsumer, GLCleanable
{

    private final int backgroundColor;
    private final String hoverText;
    private volatile GLLoadedTextTexture hoverTextTexture;
    private final GLTextTextureCache textTextureCache;
    private boolean textureRequested;

    public DrawableHoverText(GLTextTextureCache gltexttexturecache, String s, int i)
    {
        textureRequested = false;
        textTextureCache = gltexttexturecache;
        hoverText = s;
        backgroundColor = i;
    }

    public final void DrawAtWorld(RenderContext rendercontext, float f, float f1, float f2, float f3, MatrixStack matrixstack, boolean flag, 
            int i)
    {
        float af[] = new float[8];
        float af1[] = rendercontext.modelViewMatrix.getMatrixData();
        int j = rendercontext.modelViewMatrix.getMatrixDataOffset();
        float af2[] = matrixstack.getMatrixData();
        int k = matrixstack.getMatrixDataOffset();
        af[0] = f;
        af[1] = f1;
        af[2] = f2;
        af[3] = 1.0F;
        Matrix.multiplyMV(af, 4, af1, j, af, 0);
        af[5] = af[5] + f3;
        if (rendercontext.hasGL20)
        {
            System.arraycopy(af, 4, af, 0, 4);
        } else
        {
            Matrix.multiplyMV(af, 0, af2, k, af, 4);
        }
        if (af[3] != 0.0F)
        {
            f = af[0] / af[3];
            f1 = af[1] / af[3];
            if (af[3] != 0.0F)
            {
                GLDraw(rendercontext, f, f1, af[2] / af[3], flag, i);
            }
        }
    }

    public void GLCleanup()
    {
        if (textTextureCache != null)
        {
            textTextureCache.CancelRequest(this);
        }
        textureRequested = false;
        hoverTextTexture = null;
    }

    public final void GLDraw(RenderContext rendercontext, float f, float f1, float f2, boolean flag, int i)
    {
        if (!textureRequested)
        {
            textureRequested = true;
            textTextureCache.RequestResource(DrawableTextParams.create(hoverText, backgroundColor), this);
        }
        GLLoadedTextTexture glloadedtexttexture = hoverTextTexture;
        if (glloadedtexttexture != null)
        {
            float f3 = ((float)glloadedtexttexture.getWidth() * 2.0F) / (float)rendercontext.viewportRect[2];
            float f4 = ((float)glloadedtexttexture.getHeight() * 2.0F) / (float)rendercontext.viewportRect[3];
            if (rendercontext.hasGL20)
            {
                GLES20.glUniform3f(rendercontext.quadProgram.uPreTranslate, f, f1, f2);
                GLES20.glUniform3f(rendercontext.quadProgram.uScale, f3, f4, 1.0F);
                GLES20.glUniform3f(rendercontext.quadProgram.uPostTranslate, 0.0F, glloadedtexttexture.baselineOffset, 0.0F);
                glloadedtexttexture.GLDraw();
                if (flag)
                {
                    GLES20.glUniform4f(rendercontext.quadProgram.uColor, (float)(i >> 0 & 0xff) / 255F, (float)(i >> 8 & 0xff) / 255F, (float)(i >> 16 & 0xff) / 255F, (float)(255 - (i >> 24 & 0xff)) / 255F);
                    GLES20.glUniform1i(rendercontext.quadProgram.uColorize, 1);
                } else
                {
                    GLES20.glUniform4f(rendercontext.quadProgram.uColor, 1.0F, 1.0F, 1.0F, 1.0F);
                    GLES20.glUniform1i(rendercontext.quadProgram.uColorize, 0);
                }
            } else
            {
                GLES10.glLoadIdentity();
                GLES10.glTranslatef(f, f1, f2);
                GLES10.glScalef(f3, f4, 1.0F);
                GLES10.glTranslatef(0.0F, glloadedtexttexture.baselineOffset, 0.0F);
                if (flag)
                {
                    GLES10.glColor4f((float)(i >> 0 & 0xff) / 255F, (float)(i >> 8 & 0xff) / 255F, (float)(i >> 16 & 0xff) / 255F, 1.0F - (float)(i >> 24 & 0xff) / 255F);
                } else
                {
                    GLES10.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }
                glloadedtexttexture.GLDraw();
            }
            rendercontext.quad.DrawQuad(rendercontext);
        }
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof GLLoadedTextTexture)
        {
            hoverTextTexture = (GLLoadedTextTexture)obj;
        } else
        if (obj == null)
        {
            hoverTextTexture = null;
            return;
        }
    }
}
