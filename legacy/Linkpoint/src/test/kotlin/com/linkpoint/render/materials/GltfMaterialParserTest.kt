package com.linkpoint.render.materials

import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class GltfMaterialParserTest {

    @Test
    fun testParseFullGltfMaterialJson() {
        val baseColorUuid = UUID.randomUUID()
        val normalUuid = UUID.randomUUID()
        val mrUuid = UUID.randomUUID()
        val emissiveUuid = UUID.randomUUID()
        val occlusionUuid = UUID.randomUUID()

        val json = """
            {
              "materials": [
                {
                  "name": "PBRTestMaterial",
                  "pbrMetallicRoughness": {
                    "baseColorFactor": [0.8, 0.2, 0.5, 1.0],
                    "metallicFactor": 0.75,
                    "roughnessFactor": 0.25,
                    "baseColorTexture": {
                      "index": 0,
                      "extensions": {
                        "KHR_texture_transform": {
                          "scale": [2.0, 2.0],
                          "offset": [0.1, 0.1],
                          "rotation": 0.5
                        }
                      }
                    },
                    "metallicRoughnessTexture": {
                      "index": 1
                    }
                  },
                  "normalTexture": {
                    "index": 2,
                    "scale": 1.5
                  },
                  "emissiveTexture": {
                    "index": 3
                  },
                  "emissiveFactor": [0.5, 0.5, 0.0],
                  "occlusionTexture": {
                    "index": 4,
                    "strength": 0.8
                  },
                  "alphaMode": "MASK",
                  "alphaCutoff": 0.6,
                  "doubleSided": true
                }
              ],
              "textures": [
                { "source": 0 },
                { "source": 1 },
                { "source": 2 },
                { "source": 3 },
                { "source": 4 }
              ],
              "images": [
                { "uri": "uuid:$baseColorUuid" },
                { "uri": "uuid:$mrUuid" },
                { "uri": "uuid:$normalUuid" },
                { "uri": "uuid:$emissiveUuid" },
                { "uri": "uuid:$occlusionUuid" }
              ]
            }
        """.trimIndent()

        val descriptor = GltfMaterialParser.parseJson(json)

        assertNotNull(descriptor)
        assertEquals(0.8f, descriptor.baseColor.x, 0.001f)
        assertEquals(0.2f, descriptor.baseColor.y, 0.001f)
        assertEquals(0.5f, descriptor.baseColor.z, 0.001f)
        assertEquals(1.0f, descriptor.baseColor.w, 0.001f)

        assertEquals(0.75f, descriptor.metallicFactor, 0.001f)
        assertEquals(0.25f, descriptor.roughnessFactor, 0.001f)

        assertNotNull(descriptor.baseColorTexture)
        assertEquals(baseColorUuid, descriptor.baseColorTexture?.resolvedId)

        assertNotNull(descriptor.metallicRoughnessTexture)
        assertEquals(mrUuid, descriptor.metallicRoughnessTexture?.resolvedId)

        assertNotNull(descriptor.normalTexture)
        assertEquals(normalUuid, descriptor.normalTexture?.resolvedId)
        assertEquals(1.5f, descriptor.normalScale, 0.001f)

        assertNotNull(descriptor.emissiveTexture)
        assertEquals(emissiveUuid, descriptor.emissiveTexture?.resolvedId)
        assertEquals(0.5f, descriptor.emissiveFactor.x, 0.001f)
        assertEquals(0.5f, descriptor.emissiveFactor.y, 0.001f)

        assertNotNull(descriptor.occlusionTexture)
        assertEquals(occlusionUuid, descriptor.occlusionTexture?.resolvedId)
        assertEquals(0.8f, descriptor.occlusionFactor, 0.001f)

        assertEquals(MaterialDescriptor.AlphaMode.MASK, descriptor.alphaMode)
        assertEquals(0.6f, descriptor.alphaCutoff, 0.001f)
        assertTrue(descriptor.doubleSided)

        assertEquals(2.0f, descriptor.uvTransform.scaleS, 0.001f)
        assertEquals(2.0f, descriptor.uvTransform.scaleT, 0.001f)
        assertEquals(0.1f, descriptor.uvTransform.offsetS, 0.001f)
        assertEquals(0.5f, descriptor.uvTransform.rotation, 0.001f)
    }

    @Test
    fun testParseLlsdMaterialEnvelope() {
        val baseColorUuid = UUID.randomUUID()
        val normalUuid = UUID.randomUUID()

        val llsd = LLSDMap().apply {
            this["gltf_json"] = LLSDString("""
                {
                  "materials": [
                    {
                      "pbrMetallicRoughness": {
                        "baseColorTexture": { "index": 0 }
                      },
                      "normalTexture": { "index": 1 }
                    }
                  ],
                  "images": [
                    { "uri": "$baseColorUuid" },
                    { "uri": "$normalUuid" }
                  ]
                }
            """.trimIndent())
        }

        val descriptor = GltfMaterialParser.parseLlsd(llsd)
        assertNotNull(descriptor.baseColorTexture)
        assertEquals(baseColorUuid, descriptor.baseColorTexture?.resolvedId)

        assertNotNull(descriptor.normalTexture)
        assertEquals(normalUuid, descriptor.normalTexture?.resolvedId)
    }

    @Test
    fun testMissingTextureChannelsFallbackToDefaults() {
        val json = """
            {
              "materials": [
                {
                  "name": "MinimalMaterial",
                  "pbrMetallicRoughness": {
                    "metallicFactor": 0.0,
                    "roughnessFactor": 1.0
                  }
                }
              ]
            }
        """.trimIndent()

        val descriptor = GltfMaterialParser.parseJson(json)
        assertNull(descriptor.baseColorTexture)
        assertNull(descriptor.normalTexture)
        assertNull(descriptor.metallicRoughnessTexture)
        assertNull(descriptor.emissiveTexture)
        assertNull(descriptor.occlusionTexture)

        assertEquals(0.0f, descriptor.metallicFactor, 0.001f)
        assertEquals(1.0f, descriptor.roughnessFactor, 0.001f)
    }

    @Test
    fun testParseLlsdTexturesSubMapAndFlatAliases() {
        val baseColorUuid = UUID.randomUUID()
        val normalUuid = UUID.randomUUID()
        val mrUuid = UUID.randomUUID()
        val emissiveUuid = UUID.randomUUID()
        val occlusionUuid = UUID.randomUUID()

        val llsd = LLSDMap().apply {
            this["BaseColor"] = LLSDArray().apply {
                add(LLSDReal(0.5))
                add(LLSDReal(0.6))
                add(LLSDReal(0.7))
                add(LLSDReal(0.9))
            }
            this["Metallic"] = LLSDReal(0.4)
            this["Roughness"] = LLSDReal(0.6)
            this["Textures"] = LLSDMap().apply {
                this["BaseColor"] = LLSDUUID(baseColorUuid)
                this["Normal"] = LLSDString(normalUuid.toString())
                this["MetallicRoughness"] = LLSDUUID(mrUuid)
                this["Emissive"] = LLSDUUID(emissiveUuid)
                this["Occlusion"] = LLSDUUID(occlusionUuid)
            }
        }

        val descriptor = GltfMaterialParser.parseLlsd(llsd)

        assertEquals(0.5f, descriptor.baseColor.x, 0.001f)
        assertEquals(0.6f, descriptor.baseColor.y, 0.001f)
        assertEquals(0.7f, descriptor.baseColor.z, 0.001f)
        assertEquals(0.9f, descriptor.baseColor.w, 0.001f)

        assertEquals(0.4f, descriptor.metallicFactor, 0.001f)
        assertEquals(0.6f, descriptor.roughnessFactor, 0.001f)

        assertNotNull(descriptor.baseColorTexture)
        assertEquals(baseColorUuid, descriptor.baseColorTexture?.resolvedId)

        assertNotNull(descriptor.normalTexture)
        assertEquals(normalUuid, descriptor.normalTexture?.resolvedId)

        assertNotNull(descriptor.metallicRoughnessTexture)
        assertEquals(mrUuid, descriptor.metallicRoughnessTexture?.resolvedId)

        assertNotNull(descriptor.emissiveTexture)
        assertEquals(emissiveUuid, descriptor.emissiveTexture?.resolvedId)

        assertNotNull(descriptor.occlusionTexture)
        assertEquals(occlusionUuid, descriptor.occlusionTexture?.resolvedId)
    }

    @Test
    fun testPbrMaterialDecoderDelegation() {
        val baseUuid = UUID.randomUUID()
        val llsd = LLSDMap().apply {
            this["base_color_texture"] = LLSDUUID(baseUuid)
            this["metallic"] = LLSDReal(0.7)
            this["roughness"] = LLSDReal(0.2)
        }

        @Suppress("DEPRECATION")
        val descriptor = PbrMaterialDecoder.decodeFromLLSD(llsd)

        assertEquals(0.7f, descriptor.metallicFactor, 0.001f)
        assertEquals(0.2f, descriptor.roughnessFactor, 0.001f)
        assertNotNull(descriptor.baseColorTexture)
        assertEquals(baseUuid, descriptor.baseColorTexture?.resolvedId)
    }
}
