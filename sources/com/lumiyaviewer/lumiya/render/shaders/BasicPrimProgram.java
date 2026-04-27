// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class BasicPrimProgram extends ShaderProgram
{

    public int LightAmbientColor;
    public int LightDiffuseColor;
    public int LightDiffuseDir;
    public int sTexture;
    public int uMVPMatrix;
    public int uObjCoordScale;
    public int uObjWorldMatrix;
    public int useTexture;
    public int vColor;
    public int vNormal;
    public int vPosition;
    public int vTexCoord;

    public BasicPrimProgram(Shader shader, Shader shader1)
    {
        super(shader, shader1);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    public void SetupLighting(RenderContext rendercontext, WindlightPreset windlightpreset)
    {
        if (windlightpreset != null)
        {
            GLES20.glUniform3f(LightDiffuseDir, windlightpreset.lightnorm[0], windlightpreset.lightnorm[2], -windlightpreset.lightnorm[1]);
            if (Math.abs(windlightpreset.lightnorm[1]) > 0.1F)
            {
                int i = LightDiffuseColor;
                float af[];
                if (rendercontext.underWater)
                {
                    af = windlightpreset.sunlightBelowWater;
                } else
                {
                    af = windlightpreset.sunlight_color;
                }
                GLES20.glUniform3fv(i, 1, af, 0);
            } else
            {
                GLES20.glUniform3f(LightDiffuseColor, 0.0F, 0.0F, 0.0F);
            }
            i = LightAmbientColor;
            if (rendercontext.underWater)
            {
                rendercontext = windlightpreset.ambientBelowWater;
            } else
            {
                rendercontext = windlightpreset.ambient;
            }
            GLES20.glUniform3fv(i, 1, rendercontext, 0);
            return;
        } else
        {
            GLES20.glUniform3f(LightDiffuseDir, 0.0F, 1.0F, 0.0F);
            GLES20.glUniform3f(LightDiffuseColor, 0.0F, 0.0F, 0.0F);
            GLES20.glUniform3f(LightAmbientColor, 1.0F, 1.0F, 1.0F);
            return;
        }
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        vTexCoord = GLES20.glGetAttribLocation(handle, "vTexCoord");
        vNormal = GLES20.glGetAttribLocation(handle, "vNormal");
        vColor = GLES20.glGetUniformLocation(handle, "vColor");
        sTexture = GLES20.glGetUniformLocation(handle, "sTexture");
        useTexture = GLES20.glGetUniformLocation(handle, "useTexture");
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix");
        uObjWorldMatrix = GLES20.glGetUniformLocation(handle, "uObjWorldMatrix");
        uObjCoordScale = GLES20.glGetUniformLocation(handle, "uObjCoordScale");
        LightDiffuseDir = GLES20.glGetUniformLocation(handle, "LightDiffuseDir");
        LightDiffuseColor = GLES20.glGetUniformLocation(handle, "LightDiffuseColor");
        LightAmbientColor = GLES20.glGetUniformLocation(handle, "LightAmbientColor");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }

    public void setTextureEnabled(boolean flag)
    {
        int j = useTexture;
        int i;
        if (flag)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        GLES20.glUniform1i(j, i);
    }
}
