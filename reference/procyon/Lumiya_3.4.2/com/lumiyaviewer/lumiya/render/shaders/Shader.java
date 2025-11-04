// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.lumiyaviewer.lumiya.LumiyaApp;

public enum Shader
{
    AvatarVertexShader("AvatarVertexShader", 5, 35633, "avatar.vsh"), 
    BoundingBoxFragmentShader("BoundingBoxFragmentShader", 12, 35632, "bounding_box_30.fsh"), 
    BoundingBoxVertexShader("BoundingBoxVertexShader", 11, 35633, "bounding_box_30.vsh"), 
    ExtTextureFragmentShader("ExtTextureFragmentShader", 21, 35632, "external_texture.fsh"), 
    ExtTextureVertexShader("ExtTextureVertexShader", 20, 35633, "external_texture.vsh"), 
    FXAAFragmentShader("FXAAFragmentShader", 25, 35632, "fxaa.fsh"), 
    FXAAVertexShader("FXAAVertexShader", 24, 35633, "fxaa.vsh"), 
    FlexiVertexShader("FlexiVertexShader", 6, 35633, "prim_flexible.vsh"), 
    PrimFragmentShader("PrimFragmentShader", 0, 35632, "prim.fsh"), 
    PrimFragmentShader30("PrimFragmentShader30", 1, 35632, "prim_30.fsh"), 
    PrimOpaqueFragmentShader("PrimOpaqueFragmentShader", 2, 35632, "prim_opaque.fsh"), 
    PrimOpaqueFragmentShader30("PrimOpaqueFragmentShader30", 3, 35632, "prim_opaque_30.fsh"), 
    PrimVertexShader("PrimVertexShader", 4, 35633, "prim.vsh"), 
    QuadFragmentShader("QuadFragmentShader", 10, 35632, "quad.fsh"), 
    QuadVertexShader("QuadVertexShader", 9, 35633, "quad.vsh"), 
    RawFragmentShader("RawFragmentShader", 23, 35632, "raw.fsh"), 
    RawVertexShader("RawVertexShader", 22, 35633, "raw.vsh"), 
    RiggedMeshVertexShader("RiggedMeshVertexShader", 7, 35633, "rigged_mesh.vsh"), 
    RiggedMeshVertexShader30("RiggedMeshVertexShader30", 8, 35633, "rigged_mesh_30.vsh"), 
    SkyFragmentShader("SkyFragmentShader", 16, 35632, "sky.fsh"), 
    SkyNoCloudsFragmentShader("SkyNoCloudsFragmentShader", 17, 35632, "sky_no_clouds.fsh"), 
    SkyVertexShader("SkyVertexShader", 15, 35633, "sky.vsh"), 
    StarsFragmentShader("StarsFragmentShader", 19, 35632, "stars.fsh"), 
    StarsVertexShader("StarsVertexShader", 18, 35633, "stars.vsh"), 
    WaterFragmentShader("WaterFragmentShader", 14, 35632, "water.fsh"), 
    WaterVertexShader("WaterVertexShader", 13, 35633, "water.vsh");
    
    private final String fileName;
    private int handle;
    private final int type;
    
    private Shader(final String name, final int ordinal, final int type, final String fileName) {
        this.type = type;
        this.fileName = fileName;
    }
    
    private String getShaderCode(final ShaderPreprocessor shaderPreprocessor) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(LumiyaApp.getAssetManager().open("shaders/" + this.fileName)));
            final String processCode = shaderPreprocessor.processCode(bufferedReader);
            bufferedReader.close();
            return processCode;
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public int Compile(final ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
        Debug.Printf("Shaders: Compiling shader '%s'...", this.fileName);
        final String shaderCode = this.getShaderCode(shaderPreprocessor);
        if (shaderCode == null) {
            this.handle = 0;
            throw new ShaderCompileException("No shader code for " + this.fileName);
        }
        GLES20.glShaderSource(this.handle = GLES20.glCreateShader(this.type), shaderCode);
        GLES20.glCompileShader(this.handle);
        final int[] array = { 0 };
        GLES20.glGetShaderiv(this.handle, 35713, array, 0);
        if (array[0] != 1) {
            throw new ShaderCompileException(String.format("Shader (%s) compile error: '%s'", this.fileName, GLES20.glGetShaderInfoLog(this.handle)));
        }
        return this.handle;
    }
    
    public int getHandle() {
        return this.handle;
    }
}
