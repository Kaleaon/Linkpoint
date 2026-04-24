package com.linkpoint.inventory

import com.linkpoint.avatar.WearableType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class OutfitManagerTest {

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
    }

    @Test
    fun affectedBakeChannelsAreTargeted() {
        val skinChannels = affectedBakeChannelsForType(WearableType.SKIN)
        val shirtChannels = affectedBakeChannelsForType(WearableType.SHIRT)
        assertTrue(skinChannels.size > shirtChannels.size)
        assertTrue(shirtChannels.isNotEmpty())
    }
}
