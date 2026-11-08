package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32
import android.util.Log

/**
 * Compiles and links GLSL ES 3.20 shader programs.
 *
 * Design lineage: Lumiya `Shader.java` + `ShaderPreprocessor.java`, modernised
 * to GLSL ES 3.20 with support for:
 *   - Uniform Buffer Objects (std140 layout)
 *   - Shader Storage Buffers
 *   - Explicit attribute locations  (layout(location = N))
 *   - Explicit uniform binding points
 *   - Preprocessor macros (#define injection)
 */
class ShaderCompiler {

    companion object {
        private const val TAG = "ShaderCompiler"
    }

    /**
     * Compile a vertex + fragment program, returning the linked program handle.
     * Returns 0 on failure.
     */
    fun compile(
        vertexSource: String,
        fragmentSource: String,
        defines: Map<String, String> = emptyMap()
    ): Int {
        val vertHandle = compileStage(GLES32.GL_VERTEX_SHADER, preprocess(vertexSource, defines))
        if (vertHandle == 0) return 0

        val fragHandle = compileStage(GLES32.GL_FRAGMENT_SHADER, preprocess(fragmentSource, defines))
        if (fragHandle == 0) {
            GLES32.glDeleteShader(vertHandle)
            return 0
        }

        val program = GLES32.glCreateProgram()
        GLES32.glAttachShader(program, vertHandle)
        GLES32.glAttachShader(program, fragHandle)
        GLES32.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val info = GLES32.glGetProgramInfoLog(program)
            Log.e(TAG, "Program link failed:\n$info")
            GLES32.glDeleteProgram(program)
            GLES32.glDeleteShader(vertHandle)
            GLES32.glDeleteShader(fragHandle)
            return 0
        }

        // Shaders can be detached after linking
        GLES32.glDetachShader(program, vertHandle)
        GLES32.glDetachShader(program, fragHandle)
        GLES32.glDeleteShader(vertHandle)
        GLES32.glDeleteShader(fragHandle)

        return program
    }

    // ─────────────────────────────────────────────────────────────────────

    private fun compileStage(type: Int, source: String): Int {
        val shader = GLES32.glCreateShader(type)
        GLES32.glShaderSource(shader, source)
        GLES32.glCompileShader(shader)

        val status = IntArray(1)
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            val info = GLES32.glGetShaderInfoLog(shader)
            val typeName = if (type == GLES32.GL_VERTEX_SHADER) "VERTEX" else "FRAGMENT"
            Log.e(TAG, "$typeName shader compile failed:\n$info")
            GLES32.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    /**
     * Inject `#define` lines right after the `#version` directive.
     * If no `#version` is present one is prepended.
     */
    private fun preprocess(source: String, defines: Map<String, String>): String {
        val sb = StringBuilder()
        val lines = source.lines()
        var versionEmitted = false

        for (line in lines) {
            sb.appendLine(line)
            if (!versionEmitted && line.trimStart().startsWith("#version")) {
                versionEmitted = true
                for ((k, v) in defines) {
                    sb.appendLine("#define $k $v")
                }
            }
        }
        if (!versionEmitted) {
            // Prepend version + defines
            val header = buildString {
                appendLine("#version 320 es")
                for ((k, v) in defines) appendLine("#define $k $v")
            }
            return header + sb.toString()
        }
        return sb.toString()
    }
}
