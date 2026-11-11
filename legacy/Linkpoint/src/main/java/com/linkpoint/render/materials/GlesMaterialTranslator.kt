package com.linkpoint.render.materials

import android.opengl.GLES32
import com.linkpoint.render.lumiya.shaders.PrimShaderProgram
import kotlin.math.cos
import kotlin.math.sin

/** Converts [MaterialDescriptor] into GLES uniform + texture bindings for 5-channel PBR materials. */
object GlesMaterialTranslator {

    data class TextureBindings(
        val baseColorHandle: Int = 0,
        val normalHandle: Int = 0,
        val metallicRoughnessHandle: Int = 0,
        val emissiveHandle: Int = 0,
        val occlusionHandle: Int = 0
    )

    fun apply(
        program: PrimShaderProgram,
        descriptor: MaterialDescriptor,
        bindings: TextureBindings = TextureBindings(),
        isGles20Fallback: Boolean = false
    ) {
        if (isGles20Fallback) {
            val phong = PbrToPhongTranscoder.transcode(descriptor)
            program.setColor(
                phong.diffuseR,
                phong.diffuseG,
                phong.diffuseB,
                phong.diffuseA
            )
            program.setSpecularColor(
                phong.specularR,
                phong.specularG,
                phong.specularB
            )
            program.setSpecularExponent(phong.specularExponent)
            program.setTexMatrix(buildTexMatrix(descriptor.uvTransform))

            val useBaseColor = descriptor.baseColorTexture != null && bindings.baseColorHandle != 0
            program.setHasBaseColorMap(useBaseColor)
            program.setUseTexture(useBaseColor)
            if (useBaseColor) {
                GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.baseColorHandle)
                program.setBaseColorMapSampler(0)
                program.setTextureSampler(0)
            }

            program.setHasNormalMap(false)
            program.setHasMetallicRoughnessMap(false)
            program.setHasEmissiveMap(false)
            program.setHasOcclusionMap(false)
            program.setIsGles20Fallback(true)
            return
        }

        program.setColor(
            descriptor.baseColor.x,
            descriptor.baseColor.y,
            descriptor.baseColor.z,
            descriptor.baseColor.w
        )
        program.setTexMatrix(buildTexMatrix(descriptor.uvTransform))

        program.setMetallicFactor(descriptor.metallicFactor)
        program.setRoughnessFactor(descriptor.roughnessFactor)
        program.setEmissiveFactor(descriptor.emissiveFactor.x, descriptor.emissiveFactor.y, descriptor.emissiveFactor.z)
        program.setOcclusionFactor(descriptor.occlusionFactor)
        program.setNormalScale(descriptor.normalScale)

        program.setIsGles20Fallback(false)

        // Channel 0: Base Color Map
        val useBaseColor = descriptor.baseColorTexture != null && bindings.baseColorHandle != 0
        program.setHasBaseColorMap(useBaseColor)
        program.setUseTexture(useBaseColor)
        if (useBaseColor) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.baseColorHandle)
            program.setBaseColorMapSampler(0)
            program.setTextureSampler(0)
        }

        // Channel 1: Normal Map
        val useNormal = descriptor.normalTexture != null && bindings.normalHandle != 0
        program.setHasNormalMap(useNormal)
        if (useNormal) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.normalHandle)
            program.setNormalMapSampler(1)
        }

        // Channel 2: Metallic-Roughness Map
        val useMR = descriptor.metallicRoughnessTexture != null && bindings.metallicRoughnessHandle != 0
        program.setHasMetallicRoughnessMap(useMR)
        if (useMR) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE2)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.metallicRoughnessHandle)
            program.setMetallicRoughnessMapSampler(2)
        }

        // Channel 3: Emissive Map
        val useEmissive = descriptor.emissiveTexture != null && bindings.emissiveHandle != 0
        program.setHasEmissiveMap(useEmissive)
        if (useEmissive) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE3)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.emissiveHandle)
            program.setEmissiveMapSampler(3)
        }

        // Channel 4: Occlusion Map
        val useOcclusion = descriptor.occlusionTexture != null && bindings.occlusionHandle != 0
        program.setHasOcclusionMap(useOcclusion)
        if (useOcclusion) {
            GLES32.glActiveTexture(GLES32.GL_TEXTURE4)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, bindings.occlusionHandle)
            program.setOcclusionMapSampler(4)
        }
    }

    private fun buildTexMatrix(uv: MaterialDescriptor.UvTransform): FloatArray {
        return uv.matrix
    }
}
