package com.linkpoint.protocol.messages

import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

/**
 * Byte-for-byte parity between Linkpoint's [SLMessagePackers] and the
 * Lumiya reference implementations transcribed in [LumiyaReferencePackers].
 *
 * If any of these tests fails, Linkpoint is generating a packet shape
 * that the simulator silently drops — exactly the symptom seen in
 * production where IMs and group chat stopped working without any
 * server-side error.
 *
 * The reference packers are pinned to the decompiled source under
 * `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/messages/`,
 * which itself matches the canonical LL `message_template.msg` shipped
 * at `src/test/resources/protocol/message_template.msg`.
 */
class WireFormatParityTest {

    private val agentId = UUID.fromString("11111111-2222-3333-4444-555555555555")
    private val sessionId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
    private val regionId = UUID.fromString("00000000-0000-0000-0000-000000000000")
    private val targetId = UUID.fromString("12345678-1234-1234-1234-1234567890ab")
    private val groupId  = UUID.fromString("deadbeef-dead-beef-dead-beefdeadbeef")
    private val identity = AgentIdentity(
        agentId = agentId,
        sessionId = sessionId,
        circuitCode = 1234
    )

    // ─────────────────────────────────────────────────────────────────
    // ChatFromViewer (Low 80) — local chat
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun `ChatFromViewer hello world matches Lumiya wire format`() {
        val msg = "hello world"
        val linkpoint = SLMessagePackers.packChatFromViewer(
            identity = identity,
            message = msg,
            type = 1,        // CHAT_TYPE_NORMAL
            channel = 0
        )
        val lumiya = LumiyaReferencePackers.packChatFromViewerBody(
            agentId = agentId,
            sessionId = sessionId,
            message = msg,
            type = 1,
            channel = 0
        )
        assertArrayEquals(
            "ChatFromViewer body diverges from Lumiya — local chat will be dropped",
            lumiya,
            linkpoint
        )
    }

    @Test
    fun `ChatFromViewer empty body still matches`() {
        val linkpoint = SLMessagePackers.packChatFromViewer(identity, "", 0, -2147483648)
        val lumiya = LumiyaReferencePackers.packChatFromViewerBody(
            agentId, sessionId, "", 0, -2147483648
        )
        assertArrayEquals(lumiya, linkpoint)
    }

    @Test
    fun `ChatFromViewer fixed-size invariant`() {
        // 32 (AgentData) + 2 (Variable 2 length) + 1 (NUL) + 1 (Type) + 4 (Channel) = 40
        val bytes = SLMessagePackers.packChatFromViewer(identity, "", 1, 0)
        assertEquals("ChatFromViewer for empty message must be 40 bytes", 40, bytes.size)
    }

    // ─────────────────────────────────────────────────────────────────
    // ImprovedInstantMessage (Low 254) — IMs, typing, group bring-up
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun `1-1 IM body matches Lumiya wire format byte-for-byte`() {
        val message = "Hello, friend!"
        val ts = 1714000000

        val linkpoint = SLMessagePackers.packImprovedInstantMessage(
            identity = identity,
            fromGroup = false,
            toAgentId = targetId,
            dialog = 0,                     // IM_NOTHING_SPECIAL
            id = sessionId,
            timestamp = ts,
            fromAgentName = "You",
            message = message
        )
        val lumiya = LumiyaReferencePackers.packImprovedInstantMessageBody(
            agentId = agentId,
            sessionId = sessionId,
            fromGroup = false,
            toAgentId = targetId,
            parentEstateId = 0,
            regionId = regionId,
            position = LLVector3.zero(),
            offline = 0,
            dialog = 0,
            id = sessionId,
            timestamp = ts,
            fromAgentName = "You",
            message = message,
            binaryBucket = ByteArray(0)
        )
        assertArrayEquals(
            "1:1 IM body diverges from Lumiya — server will silently drop",
            lumiya,
            linkpoint
        )
    }

    @Test
    fun `IM body has no trailing EstateBlock or MetaData fields`() {
        // Regression test for the 5-extra-bytes bug
        // (IMManager.sendP2PInstantMessage previously wrote
        // `payload.putInt(0); payload.put(0)` after BinaryBucket).
        // Empty message + empty name + empty bucket → fixed-size body:
        //   32 (AgentData) + 1 (FromGroup) + 16 (ToAgentID)
        // + 4 (ParentEstateID) + 16 (RegionID) + 12 (Position)
        // + 1 (Offline) + 1 (Dialog) + 16 (ID) + 4 (Timestamp)
        // + 1 (FromAgentName length=1) + 1 (NUL)
        // + 2 (Message length=1) + 1 (NUL)
        // + 2 (BinaryBucket length=0)
        // = 110
        val bytes = SLMessagePackers.packImprovedInstantMessage(
            identity = identity,
            fromGroup = false,
            toAgentId = targetId,
            dialog = 0,
            id = sessionId,
            timestamp = 0,
            fromAgentName = "",
            message = ""
        )
        assertEquals(
            "ImprovedInstantMessage with empty fields must be exactly 110 bytes; " +
                "any larger value indicates the malformed-trailing-bytes bug returned",
            110,
            bytes.size
        )
    }

    @Test
    fun `typing-start IM matches Lumiya wire format`() {
        val ts = 1714000000
        val linkpoint = SLMessagePackers.packImprovedInstantMessage(
            identity = identity,
            fromGroup = false,
            toAgentId = targetId,
            dialog = 41,            // IM_TYPING_START
            id = sessionId,
            timestamp = ts,
            fromAgentName = "",
            message = ""
        )
        val lumiya = LumiyaReferencePackers.packImprovedInstantMessageBody(
            agentId = agentId,
            sessionId = sessionId,
            fromGroup = false,
            toAgentId = targetId,
            parentEstateId = 0,
            regionId = regionId,
            position = LLVector3.zero(),
            offline = 0,
            dialog = 41,
            id = sessionId,
            timestamp = ts,
            fromAgentName = "",
            message = "",
            binaryBucket = ByteArray(0)
        )
        assertArrayEquals(
            "Typing-start IM body diverges from Lumiya — typing indicators will not show",
            lumiya,
            linkpoint
        )
    }

    @Test
    fun `group session-start (Dialog=15) matches Lumiya wire format`() {
        // Lumiya `SLAgentCircuit.SendGroupSessionStart:721-738`:
        //   ToAgentID = ID = groupUUID
        //   FromGroup = false
        //   Dialog = 15
        //   BinaryBucket = new byte[1] (single zero byte)
        val ts = 1714000000
        val linkpoint = SLMessagePackers.packImprovedInstantMessage(
            identity = identity,
            fromGroup = false,
            toAgentId = groupId,
            dialog = 15,
            id = groupId,
            timestamp = ts,
            fromAgentName = "You",
            message = "",
            binaryBucket = byteArrayOf(0)
        )
        val lumiya = LumiyaReferencePackers.packImprovedInstantMessageBody(
            agentId = agentId,
            sessionId = sessionId,
            fromGroup = false,
            toAgentId = groupId,
            parentEstateId = 0,
            regionId = regionId,
            position = LLVector3.zero(),
            offline = 0,
            dialog = 15,
            id = groupId,
            timestamp = ts,
            fromAgentName = "You",
            message = "",
            binaryBucket = byteArrayOf(0)
        )
        assertArrayEquals(
            "Group session-start (Dialog=15) diverges from Lumiya — group chat won't bootstrap",
            lumiya,
            linkpoint
        )
    }

    @Test
    fun `group chat (Dialog=17) matches Lumiya wire format`() {
        // Lumiya `SLAgentCircuit.SendGroupInstantMessage:1671-1696`.
        val ts = 1714000000
        val msg = "Hi everyone!"
        val linkpoint = SLMessagePackers.packImprovedInstantMessage(
            identity = identity,
            fromGroup = false,
            toAgentId = groupId,
            dialog = 17,
            id = groupId,
            timestamp = ts,
            fromAgentName = "You",
            message = msg,
            binaryBucket = byteArrayOf(0)
        )
        val lumiya = LumiyaReferencePackers.packImprovedInstantMessageBody(
            agentId = agentId,
            sessionId = sessionId,
            fromGroup = false,
            toAgentId = groupId,
            parentEstateId = 0,
            regionId = regionId,
            position = LLVector3.zero(),
            offline = 0,
            dialog = 17,
            id = groupId,
            timestamp = ts,
            fromAgentName = "You",
            message = msg,
            binaryBucket = byteArrayOf(0)
        )
        assertArrayEquals(
            "Group chat (Dialog=17) body diverges from Lumiya — group messages will be dropped",
            lumiya,
            linkpoint
        )
    }

    @Test
    fun `IM with non-ASCII UTF-8 round-trips`() {
        val message = "Héllo, wörld! 🌍"
        val ts = 0
        val linkpoint = SLMessagePackers.packImprovedInstantMessage(
            identity = identity,
            fromGroup = false,
            toAgentId = targetId,
            dialog = 0,
            id = sessionId,
            timestamp = ts,
            fromAgentName = "Tëst",
            message = message
        )
        val lumiya = LumiyaReferencePackers.packImprovedInstantMessageBody(
            agentId = agentId,
            sessionId = sessionId,
            fromGroup = false,
            toAgentId = targetId,
            parentEstateId = 0,
            regionId = regionId,
            position = LLVector3.zero(),
            offline = 0,
            dialog = 0,
            id = sessionId,
            timestamp = ts,
            fromAgentName = "Tëst",
            message = message,
            binaryBucket = ByteArray(0)
        )
        assertArrayEquals(lumiya, linkpoint)
    }

    // ─────────────────────────────────────────────────────────────────
    // Header-byte checks (would catch any future endian regression)
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun `AgentID is written big-endian (RFC 4122 network byte order)`() {
        // 11111111-2222-3333-4444-555555555555:
        //   MSB long = 0x1111111122223333
        //   LSB long = 0x4444555555555555
        // First 16 bytes of body should be the AgentID in BE.
        val expected = byteArrayOf(
            0x11, 0x11, 0x11, 0x11, 0x22, 0x22, 0x33, 0x33,
            0x44, 0x44, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55
        )
        val body = SLMessagePackers.packChatFromViewer(identity, "", 1, 0)
        val actualPrefix = body.copyOfRange(0, 16)
        assertArrayEquals(
            "AgentID is not in big-endian wire order — circuit auth will fail",
            expected,
            actualPrefix
        )
    }

    @Test
    fun `Channel is written little-endian`() {
        // ChatFromViewer body layout: [16 AgentID][16 SessionID][2 len][len bytes][1 type][4 channel LE]
        // For empty message (len=1, NUL only): channel starts at byte 32+2+1+1 = 36
        val body = SLMessagePackers.packChatFromViewer(identity, "", 1, 0x12345678)
        // 36, 37, 38, 39 should be 0x78, 0x56, 0x34, 0x12 (LE)
        assertEquals(0x78.toByte(), body[36])
        assertEquals(0x56.toByte(), body[37])
        assertEquals(0x34.toByte(), body[38])
        assertEquals(0x12.toByte(), body[39])
    }
}
