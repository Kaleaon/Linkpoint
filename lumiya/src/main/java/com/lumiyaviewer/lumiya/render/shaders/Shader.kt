package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

enum class Shader(private val type: Int, private val fileName: String) {
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
    FXAAFragmentShader(35632, "fxaa.fsh");

    private var handle: Int = 0

    private fun getShaderCode(shaderPreprocessor: ShaderPreprocessor): String? {
        return try {
            LumiyaApp.getAssetManager()?.open("shaders/$fileName")?.use { open ->
                BufferedReader(InputStreamReader(open)).use { bufferedReader ->
                    shaderPreprocessor.processCode(bufferedReader)
                }
            }
        } catch (e: IOException) {
            null
        }
    }

    @Throws(ShaderCompileException::class)
    fun Compile(shaderPreprocessor: ShaderPreprocessor): Int {
        Debug.Printf("Shaders: Compiling shader '%s'...", arrayOf(fileName))
        val shaderCode = getShaderCode(shaderPreprocessor) ?: run {
            handle = 0
            throw ShaderCompileException("No shader code for $fileName")
        }
        handle = GLES20.glCreateShader(type)
        GLES20.glShaderSource(handle, shaderCode)
        GLES20.glCompileShader(handle)
        val iArr = IntArray(1)
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, iArr, 0)
        if (iArr[0] == 1) {
            return handle
        }
        val errorLog = GLES20.glGetShaderInfoLog(handle)
        throw ShaderCompileException("Shader ($fileName) compile error: '$errorLog'")
    }

    fun getHandle(): Int {
        return handle
    }
}
