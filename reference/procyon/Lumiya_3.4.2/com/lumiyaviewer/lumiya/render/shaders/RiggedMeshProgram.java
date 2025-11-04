// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class RiggedMeshProgram extends PrimProgram
{
    public int uBindShapeMatrix;
    public int uJointVectors;
    public int vJoint;
    public int vWeight;
    
    public RiggedMeshProgram(final boolean b) {
        final Shader riggedMeshVertexShader = Shader.RiggedMeshVertexShader;
        Shader shader;
        if (b) {
            shader = Shader.PrimOpaqueFragmentShader;
        }
        else {
            shader = Shader.PrimFragmentShader;
        }
        super(riggedMeshVertexShader, shader);
    }
    
    @Override
    protected void bindVariables() {
        super.bindVariables();
        this.uBindShapeMatrix = GLES20.glGetUniformLocation(this.handle, "uBindShapeMatrix");
        this.uJointVectors = GLES20.glGetUniformLocation(this.handle, "uJointVectors");
        this.vWeight = GLES20.glGetAttribLocation(this.handle, "vWeight");
        this.vJoint = GLES20.glGetAttribLocation(this.handle, "vJoint");
    }
}
