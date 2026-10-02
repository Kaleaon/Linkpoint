package com.linkpoint.render.materials

import com.google.android.filament.Engine
import com.google.android.filament.Material
import com.google.android.filament.MaterialInstance
import com.google.android.filament.Texture
import com.google.android.filament.TextureSampler
import java.util.concurrent.ConcurrentHashMap

/** Converts [MaterialDescriptor] into Filament MaterialInstance parameters and batches descriptor sets for Vulkan. */
object FilamentMaterialTranslator {

    private val repeatSampler by lazy {
        TextureSampler(
            TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
            TextureSampler.MagFilter.LINEAR,
            TextureSampler.WrapMode.REPEAT
        )
    }

    data class TextureBindings(
        val baseColor: Texture? = null,
        val normal: Texture? = null,
        val metallicRoughness: Texture? = null,
        val emissive: Texture? = null
    )

    // Descriptor set cache to pool and batch identical material configurations, avoiding per-object program switching.
    private val instanceCache = ConcurrentHashMap<Long, MaterialInstance>()

    fun computeDescriptorKey(descriptor: MaterialDescriptor, bindings: TextureBindings): Long {
        var h = descriptor.hashCode().toLong()
        h = 31 * h + (bindings.baseColor?.let { System.identityHashCode(it) } ?: 0)
        h = 31 * h + (bindings.normal?.let { System.identityHashCode(it) } ?: 0)
        h = 31 * h + (bindings.metallicRoughness?.let { System.identityHashCode(it) } ?: 0)
        h = 31 * h + (bindings.emissive?.let { System.identityHashCode(it) } ?: 0)
        return h
    }

    /**
     * Obtains a batched/pooled MaterialInstance matching the descriptor and texture bindings.
     * Reuses existing Vulkan descriptor sets to minimize pipeline and program switching overhead.
     */
    fun getOrCreateInstance(
        baseMaterial: Material,
        descriptor: MaterialDescriptor,
        bindings: TextureBindings = TextureBindings()
    ): MaterialInstance {
        val key = computeDescriptorKey(descriptor, bindings)
        return instanceCache.computeIfAbsent(key) {
            val instance = baseMaterial.createInstance()
            apply(instance, descriptor, bindings)
            instance
        }
    }

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
            instance.setParameter("baseColorMap", bindings.baseColor, repeatSampler)
            instance.setParameter("hasTexture", 1f)
        } else {
            instance.setParameter("hasTexture", 0f)
        }

        bindings.normal?.let { instance.setParameter("normalMap", it, repeatSampler) }
        bindings.metallicRoughness?.let { instance.setParameter("metallicRoughnessMap", it, repeatSampler) }
        bindings.emissive?.let { instance.setParameter("emissiveMap", it, repeatSampler) }
    }

    /** Clears the pooled material instances. */
    fun clearPool(engine: Engine? = null) {
        if (engine != null) {
            instanceCache.values.forEach { instance ->
                try {
                    engine.destroyMaterialInstance(instance)
                } catch (ignored: Exception) {
                    // Ignore already-destroyed or native handle errors during teardown
                }
            }
        }
        instanceCache.clear()
    }
}
