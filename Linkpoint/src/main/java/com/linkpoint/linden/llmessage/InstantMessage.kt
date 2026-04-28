package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLUUID

enum class IMType(val value: Int) {
    NOTHING_SPECIAL(0),
    MESSAGEBOX(1),
    GROUP_INVITATION(3),
    INVENTORY_OFFERED(4),
    INVENTORY_ACCEPTED(5),
    INVENTORY_DECLINED(6),
    GROUP_VOTE(7),
    GROUP_MESSAGE_DEPRECATED(8),
    TASK_INVENTORY_OFFERED(9),
    TASK_INVENTORY_ACCEPTED(10),
    TASK_INVENTORY_DECLINED(11),
    NEW_USER_DEFAULT(12),
    SESSION_INVITE(13),
    SESSION_P2P_INVITE(14),
    SESSION_GROUP_START(15),
    SESSION_CONFERENCE_START(16),
    SESSION_SEND(17),
    SESSION_LEAVE(18),
    FROM_TASK(19),
    DO_NOT_DISTURB_AUTO_RESPONSE(20),
    CONSOLE_AND_CHAT_HISTORY(21),
    LURE_USER(22),
    LURE_ACCEPTED(23),
    LURE_DECLINED(24),
    GODLIKE_LURE_USER(25),
    TELEPORT_REQUEST(26),
    GROUP_ELECTION_DEPRECATED(27),
    GOTO_URL(28),
    FROM_TASK_AS_ALERT(31),
    GROUP_NOTICE(32),
    GROUP_NOTICE_INVENTORY_ACCEPTED(33),
    GROUP_NOTICE_INVENTORY_DECLINED(34),
    GROUP_INVITATION_ACCEPT(35),
    GROUP_INVITATION_DECLINE(36),
    GROUP_NOTICE_REQUESTED(37),
    FRIENDSHIP_OFFERED(38),
    FRIENDSHIP_ACCEPTED(39),
    FRIENDSHIP_DECLINED_DEPRECATED(40),
    TYPING_START(41),
    TYPING_STOP(42);

    companion object {
        fun fromValue(v: Int): IMType = entries.firstOrNull { it.value == v } ?: NOTHING_SPECIAL
    }
}

enum class IMOnline(val value: UByte) {
    ONLINE(0u),
    OFFLINE(1u);

    companion object {
        fun fromValue(v: UByte): IMOnline = if (v == 0u.toUByte()) ONLINE else OFFLINE
    }
}

object IMFlags {
    const val NONE: UByte = 0u
    const val FROM_GROUP: UByte = 1u
}

data class InstantMessage(
    val fromId: LLUUID,
    val toId: LLUUID,
    val sessionId: LLUUID,
    val message: String,
    val type: IMType,
    val offline: IMOnline,
    val timestamp: UInt,
    val fromName: String,
    val binaryBucket: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InstantMessage) return false
        return fromId == other.fromId &&
            toId == other.toId &&
            sessionId == other.sessionId &&
            message == other.message &&
            type == other.type &&
            offline == other.offline &&
            timestamp == other.timestamp &&
            fromName == other.fromName &&
            binaryBucket.contentEquals(other.binaryBucket)
    }

    override fun hashCode(): Int {
        var result = fromId.hashCode()
        result = 31 * result + toId.hashCode()
        result = 31 * result + sessionId.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + offline.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + fromName.hashCode()
        result = 31 * result + binaryBucket.contentHashCode()
        return result
    }

    companion object {
        val EMPTY_BINARY_BUCKET: ByteArray = byteArrayOf(0)
        const val NO_TIMESTAMP: UInt = 0u
        var SYSTEM_FROM: String = "Second Life"
        const val INTERACTIVE_SYSTEM_FROM: String = "Interactive System Message"
        const val IM_TTL: Int = 1
    }
}
