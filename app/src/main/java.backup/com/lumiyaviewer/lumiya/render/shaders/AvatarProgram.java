package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class AvatarProgram extends BasicPrimProgram {
    public int uJointMap;
    public int uJointMapLength;
    public int uJointMatrix;
    public int uUseWeight;
    public int vWeight;

    public AvatarProgram() {
        super(Shader.AvatarVertexShader, Shader.PrimFragmentShader);
    }

    protected void bindVariables() {
        super.bindVariables();
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight");
        this.uJointMatrix = GLES20.glGetUniformLocation(this.handle, "uJointMatrix");
        this.uJointMap = GLES20.glGetUniformLocation(this.handle, "uJointMap");
        this.uJointMapLength = GLES20.glGetUniformLocation(this.handle, "uJointMapLength");
        this.uUseWeight = GLES20.glGetUniformLocation(this.handle, "uUseWeight");
    }
}
