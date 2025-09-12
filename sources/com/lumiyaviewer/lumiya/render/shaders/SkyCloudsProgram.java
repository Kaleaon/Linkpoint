// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            SkyProgram, Shader

public class SkyCloudsProgram extends SkyProgram
{

    public int cloudAdd;
    public int cloudColor;
    public int cloudGamma;
    public int textureSampler;

    public SkyCloudsProgram()
    {
        super(Shader.SkyFragmentShader);
    }

    public void ApplyWindlight(RenderContext rendercontext)
    {
        super.ApplyWindlight(rendercontext);
        rendercontext = rendercontext.windlightPreset;
        GLES20.glUniform3f(cloudColor, ((WindlightPreset) (rendercontext)).cloud_color[0], ((WindlightPreset) (rendercontext)).cloud_color[1], ((WindlightPreset) (rendercontext)).cloud_color[2]);
        GLES20.glUniform1f(cloudGamma, ((WindlightPreset) (rendercontext)).cloud_pos_density1[2]);
        GLES20.glUniform1f(cloudAdd, ((WindlightPreset) (rendercontext)).cloud_shadow[0] - 0.5F);
        GLES20.glUniform1i(textureSampler, 0);
    }

    protected void bindVariables()
    {
        super.bindVariables();
        textureSampler = GLES20.glGetUniformLocation(handle, "textureSampler");
        cloudColor = GLES20.glGetUniformLocation(handle, "cloudColor");
        cloudGamma = GLES20.glGetUniformLocation(handle, "cloudGamma");
        cloudAdd = GLES20.glGetUniformLocation(handle, "cloudAdd");
    }

    public boolean hasCloudsTexture()
    {
        return true;
    }
}
