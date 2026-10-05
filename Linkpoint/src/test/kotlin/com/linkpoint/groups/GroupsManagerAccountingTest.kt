package com.linkpoint.groups

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.types.getUUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class GroupsManagerAccountingTest {

    @Test
    fun `requestAccountSummary serializes AgentData with agentId, sessionId, then groupId`() = runBlocking {
        val agentId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val sessionId = UUID.fromString("22222222-2222-2222-2222-222222222222")
        val groupId = UUID.fromString("33333333-3333-3333-3333-333333333333")

        val udpConnection: UDPConnectionFixed = mock()
        whenever(udpConnection.getSessionId()).thenReturn(sessionId)

        val capabilityManager = CapabilityManager()
        val manager = GroupsManager(udpConnection, capabilityManager, agentId)

        manager.requestAccountSummary(groupId)

        val payloadCaptor = argumentCaptor<ByteArray>()
        verify(udpConnection).sendPacket(
            eq(MessageIdRegistry.GROUP_ACCOUNT_SUMMARY_REQUEST),
            payloadCaptor.capture(),
            eq(true),
            eq(false),
            org.mockito.kotlin.anyOrNull()
        )

        val payload = payloadCaptor.firstValue
        assertEquals(60, payload.size)

        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData header verification: AgentID (0..15), SessionID (16..31), GroupID (32..47)
        assertEquals(agentId, buffer.getUUID())
        assertEquals(sessionId, buffer.getUUID())
        assertEquals(groupId, buffer.getUUID())

        // MoneyData block verification: RequestID (int), IntervalDays (int), CurrentInterval (int)
        assertEquals(0, buffer.int)
        assertEquals(-1, buffer.int)
        assertEquals(0, buffer.int)
    }

    @Test
    fun `requestAccountDetails serializes AgentData with agentId, sessionId, then groupId`() = runBlocking {
        val agentId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val sessionId = UUID.fromString("22222222-2222-2222-2222-222222222222")
        val groupId = UUID.fromString("33333333-3333-3333-3333-333333333333")

        val udpConnection: UDPConnectionFixed = mock()
        whenever(udpConnection.getSessionId()).thenReturn(sessionId)

        val capabilityManager = CapabilityManager()
        val manager = GroupsManager(udpConnection, capabilityManager, agentId)

        manager.requestAccountDetails(groupId)

        val payloadCaptor = argumentCaptor<ByteArray>()
        verify(udpConnection).sendPacket(
            eq(MessageIdRegistry.GROUP_ACCOUNT_DETAILS_REQUEST),
            payloadCaptor.capture(),
            eq(true),
            eq(false),
            org.mockito.kotlin.anyOrNull()
        )

        val payload = payloadCaptor.firstValue
        assertEquals(60, payload.size)

        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData header verification: AgentID (0..15), SessionID (16..31), GroupID (32..47)
        assertEquals(agentId, buffer.getUUID())
        assertEquals(sessionId, buffer.getUUID())
        assertEquals(groupId, buffer.getUUID())

        // MoneyData block verification: RequestID (int), IntervalDays (int), CurrentInterval (int)
        assertEquals(0, buffer.int)
        assertEquals(-1, buffer.int)
        assertEquals(0, buffer.int)
    }

    @Test
    fun `requestAccountTransactions serializes AgentData with agentId, sessionId, then groupId`() = runBlocking {
        val agentId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val sessionId = UUID.fromString("22222222-2222-2222-2222-222222222222")
        val groupId = UUID.fromString("33333333-3333-3333-3333-333333333333")

        val udpConnection: UDPConnectionFixed = mock()
        whenever(udpConnection.getSessionId()).thenReturn(sessionId)

        val capabilityManager = CapabilityManager()
        val manager = GroupsManager(udpConnection, capabilityManager, agentId)

        manager.requestAccountTransactions(groupId)

        val payloadCaptor2 = argumentCaptor<ByteArray>()
        verify(udpConnection).sendPacket(
            eq(MessageIdRegistry.GROUP_ACCOUNT_TRANSACTIONS_REQUEST),
            payloadCaptor2.capture(),
            eq(true),
            eq(false),
            org.mockito.kotlin.anyOrNull()
        )

        val payload = payloadCaptor2.firstValue
        assertEquals(60, payload.size)

        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData header verification: AgentID (0..15), SessionID (16..31), GroupID (32..47)
        assertEquals(agentId, buffer.getUUID())
        assertEquals(sessionId, buffer.getUUID())
        assertEquals(groupId, buffer.getUUID())

        // MoneyData block verification: RequestID (int), IntervalDays (int), CurrentInterval (int)
        assertEquals(0, buffer.int)
        assertEquals(-1, buffer.int)
        assertEquals(0, buffer.int)
    }
}
