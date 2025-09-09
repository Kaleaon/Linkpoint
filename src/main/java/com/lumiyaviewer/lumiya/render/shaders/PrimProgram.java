package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class PrimProgram extends BasicPrimProgram {
    public int uTexMatrix;

    public PrimProgram(Shader shader, Shader shader2) {
        super(shader, shader2);
    }

    public PrimProgram(boolean z) {
        super(Shader.PrimVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader);
    }

    protected void bindVariables() {
        super.bindVariables();
        this.uTexMatrix = GLES20.glGetUniformLocation(this.handle, "uTexMatrix");
    }
}
