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
    private val pendingFetches = ConcurrentHashMap.newKeySet<UUID>()
    private var texturePrefetcher: ((List<UUID>) -> Unit)? = null

    fun setTexturePrefetcher(prefetcher: (List<UUID>) -> Unit) {
        this.texturePrefetcher = prefetcher
    }

    suspend fun prefetchMaterials(materialIds: List<UUID>): Map<UUID, MaterialDescriptor> {
        val nullUuid = UUID(0L, 0L)
        val uncached = materialIds.filter { id ->
            id != nullUuid && !materialCache.containsKey(id) && pendingFetches.add(id)
        }
        if (uncached.isEmpty()) return emptyMap()

        return try {
            fetchAndParseRenderMaterials(uncached)
        } finally {
            pendingFetches.removeAll(uncached.toSet())
        }
    }

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
        val request = LLSDMap().apply {
            this["object_ids"] = LLSDArray().apply {
                objectIds.forEach { add(LLSDString(it.toString())) }
            }
        }
        val response = capabilityRequester.request(CapabilityManager.CAP_RENDER_MATERIALS, request) as? LLSDMap
            ?: return emptyMap()
        return parseAndCacheMaterials(response)
    }

    fun getMaterialDescriptor(materialId: UUID): MaterialDescriptor? {
        return materialCache[materialId]
    }

    fun cacheMaterialDescriptor(materialId: UUID, descriptor: MaterialDescriptor) {
        materialCache[materialId] = descriptor
        prefetchSubTextures(descriptor)
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
                prefetchSubTextures(descriptor)
            }
        } else {
            // Check direct map entries
            for ((key, value) in response.value) {
                val materialId = try { UUID.fromString(key) } catch (e: Exception) { null } ?: continue
                val matMap = value as? LLSDMap ?: continue
                val descriptor = parseMaterialEntry(matMap)
                materialCache[materialId] = descriptor
                results[materialId] = descriptor
                prefetchSubTextures(descriptor)
            }
        }

        return results
    }

    private fun prefetchSubTextures(descriptor: MaterialDescriptor) {
        val prefetcher = texturePrefetcher ?: return
        val textureIds = mutableListOf<UUID>()

        fun checkRef(ref: MaterialDescriptor.TextureRef?) {
            if (ref != null && ref.isDownloadable && ref.resolvedId != UUID(0L, 0L)) {
                textureIds.add(ref.resolvedId)
            }
        }

        checkRef(descriptor.baseColorTexture)
        checkRef(descriptor.normalTexture)
        checkRef(descriptor.metallicRoughnessTexture)
        checkRef(descriptor.emissiveTexture)
        checkRef(descriptor.occlusionTexture)

        if (textureIds.isNotEmpty()) {
            prefetcher(textureIds)
        }
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
