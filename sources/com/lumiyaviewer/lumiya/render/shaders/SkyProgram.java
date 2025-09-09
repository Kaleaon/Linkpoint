package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;

public class SkyProgram extends ShaderProgram {
    public int hazeColor;
    public int hazeHorizon;
    public int skyColor;
    public int uMVPMatrix;
    public int vPosition;

    public SkyProgram() {
        super(Shader.SkyVertexShader, Shader.SkyNoCloudsFragmentShader);
    }

    public SkyProgram(Shader shader) {
        super(Shader.SkyVertexShader, shader);
    }

    public void ApplyWindlight(RenderContext renderContext) {
        WindlightPreset windlightPreset = renderContext.windlightPreset;
        GLES20.glUniform3f(this.skyColor, (windlightPreset.blue_horizon[0] + windlightPreset.sunlight_color[0] + windlightPreset.ambient[0]) * windlightPreset.blue_density[0], (windlightPreset.blue_horizon[1] + windlightPreset.sunlight_color[1] + windlightPreset.ambient[1]) * windlightPreset.blue_density[1], (windlightPreset.blue_horizon[2] + windlightPreset.sunlight_color[2] + windlightPreset.ambient[2]) * windlightPreset.blue_density[2]);
        GLES20.glUniform1f(this.hazeHorizon, windlightPreset.haze_horizon[0]);
        GLES20.glUniform3f(this.hazeColor, windlightPreset.haze_density[0] * windlightPreset.ambient[0], windlightPreset.haze_density[0] * windlightPreset.ambient[1], windlightPreset.ambient[2] * windlightPreset.haze_density[0]);
    }

    public /* bridge */ /* synthetic */ int Compile(ShaderPreprocessor shaderPreprocessor) {
        return super.Compile(shaderPreprocessor);
    }

    /* access modifiers changed from: protected */
    public void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.skyColor = GLES20.glGetUniformLocation(this.handle, "skyColor");
        this.hazeHorizon = GLES20.glGetUniformLocation(this.handle, "hazeHorizon");
        this.hazeColor = GLES20.glGetUniformLocation(this.handle, "hazeColor");
    }

    public /* bridge */ /* synthetic */ int getHandle() {
        return super.getHandle();
    }

    public boolean hasCloudsTexture() {
        return false;
    }
}
