package com.linkpoint.render.materials

import com.linkpoint.protocol.llsd.LLSDMap

/**
 * Decodes glTF 2.0 PBR material payloads (from LLSD or data structures).
 *
 * @deprecated Use [GltfMaterialParser.parseLlsd] instead.
 */
@Deprecated(
    message = "PbrMaterialDecoder is deprecated; use GltfMaterialParser.parseLlsd instead.",
    replaceWith = ReplaceWith("GltfMaterialParser.parseLlsd(data)", "com.linkpoint.render.materials.GltfMaterialParser")
)
object PbrMaterialDecoder {

    /**
     * Decodes an LLSD Map payload representing a glTF 2.0 PBR material into a [MaterialDescriptor].
     *
     * @param data The LLSD map payload containing PBR factors and texture maps.
     * @param enableMetallicRoughness When true (default), extracts metallic-roughness texture maps for GLES 3.0+ rendering.
     * @deprecated Use [GltfMaterialParser.parseLlsd] instead.
     */
    @Deprecated(
        message = "decodeFromLLSD is deprecated; use GltfMaterialParser.parseLlsd instead.",
        replaceWith = ReplaceWith("GltfMaterialParser.parseLlsd(data)", "com.linkpoint.render.materials.GltfMaterialParser")
    )
    @JvmOverloads
    fun decodeFromLLSD(data: LLSDMap, enableMetallicRoughness: Boolean = true): MaterialDescriptor {
        val descriptor = GltfMaterialParser.parseLlsd(data)
        return if (!enableMetallicRoughness && descriptor.metallicRoughnessTexture != null) {
            descriptor.copy(metallicRoughnessTexture = null)
        } else {
            descriptor
        }
    }
}
