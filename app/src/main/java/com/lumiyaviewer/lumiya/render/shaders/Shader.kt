package com.lumiyaviewer.lumiya.render.shaders

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * OpenGL ES shader enumeration.
 * Each enum value represents a shader file that can be compiled.
 */
enum class Shader(
    private val type: Int,
    private val fileName: String
) {
    PrimFragmentShader(GLES20.GL_FRAGMENT_SHADER, "prim.fsh"),
    PrimFragmentShader30(GLES20.GL_FRAGMENT_SHADER, "prim_30.fsh"),
    PrimOpaqueFragmentShader(GLES20.GL_FRAGMENT_SHADER, "prim_opaque.fsh"),
    PrimOpaqueFragmentShader30(GLES20.GL_FRAGMENT_SHADER, "prim_opaque_30.fsh"),
    PrimVertexShader(GLES20.GL_VERTEX_SHADER, "prim.vsh"),
    AvatarVertexShader(GLES20.GL_VERTEX_SHADER, "avatar.vsh"),
    FlexiVertexShader(GLES20.GL_VERTEX_SHADER, "prim_flexible.vsh"),
    RiggedMeshVertexShader(GLES20.GL_VERTEX_SHADER, "rigged_mesh.vsh"),
    RiggedMeshVertexShader30(GLES20.GL_VERTEX_SHADER, "rigged_mesh_30.vsh"),
    QuadVertexShader(GLES20.GL_VERTEX_SHADER, "quad.vsh"),
    QuadFragmentShader(GLES20.GL_FRAGMENT_SHADER, "quad.fsh"),
    BoundingBoxVertexShader(GLES20.GL_VERTEX_SHADER, "bounding_box_30.vsh"),
    BoundingBoxFragmentShader(GLES20.GL_FRAGMENT_SHADER, "bounding_box_30.fsh"),
    WaterVertexShader(GLES20.GL_VERTEX_SHADER, "water.vsh"),
    WaterFragmentShader(GLES20.GL_FRAGMENT_SHADER, "water.fsh"),
    SkyVertexShader(GLES20.GL_VERTEX_SHADER, "sky.vsh"),
    SkyFragmentShader(GLES20.GL_FRAGMENT_SHADER, "sky.fsh"),
    SkyNoCloudsFragmentShader(GLES20.GL_FRAGMENT_SHADER, "sky_no_clouds.fsh"),
    StarsVertexShader(GLES20.GL_VERTEX_SHADER, "stars.vsh"),
    StarsFragmentShader(GLES20.GL_FRAGMENT_SHADER, "stars.fsh"),
    ExtTextureVertexShader(GLES20.GL_VERTEX_SHADER, "external_texture.vsh"),
    ExtTextureFragmentShader(GLES20.GL_FRAGMENT_SHADER, "external_texture.fsh"),
    RawVertexShader(GLES20.GL_VERTEX_SHADER, "raw.vsh"),
    RawFragmentShader(GLES20.GL_FRAGMENT_SHADER, "raw.fsh"),
    FXAAVertexShader(GLES20.GL_VERTEX_SHADER, "fxaa.vsh"),
    FXAAFragmentShader(GLES20.GL_FRAGMENT_SHADER, "fxaa.fsh");

    private var handle: Int = 0

    /**
     * Load and preprocess shader source code from assets.
     */
    @Throws(ShaderCompileException::class)
    private fun getShaderCode(shaderPreprocessor: ShaderPreprocessor): String {
        val assetManager = LumiyaApp.getAssetManager()
            ?: throw ShaderCompileException("AssetManager is null when loading shader $fileName")
        
        return try {
            val inputStream = assetManager.open("shaders/$fileName")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val processedCode = shaderPreprocessor.processCode(bufferedReader)
            bufferedReader.close()
            processedCode
        } catch (e: IOException) {
            throw ShaderCompileException("Failed to load shader file $fileName: ${e.message}")
        }
    }

    /**
     * Compile this shader.
     * @param shaderPreprocessor Preprocessor for shader source
     * @return The OpenGL shader handle
     * @throws ShaderCompileException if compilation fails
     */
    @Throws(ShaderCompileException::class)
    fun compile(shaderPreprocessor: ShaderPreprocessor): Int {
        Debug.printf("Shaders: Compiling shader '%s'...", fileName)
        
        val shaderCode = getShaderCode(shaderPreprocessor)
        
        // Create and compile shader
        handle = GLES20.glCreateShader(type)
        GLES20.glShaderSource(handle, shaderCode)
        GLES20.glCompileShader(handle)
        
        // Check compilation status
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        
        if (compileStatus[0] != GLES20.GL_TRUE) {
            val errorLog = GLES20.glGetShaderInfoLog(handle)
            throw ShaderCompileException("Shader ($fileName) compile error: '$errorLog'")
        }
        
        return handle
    }

    /**
     * Get the OpenGL shader handle.
     */
    fun getHandle(): Int {
        return handle
    }
}

