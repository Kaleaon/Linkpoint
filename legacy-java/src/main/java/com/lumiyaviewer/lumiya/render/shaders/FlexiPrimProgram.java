package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

/* loaded from: classes.dex */
public class FlexiPrimProgram extends PrimProgram {
    public int uNumSectionMatrices;
    public int uSectionMatrices;

    public FlexiPrimProgram(boolean z) {
        super(Shader.FlexiVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.render.shaders.PrimProgram, com.lumiyaviewer.lumiya.render.shaders.BasicPrimProgram, com.lumiyaviewer.lumiya.render.shaders.ShaderProgram
    public void bindVariables() {
        super.bindVariables();
        this.uSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uSectionMatrices");
        this.uNumSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uNumSectionMatrices");
    }
}
