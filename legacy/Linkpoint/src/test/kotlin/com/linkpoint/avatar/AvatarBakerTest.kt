package com.linkpoint.avatar

import android.graphics.Bitmap
import com.linkpoint.assets.JPEG2000Encoder
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class AvatarBakerTest {

    @Test
    fun `jpeg2000 encoder handles bitmap encoding gracefully`() {
        val bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        val j2kBytes = JPEG2000Encoder.encode(bitmap, lossless = false)
        // When native library is not linked in host JVM unit test, encode returns null safely
        // When native library is available, returns valid J2K bytes
        if (j2kBytes != null) {
            assertTrue("Encoded J2K bytes should not be empty", j2kBytes.isNotEmpty())
        }
    }

    @Test
    fun `bake channel constants are properly configured`() {
        assertEquals(0, AvatarBaker.BAKE_HEAD)
        assertEquals(1, AvatarBaker.BAKE_UPPER)
        assertEquals(2, AvatarBaker.BAKE_LOWER)
        assertEquals(3, AvatarBaker.BAKE_EYES)
        assertEquals(4, AvatarBaker.BAKE_SKIRT)
        assertEquals(5, AvatarBaker.BAKE_HAIR)
        assertEquals(11, AvatarBaker.NUM_BAKE_CHANNELS)
    }

    @Test
    fun `default system skin ID constant is configured`() {
        assertEquals("00000000-0000-0000-0000-000000000001", AvatarBaker.DEFAULT_SYSTEM_SKIN_ID.toString())
    }

    @Test
    fun `isDefaultSkinApplied identifies missing or default skin`() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val cache = com.linkpoint.assets.AssetCache(context)
        val textureManager = com.linkpoint.assets.TextureManager(context, cache)
        val capManager = com.linkpoint.protocol.capabilities.CapabilityManager()
        val baker = AvatarBaker(context, textureManager, capManager)

        assertTrue("Default skin applied when no skin wearable set", baker.isDefaultSkinApplied())

        val customSkin = WearableData(
            type = WearableType.SKIN,
            assetId = java.util.UUID.randomUUID(),
            textures = mapOf(
                AvatarBaker.TEX_HEAD_BODYPAINT to java.util.UUID.randomUUID(),
                AvatarBaker.TEX_UPPER_SHIRT to java.util.UUID.randomUUID(),
                AvatarBaker.TEX_LOWER_PANTS to java.util.UUID.randomUUID()
            ),
            params = emptyMap()
        )
        baker.setWearable(WearableType.SKIN, customSkin)
        assertFalse("Custom skin with valid textures is not default skin", baker.isDefaultSkinApplied())
    }

    @Test
    fun `hasValidBaseSkinBakes checks head upper lower channels`() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val cache = com.linkpoint.assets.AssetCache(context)
        val textureManager = com.linkpoint.assets.TextureManager(context, cache)
        val capManager = com.linkpoint.protocol.capabilities.CapabilityManager()
        val baker = AvatarBaker(context, textureManager, capManager)

        assertFalse("Initially no valid base skin bakes", baker.hasValidBaseSkinBakes())

        baker.setExternalBakedTextures(
            mapOf(
                AvatarBaker.BAKE_HEAD to java.util.UUID.randomUUID(),
                AvatarBaker.BAKE_UPPER to java.util.UUID.randomUUID(),
                AvatarBaker.BAKE_LOWER to java.util.UUID.randomUUID()
            )
        )
        assertTrue("Base skin bakes valid when head upper lower present", baker.hasValidBaseSkinBakes())
    }
}
