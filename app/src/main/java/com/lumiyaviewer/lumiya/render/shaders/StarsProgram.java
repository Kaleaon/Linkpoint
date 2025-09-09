package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;

public class StarsProgram extends ShaderProgram {
    public int uMVPMatrix;
    public int uStarColor;
    public int vPosition;

    public StarsProgram() {
        super(Shader.StarsVertexShader, Shader.StarsFragmentShader);
    }

    public void ApplyWindlight(RenderContext renderContext) {
        GLES20.glUniform4f(this.uStarColor, 1.0f, 1.0f, 1.0f, renderContext.windlightPreset.star_brightness);
    }

    protected void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.uStarColor = GLES20.glGetUniformLocation(this.handle, "uStarColor");
    }
}
