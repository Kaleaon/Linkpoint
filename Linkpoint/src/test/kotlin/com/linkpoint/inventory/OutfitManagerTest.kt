package com.linkpoint.inventory

import com.linkpoint.avatar.WearableType
import com.linkpoint.avatar.AvatarBaker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.UUID

class OutfitManagerTest {
    companion object {
        private const val WEARABLE_PARSE_SUCCESS_THRESHOLD = 0.95f
    }

    @Test
    fun validWearableParsing() {
        val assetId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val textureId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        val raw = """
            LLWearable version 22
            type 4
            parameters 3
            31 0.75
            80 0.2
            81 0.4
            textures 1
            1 $textureId
        """.trimIndent().toByteArray()

        val parsed = WearableAssetParser.parse(raw, WearableType.SHAPE, assetId)

        assertNotNull(parsed)
        parsed!!
        assertEquals(WearableType.SHIRT, parsed.type)
        assertEquals(assetId, parsed.assetId)
        assertEquals(0.75f, parsed.params[31] ?: -1f, 0.0001f)
        assertEquals(textureId, parsed.textures[1])
        assertNull("Tint requires RGB params 80/81/82", parsed.tintColor)
    }

    @Test
    fun malformedWearableFallback() {
        val assetId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc")
        val malformedRaw = """
            LLWearable version 22
            type nope
            parameters 2
            80 0.1
        """.trimIndent().toByteArray()

        val parsed = WearableAssetParser.parse(malformedRaw, WearableType.HAIR, assetId)
        assertNull(parsed)
    }

    @Test
    fun replaceVsMultiWearBehaviorConsistency() {
        val mutableState = mutableMapOf<WearableType, UUID>()
        val first = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val second = UUID.fromString("22222222-2222-2222-2222-222222222222")

        applyWearableSelection(mutableState, WearableType.SHIRT, first, _replace = true)
        assertEquals(first, mutableState[WearableType.SHIRT])

        applyWearableSelection(mutableState, WearableType.SHIRT, second, _replace = true)
        assertEquals(second, mutableState[WearableType.SHIRT])

        applyWearableSelection(mutableState, WearableType.SHIRT, first, _replace = false)
        assertEquals(
            "Current implementation keeps one active wearable per type regardless of replace flag",
            first,
            mutableState[WearableType.SHIRT]
        )

        applyWearableSelection(mutableState, WearableType.PANTS, second, _replace = false)
        assertEquals("Multi-wear add should not evict other wearable types", first, mutableState[WearableType.SHIRT])
        assertEquals(second, mutableState[WearableType.PANTS])
    }

    @Test
    fun parsedOutputIncludesTintParamsAndFiltersNullTextures() {
        val assetId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd")
        val includedTexture = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee")
        val nullTexture = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val raw = """
            LLWearable version 22
            type 13
            parameters 4
            31 0.75
            80 0.1
            81 0.5
            82 1.2
            textures 2
            1 $includedTexture
            2 $nullTexture
        """.trimIndent().toByteArray()

        val parsed = WearableAssetParser.parse(raw, WearableType.SHIRT, assetId)
        assertNotNull(parsed)
        parsed!!
        // Asset declares `type 13` which is ALPHA per the SL wearable
        // type table (GLOVES is 9). The parser correctly trusts the
        // asset's self-declaration over the caller's hint, so the
        // expected type is ALPHA, not GLOVES.
        assertEquals(WearableType.ALPHA, parsed.type)
        assertEquals(4, parsed.params.size)
        assertEquals(0.75f, parsed.params[31] ?: -1f, 0.0001f)
        assertEquals(includedTexture, parsed.textures[1])
        assertFalse("NULL texture UUID entries must be ignored", parsed.textures.containsKey(2))
        assertEquals(intArrayOf(26, 128, 255).toList(), parsed.tintColor?.toList())
    }

    @Test
    fun parseDiagnosticsContainCorruptionReason() {
        val assetId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff")
        val malformedRaw = """
            LLWearable version 22
            textures 1
            bad-index not-a-uuid
        """.trimIndent().toByteArray()

        val result = WearableAssetParser.parseWithDiagnostics(malformedRaw, WearableType.HAIR, assetId)
        assertNull(result.data)
        assertTrue(result.error?.contains("invalid texture index") == true)
    }

    @Test
    fun knownWearableCorpusMeetsParseSuccessThreshold() {
        val corpus = listOf(
            """
            LLWearable version 22
            type 0
            parameters 1
            31 0.40
            textures 0
            """.trimIndent().toByteArray(),
            """
            LLWearable version 22
            type 5
            parameters 2
            80 0.25
            81 0.75
            textures 1
            0 11111111-1111-1111-1111-111111111111
            """.trimIndent().toByteArray(),
            """
            LLWearable version 22
            type 10
            parameters 4
            80 1.0
            81 0.0
            82 0.5
            93 0.2
            textures 2
            2 22222222-2222-2222-2222-222222222222
            3 33333333-3333-3333-3333-333333333333
            """.trimIndent().toByteArray(),
            """
            LLWearable version 22
            textures 1
            1 44444444-4444-4444-4444-444444444444
            """.trimIndent().toByteArray()
        )
        val successCount = corpus.count { bytes ->
            WearableAssetParser.parseWithDiagnostics(
                raw = bytes,
                defaultType = WearableType.SHIRT,
                assetId = UUID.randomUUID()
            ).data != null
        }
        val successRate = successCount.toFloat() / corpus.size
        assertTrue(
            "Expected parse success rate >= $WEARABLE_PARSE_SUCCESS_THRESHOLD, got $successRate",
            successRate >= WEARABLE_PARSE_SUCCESS_THRESHOLD
        )
    }

    @Test
    fun telemetryCountersTrackTypedFailureReasons() {
        WearableAssetTelemetry.reset()

        WearableAssetTelemetry.recordFallback(WearableLoadFailureReason.MISSING_FETCHER)
        WearableAssetTelemetry.recordFallback(WearableLoadFailureReason.MISSING_ASSET_BYTES)
        WearableAssetTelemetry.recordFallback(WearableLoadFailureReason.CORRUPT_ASSET_PAYLOAD)
        WearableAssetTelemetry.recordFallback(WearableLoadFailureReason.FETCH_EXCEPTION)
        WearableAssetTelemetry.recordFallback(WearableLoadFailureReason.CORRUPT_ASSET_PAYLOAD)

        assertEquals(1, WearableAssetTelemetry.count(WearableLoadFailureReason.MISSING_FETCHER))
        assertEquals(1, WearableAssetTelemetry.count(WearableLoadFailureReason.MISSING_ASSET_BYTES))
        assertEquals(2, WearableAssetTelemetry.count(WearableLoadFailureReason.CORRUPT_ASSET_PAYLOAD))
        assertEquals(1, WearableAssetTelemetry.count(WearableLoadFailureReason.FETCH_EXCEPTION))
    }

    @Test
    fun affectedBakeChannelsMatchExpectedOutputs() {
        assertEquals(
            setOf(AvatarBaker.BAKE_HAIR),
            affectedBakeChannelsForType(WearableType.HAIR)
        )
        assertEquals(
            setOf(AvatarBaker.BAKE_EYES),
            affectedBakeChannelsForType(WearableType.EYES)
        )
        assertEquals(
            setOf(AvatarBaker.BAKE_UPPER, AvatarBaker.BAKE_AUX2),
            affectedBakeChannelsForType(WearableType.SHIRT)
        )
        assertEquals(
            setOf(AvatarBaker.BAKE_LOWER, AvatarBaker.BAKE_AUX3),
            affectedBakeChannelsForType(WearableType.PANTS)
        )
        assertTrue(affectedBakeChannelsForType(WearableType.SKIN).size >= 8)
    }
}
