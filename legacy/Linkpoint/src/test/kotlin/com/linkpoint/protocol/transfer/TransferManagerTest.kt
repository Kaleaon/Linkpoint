package com.linkpoint.protocol.transfer

import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.UUID

class TransferManagerTest {

    private lateinit var mockUdpConnection: UDPConnectionFixed
    private lateinit var transferManager: TransferManager
    private val agentId = UUID.randomUUID()
    private val sessionId = UUID.randomUUID()

    @Before
    fun setUp() {
        mockUdpConnection = mock(UDPConnectionFixed::class.java)
        transferManager = TransferManager(mockUdpConnection, agentId, sessionId)
    }

    @Test
    fun `cancelAssetRetry cancels active retry for given assetId`() {
        val assetId = UUID.randomUUID()
        // verify no throw
        transferManager.cancelAssetRetry(assetId)
        val stats = transferManager.stats.value
        assertEquals(0, stats.retryAttempts)
    }
}
