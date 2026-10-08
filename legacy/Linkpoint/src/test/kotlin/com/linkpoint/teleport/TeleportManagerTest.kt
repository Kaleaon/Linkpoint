package com.linkpoint.teleport

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.types.getUUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class TeleportManagerTest {

    private val agentId = UUID.fromString("11111111-1111-1111-1111-111111111111")
    private val sessionId = UUID.fromString("22222222-2222-2222-2222-222222222222")
    private val senderId = UUID.fromString("33333333-3333-3333-3333-333333333333")
    private val lureId = UUID.fromString("44444444-4444-4444-4444-444444444444")

    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var capabilityManager: CapabilityManager
    private lateinit var teleportManager: TeleportManager

    @Before
    fun setUp() {
        udpConnection = mock()
        whenever(udpConnection.getSessionId()).thenReturn(sessionId)
        capabilityManager = CapabilityManager()
        teleportManager = TeleportManager(udpConnection, capabilityManager, agentId)
    }

    @Test
    fun testDeclineTeleportLureTransmitsImprovedInstantMessageWithDeclineDialog() {
        val lure = TeleportLure(
            lureId = lureId,
            senderId = senderId,
            senderName = "Sender Resident",
            regionName = "Test Region",
            message = "Join me!"
        )

        teleportManager.declineTeleportLure(lure)

        val payloadCaptor = argumentCaptor<ByteArray>()
        verify(udpConnection).sendPacket(
            eq(MessageIdRegistry.IMPROVED_INSTANT_MESSAGE),
            payloadCaptor.capture(),
            eq(true),
            eq(false),
            anyOrNull()
        )

        val payload = payloadCaptor.firstValue
        val buf = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData header
        assertEquals(agentId, buf.getUUID())
        assertEquals(sessionId, buf.getUUID())

        // MessageBlock
        val fromGroup = buf.get()
        assertEquals(0.toByte(), fromGroup)

        val toAgentId = buf.getUUID()
        assertEquals(senderId, toAgentId)

        buf.int // parentEstateId
        buf.getUUID() // regionId
        buf.float; buf.float; buf.float // position
        buf.get() // offline

        val dialog = buf.get().toInt()
        assertEquals(24, dialog) // IM_LURE_DECLINED

        val parsedLureId = buf.getUUID()
        assertEquals(lureId, parsedLureId)
    }

    @Test
    fun testDeclineTeleportLureHandlesUdpExceptionGracefully() {
        whenever(udpConnection.sendPacket(any(), any(), any(), any(), anyOrNull()))
            .thenThrow(RuntimeException("Socket disconnected"))

        val lure = TeleportLure(
            lureId = lureId,
            senderId = senderId,
            senderName = "Sender Resident",
            regionName = "Test Region",
            message = "Join me!"
        )

        // Should not throw exception
        teleportManager.declineTeleportLure(lure)
    }

    @Test
    fun testSendTeleportLureTransmitsStartLurePacket() = runBlocking {
        val result = teleportManager.sendTeleportLure(senderId, "Join me!")

        assertTrue(result)

        val payloadCaptor = argumentCaptor<ByteArray>()
        verify(udpConnection).sendPacket(
            eq(MessageIdRegistry.START_LURE),
            payloadCaptor.capture(),
            eq(true),
            eq(false),
            anyOrNull()
        )

        val payload = payloadCaptor.firstValue
        val buf = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData
        assertEquals(agentId, buf.getUUID())
        assertEquals(sessionId, buf.getUUID())

        // Info: LureType (U8 = 0)
        val lureType = buf.get().toInt()
        assertEquals(0, lureType)
    }

    @Test
    fun testAcceptTeleportLureTransmitsTeleportLureRequestPacket() = runBlocking {
        val lure = TeleportLure(
            lureId = lureId,
            senderId = senderId,
            senderName = "Sender Resident",
            regionName = "Test Region",
            message = "Join me!"
        )

        val result = teleportManager.acceptTeleportLure(lure)

        assertEquals(TeleportResult.Pending, result)

        val payloadCaptor = argumentCaptor<ByteArray>()
        verify(udpConnection).sendPacket(
            eq(MessageIdRegistry.TELEPORT_LURE_REQUEST),
            payloadCaptor.capture(),
            eq(true),
            eq(false),
            anyOrNull()
        )

        val payload = payloadCaptor.firstValue
        val buf = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData
        assertEquals(agentId, buf.getUUID())
        assertEquals(sessionId, buf.getUUID())

        // Info: SenderID, LureID
        assertEquals(senderId, buf.getUUID())
        assertEquals(lureId, buf.getUUID())
    }
}
