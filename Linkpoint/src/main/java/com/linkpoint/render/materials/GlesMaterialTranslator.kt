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

    fun apply(
        program: PrimShaderProgram,
        descriptor: MaterialDescriptor,
        bindings: TextureBindings = TextureBindings()
    ) {
        // Transcode PBR factors to Blinn-Phong specular and diffuse parameters on the CPU
        val phong = PbrToPhongTranscoder.transcode(descriptor)

        // Set diffuse color uniform
        program.setColor(
            phong.diffuseR,
            phong.diffuseG,
            phong.diffuseB,
            phong.diffuseA
        )

        // Set specular color and exponent uniforms
        program.setSpecularColor(
            phong.specularR,
            phong.specularG,
            phong.specularB
        )
        program.setSpecularExponent(phong.specularExponent)

        // Set texture UV matrix
        program.setTexMatrix(buildTexMatrix(descriptor.uvTransform))

        // Requirement 3 & 4: Map base color map to diffuse sampler on GL_TEXTURE0.
        // Fall back to neutral white diffuse values if texture handle is missing/0.
        val useBaseColorMap = descriptor.baseColorTexture != null && bindings.baseColorHandle != 0
        program.setUseTexture(useBaseColorMap)
        if (useBaseColorMap) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.baseColorHandle)
            program.setTextureSampler(0)
        }

        // Map normal map to texture unit 1 (GL_TEXTURE1)
        val useNormalMap = descriptor.normalTexture != null && bindings.normalHandle != 0
        program.setUseNormalMap(useNormalMap)
        if (useNormalMap) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.normalHandle)
            program.setNormalMapSampler(1)
        } else {
            // Unbind normal map or ensure unit 1 is clear to prevent unbound sampler errors
            GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        }

        // Note: metallicRoughnessHandle is explicitly discarded to remain strictly within
        // OpenGL ES 2.0 sampler limits (maximum 2-3 samplers per pass).
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
