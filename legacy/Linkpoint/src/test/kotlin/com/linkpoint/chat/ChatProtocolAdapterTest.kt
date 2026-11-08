package com.linkpoint.chat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ChatProtocolAdapterTest {

    private lateinit var adapter: ChatProtocolAdapter
    private val startedSessions = mutableListOf<UUID>()
    private val dispatchedMessages = mutableListOf<Triple<UUID, UUID, String>>()

    @Before
    fun setUp() {
        startedSessions.clear()
        dispatchedMessages.clear()
        adapter = ChatProtocolAdapter(
            sendSessionStartHandler = { groupId ->
                startedSessions.add(groupId)
                true
            },
            sendGroupMessageHandler = { groupId, sessionUuid, message ->
                dispatchedMessages.add(Triple(groupId, sessionUuid, message))
                true
            }
        )
    }

    @Test
    fun testInitialGroupSessionStateIsIdle() {
        val groupId = UUID.randomUUID()
        assertEquals(GroupSessionState.IDLE, adapter.getSessionState(groupId))
        assertNull(adapter.getSessionUuid(groupId))
        assertTrue(adapter.getPendingMessages(groupId).isEmpty())
    }

    @Test
    fun testFirstGroupMessageStartsNegotiationAndQueuesMessage() {
        val groupId = UUID.randomUUID()
        val result = adapter.sendGroupMessage(groupId, "Hello group")

        assertEquals(GroupMessageResult.QUEUED, result)
        assertEquals(GroupSessionState.NEGOTIATING, adapter.getSessionState(groupId))
        assertEquals(1, startedSessions.size)
        assertEquals(groupId, startedSessions.first())
        assertEquals(listOf("Hello group"), adapter.getPendingMessages(groupId))
        assertTrue(dispatchedMessages.isEmpty())
    }

    @Test
    fun testSubsequentMessagesQueueWhileNegotiating() {
        val groupId = UUID.randomUUID()
        adapter.sendGroupMessage(groupId, "First message")
        adapter.sendGroupMessage(groupId, "Second message")
        adapter.sendGroupMessage(groupId, "Third message")

        assertEquals(GroupSessionState.NEGOTIATING, adapter.getSessionState(groupId))
        assertEquals(1, startedSessions.size) // Only 1 handshake initiated
        assertEquals(listOf("First message", "Second message", "Third message"), adapter.getPendingMessages(groupId))
        assertTrue(dispatchedMessages.isEmpty())
    }

    @Test
    fun testSessionStartReplyFlushesQueuedMessagesAndSetsActive() {
        val groupId = UUID.randomUUID()
        val negotiatedSessionUuid = UUID.randomUUID()

        adapter.sendGroupMessage(groupId, "Msg 1")
        adapter.sendGroupMessage(groupId, "Msg 2")

        adapter.onSessionStartReply(groupId, negotiatedSessionUuid, success = true)

        assertEquals(GroupSessionState.ACTIVE, adapter.getSessionState(groupId))
        assertEquals(negotiatedSessionUuid, adapter.getSessionUuid(groupId))
        assertTrue(adapter.getPendingMessages(groupId).isEmpty())

        assertEquals(2, dispatchedMessages.size)
        assertEquals(Triple(groupId, negotiatedSessionUuid, "Msg 1"), dispatchedMessages[0])
        assertEquals(Triple(groupId, negotiatedSessionUuid, "Msg 2"), dispatchedMessages[1])
    }

    @Test
    fun testSubsequentMessagesSendImmediatelyWhenActive() {
        val groupId = UUID.randomUUID()
        val negotiatedSessionUuid = UUID.randomUUID()

        adapter.sendGroupMessage(groupId, "Initial msg")
        adapter.onSessionStartReply(groupId, negotiatedSessionUuid, success = true)

        val result = adapter.sendGroupMessage(groupId, "Immediate msg")

        assertEquals(GroupMessageResult.SENT, result)
        assertEquals(2, dispatchedMessages.size)
        assertEquals(Triple(groupId, negotiatedSessionUuid, "Immediate msg"), dispatchedMessages[1])
    }

    @Test
    fun testFailedSessionStartReplyResetsToIdleAndClearsQueue() {
        val groupId = UUID.randomUUID()
        val dummySessionUuid = UUID.randomUUID()

        adapter.sendGroupMessage(groupId, "Failed msg")
        adapter.onSessionStartReply(groupId, dummySessionUuid, success = false)

        assertEquals(GroupSessionState.IDLE, adapter.getSessionState(groupId))
        assertNull(adapter.getSessionUuid(groupId))
        assertTrue(adapter.getPendingMessages(groupId).isEmpty())
        assertTrue(dispatchedMessages.isEmpty())
    }

    @Test
    fun testDisconnectionResetsAllSessionsAndClearsRegistry() {
        val group1 = UUID.randomUUID()
        val group2 = UUID.randomUUID()
        val session1 = UUID.randomUUID()

        adapter.sendGroupMessage(group1, "Msg G1")
        adapter.onSessionStartReply(group1, session1, success = true)

        adapter.sendGroupMessage(group2, "Msg G2")

        adapter.onDisconnected()

        assertEquals(GroupSessionState.IDLE, adapter.getSessionState(group1))
        assertEquals(GroupSessionState.IDLE, adapter.getSessionState(group2))
        assertNull(adapter.getSessionUuid(group1))
        assertNull(adapter.getSessionUuid(group2))
    }

    @Test
    fun testRegisterActiveSessionSetsActiveStateAndUuid() {
        val groupId = UUID.randomUUID()
        val sessionUuid = UUID.randomUUID()

        adapter.registerActiveSession(groupId, sessionUuid)

        assertEquals(GroupSessionState.ACTIVE, adapter.getSessionState(groupId))
        assertEquals(sessionUuid, adapter.getSessionUuid(groupId))

        val result = adapter.sendGroupMessage(groupId, "Hello direct active")
        assertEquals(GroupMessageResult.SENT, result)
        assertEquals(1, dispatchedMessages.size)
        assertEquals(Triple(groupId, sessionUuid, "Hello direct active"), dispatchedMessages.first())
    }

    @Test
    fun testGetSessionRecordSnapshotCopy() {
        val groupId = UUID.randomUUID()
        adapter.sendGroupMessage(groupId, "Snapshot msg")

        val record = adapter.getSessionRecord(groupId)
        assertTrue(record != null)
        assertEquals(GroupSessionState.NEGOTIATING, record?.state)
        assertEquals(listOf("Snapshot msg"), record?.pendingMessages)
    }

    @Test
    fun testFailedSessionStartHandlerResetsStateToIdle() {
        val failingAdapter = ChatProtocolAdapter(
            sendSessionStartHandler = { false },
            sendGroupMessageHandler = { _, _, _ -> true }
        )
        val groupId = UUID.randomUUID()
        val result = failingAdapter.sendGroupMessage(groupId, "Fail start")

        assertEquals(GroupMessageResult.FAILED, result)
        assertEquals(GroupSessionState.IDLE, failingAdapter.getSessionState(groupId))
        assertTrue(failingAdapter.getPendingMessages(groupId).isEmpty())
    }
}
