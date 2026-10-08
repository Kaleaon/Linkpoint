package com.linkpoint.protocol.messages

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

/**
 * Inbound parity for `ChatFromSimulator` (Low 139, ChatType the third leg
 * of the broken-chat tripod alongside `ImprovedInstantMessage` and
 * `ChatFromViewer`).
 *
 * `ChatFromSimulator` is a server → client message — no viewer in the
 * SL ecosystem packs it outbound — so the reference packer here is
 * built straight from `message_template.msg:2902-2920`. The parser
 * under test is `ChatMessageParsers.parseChatFromSimulator` (called via
 * `MessageParser.parseChatFromSimulator`).
 *
 * Field layout the parser must walk (no AgentData block — there's no
 * "skipped SessionID" gotcha here, but the asymmetric Variable-1 vs
 * Variable-2 length prefixes have historically tripped up third-party
 * SL clients):
 *
 * ```
 * FromName    (Variable 1: u8 len + bytes incl NUL)
 * SourceID    (UUID, 16 BE)
 * OwnerID     (UUID, 16 BE)
 * SourceType  (U8 — 0=System, 1=Agent, 2=Object)
 * ChatType    (U8 — 0=Whisper, 1=Normal, 2=Shout, 6=Debug, 7=Region)
 * Audible     (U8 — 1=Fully, 0=Barely, 255 (-1)=Not)
 * Position    (LLVector3 — 12 bytes, 3 LE floats)
 * Message     (Variable 2: u16 LE len + bytes incl NUL)
 * ```
 */
@RunWith(AndroidJUnit4::class)
class ChatFromSimulatorParserTest {

    private val sourceId = UUID.fromString("a1b2c3d4-0000-4000-8000-000000000001")
    private val ownerId = UUID.fromString("a1b2c3d4-0000-4000-8000-000000000002")

    @Test
    fun `normal chat from a nearby avatar parses every field`() {
        val payload = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "Resident Avatar",
            sourceId = sourceId,
            ownerId = ownerId,
            sourceType = 1,             // Agent
            chatType = 1,               // Normal
            audible = 1,                // Fully
            position = LLVector3(128.5f, 64.25f, 21f),
            message = "Hello world!"
        )
        val parsed = MessageParser.parseChatFromSimulator(payload)
        assertNotNull("ChatFromSimulator must parse non-null", parsed)
        assertEquals("Resident Avatar", parsed!!.fromName)
        assertEquals(sourceId, parsed.sourceId)
        assertEquals(ownerId, parsed.ownerId)
        assertEquals(ChatSourceType.AGENT, parsed.sourceType)
        assertEquals(ChatType.NORMAL, parsed.chatType)
        assertEquals(1, parsed.audible)
        assertEquals(128.5f, parsed.position.x, 0f)
        assertEquals(64.25f, parsed.position.y, 0f)
        assertEquals(21f, parsed.position.z, 0f)
        assertEquals("Hello world!", parsed.message)
    }

    @Test
    fun `shout from an object parses with object source type`() {
        val payload = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "Welcome Sign",
            sourceId = sourceId,
            ownerId = ownerId,
            sourceType = 2,             // Object
            chatType = 2,               // Shout
            audible = 1,
            position = LLVector3(0f, 0f, 0f),
            message = "Welcome to the region"
        )
        val parsed = MessageParser.parseChatFromSimulator(payload)
        assertNotNull(parsed)
        assertEquals(ChatSourceType.OBJECT, parsed!!.sourceType)
        assertEquals(ChatType.SHOUT, parsed.chatType)
        assertEquals("Welcome Sign", parsed.fromName)
        assertEquals("Welcome to the region", parsed.message)
    }

    @Test
    fun `whisper from agent at non-zero position parses`() {
        val payload = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "Whisperer",
            sourceId = sourceId,
            ownerId = ownerId,
            sourceType = 1,
            chatType = 0,               // Whisper
            audible = 0,                // Barely audible
            position = LLVector3(200f, 100f, 50f),
            message = "psst..."
        )
        val parsed = MessageParser.parseChatFromSimulator(payload)
        assertNotNull(parsed)
        assertEquals(ChatType.WHISPER, parsed!!.chatType)
        assertEquals(0, parsed.audible)
        assertEquals("psst...", parsed.message)
    }

    @Test
    fun `system message parses with system source type`() {
        val payload = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "Second Life",
            sourceId = UUID(0L, 0L),
            ownerId = UUID(0L, 0L),
            sourceType = 0,             // System
            chatType = 6,               // Debug/system
            audible = 1,
            position = LLVector3(0f, 0f, 0f),
            message = "You have logged in successfully."
        )
        val parsed = MessageParser.parseChatFromSimulator(payload)
        assertNotNull(parsed)
        assertEquals(ChatSourceType.SYSTEM, parsed!!.sourceType)
        assertEquals(ChatType.DEBUG, parsed.chatType)
        assertEquals(UUID(0L, 0L), parsed.sourceId)
    }

    @Test
    fun `empty message round-trips to empty string`() {
        // An empty message still has a 1-byte length prefix and a NUL
        // terminator on the wire (Variable 2: len=1, content=[0x00]).
        val payload = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "Bot",
            sourceId = sourceId,
            ownerId = ownerId,
            sourceType = 1,
            chatType = 1,
            audible = 1,
            position = LLVector3(0f, 0f, 0f),
            message = ""
        )
        val parsed = MessageParser.parseChatFromSimulator(payload)
        assertNotNull(parsed)
        assertEquals("", parsed!!.message)
    }

    @Test
    fun `non-ASCII UTF-8 message round-trips cleanly`() {
        val msg = "Привет! 日本語 🌍"
        val payload = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "Multi-byte",
            sourceId = sourceId,
            ownerId = ownerId,
            sourceType = 1,
            chatType = 1,
            audible = 1,
            position = LLVector3(0f, 0f, 0f),
            message = msg
        )
        val parsed = MessageParser.parseChatFromSimulator(payload)
        assertNotNull(parsed)
        assertEquals(msg, parsed!!.message)
    }

    @Test
    fun `truncated buffer returns null instead of throwing`() {
        // A packet truncated mid-field should drop, not bubble up to the
        // I/O thread as an unchecked exception.
        val full = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "X",
            sourceId = sourceId,
            ownerId = ownerId,
            sourceType = 1,
            chatType = 1,
            audible = 1,
            position = LLVector3(0f, 0f, 0f),
            message = "hi"
        )
        // Cut off in the middle of Position — parser should fail safely.
        val truncated = full.copyOf(full.size - 14)
        val parsed = MessageParser.parseChatFromSimulator(truncated)
        assertNull("Truncated packet must not throw and must return null", parsed)
    }

    @Test
    fun `Variable-1 length prefix is u8 and Variable-2 is u16-LE`() {
        // Spot-check the byte layout of a small fixed payload by
        // examining the reference output. This catches a future
        // regression where someone "fixes" Variable 1 to be u16 or
        // Variable 2 to be u8.
        val payload = LumiyaReferencePackers.packChatFromSimulatorBody(
            fromName = "ABC",            // Variable 1: 1-byte len = 4 (incl NUL), bytes "ABC\0"
            sourceId = UUID(0L, 0L),
            ownerId = UUID(0L, 0L),
            sourceType = 0,
            chatType = 1,
            audible = 1,
            position = LLVector3(0f, 0f, 0f),
            message = "Z"                // Variable 2: 2-byte LE len = 2 (incl NUL), bytes "Z\0"
        )
        // FromName section: byte 0 = length, then "ABC" + NUL = 5 bytes total
        assertEquals("Variable 1 length prefix must be 1 byte", 4.toByte(), payload[0])
        assertEquals('A'.code.toByte(), payload[1])
        assertEquals('B'.code.toByte(), payload[2])
        assertEquals('C'.code.toByte(), payload[3])
        assertEquals(0.toByte(), payload[4])
        // Message length is at offset 5 + 16 + 16 + 3 + 12 = 52
        // Variable 2 little-endian length = 2, encoded as 0x02 0x00
        assertEquals("Variable 2 length low byte", 2.toByte(), payload[52])
        assertEquals("Variable 2 length high byte", 0.toByte(), payload[53])
        assertEquals('Z'.code.toByte(), payload[54])
        assertEquals(0.toByte(), payload[55])
    }
}
