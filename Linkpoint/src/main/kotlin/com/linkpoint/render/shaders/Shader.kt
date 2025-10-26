package com.linkpoint.render.shaders

import android.opengl.GLES20
import com.linkpoint.Debug
import com.linkpoint.LinkpointApp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

enum class Shader {
    PrimFragmentShader(35632, "prim.fsh"),
    PrimFragmentShader30(35632, "prim_30.fsh"),
    PrimOpaqueFragmentShader(35632, "prim_opaque.fsh"),
    PrimOpaqueFragmentShader30(35632, "prim_opaque_30.fsh"),
    PrimVertexShader(35633, "prim.vsh"),
    AvatarVertexShader(35633, "avatar.vsh"),
    FlexiVertexShader(35633, "prim_flexible.vsh"),
    RiggedMeshVertexShader(35633, "rigged_mesh.vsh"),
    RiggedMeshVertexShader30(35633, "rigged_mesh_30.vsh"),
    QuadVertexShader(35633, "quad.vsh"),
    QuadFragmentShader(35632, "quad.fsh"),
    BoundingBoxVertexShader(35633, "bounding_box_30.vsh"),
    BoundingBoxFragmentShader(35632, "bounding_box_30.fsh"),
    WaterVertexShader(35633, "water.vsh"),
    WaterFragmentShader(35632, "water.fsh"),
    SkyVertexShader(35633, "sky.vsh"),
    SkyFragmentShader(35632, "sky.fsh"),
    SkyNoCloudsFragmentShader(35632, "sky_no_clouds.fsh"),
    StarsVertexShader(35633, "stars.vsh"),
    StarsFragmentShader(35632, "stars.fsh"),
    ExtTextureVertexShader(35633, "external_texture.vsh"),
    ExtTextureFragmentShader(35632, "external_texture.fsh"),
    RawVertexShader(35633, "raw.vsh"),
    RawFragmentShader(35632, "raw.fsh"),
    FXAAVertexShader(35633, "fxaa.vsh"),
    FXAAFragmentShader(35632, "fxaa.fsh")
    
    private val String fileName
    private Int handle
    private val Int type

    private Shader(Int i, String str) {
        this.type = i
        this.fileName = str
    }

     private fun getShaderCode(shaderPreprocessor: ShaderPreprocessor): String {
        try {
            val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(LinkpointApp.getAssetManager().open("shaders/" + this.fileName)))
            val processCode: String = shaderPreprocessor.processCode(bufferedReader)
            bufferedReader.close()
            return processCode
        } catch (IOException e) {
            return null
        }
    }

    public fun Compile(shaderPreprocessor: ShaderPreprocessor): Int throws ShaderCompileException {
        Debug.Printf("Shaders: Compiling shader '%s'...", this.fileName)
        val shaderCode: String = getShaderCode(shaderPreprocessor)
        if (shaderCode == null) {
            this.handle = 0
            throw ShaderCompileException("No shader code for " + this.fileName)
        }
        this.handle = GLES20.glCreateShader(this.type)
        GLES20.glShaderSource(this.handle, shaderCode)
        GLES20.glCompileShader(this.handle)
        val iArr: IntArray = Int[1]
        GLES20.glGetShaderiv(this.handle, 35713, iArr, 0)
        if (iArr[0] == 1) {
            return this.handle
        }
        shaderCode = GLES20.glGetShaderInfoLog(this.handle)
        throw ShaderCompileException(String.format("Shader (%s) compile error: '%s'", Array<Any>{this.fileName, shaderCode}))
    }

     public fun getHandle(): Int {
        return this.handle
    }
}
