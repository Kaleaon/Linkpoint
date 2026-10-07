package com.linkpoint.render.materials

import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import com.linkpoint.protocol.llsd.LLSDValue
import java.util.UUID

/**
 * Decodes glTF 2.0 PBR material payloads (from LLSD or data structures) and extracts PBR factors
 * and capability-aware texture maps.
 */
object PbrMaterialDecoder {

    /**
     * Decodes an LLSD Map payload representing a glTF 2.0 PBR material into a [MaterialDescriptor].
     * @param data The LLSD map payload containing PBR factors and texture maps.
     * @param enableMetallicRoughness When true (default), extracts metallic-roughness texture maps for GLES 3.0+ rendering.
     */
    @JvmOverloads
    fun decodeFromLLSD(data: LLSDMap, enableMetallicRoughness: Boolean = true): MaterialDescriptor {
        var baseR = 1f
        var baseG = 1f
        var baseB = 1f
        var baseA = 1f

        data.getArray("BaseColor")?.let { colorArr ->
            if (colorArr.size >= 3) {
                baseR = asFloat(colorArr[0]) ?: 1f
                baseG = asFloat(colorArr[1]) ?: 1f
                baseB = asFloat(colorArr[2]) ?: 1f
            }
            if (colorArr.size >= 4) {
                baseA = asFloat(colorArr[3]) ?: 1f
            }
        }

        val metallic = data.getReal("Metallic")?.toFloat()
            ?: data.getReal("metallicFactor")?.toFloat()
            ?: 0f

        val roughness = data.getReal("Roughness")?.toFloat()
            ?: data.getReal("roughnessFactor")?.toFloat()
            ?: 0.5f

        var baseColorTexRef: MaterialDescriptor.TextureRef? = null
        var normalTexRef: MaterialDescriptor.TextureRef? = null
        var metallicRoughnessTexRef: MaterialDescriptor.TextureRef? = null

        data.getMap("Textures")?.let { texturesMap ->
            texturesMap["BaseColor"]?.let { parseUUID(it)?.let { uuid ->
                baseColorTexRef = MaterialDescriptor.TextureRef(uuid, uuid)
            }}
            texturesMap["Normal"]?.let { parseUUID(it)?.let { uuid ->
                normalTexRef = MaterialDescriptor.TextureRef(uuid, uuid)
            }}
            if (enableMetallicRoughness) {
                (texturesMap["MetallicRoughness"] ?: texturesMap["metallic_roughness"])?.let { parseUUID(it)?.let { uuid ->
                    metallicRoughnessTexRef = MaterialDescriptor.TextureRef(uuid, uuid)
                }}
            }
        }

        if (baseColorTexRef == null) {
            data["BaseColorTexture"]?.let { parseUUID(it)?.let { uuid ->
                baseColorTexRef = MaterialDescriptor.TextureRef(uuid, uuid)
            }}
        }
        if (normalTexRef == null) {
            data["NormalTexture"]?.let { parseUUID(it)?.let { uuid ->
                normalTexRef = MaterialDescriptor.TextureRef(uuid, uuid)
            }}
        }
        if (enableMetallicRoughness && metallicRoughnessTexRef == null) {
            (data["MetallicRoughnessTexture"] ?: data["metallic_roughness_texture"] ?: data["MetallicRoughness"])?.let { parseUUID(it)?.let { uuid ->
                metallicRoughnessTexRef = MaterialDescriptor.TextureRef(uuid, uuid)
            }}
        }

        return MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(baseR, baseG, baseB, baseA),
            baseColorTexture = baseColorTexRef,
            normalTexture = normalTexRef,
            metallicRoughnessTexture = metallicRoughnessTexRef,
            metallicFactor = metallic,
            roughnessFactor = roughness
        )
    }

    private fun asFloat(value: LLSDValue): Float? = when (value) {
        is LLSDReal -> value.value.toFloat()
        is LLSDInteger -> value.value.toFloat()
        else -> null
    }

    private fun parseUUID(valObj: LLSDValue): UUID? = when (valObj) {
        is LLSDUUID -> valObj.value
        is LLSDString -> try { UUID.fromString(valObj.value) } catch (_: Exception) { null }
        else -> null
    }
}
