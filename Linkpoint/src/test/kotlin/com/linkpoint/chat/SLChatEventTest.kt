package com.linkpoint.chat

import com.linkpoint.linden.llmessage.IMType
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.MessageEventListener
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class TestUDPConnection : UDPConnectionFixed() {
    val sentPackets = mutableListOf<Pair<Int, ByteArray>>()

    override fun sendPacket(
        messageId: Int,
        payload: ByteArray,
        reliable: Boolean,
        zerocoded: Boolean,
        listener: MessageEventListener?
    ): Int {
        sentPackets.add(messageId to payload)
        return 1
    }
}

class SLChatEventTest {

    private val agentId = UUID.randomUUID()
    private val udpConn = TestUDPConnection().apply {
        configure("127.0.0.1", 13000, 12345)
        setSessionInfo(UUID.randomUUID(), agentId)
    }
    private val capManager = CapabilityManager()
    private val imManager = IMManager(udpConn, capManager, agentId)

    @Test
    fun `test decode group invitation with valid binary bucket fee`() {
        val sessionId = UUID.randomUUID()
        val senderId = UUID.randomUUID()
        val feeBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(50).array()

        val event = imManager.decodeToSLChatEvent(
            fromAgentId = senderId,
            fromName = "Group Owner",
            message = "Join my group",
            sessionId = sessionId,
            dialogType = IMType.GROUP_INVITATION,
            timestamp = 1000L,
            binaryBucket = feeBuffer,
            isOutgoing = false
        )

        assertTrue(event is SLChatEvent.GroupInvitation)
        val groupEvent = event as SLChatEvent.GroupInvitation
        assertEquals(50, groupEvent.joinFee)
        assertEquals(senderId, groupEvent.groupId)
        assertEquals("Join my group", groupEvent.message)
    }

    @Test
    fun `test decode group invitation with corrupt or empty binary bucket falls back safely`() {
        val sessionId = UUID.randomUUID()
        val senderId = UUID.randomUUID()

        // Short/corrupt binary bucket (< 4 bytes)
        val shortBucket = byteArrayOf(1, 2)
        val event = imManager.decodeToSLChatEvent(
            fromAgentId = senderId,
            fromName = "Group Owner",
            message = "Join my group",
            sessionId = sessionId,
            dialogType = IMType.GROUP_INVITATION,
            timestamp = 1000L,
            binaryBucket = shortBucket,
            isOutgoing = false
        )

        assertTrue(event is SLChatEvent.GroupInvitation)
        val groupEvent = event as SLChatEvent.GroupInvitation
        assertEquals(0, groupEvent.joinFee)
    }

    @Test
    fun `test handleIncomingIM emits typed SLChatEvent through messageFlow`() = runBlocking {
        val sessionId = UUID.randomUUID()
        val senderId = UUID.randomUUID()

        imManager.handleIncomingIM(
            fromAgentId = senderId,
            fromName = "Friend Avatar",
            message = "Wants to be friends",
            sessionId = sessionId,
            dialogType = IMType.FRIENDSHIP_OFFERED,
            timestamp = 2000L
        )

        val emittedEvent = imManager.messageFlow.first()
        assertTrue("Emitted event should be FriendshipOffer", emittedEvent is SLChatEvent.FriendshipOffer)
        val offer = emittedEvent as SLChatEvent.FriendshipOffer
        assertEquals(senderId, offer.fromAgentId)
        assertEquals("Friend Avatar", offer.fromName)
        assertEquals(CardActionState.PENDING, offer.actionState)
    }

    @Test
    fun `test respondToFriendshipOffer accept sends ImprovedInstantMessage with dialog code 39`() {
        val sessionId = UUID.randomUUID()
        val senderId = UUID.randomUUID()

        val offer = SLChatEvent.FriendshipOffer(
            sessionId = sessionId,
            fromAgentId = senderId,
            fromName = "Friend Avatar",
            message = "Wants to be friends",
            dialogType = IMType.FRIENDSHIP_OFFERED,
            timestamp = 2000L
        )

        imManager.respondToFriendshipOffer(offer, accept = true)

        assertEquals(CardActionState.ACCEPTED, offer.actionState)
        assertEquals(1, udpConn.sentPackets.size)

        val (msgId, payload) = udpConn.sentPackets[0]
        assertEquals(MessageIdRegistry.IMPROVED_INSTANT_MESSAGE, msgId)

        // Read dialog byte from payload
        // In ImprovedInstantMessage, Dialog byte is at offset 81:
        // AgentData.AgentID (16) + SessionID (16) + FromGroup (1) + ToAgentID (16) + ParentEstateID (4) + RegionID (16) + Position (12) + Offline (1) = 82, Dialog at offset 82
        val dialogCode = payload[82].toInt() and 0xFF
        assertEquals(39, dialogCode)
    }

    @Test
    fun `test respondToGroupInvitation decline sends ImprovedInstantMessage with dialog code 36`() {
        val sessionId = UUID.randomUUID()
        val groupId = UUID.randomUUID()

        val invite = SLChatEvent.GroupInvitation(
            sessionId = sessionId,
            fromAgentId = groupId,
            fromName = "Group Host",
            message = "Join group",
            dialogType = IMType.GROUP_INVITATION,
            timestamp = 2000L,
            groupId = groupId,
            joinFee = 100
        )

        imManager.respondToGroupInvitation(invite, accept = false)

        assertEquals(CardActionState.DECLINED, invite.actionState)
        assertEquals(1, udpConn.sentPackets.size)

        val (msgId, payload) = udpConn.sentPackets[0]
        assertEquals(MessageIdRegistry.IMPROVED_INSTANT_MESSAGE, msgId)

        val dialogCode = payload[82].toInt() and 0xFF
        assertEquals(36, dialogCode)
    }

    @Test
    fun `test response actions are idempotent`() {
        val sessionId = UUID.randomUUID()
        val senderId = UUID.randomUUID()

        val offer = SLChatEvent.FriendshipOffer(
            sessionId = sessionId,
            fromAgentId = senderId,
            fromName = "Friend Avatar",
            message = "Wants to be friends",
            dialogType = IMType.FRIENDSHIP_OFFERED,
            timestamp = 2000L
        )

        // First response
        imManager.respondToFriendshipOffer(offer, accept = true)
        assertEquals(1, udpConn.sentPackets.size)
        assertEquals(CardActionState.ACCEPTED, offer.actionState)

        // Second response attempt (duplicate tap)
        imManager.respondToFriendshipOffer(offer, accept = true)
        // Packet count should still be 1!
        assertEquals(1, udpConn.sentPackets.size)
    }
}
