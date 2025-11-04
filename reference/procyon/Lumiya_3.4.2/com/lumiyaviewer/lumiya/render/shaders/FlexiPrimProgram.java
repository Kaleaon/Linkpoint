// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class FlexiPrimProgram extends PrimProgram
{
    public int uNumSectionMatrices;
    public int uSectionMatrices;
    
    public FlexiPrimProgram(final boolean b) {
        final Shader flexiVertexShader = Shader.FlexiVertexShader;
        Shader shader;
        if (b) {
            shader = Shader.PrimOpaqueFragmentShader;
        }
        else {
            shader = Shader.PrimFragmentShader;
        }
        super(flexiVertexShader, shader);
    }
    
    @Override
    protected void bindVariables() {
        super.bindVariables();
        this.uSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uSectionMatrices");
        this.uNumSectionMatrices = GLES20.glGetUniformLocation(this.handle, "uNumSectionMatrices");
    }
}
