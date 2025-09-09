package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class FlexiPrimProgram extends PrimProgram {
    public int uNumSectionMatrices;
    public int uSectionMatrices;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FlexiPrimProgram(boolean z) {
        super(Shader.FlexiVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader);
    }

    /* access modifiers changed from: protected */
    public void bindVariables() {
        super.bindVariables();
        this.uSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uSectionMatrices");
        this.uNumSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uNumSectionMatrices");
    }
}
