package com.linkpoint.utils

/**
 * Raw packet event containing unformatted packet metadata and payload reference.
 * Posted from network I/O threads to the background coroutine channel in [SessionLogRecorder]
 * to eliminate string formatting and SharedPreferences reads on network threads.
 */
data class RawPacketEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val type: SessionLogRecorder.EntryType,
    val tag: String = "UDP",
    val messageId: Int? = null,
    val messageName: String? = null,
    val sequenceNumber: Int? = null,
    val reliable: Boolean? = null,
    val handlerFound: Boolean? = null,
    val customMessage: String? = null,
    val payload: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawPacketEvent

        if (timestamp != other.timestamp) return false
        if (type != other.type) return false
        if (tag != other.tag) return false
        if (messageId != other.messageId) return false
        if (messageName != other.messageName) return false
        if (sequenceNumber != other.sequenceNumber) return false
        if (reliable != other.reliable) return false
        if (handlerFound != other.handlerFound) return false
        if (customMessage != other.customMessage) return false
        if (payload != null) {
            if (other.payload == null) return false
            if (!payload.contentEquals(other.payload)) return false
        } else if (other.payload != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + (messageId ?: 0)
        result = 31 * result + (messageName?.hashCode() ?: 0)
        result = 31 * result + (sequenceNumber ?: 0)
        result = 31 * result + (reliable?.hashCode() ?: 0)
        result = 31 * result + (handlerFound?.hashCode() ?: 0)
        result = 31 * result + (customMessage?.hashCode() ?: 0)
        result = 31 * result + (payload?.contentHashCode() ?: 0)
        return result
    }
}
