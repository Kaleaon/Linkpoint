// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class RawShaderProgram extends ShaderProgram
{
    public int textureSampler;
    public int uMVPMatrix;
    public int vPosition;
    public int vTexCoord;
    public int vTextureTransformMatrix;
    
    public RawShaderProgram(final boolean b) {
        Shader shader;
        if (b) {
            shader = Shader.ExtTextureVertexShader;
        }
        else {
            shader = Shader.RawVertexShader;
        }
        Shader shader2;
        if (b) {
            shader2 = Shader.ExtTextureFragmentShader;
        }
        else {
            shader2 = Shader.RawFragmentShader;
        }
        super(shader, shader2);
    }
    
    @Override
    protected void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.vTexCoord = GLES20.glGetAttribLocation(this.handle, "vTexCoord");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.textureSampler = GLES20.glGetUniformLocation(this.handle, "vTexture");
        this.vTextureTransformMatrix = GLES20.glGetUniformLocation(this.handle, "vTextureTransformMatrix");
    }
}
