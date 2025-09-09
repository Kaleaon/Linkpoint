package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class RiggedMeshProgram extends PrimProgram {
    public int uBindShapeMatrix;
    public int uJointVectors;
    public int vJoint;
    public int vWeight;

    public RiggedMeshProgram(boolean z) {
        super(Shader.RiggedMeshVertexShader, z ? Shader.PrimOpaqueFragmentShader : Shader.PrimFragmentShader);
    }

    protected void bindVariables() {
        super.bindVariables();
        this.uBindShapeMatrix = GLES20.glGetUniformLocation(this.handle, "uBindShapeMatrix");
        this.uJointVectors = GLES20.glGetUniformLocation(this.handle, "uJointVectors");
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight");
        this.vJoint = GLES20.glGetAttribLocation(this.handle, "vJoint");
    }
}
