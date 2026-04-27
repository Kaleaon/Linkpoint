package com.linkpoint.render.materials

import com.google.android.filament.MaterialInstance
import com.google.android.filament.Texture
import com.google.android.filament.TextureSampler

/** Converts [MaterialDescriptor] into Filament MaterialInstance parameters. */
object FilamentMaterialTranslator {

    private val REPEAT_SAMPLER = TextureSampler(
        TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
        TextureSampler.MagFilter.LINEAR,
        TextureSampler.WrapMode.REPEAT
    )

    data class TextureBindings(
        val baseColor: Texture? = null,
        val normal: Texture? = null,
        val metallicRoughness: Texture? = null,
        val emissive: Texture? = null
    )

    fun apply(instance: MaterialInstance, descriptor: MaterialDescriptor, bindings: TextureBindings = TextureBindings()) {
        instance.setParameter(
            "baseColor",
            descriptor.baseColor.x,
            descriptor.baseColor.y,
            descriptor.baseColor.z,
            descriptor.baseColor.w
        )
        instance.setParameter("texScale", descriptor.uvTransform.scaleS, descriptor.uvTransform.scaleT)
        instance.setParameter("texOffset", descriptor.uvTransform.offsetS, descriptor.uvTransform.offsetT)
        instance.setParameter("texRotation", descriptor.uvTransform.rotation)
        instance.setParameter("metallic", descriptor.metallicFactor)
        instance.setParameter("roughness", descriptor.roughnessFactor)

        // Explicit missing-texture handling: if base texture is unavailable,
        // both backends fall back to descriptor baseColor/alpha with hasTexture=0.
        if (descriptor.baseColorTexture != null && bindings.baseColor != null) {
            instance.setParameter("baseColorMap", bindings.baseColor, REPEAT_SAMPLER)
            instance.setParameter("hasTexture", 1f)
        } else {
            instance.setParameter("hasTexture", 0f)
        }

        bindings.normal?.let { instance.setParameter("normalMap", it, REPEAT_SAMPLER) }
        bindings.metallicRoughness?.let { instance.setParameter("metallicRoughnessMap", it, REPEAT_SAMPLER) }
        bindings.emissive?.let { instance.setParameter("emissiveMap", it, REPEAT_SAMPLER) }
    }
}
