package com.linkpoint.render.materials

import android.opengl.GLES32
import com.linkpoint.render.lumiya.shaders.PrimShaderProgram
import kotlin.math.cos
import kotlin.math.sin

/** Converts [MaterialDescriptor] into GLES uniform + texture bindings. */
object GlesMaterialTranslator {

    data class TextureBindings(
        val baseColorHandle: Int = 0,
        val normalHandle: Int = 0,
        val metallicRoughnessHandle: Int = 0,
        val emissiveHandle: Int = 0
    )

    fun apply(program: PrimShaderProgram, descriptor: MaterialDescriptor, bindings: TextureBindings = TextureBindings()) {
        program.setColor(
            descriptor.baseColor.x,
            descriptor.baseColor.y,
            descriptor.baseColor.z,
            descriptor.baseColor.w
        )
        program.setTexMatrix(buildTexMatrix(descriptor.uvTransform))

        val useTexture = descriptor.baseColorTexture != null && bindings.baseColorHandle != 0
        program.setUseTexture(useTexture)
        if (useTexture) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.baseColorHandle)
            program.setTextureSampler(0)
        }
    }

    private fun buildTexMatrix(uv: MaterialDescriptor.UvTransform): FloatArray {
        val c = cos(uv.rotation)
        val s = sin(uv.rotation)
        return floatArrayOf(
            uv.scaleS * c,  uv.scaleS * -s, 0f, 0f,
            uv.scaleT * s,  uv.scaleT * c,  0f, 0f,
            0f,             0f,             1f, 0f,
            uv.offsetS,     uv.offsetT,     0f, 1f
        )
    }
}
