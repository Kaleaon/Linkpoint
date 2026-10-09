package com.linkpoint.chat

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.types.getUUID
import com.linkpoint.teleport.TeleportLure
import com.linkpoint.linden.llmessage.IMType
import com.linkpoint.teleport.TeleportManager
import com.linkpoint.teleport.TeleportResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class IMManagerTest {

    private val agentId = UUID.fromString("11111111-1111-1111-1111-111111111111")
    private val sessionId = UUID.fromString("22222222-2222-2222-2222-222222222222")
    private val senderId = UUID.fromString("33333333-3333-3333-3333-333333333333")
    private val lureId = UUID.fromString("44444444-4444-4444-4444-444444444444")

    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var capabilityManager: CapabilityManager
    private lateinit var mockTeleportManager: TeleportManager
    private lateinit var imManager: IMManager

    @Before
    fun setUp() {
        udpConnection = mock()
        whenever(udpConnection.getSessionId()).thenReturn(sessionId)
        whenever(udpConnection.getCircuitCode()).thenReturn(12345)

        capabilityManager = CapabilityManager()
        imManager = IMManager(udpConnection, capabilityManager, agentId)

        mockTeleportManager = mock()
        imManager.teleportManager = mockTeleportManager
    }

    @Test
    fun testRespondToTeleportLureAcceptSendsUdpMessageAndDelegatesToTeleportManager() = runTest {
        whenever(mockTeleportManager.acceptTeleportLure(any())).thenReturn(TeleportResult.Pending)

        val event = SLChatEvent.TeleportLure(
            id = UUID.randomUUID(),
            sessionId = lureId,
            fromAgentId = senderId,
            fromName = "Sender Resident",
            message = "Come join me!",
            dialogType = IMType.LURE_USER,
            timestamp = System.currentTimeMillis(),
            isOutgoing = false,
            lureId = lureId,
            regionName = "Aharon"
        )

        imManager.respondToTeleportLure(event, accept = true)

        assertEquals(CardActionState.ACCEPTED, event.actionState)

        // Verify ImprovedInstantMessage packet sent with IM_LURE_ACCEPTED (dialog 23)
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

        // AgentData
        assertEquals(agentId, buf.getUUID())
        assertEquals(sessionId, buf.getUUID())

        // MessageBlock
        buf.get() // fromGroup
        assertEquals(senderId, buf.getUUID()) // toAgentId
        buf.int // parentEstateId
        buf.getUUID() // regionId
        buf.float; buf.float; buf.float // position
        buf.get() // offline

        val dialog = buf.get().toInt()
        assertEquals(IMType.LURE_ACCEPTED.value, dialog)

        val expectedLure = TeleportLure(
            lureId = lureId,
            senderId = senderId,
            senderName = "Sender Resident",
            regionName = "Aharon",
            message = "Come join me!",
            timestamp = event.timestamp
        )

        val lureCaptor = argumentCaptor<TeleportLure>()
        verify(mockTeleportManager).acceptTeleportLure(lureCaptor.capture())
        val capturedLure = lureCaptor.firstValue

        assertEquals(expectedLure.lureId, capturedLure.lureId)
        assertEquals(expectedLure.senderId, capturedLure.senderId)
        assertEquals(expectedLure.senderName, capturedLure.senderName)
    }

    @Test
    fun testRespondToTeleportLureDeclineSendsDeclineUdpMessage() {
        val event = SLChatEvent.TeleportLure(
            id = UUID.randomUUID(),
            sessionId = lureId,
            fromAgentId = senderId,
            fromName = "Sender Resident",
            message = "Come join me!",
            dialogType = IMType.LURE_USER,
            timestamp = System.currentTimeMillis(),
            isOutgoing = false,
            lureId = lureId,
            regionName = "Aharon"
        )

        imManager.respondToTeleportLure(event, accept = false)

        assertEquals(CardActionState.DECLINED, event.actionState)

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

        // AgentData
        assertEquals(agentId, buf.getUUID())
        assertEquals(sessionId, buf.getUUID())

        // Skip to dialog
        buf.get() // fromGroup
        assertEquals(senderId, buf.getUUID())
        buf.int; buf.getUUID(); buf.float; buf.float; buf.float; buf.get()

        val dialog = buf.get().toInt()
        assertEquals(IMType.LURE_DECLINED.value, dialog)
    }
}
