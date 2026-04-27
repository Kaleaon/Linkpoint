// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class StarsProgram extends ShaderProgram
{

    public int uMVPMatrix;
    public int uStarColor;
    public int vPosition;

    public StarsProgram()
    {
        super(Shader.StarsVertexShader, Shader.StarsFragmentShader);
    }

    public void ApplyWindlight(RenderContext rendercontext)
    {
        GLES20.glUniform4f(uStarColor, 1.0F, 1.0F, 1.0F, rendercontext.windlightPreset.star_brightness);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix");
        uStarColor = GLES20.glGetUniformLocation(handle, "uStarColor");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }
}
