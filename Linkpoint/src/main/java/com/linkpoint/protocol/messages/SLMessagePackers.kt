package com.linkpoint.protocol.messages

import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.putUUID
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Canonical, pure functions that produce the **exact bytes** the simulator
 * expects for each Second Life UDP message body.
 *
 * Each builder is transcribed directly from the LL `message_template.msg`
 * (mirrored under `src/test/resources/protocol/message_template.msg`) and
 * cross-checked against Lumiya's `PackPayload` implementation under
 * `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/messages/`.
 *
 * **Layering note.** The bytes returned here are the message body *only* —
 * everything from byte 0 of the AgentData block to the final field. The
 * 6-byte LLUDP header (flags + seq + extra-header byte) and the 1/2/4-byte
 * message-ID prefix are added by [UDPConnectionFixed.sendPacket] /
 * [UDPConnectionFixed.encodeMessageId]. Putting them in this layer would
 * couple every message to the connection; keeping them out makes builders
 * trivially unit-testable against a Lumiya golden (see
 * `WireFormatParityTest`).
 *
 * **Why this file exists.** Three independent IM senders in `IMManager`
 * each open-coded the same `ImprovedInstantMessage` packing logic, and all
 * three carried the same 5-byte tail (a non-existent `EstateBlock` +
 * `MetaData` block) that the simulator silently rejected. Centralising the
 * builder eliminates the class of bug.
 */
object SLMessagePackers {

    private const val IM_NAME_MAX = 255
    private const val IM_VAR2_MAX = 65535

    /**
     * Pack the body of an **ImprovedInstantMessage** (Low-frequency 254).
     *
     * Wire format per `message_template.msg:5613-5644` and Lumiya
     * `messages/ImprovedInstantMessage.java:51-70`:
     *
     * ```
     * AgentData     { AgentID(16 BE) SessionID(16 BE) }
     * MessageBlock  { FromGroup(1)  ToAgentID(16 BE)  ParentEstateID(4 LE)
     *                 RegionID(16 BE)  Position(12 = 3 LE float)
     *                 Offline(1)  Dialog(1)  ID(16 BE)  Timestamp(4 LE)
     *                 FromAgentName(Variable 1: u8 length + bytes incl. NUL)
     *                 Message(Variable 2: u16 LE length + bytes incl. NUL)
     *                 BinaryBucket(Variable 2: u16 LE length + bytes) }
     * ```
     *
     * **There is no trailing `EstateBlock` or `MetaData` block.** The
     * sim parses `BinaryBucket`, then expects either end-of-packet or
     * the appended-ACK trailer. Any extra bytes after `BinaryBucket`
     * are a parse error → packet silently dropped.
     *
     * @param fromAgentName UTF-8 string. Sender's display/legacy name.
     *   Lumiya passes the resolved cache name; the sim is tolerant of
     *   "You" or any short non-empty string, but **must not** be empty
     *   (1-byte length prefix + NUL = 2 bytes minimum).
     * @param message UTF-8 string. May be empty (e.g., typing
     *   indicators), in which case Variable 2 encodes as
     *   `[0x01 0x00 0x00]` (length 1, just the NUL).
     * @param binaryBucket payload for dialog-specific data:
     *   - empty for normal IMs (Dialog=0)
     *   - `byteArrayOf(0)` (single zero byte, length 1) for group
     *     session-start (Dialog=15) and group chat (Dialog=17), per
     *     Lumiya `SLAgentCircuit.SendGroupSessionStart` and
     *     `SendGroupInstantMessage`. **Empty buckets are sometimes
     *     rejected by SL on Dialog=15 — use `byteArrayOf(0)`.**
     */
    fun packImprovedInstantMessage(
        identity: AgentIdentity,
        fromGroup: Boolean,
        toAgentId: UUID,
        parentEstateId: Int = 0,
        regionId: UUID = AgentIdentity.ZERO_UUID,
        position: LLVector3 = LLVector3.zero(),
        offline: Int = 0,
        dialog: Int,
        id: UUID,
        timestamp: Int = 0,
        fromAgentName: String,
        message: String,
        binaryBucket: ByteArray = ByteArray(0)
    ): ByteArray {
        val nameBytes = fromAgentName.toByteArray(Charsets.UTF_8)
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        val nameLen = nameBytes.size + 1  // include trailing NUL
        val messageLen = messageBytes.size + 1
        require(nameLen <= IM_NAME_MAX) {
            "FromAgentName Variable 1 length $nameLen exceeds 255"
        }
        require(messageLen <= IM_VAR2_MAX) {
            "Message Variable 2 length $messageLen exceeds 65535"
        }
        require(binaryBucket.size <= IM_VAR2_MAX) {
            "BinaryBucket Variable 2 length ${binaryBucket.size} exceeds 65535"
        }

        // 32 (AgentData) + 1 + 16 + 4 + 16 + 12 + 1 + 1 + 16 + 4 (MessageBlock fixed)
        // + (1 + nameLen) + (2 + messageLen) + (2 + bucketLen)
        val total = 32 + 1 + 16 + 4 + 16 + 12 + 1 + 1 + 16 + 4 +
            (1 + nameLen) + (2 + messageLen) + (2 + binaryBucket.size)
        val buf = ByteBuffer.allocate(total).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData
        buf.putUUID(identity.agentId)
        buf.putUUID(identity.sessionId)

        // MessageBlock
        buf.put(if (fromGroup) 1.toByte() else 0.toByte())
        buf.putUUID(toAgentId)
        buf.putInt(parentEstateId)
        buf.putUUID(regionId)
        buf.putFloat(position.x); buf.putFloat(position.y); buf.putFloat(position.z)
        buf.put(offline.toByte())
        buf.put(dialog.toByte())
        buf.putUUID(id)
        buf.putInt(timestamp)

        // FromAgentName: Variable 1 (u8 length, NUL-terminated)
        buf.put(nameLen.toByte())
        buf.put(nameBytes)
        buf.put(0)

        // Message: Variable 2 (u16 LE length, NUL-terminated)
        buf.putShort(messageLen.toShort())
        buf.put(messageBytes)
        buf.put(0)

        // BinaryBucket: Variable 2 (u16 LE length, raw bytes — no NUL)
        buf.putShort(binaryBucket.size.toShort())
        buf.put(binaryBucket)

        check(buf.position() == total) {
            "ImprovedInstantMessage size mismatch: wrote ${buf.position()} expected $total"
        }
        return buf.array()
    }

    /**
     * Pack the body of a **ChatFromViewer** (Low-frequency 80).
     *
     * Wire format per `message_template.msg:5439-5453` and Lumiya
     * `messages/ChatFromViewer.java:42-51`:
     *
     * ```
     * AgentData  { AgentID(16 BE)  SessionID(16 BE) }
     * ChatData   { Message(Variable 2: u16 LE length + bytes incl. NUL)
     *              Type(1)  Channel(4 LE) }
     * ```
     *
     * Lumiya marks this `isReliable = true` (`SLAgentCircuit.java:1724`)
     * — callers must mirror by passing `reliable = true` to
     * [UDPConnectionFixed.sendPacket].
     */
    fun packChatFromViewer(
        identity: AgentIdentity,
        message: String,
        type: Int,
        channel: Int
    ): ByteArray {
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        val messageLen = messageBytes.size + 1
        require(messageLen <= IM_VAR2_MAX) {
            "ChatFromViewer Message length $messageLen exceeds 65535"
        }

        // 32 (AgentData) + (2 + messageLen) + 1 (Type) + 4 (Channel)
        val total = 32 + 2 + messageLen + 1 + 4
        val buf = ByteBuffer.allocate(total).order(ByteOrder.LITTLE_ENDIAN)

        buf.putUUID(identity.agentId)
        buf.putUUID(identity.sessionId)

        buf.putShort(messageLen.toShort())
        buf.put(messageBytes)
        buf.put(0)

        buf.put(type.toByte())
        buf.putInt(channel)

        check(buf.position() == total) {
            "ChatFromViewer size mismatch: wrote ${buf.position()} expected $total"
        }
        return buf.array()
    }
}
