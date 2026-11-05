package com.linkpoint.avatar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.assets.TextureManager
import com.linkpoint.inventory.WearableAssetTelemetry
import com.linkpoint.inventory.WearableLoadFailureReason
import com.linkpoint.protocol.capabilities.CapabilityManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AppearanceManagerTest {

    private lateinit var context: Context
    private lateinit var baker: AvatarBaker

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val cache = com.linkpoint.assets.AssetCache(context)
        val textureManager = TextureManager(context, cache)
        val capManager = CapabilityManager()
        baker = AvatarBaker(context, textureManager, capManager)
        WearableAssetTelemetry.reset()
    }

    @Test
    fun `sendAppearanceUpdate defers when base skin bakes missing`() = runBlocking {
        val appearanceManager = AppearanceManager(avatarBaker = baker)

        appearanceManager.sendAppearanceUpdate()

        assertEquals(1, WearableAssetTelemetry.count(WearableLoadFailureReason.APPEARANCE_DEFERRED))
        val diag = appearanceManager.getDiagnostics()
        assertEquals(0, diag.updatesSent)
        assertTrue(diag.lastUpdateError?.contains("base skin channels missing") == true)
    }

    @Test
    fun `sendAppearanceUpdate proceeds when base skin bakes present`() = runBlocking {
        baker.setExternalBakedTextures(
            mapOf(
                AvatarBaker.BAKE_HEAD to UUID.randomUUID(),
                AvatarBaker.BAKE_UPPER to UUID.randomUUID(),
                AvatarBaker.BAKE_LOWER to UUID.randomUUID()
            )
        )
        val appearanceManager = AppearanceManager(avatarBaker = baker)

        appearanceManager.sendAppearanceUpdate()

        assertEquals(0, WearableAssetTelemetry.count(WearableLoadFailureReason.APPEARANCE_DEFERRED))
        val diag = appearanceManager.getDiagnostics()
        assertEquals(1, diag.updatesSent)
    }
}
