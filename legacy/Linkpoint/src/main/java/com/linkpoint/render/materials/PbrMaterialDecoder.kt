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
     * @deprecated Use [GltfMaterialParser.parseLlsd] instead.
     */
    @Deprecated(
        message = "decodeFromLLSD is deprecated; use GltfMaterialParser.parseLlsd instead.",
        replaceWith = ReplaceWith("GltfMaterialParser.parseLlsd(data)", "com.linkpoint.render.materials.GltfMaterialParser")
    )
    fun decodeFromLLSD(data: LLSDMap): MaterialDescriptor {
        return GltfMaterialParser.parseLlsd(data)
    }
}
