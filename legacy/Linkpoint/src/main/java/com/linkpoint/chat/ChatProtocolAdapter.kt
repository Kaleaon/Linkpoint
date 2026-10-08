package com.linkpoint.chat

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * State of a group chat session negotiation.
 */
enum class GroupSessionState {
    IDLE,
    NEGOTIATING,
    ACTIVE
}

/**
 * In-memory record mapping a group ID to its negotiated session UUID, state, and queued messages.
 */
data class GroupSessionRecord(
    val groupId: UUID,
    var sessionUuid: UUID? = null,
    var state: GroupSessionState = GroupSessionState.IDLE,
    val pendingMessages: MutableList<String> = mutableListOf()
)

/**
 * Result status of dispatching or queueing a group message.
 */
enum class GroupMessageResult {
    SENT,
    QUEUED,
    FAILED
}

/**
 * Integrated ChatProtocolAdapter managing group chat session negotiation state machines.
 *
 * Ensures all outgoing group messages verify or establish a valid negotiated session UUID
 * before dispatching over UDP or HTTP transport, preventing unnegotiated packets from being
 * dropped by simulators.
 */
class ChatProtocolAdapter(
    private val sendSessionStartHandler: ((groupId: UUID) -> Boolean)? = null,
    private val sendGroupMessageHandler: ((groupId: UUID, sessionUuid: UUID, message: String) -> Boolean)? = null
) {
    private val registry = ConcurrentHashMap<UUID, GroupSessionRecord>()

    /**
     * Gets the current session state for a group ID.
     */
    fun getSessionState(groupId: UUID): GroupSessionState {
        return registry[groupId]?.state ?: GroupSessionState.IDLE
    }

    /**
     * Gets the negotiated session UUID for an active group session, if available.
     */
    fun getSessionUuid(groupId: UUID): UUID? {
        val record = registry[groupId]
        return if (record?.state == GroupSessionState.ACTIVE) record.sessionUuid else null
    }

    /**
     * Gets the queued pending messages for a group ID.
     */
    fun getPendingMessages(groupId: UUID): List<String> {
        val record = registry[groupId] ?: return emptyList()
        synchronized(record) {
            return record.pendingMessages.toList()
        }
    }

    /**
     * Returns a thread-safe snapshot copy of the [GroupSessionRecord] for a group ID.
     */
    fun getSessionRecord(groupId: UUID): GroupSessionRecord? {
        val record = registry[groupId] ?: return null
        synchronized(record) {
            return record.copy(pendingMessages = record.pendingMessages.toMutableList())
        }
    }

    /**
     * Sends a group chat message or queues it if session negotiation is required.
     *
     * - If the session is [GroupSessionState.ACTIVE], dispatches the message immediately.
     * - If [GroupSessionState.NEGOTIATING], queues the message.
     * - If [GroupSessionState.IDLE], transitions state to [GroupSessionState.NEGOTIATING],
     *   queues the message, and triggers session negotiation.
     */
    fun sendGroupMessage(groupId: UUID, message: String): GroupMessageResult {
        val record = registry.computeIfAbsent(groupId) { GroupSessionRecord(groupId = it) }

        synchronized(record) {
            when (record.state) {
                GroupSessionState.ACTIVE -> {
                    val sessionUuid = record.sessionUuid ?: groupId
                    val success = sendGroupMessageHandler?.invoke(groupId, sessionUuid, message) ?: true
                    return if (success) GroupMessageResult.SENT else GroupMessageResult.FAILED
                }
                GroupSessionState.NEGOTIATING -> {
                    record.pendingMessages.add(message)
                    return GroupMessageResult.QUEUED
                }
                GroupSessionState.IDLE -> {
                    record.state = GroupSessionState.NEGOTIATING
                    record.pendingMessages.add(message)
                    val startSuccess = sendSessionStartHandler?.invoke(groupId) ?: true
                    if (!startSuccess) {
                        record.state = GroupSessionState.IDLE
                        record.pendingMessages.remove(message)
                        return GroupMessageResult.FAILED
                    }
                    return GroupMessageResult.QUEUED
                }
            }
        }
    }

    /**
     * Callback invoked when a session start reply is received from the simulator or capability endpoint.
     *
     * Upon confirmation ([success] = true), registers the negotiated session UUID, transitions
     * state to [GroupSessionState.ACTIVE], and flushes all queued pending messages.
     * On failure, resets the session state to [GroupSessionState.IDLE] and clears pending queues.
     */
    fun onSessionStartReply(groupId: UUID, negotiatedSessionUuid: UUID, success: Boolean) {
        val record = registry[groupId] ?: GroupSessionRecord(groupId = groupId).also { registry[groupId] = it }

        val messagesToFlush = mutableListOf<String>()
        val sessionUuidToUse: UUID

        synchronized(record) {
            if (success) {
                record.sessionUuid = negotiatedSessionUuid
                record.state = GroupSessionState.ACTIVE
                messagesToFlush.addAll(record.pendingMessages)
                record.pendingMessages.clear()
                sessionUuidToUse = negotiatedSessionUuid
            } else {
                record.state = GroupSessionState.IDLE
                record.sessionUuid = null
                record.pendingMessages.clear()
                return
            }
        }

        // Flush queued messages outside sync block
        for (msg in messagesToFlush) {
            sendGroupMessageHandler?.invoke(groupId, sessionUuidToUse, msg)
        }
    }

    /**
     * Registers an active group session directly (e.g. from an incoming session invitation or existing session).
     */
    fun registerActiveSession(groupId: UUID, sessionUuid: UUID) {
        val record = registry.computeIfAbsent(groupId) { GroupSessionRecord(groupId = it) }
        synchronized(record) {
            record.sessionUuid = sessionUuid
            record.state = GroupSessionState.ACTIVE
        }
    }

    /**
     * Clears all negotiated group session UUIDs and resets states upon receiving disconnection events.
     */
    fun onDisconnected() {
        registry.forEach { (_, record) ->
            synchronized(record) {
                record.state = GroupSessionState.IDLE
                record.sessionUuid = null
                record.pendingMessages.clear()
            }
        }
        registry.clear()
    }
}
