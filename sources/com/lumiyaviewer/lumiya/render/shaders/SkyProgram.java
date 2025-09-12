// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderProgram, Shader, ShaderPreprocessor

public class SkyProgram extends ShaderProgram
{

    public int hazeColor;
    public int hazeHorizon;
    public int skyColor;
    public int uMVPMatrix;
    public int vPosition;

    public SkyProgram()
    {
        super(Shader.SkyVertexShader, Shader.SkyNoCloudsFragmentShader);
    }

    public SkyProgram(Shader shader)
    {
        super(Shader.SkyVertexShader, shader);
    }

    public void ApplyWindlight(RenderContext rendercontext)
    {
        rendercontext = rendercontext.windlightPreset;
        GLES20.glUniform3f(skyColor, (((WindlightPreset) (rendercontext)).blue_horizon[0] + ((WindlightPreset) (rendercontext)).sunlight_color[0] + ((WindlightPreset) (rendercontext)).ambient[0]) * ((WindlightPreset) (rendercontext)).blue_density[0], (((WindlightPreset) (rendercontext)).blue_horizon[1] + ((WindlightPreset) (rendercontext)).sunlight_color[1] + ((WindlightPreset) (rendercontext)).ambient[1]) * ((WindlightPreset) (rendercontext)).blue_density[1], (((WindlightPreset) (rendercontext)).blue_horizon[2] + ((WindlightPreset) (rendercontext)).sunlight_color[2] + ((WindlightPreset) (rendercontext)).ambient[2]) * ((WindlightPreset) (rendercontext)).blue_density[2]);
        GLES20.glUniform1f(hazeHorizon, ((WindlightPreset) (rendercontext)).haze_horizon[0]);
        int i = hazeColor;
        float f = ((WindlightPreset) (rendercontext)).haze_density[0];
        float f1 = ((WindlightPreset) (rendercontext)).ambient[0];
        float f2 = ((WindlightPreset) (rendercontext)).haze_density[0];
        float f3 = ((WindlightPreset) (rendercontext)).ambient[1];
        float f4 = ((WindlightPreset) (rendercontext)).haze_density[0];
        GLES20.glUniform3f(i, f * f1, f2 * f3, ((WindlightPreset) (rendercontext)).ambient[2] * f4);
    }

    public volatile int Compile(ShaderPreprocessor shaderpreprocessor)
    {
        return super.Compile(shaderpreprocessor);
    }

    protected void bindVariables()
    {
        vPosition = GLES20.glGetAttribLocation(handle, "vPosition");
        uMVPMatrix = GLES20.glGetUniformLocation(handle, "uMVPMatrix");
        skyColor = GLES20.glGetUniformLocation(handle, "skyColor");
        hazeHorizon = GLES20.glGetUniformLocation(handle, "hazeHorizon");
        hazeColor = GLES20.glGetUniformLocation(handle, "hazeColor");
    }

    public volatile int getHandle()
    {
        return super.getHandle();
    }

    public boolean hasCloudsTexture()
    {
        return false;
    }
}
