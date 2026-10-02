package com.linkpoint.render

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.FakeCapabilityRequester
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID

class RenderMaterialsManagerTest {

    @Test
    fun testFetchAndCacheRenderMaterials() = runBlocking {
        val matId = UUID.randomUUID()
        val fakeRequester = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_RENDER_MATERIALS, LLSDMap().apply {
                this["materials"] = LLSDArray().apply {
                    add(LLSDMap().apply {
                        this["id"] = LLSDString(matId.toString())
                        this["gltf_json"] = LLSDString("""
                            {
                              "materials": [
                                {
                                  "pbrMetallicRoughness": {
                                    "metallicFactor": 0.5,
                                    "roughnessFactor": 0.2
                                  }
                                }
                              ]
                            }
                        """.trimIndent())
                    })
                }
            })
        }

        val manager = RenderMaterialsManager(fakeRequester)
        val results = manager.fetchAndParseRenderMaterials(listOf(matId))

        assertNotNull(results[matId])
        assertEquals(0.5f, results[matId]?.metallicFactor ?: 0f, 0.001f)
        assertEquals(0.2f, results[matId]?.roughnessFactor ?: 0f, 0.001f)

        // Verify cached lookup
        val cached = manager.getMaterialDescriptor(matId)
        assertNotNull(cached)
        assertEquals(0.5f, cached?.metallicFactor ?: 0f, 0.001f)
    }
}
