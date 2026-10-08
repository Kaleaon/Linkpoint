package com.linkpoint.world

import com.linkpoint.network.core.NetworkSessionManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@OptIn(ExperimentalCoroutinesApi::class)
class RegionCrossingManagerTest {

    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var capabilityManager: CapabilityManager
    private lateinit var networkSessionManager: NetworkSessionManager
    private lateinit var regionCrossingManager: RegionCrossingManager

    private val originRegion = RegionInfo(
        handle = 1000L,
        name = "OriginRegion",
        simIP = "192.168.1.10",
        simPort = 13000,
        seedCapability = "https://sim1.sl.net/seed"
    )

    @Before
    fun setUp() {
        udpConnection = mock()
        capabilityManager = mock()
        networkSessionManager = mock()

        // Default origin circuit setup
        whenever(udpConnection.getSimIP()).thenReturn("192.168.1.10")
        whenever(udpConnection.getSimPort()).thenReturn(13000)
        whenever(udpConnection.getCircuitCode()).thenReturn(10001)
        whenever(capabilityManager.getSeedCapability()).thenReturn("https://sim1.sl.net/seed")
        whenever(capabilityManager.getCapabilitiesSnapshot()).thenReturn(mapOf("EventQueueGet" to "https://sim1.sl.net/eqg"))

        regionCrossingManager = RegionCrossingManager(
            udpConnection = udpConnection,
            capabilityManager = capabilityManager,
            networkSessionManager = networkSessionManager
        )
        regionCrossingManager.setCurrentRegion(originRegion)
    }

    @Test
    fun handleRegionCrossing_successfulTwoPhaseHandoff() = runTest {
        // Destination handshake succeeds
        whenever(udpConnection.connect()).thenReturn(true)
        whenever(capabilityManager.initialize(eq("https://sim2.sl.net/seed"))).thenReturn(true)

        val result = regionCrossingManager.handleRegionCrossing(
            newSimIP = "192.168.1.20",
            newSimPort = 13001,
            newCircuitCode = 10002,
            seedCapability = "https://sim2.sl.net/seed",
            regionHandle = 2000L,
            regionName = "DestinationRegion"
        )

        assertTrue(result)
        assertEquals("DestinationRegion", regionCrossingManager.currentRegion.value?.name)
        assertEquals(2000L, regionCrossingManager.currentRegion.value?.handle)
        assertFalse(regionCrossingManager.isCrossing())

        // Verify phase sequence
        verify(udpConnection).configure("192.168.1.20", 13001, 10002)
        verify(udpConnection).connect()
        verify(capabilityManager).initialize("https://sim2.sl.net/seed")
        verify(udpConnection).startAgentUpdates()
        verify(networkSessionManager, never()).triggerConnectionRecovery()
    }

    @Test
    fun handleRegionCrossing_destinationUdpFailure_rollsBackToOrigin() = runTest {
        // Destination UDP fails (1st connect call), origin UDP succeeds (2nd connect call)
        whenever(udpConnection.connect())
            .thenReturn(false) // Phase 2 destination connect fails
            .thenReturn(true)  // Rollback origin connect succeeds

        whenever(capabilityManager.initialize(eq("https://sim1.sl.net/seed"))).thenReturn(true)

        val events = mutableListOf<RegionCrossingEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            regionCrossingManager.crossingEvents.collect { events.add(it) }
        }

        val result = regionCrossingManager.handleRegionCrossing(
            newSimIP = "192.168.1.20",
            newSimPort = 13001,
            newCircuitCode = 10002,
            seedCapability = "https://sim2.sl.net/seed",
            regionHandle = 2000L,
            regionName = "DestinationRegion"
        )

        assertFalse(result)
        // Current region must remain origin region
        assertEquals("OriginRegion", regionCrossingManager.currentRegion.value?.name)
        assertFalse(regionCrossingManager.isCrossing())

        // Verify rollback re-configured origin socket parameters
        verify(udpConnection).configure("192.168.1.20", 13001, 10002)
        verify(udpConnection).configure("192.168.1.10", 13000, 10001)
        verify(udpConnection, times(2)).connect()
        verify(udpConnection).startAgentUpdates()

        // Session recovery should NOT be triggered on successful origin rollback
        verify(networkSessionManager, never()).triggerConnectionRecovery()

        // Event flow should emit Failed with rollback message
        val failedEvent = events.filterIsInstance<RegionCrossingEvent.Failed>().firstOrNull()
        assertNotNull(failedEvent)
        assertTrue(failedEvent!!.error.contains("rolled back to origin region"))

        job.cancel()
    }

    @Test
    fun handleRegionCrossing_destinationCapabilityFailure_rollsBackToOrigin() = runTest {
        whenever(udpConnection.connect()).thenReturn(true)
        // Destination capability init fails
        whenever(capabilityManager.initialize(eq("https://sim2.sl.net/seed"))).thenReturn(false)
        // Origin capability init on rollback succeeds
        whenever(capabilityManager.initialize(eq("https://sim1.sl.net/seed"))).thenReturn(true)

        val result = regionCrossingManager.handleRegionCrossing(
            newSimIP = "192.168.1.20",
            newSimPort = 13001,
            newCircuitCode = 10002,
            seedCapability = "https://sim2.sl.net/seed",
            regionHandle = 2000L,
            regionName = "DestinationRegion"
        )

        assertFalse(result)
        assertEquals("OriginRegion", regionCrossingManager.currentRegion.value?.name)
        assertFalse(regionCrossingManager.isCrossing())

        // Verify rollback sequence restored origin circuit and capability
        verify(udpConnection).configure("192.168.1.10", 13000, 10001)
        verify(capabilityManager).initialize("https://sim1.sl.net/seed")
        verify(networkSessionManager, never()).triggerConnectionRecovery()
    }

    @Test
    fun handleRegionCrossing_dualCircuitFailure_triggersSessionRecovery() = runTest {
        // Both destination connect and origin rollback connect fail
        whenever(udpConnection.connect()).thenReturn(false)

        val events = mutableListOf<RegionCrossingEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            regionCrossingManager.crossingEvents.collect { events.add(it) }
        }

        val result = regionCrossingManager.handleRegionCrossing(
            newSimIP = "192.168.1.20",
            newSimPort = 13001,
            newCircuitCode = 10002,
            seedCapability = "https://sim2.sl.net/seed",
            regionHandle = 2000L,
            regionName = "DestinationRegion"
        )

        assertFalse(result)
        assertFalse(regionCrossingManager.isCrossing())

        // Dual-circuit failure MUST invoke central network session recovery
        verify(networkSessionManager).triggerConnectionRecovery()

        val failedEvent = events.filterIsInstance<RegionCrossingEvent.Failed>().firstOrNull()
        assertNotNull(failedEvent)
        assertTrue(failedEvent!!.error.contains("Unrecoverable dual-circuit failure"))

        job.cancel()
    }

    @Test
    fun handleRegionCrossing_alreadyCrossing_rejectsDuplicateRequest() = runTest {
        var connectCallCount = 0
        whenever(udpConnection.connect()).thenAnswer {
            connectCallCount++
            assertTrue(regionCrossingManager.isCrossing())
            kotlinx.coroutines.runBlocking {
                val duplicateResult = regionCrossingManager.handleRegionCrossing(
                    newSimIP = "192.168.1.30",
                    newSimPort = 13002,
                    newCircuitCode = 10003,
                    seedCapability = "https://sim3.sl.net/seed",
                    regionHandle = 3000L,
                    regionName = "ThirdRegion"
                )
                assertFalse(duplicateResult)
            }
            true
        }
        whenever(capabilityManager.initialize(any())).thenReturn(true)

        val result = regionCrossingManager.handleRegionCrossing(
            newSimIP = "192.168.1.20",
            newSimPort = 13001,
            newCircuitCode = 10002,
            seedCapability = "https://sim2.sl.net/seed",
            regionHandle = 2000L,
            regionName = "DestinationRegion"
        )

        assertTrue(result)
        assertEquals(1, connectCallCount)
        assertFalse(regionCrossingManager.isCrossing())
    }
}
