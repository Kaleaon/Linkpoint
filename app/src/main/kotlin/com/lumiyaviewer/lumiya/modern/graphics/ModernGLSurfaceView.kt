package com.lumiyaviewer.lumiya.modern.graphics

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.cos
import kotlin.math.sin

/**
 * Minimal OpenGL ES 3.0 surface that renders a colourful rotating triangle.
 *
 * The intent is to provide a safe, verifiable modern rendering path for Lumiya's rebuild
 * without pulling in the full historical renderer.  If the constructor succeeds the device
 * has already proven it can create an ES 3.0 context.
 */
class ModernGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val renderer = ModernGLRenderer()
    private val isReleased = AtomicBoolean(false)

    init {
        setEGLContextClientVersion(3)
        // Request an RGBA8888 surface with 24-bit depth for decent quality.
        setEGLConfigChooser(8, 8, 8, 8, 24, 0)
        preserveEGLContextOnPause = true
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun release() {
        if (isReleased.compareAndSet(false, true)) {
            queueEvent { renderer.destroy() }
        }
    }

    private class ModernGLRenderer : Renderer {
        private val vertexShaderCode = """
            #version 300 es
            layout(location = 0) in vec3 aPosition;
            layout(location = 1) in vec3 aColor;
            uniform mat4 uMVP;
            out vec3 vColor;
            void main() {
                vColor = aColor;
                gl_Position = uMVP * vec4(aPosition, 1.0);
            }
        """.trimIndent()

        private val fragmentShaderCode = """
            #version 300 es
            precision mediump float;
            in vec3 vColor;
            out vec4 fragColor;
            void main() {
                fragColor = vec4(vColor, 1.0);
            }
        """.trimIndent()

        private var program = 0
        private var mvpMatrixHandle = 0
        private lateinit var vertexBuffer: FloatBuffer

        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private val modelMatrix = FloatArray(16)
        private val mvpMatrix = FloatArray(16)

        private var angle = 0f

        override fun onSurfaceCreated(glUnused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
            GLES30.glClearColor(0.07f, 0.1f, 0.18f, 1.0f)
            GLES30.glEnable(GLES30.GL_DEPTH_TEST)

            program = createProgram(vertexShaderCode, fragmentShaderCode)
            mvpMatrixHandle = GLES30.glGetUniformLocation(program, "uMVP")

            val vertices = floatArrayOf(
                // X, Y, Z, R, G, B
                 0.0f,  0.6f, 0.0f, 1.0f, 0.2f, 0.2f,
                -0.6f, -0.4f, 0.0f, 0.2f, 1.0f, 0.3f,
                 0.6f, -0.4f, 0.0f, 0.2f, 0.4f, 1.0f
            )

            vertexBuffer = ByteBuffer
                .allocateDirect(vertices.size * Float.SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices)
            vertexBuffer.position(0)

            Matrix.setLookAtM(
                viewMatrix, 0,
                /* eye */ 0f, 0f, 2.5f,
                /* center */ 0f, 0f, 0f,
                /* up */ 0f, 1f, 0f
            )
            Matrix.setIdentityM(modelMatrix, 0)
        }

        override fun onSurfaceChanged(glUnused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
            GLES30.glViewport(0, 0, width, height)
            val aspect = width.toFloat() / height.toFloat().coerceAtLeast(1f)
            Matrix.perspectiveM(projectionMatrix, 0, 45f, aspect, 0.1f, 10f)
        }

        override fun onDrawFrame(glUnused: javax.microedition.khronos.opengles.GL10?) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

            angle += 0.8f
            Matrix.setRotateM(modelMatrix, 0, angle, sin(angle * 0.0174533f), 1f, cos(angle * 0.0174533f))

            val temp = FloatArray(16)
            Matrix.multiplyMM(temp, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, temp, 0)

            GLES30.glUseProgram(program)
            GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

            val stride = 6 * Float.SIZE_BYTES
            vertexBuffer.position(0)
            GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, stride, vertexBuffer)
            GLES30.glEnableVertexAttribArray(0)

            vertexBuffer.position(3)
            GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, stride, vertexBuffer)
            GLES30.glEnableVertexAttribArray(1)

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
        }

        fun destroy() {
            if (program != 0) {
                GLES30.glDeleteProgram(program)
                program = 0
            }
        }

        private fun createProgram(vertexSource: String, fragmentSource: String): Int {
            val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexSource)
            val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
            val program = GLES30.glCreateProgram()
            GLES30.glAttachShader(program, vertexShader)
            GLES30.glAttachShader(program, fragmentShader)
            GLES30.glLinkProgram(program)

            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val error = GLES30.glGetProgramInfoLog(program)
                GLES30.glDeleteProgram(program)
                throw IllegalStateException("Failed to link GL program: $error")
            }

            GLES30.glDeleteShader(vertexShader)
            GLES30.glDeleteShader(fragmentShader)
            return program
        }

        private fun compileShader(type: Int, source: String): Int {
            val shader = GLES30.glCreateShader(type)
            GLES30.glShaderSource(shader, source)
            GLES30.glCompileShader(shader)

            val compiled = IntArray(1)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                val error = GLES30.glGetShaderInfoLog(shader)
                GLES30.glDeleteShader(shader)
                throw IllegalStateException("Failed to compile shader: $error")
            }
            return shader
        }
    }
}
