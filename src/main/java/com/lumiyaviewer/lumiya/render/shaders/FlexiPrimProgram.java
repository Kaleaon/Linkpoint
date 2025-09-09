package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class FlexiPrimProgram extends PrimProgram {
    public int uNumSectionMatrices;
    public int uSectionMatrices;

    public FlexiPrimProgram(boolean z) {
        super(Shader.FlexiVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader);
    }

    protected void bindVariables() {
        super.bindVariables();
        this.uSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uSectionMatrices");
        this.uNumSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uNumSectionMatrices");
    }
}
