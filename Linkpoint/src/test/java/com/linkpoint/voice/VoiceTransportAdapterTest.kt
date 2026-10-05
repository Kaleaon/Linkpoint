package com.linkpoint.voice

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.linkpoint.protocol.GridKind
import com.linkpoint.protocol.capabilities.CapabilityManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VoiceTransportAdapterTest {

    private lateinit var context: Context
    private lateinit var capabilityManager: CapabilityManager
    private lateinit var factory: VoiceTransportAdapterFactory

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        capabilityManager = mock(CapabilityManager::class.java)
        factory = VoiceTransportAdapterFactory(
            capabilityManager = capabilityManager,
            peerConnectionFactoryProvider = { null }
        )
    }

    @Test
    fun testFactoryCreatesSLAdapterForSecondLife() {
        val adapter = factory.createAdapter(GridKind.SECOND_LIFE)
        assertNotNull(adapter)
        assertTrue(adapter is SLWebRTCTransportAdapter)
        assertEquals(GridKind.SECOND_LIFE, adapter.gridKind)
    }

    @Test
    fun testFactoryCreatesOpenSimAdapterForOpenSim() {
        val adapter = factory.createAdapter(GridKind.OPENSIM)
        assertNotNull(adapter)
        assertTrue(adapter is OpenSimVoiceTransportAdapter)
        assertEquals(GridKind.OPENSIM, adapter.gridKind)
    }

    @Test
    fun testSLAdapterIceServerResolutionWithProvisionedAndFallbacks() {
        val adapter = SLWebRTCTransportAdapter(
            capabilityManager = capabilityManager,
            peerConnectionFactoryProvider = { null }
        )

        // 1. Fallback to SL default STUN servers when no provisioned/custom servers given
        val defaultIce = adapter.resolveIceServerSpecs(emptyList(), null)
        assertEquals(VoiceConfig.DEFAULT_SL_STUN_SERVERS, defaultIce)

        // 2. Provisioned capability ICE servers used when provided
        val provisioned = listOf(IceServerSpec(urls = listOf("stun:sim.secondlife.com:3478")))
        val resolvedProvisioned = adapter.resolveIceServerSpecs(provisioned, null)
        assertTrue(resolvedProvisioned.containsAll(provisioned))

        // 3. Custom STUN servers included when provided in VoiceConfig
        val customConfig = VoiceConfig(
            customStunServers = listOf(IceServerSpec(urls = listOf("stun:custom.sl.com:3478")))
        )
        val resolvedCustom = adapter.resolveIceServerSpecs(emptyList(), customConfig)
        assertTrue(resolvedCustom.any { it.urls.contains("stun:custom.sl.com:3478") })
    }

    @Test
    fun testOpenSimAdapterIceServerResolutionPrioritizesCustomStunTurn() {
        val adapter = OpenSimVoiceTransportAdapter(
            capabilityManager = capabilityManager,
            peerConnectionFactoryProvider = { null }
        )

        // 1. Fallback to OpenSim defaults when no custom/provisioned servers specified
        val defaultIce = adapter.resolveIceServerSpecs(emptyList(), null)
        assertEquals(VoiceConfig.DEFAULT_OPENSIM_STUN_SERVERS, defaultIce)

        // 2. Custom TURN and STUN servers prioritized
        val customTurn = IceServerSpec(
            urls = listOf("turn:turn.opensimgrid.org:3478"),
            username = "user",
            credential = "pass"
        )
        val customStun = IceServerSpec(urls = listOf("stun:stun.opensimgrid.org:3478"))
        val config = VoiceConfig(
            customTurnServers = listOf(customTurn),
            customStunServers = listOf(customStun)
        )

        val resolvedCustom = adapter.resolveIceServerSpecs(emptyList(), config)
        assertEquals(2, resolvedCustom.size)
        assertEquals(customTurn, resolvedCustom[0])
        assertEquals(customStun, resolvedCustom[1])
    }

    @Test
    fun testVoiceManagerAdapterSwitchingAndConfigUpdates() {
        val voiceManager = VoiceManager(
            context = context,
            capabilityManager = capabilityManager,
            simulatorFeatures = null,
            initialGridKind = GridKind.SECOND_LIFE
        )

        assertEquals(GridKind.SECOND_LIFE, voiceManager.currentGridKind)
        assertTrue(voiceManager.activeAdapter is SLWebRTCTransportAdapter)

        val customConfig = VoiceConfig(
            customStunServers = listOf(IceServerSpec(urls = listOf("stun:stun.grid.org:3478"))),
            parcelLocalId = 1001
        )

        voiceManager.updateGridKind(GridKind.OPENSIM, customConfig)

        assertEquals(GridKind.OPENSIM, voiceManager.currentGridKind)
        assertEquals(customConfig, voiceManager.currentVoiceConfig)
        assertTrue(voiceManager.activeAdapter is OpenSimVoiceTransportAdapter)

        voiceManager.shutdown()
    }

    @Test
    fun testParcelLocalIdAndFallbackHandling() = runBlocking {
        var capturedParcelId: Int? = null

        val customAdapter = object : VoiceTransportAdapter {
            override val gridKind: GridKind = GridKind.OPENSIM
            override fun resolveIceServers(
                provisionedIceServers: List<IceServerSpec>,
                config: VoiceConfig?
            ) = emptyList<org.webrtc.PeerConnection.IceServer>()

            override fun resolveIceServerSpecs(
                provisionedIceServers: List<IceServerSpec>,
                config: VoiceConfig?
            ) = emptyList<IceServerSpec>()

            override suspend fun connectSpatialVoice(
                parcelLocalId: Int?,
                config: VoiceConfig?
            ): Boolean {
                capturedParcelId = parcelLocalId ?: config?.parcelLocalId
                return false // simulate failure -> fallback to parcel voice
            }

            override fun disconnect() {}
            override fun dispose() {}
        }

        val customFactory = object : VoiceTransportAdapterFactory(
            capabilityManager,
            { null }
        ) {
            override fun createAdapter(
                gridKind: GridKind,
                config: VoiceConfig?
            ): VoiceTransportAdapter = customAdapter
        }

        val voiceManager = VoiceManager(
            context = context,
            capabilityManager = capabilityManager,
            simulatorFeatures = null,
            initialGridKind = GridKind.OPENSIM,
            adapterFactory = customFactory
        )

        // Attempting joinSpatialVoice with parcelLocalId = 42
        val result = voiceManager.joinSpatialVoice(parcelLocalId = 42)

        assertEquals(42, capturedParcelId)

        voiceManager.shutdown()
    }
}
