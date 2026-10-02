package com.linkpoint.render

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.render.materials.GltfMaterialParser
import com.linkpoint.render.materials.MaterialDescriptor
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class RenderMaterialsManager(
    private val capabilityRequester: CapabilityRequester
) {
    private val materialCache = ConcurrentHashMap<UUID, MaterialDescriptor>()

    suspend fun fetchRenderMaterials(objectIds: List<UUID>): LLSDMap? {
        val request = LLSDMap().apply {
            this["object_ids"] = LLSDArray().apply {
                objectIds.forEach { add(LLSDString(it.toString())) }
            }
        }
        val response = capabilityRequester.request(CapabilityManager.CAP_RENDER_MATERIALS, request) as? LLSDMap
        if (response != null) {
            parseAndCacheMaterials(response)
        }
        return response
    }

    suspend fun fetchAndParseRenderMaterials(objectIds: List<UUID>): Map<UUID, MaterialDescriptor> {
        val response = fetchRenderMaterials(objectIds) ?: return emptyMap()
        return parseAndCacheMaterials(response)
    }

    fun getMaterialDescriptor(materialId: UUID): MaterialDescriptor? {
        return materialCache[materialId]
    }

    fun cacheMaterialDescriptor(materialId: UUID, descriptor: MaterialDescriptor) {
        materialCache[materialId] = descriptor
    }

    private fun parseAndCacheMaterials(response: LLSDMap): Map<UUID, MaterialDescriptor> {
        val results = mutableMapOf<UUID, MaterialDescriptor>()

        // The response typically contains a "materials" array or map of material entries
        val materialsData = response.getArray("materials")
        if (materialsData != null) {
            for (i in 0 until materialsData.size) {
                val matMap = materialsData.value.getOrNull(i) as? LLSDMap ?: continue
                val idStr = matMap.getString("ID") ?: matMap.getString("id") ?: continue
                val materialId = try { UUID.fromString(idStr) } catch (e: Exception) { continue }

                val descriptor = parseMaterialEntry(matMap)
                materialCache[materialId] = descriptor
                results[materialId] = descriptor
            }
        } else {
            // Check direct map entries
            for ((key, value) in response.value) {
                val materialId = try { UUID.fromString(key) } catch (e: Exception) { null } ?: continue
                val matMap = value as? LLSDMap ?: continue
                val descriptor = parseMaterialEntry(matMap)
                materialCache[materialId] = descriptor
                results[materialId] = descriptor
            }
        }

        return results
    }

    private fun parseMaterialEntry(matMap: LLSDMap): MaterialDescriptor {
        val jsonStr = matMap.getString("gltf_json") ?: matMap.getString("json")
        return if (jsonStr != null) {
            GltfMaterialParser.parseJson(jsonStr)
        } else {
            GltfMaterialParser.parseLlsd(matMap)
        }
    }
}
