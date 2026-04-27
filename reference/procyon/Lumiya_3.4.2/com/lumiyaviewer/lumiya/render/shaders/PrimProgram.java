// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class PrimProgram extends BasicPrimProgram
{
    public int uTexMatrix;
    
    public PrimProgram(final Shader shader, final Shader shader2) {
        super(shader, shader2);
    }
    
    public PrimProgram(final boolean b) {
        final Shader primVertexShader = Shader.PrimVertexShader;
        Shader shader;
        if (b) {
            shader = Shader.PrimOpaqueFragmentShader;
        }
        else {
            shader = Shader.PrimFragmentShader;
        }
        super(primVertexShader, shader);
    }
    
    @Override
    protected void bindVariables() {
        super.bindVariables();
        this.uTexMatrix = GLES20.glGetUniformLocation(this.handle, "uTexMatrix");
    }
}
