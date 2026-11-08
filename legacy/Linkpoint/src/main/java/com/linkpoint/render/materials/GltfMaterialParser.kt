package com.linkpoint.render.materials

import android.util.Log
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Parses Second Life glTF 2.0 material metadata and override payloads.
 * Supports standard glTF 2.0 JSON structures and SL LLSD material maps.
 */
object GltfMaterialParser {

    private const val TAG = "GltfMaterialParser"

    /**
     * Parse a glTF 2.0 JSON string or SL material envelope into a [MaterialDescriptor].
     */
    fun parseJson(jsonString: String): MaterialDescriptor {
        return try {
            val root = JSONObject(jsonString)
            parseJsonObject(root)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse glTF 2.0 material JSON", e)
            MaterialDescriptor()
        }
    }

    /**
     * Parse a glTF 2.0 JSONObject into a [MaterialDescriptor].
     */
    fun parseJsonObject(root: JSONObject): MaterialDescriptor {
        val materialsArray = root.optJSONArray("materials")
        val matObj = if (materialsArray != null && materialsArray.length() > 0) {
            materialsArray.optJSONObject(0) ?: JSONObject()
        } else {
            root
        }

        val pbr = matObj.optJSONObject("pbrMetallicRoughness") ?: JSONObject()

        // Base color
        val baseColorArray = pbr.optJSONArray("baseColorFactor")
        val baseColor = if (baseColorArray != null && baseColorArray.length() >= 4) {
            MaterialDescriptor.Float4(
                baseColorArray.optDouble(0, 1.0).toFloat(),
                baseColorArray.optDouble(1, 1.0).toFloat(),
                baseColorArray.optDouble(2, 1.0).toFloat(),
                baseColorArray.optDouble(3, 1.0).toFloat()
            )
        } else {
            MaterialDescriptor.Float4(1f, 1f, 1f, 1f)
        }

        // Metallic / Roughness factors
        val metallicFactor = pbr.optDouble("metallicFactor", 1.0).toFloat()
        val roughnessFactor = pbr.optDouble("roughnessFactor", 1.0).toFloat()

        // Emissive factor
        val emissiveArray = matObj.optJSONArray("emissiveFactor")
        val emissiveFactor = if (emissiveArray != null && emissiveArray.length() >= 3) {
            MaterialDescriptor.Float3(
                emissiveArray.optDouble(0, 0.0).toFloat(),
                emissiveArray.optDouble(1, 0.0).toFloat(),
                emissiveArray.optDouble(2, 0.0).toFloat()
            )
        } else {
            MaterialDescriptor.Float3.ZERO
        }

        // Normal scale and occlusion strength
        val normalTexObj = matObj.optJSONObject("normalTexture")
        val normalScale = normalTexObj?.optDouble("scale", 1.0)?.toFloat() ?: 1.0f

        val occlusionTexObj = matObj.optJSONObject("occlusionTexture")
        val occlusionStrength = occlusionTexObj?.optDouble("strength", 1.0)?.toFloat() ?: 1.0f

        // Alpha mode & cutoff
        val alphaModeStr = matObj.optString("alphaMode", "OPAQUE")
        val alphaMode = when (alphaModeStr.uppercase()) {
            "MASK" -> MaterialDescriptor.AlphaMode.MASK
            "BLEND" -> MaterialDescriptor.AlphaMode.BLEND
            else -> MaterialDescriptor.AlphaMode.OPAQUE
        }
        val alphaCutoff = matObj.optDouble("alphaCutoff", 0.5).toFloat()
        val doubleSided = matObj.optBoolean("doubleSided", false)

        // Texture references
        val texturesArray = root.optJSONArray("textures")
        val imagesArray = root.optJSONArray("images")

        val baseColorTexRef = resolveTextureRef(root, pbr.optJSONObject("baseColorTexture"), texturesArray, imagesArray)
        val normalTexRef = resolveTextureRef(root, normalTexObj, texturesArray, imagesArray)
        val metallicRoughnessTexRef = resolveTextureRef(root, pbr.optJSONObject("metallicRoughnessTexture"), texturesArray, imagesArray)
        val emissiveTexRef = resolveTextureRef(root, matObj.optJSONObject("emissiveTexture"), texturesArray, imagesArray)
        val occlusionTexRef = resolveTextureRef(root, occlusionTexObj, texturesArray, imagesArray)

        // UV transform (from baseColorTexture or extensions)
        val uvTransform = parseUvTransform(pbr.optJSONObject("baseColorTexture") ?: matObj)

        return MaterialDescriptor(
            baseColor = baseColor,
            baseColorTexture = baseColorTexRef,
            alphaMode = alphaMode,
            alphaCutoff = alphaCutoff,
            doubleSided = doubleSided,
            normalTexture = normalTexRef,
            normalScale = normalScale,
            metallicRoughnessTexture = metallicRoughnessTexRef,
            metallicFactor = metallicFactor,
            roughnessFactor = roughnessFactor,
            emissiveTexture = emissiveTexRef,
            emissiveFactor = emissiveFactor,
            occlusionTexture = occlusionTexRef,
            occlusionFactor = occlusionStrength,
            uvTransform = uvTransform
        )
    }

    /**
     * Parse an LLSDMap material payload into a [MaterialDescriptor].
     */
    fun parseLlsd(llsd: LLSDMap): MaterialDescriptor {
        val jsonString = llsd.getString("gltf_json") ?: llsd.getString("json")
        if (jsonString != null) {
            return parseJson(jsonString)
        }

        // Direct LLSD map properties
        val baseColorArr = llsd.getArray("base_color") ?: llsd.getArray("baseColorFactor")
        val baseColor = if (baseColorArr != null && baseColorArr.size >= 4) {
            MaterialDescriptor.Float4(
                readFloat(baseColorArr, 0),
                readFloat(baseColorArr, 1),
                readFloat(baseColorArr, 2),
                readFloat(baseColorArr, 3)
            )
        } else {
            MaterialDescriptor.Float4(1f, 1f, 1f, 1f)
        }

        val metallic = llsd.getReal("metallic")?.toFloat() ?: llsd.getReal("metallicFactor")?.toFloat() ?: 1f
        val roughness = llsd.getReal("roughness")?.toFloat() ?: llsd.getReal("roughnessFactor")?.toFloat() ?: 1f

        val baseColorId = llsd.getUUID("base_color_texture") ?: llsd.getUUID("baseColorTexture")
        val normalId = llsd.getUUID("normal_texture") ?: llsd.getUUID("normalTexture")
        val mrId = llsd.getUUID("metallic_roughness_texture") ?: llsd.getUUID("metallicRoughnessTexture")
        val emissiveId = llsd.getUUID("emissive_texture") ?: llsd.getUUID("emissiveTexture")
        val occlusionId = llsd.getUUID("occlusion_texture") ?: llsd.getUUID("occlusionTexture")

        val baseColorRef = baseColorId?.let { MaterialDescriptor.TextureRef(it, it) }
        val normalRef = normalId?.let { MaterialDescriptor.TextureRef(it, it) }
        val mrRef = mrId?.let { MaterialDescriptor.TextureRef(it, it) }
        val emissiveRef = emissiveId?.let { MaterialDescriptor.TextureRef(it, it) }
        val occlusionRef = occlusionId?.let { MaterialDescriptor.TextureRef(it, it) }

        return MaterialDescriptor(
            baseColor = baseColor,
            baseColorTexture = baseColorRef,
            normalTexture = normalRef,
            metallicRoughnessTexture = mrRef,
            metallicFactor = metallic,
            roughnessFactor = roughness,
            emissiveTexture = emissiveRef,
            occlusionTexture = occlusionRef
        )
    }

    private fun readFloat(arr: LLSDArray, index: Int): Float {
        val elem = arr.value.getOrNull(index) ?: return 1f
        return when (elem) {
            is LLSDReal -> elem.value.toFloat()
            is LLSDInteger -> elem.value.toFloat()
            else -> 1f
        }
    }

    private fun resolveTextureRef(
        root: JSONObject,
        texInfo: JSONObject?,
        texturesArray: JSONArray?,
        imagesArray: JSONArray?
    ): MaterialDescriptor.TextureRef? {
        if (texInfo == null) return null

        // Direct UUID in texInfo
        val directUuidStr = texInfo.optString("texture_id", texInfo.optString("uuid", ""))
        val parsedDirect = parseUuid(directUuidStr)
        if (parsedDirect != null) {
            return MaterialDescriptor.TextureRef(parsedDirect, parsedDirect)
        }

        val index = texInfo.optInt("index", -1)
        if (index < 0) return null

        // Look up index in textures -> images
        val textureObj = texturesArray?.optJSONObject(index)
        val sourceIndex = textureObj?.optInt("source", index) ?: index

        val imageObj = imagesArray?.optJSONObject(sourceIndex) ?: texturesArray?.optJSONObject(index)
        val uri = imageObj?.optString("uri", "") ?: ""

        val uuid = parseUuid(uri) ?: return null
        return MaterialDescriptor.TextureRef(uuid, uuid)
    }

    private fun parseUvTransform(obj: JSONObject): MaterialDescriptor.UvTransform {
        val ext = obj.optJSONObject("extensions")?.optJSONObject("KHR_texture_transform") ?: return MaterialDescriptor.UvTransform.IDENTITY

        val scale = ext.optJSONArray("scale")
        val scaleS = scale?.optDouble(0, 1.0)?.toFloat() ?: 1.0f
        val scaleT = scale?.optDouble(1, 1.0)?.toFloat() ?: 1.0f

        val offset = ext.optJSONArray("offset")
        val offsetS = offset?.optDouble(0, 0.0)?.toFloat() ?: 0.0f
        val offsetT = offset?.optDouble(1, 0.0)?.toFloat() ?: 0.0f

        val rotation = ext.optDouble("rotation", 0.0).toFloat()

        return MaterialDescriptor.UvTransform(scaleS, scaleT, offsetS, offsetT, rotation)
    }

    private fun parseUuid(str: String?): UUID? {
        if (str.isNullOrEmpty()) return null
        val clean = str.replace(Regex("^(?:asset|sl|uuid):(?://)?", RegexOption.IGNORE_CASE), "").trim()
        return try {
            UUID.fromString(clean)
        } catch (e: Exception) {
            null
        }
    }
}
