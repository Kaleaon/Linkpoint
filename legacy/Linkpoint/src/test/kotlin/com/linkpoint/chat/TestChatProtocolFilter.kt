package com.linkpoint.chat

import com.linkpoint.protocol.messages.ChatData
import com.linkpoint.protocol.messages.ChatSourceType
import com.linkpoint.protocol.messages.ChatType
import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID

/**
 * Unit test suite for [ChatProtocolFilter].
 *
 * Verifies that script channel prefixes, UUID strings, and system event codes
 * filter correctly without mutating standard user chat messages.
 */
class TestChatProtocolFilter {

    @Test
    fun `test script channel prefixes are stripped correctly`() {
        assertEquals("Hello world", ChatProtocolFilter.stripChannelPrefix("/0 Hello world"))
        assertEquals("Debug output", ChatProtocolFilter.stripChannelPrefix("/11 Debug output"))
        assertEquals("Script initialized", ChatProtocolFilter.stripChannelPrefix("/0: Script initialized"))
        assertEquals("Status OK", ChatProtocolFilter.stripChannelPrefix("[0] Status OK"))
        assertEquals("System ready", ChatProtocolFilter.stripChannelPrefix("[11]: System ready"))
        assertEquals("Custom channel message", ChatProtocolFilter.stripChannelPrefix("/-12345: Custom channel message"))
        assertEquals("Max channel message", ChatProtocolFilter.stripChannelPrefix("/2147483647 Max channel message"))
    }

    @Test
    fun `test standard user slash commands and math expressions are preserved`() {
        assertEquals("/me waves to everyone", ChatProtocolFilter.stripChannelPrefix("/me waves to everyone"))
        assertEquals("/shout Hello grid", ChatProtocolFilter.stripChannelPrefix("/shout Hello grid"))
        assertEquals("/whisper Secret message", ChatProtocolFilter.stripChannelPrefix("/whisper Secret message"))
        assertEquals("I bought 1/2 slice of pizza for /5 dollars", ChatProtocolFilter.stripChannelPrefix("I bought 1/2 slice of pizza for /5 dollars"))
        assertEquals("Visit https://secondlife.com/0", ChatProtocolFilter.stripChannelPrefix("Visit https://secondlife.com/0"))
    }

    @Test
    fun `test UUID strings are formatted into human readable references`() {
        val input = "Avatar 12345678-1234-1234-1234-123456789abc joined region"
        val expected = "Avatar [UUID: 12345678-1234-1234-1234-123456789abc] joined region"
        assertEquals(expected, ChatProtocolFilter.formatUuidReferences(input))

        val inputNullUuid = "Object 00000000-0000-0000-0000-000000000000 teleported"
        val expectedNullUuid = "Object [UUID: 00000000-0000-0000-0000-000000000000] teleported"
        assertEquals(expectedNullUuid, ChatProtocolFilter.formatUuidReferences(inputNullUuid))
    }

    @Test
    fun `test already formatted UUID references are not double formatted`() {
        val formatted = "Avatar [UUID: 12345678-1234-1234-1234-123456789abc] present"
        assertEquals(formatted, ChatProtocolFilter.formatUuidReferences(formatted))
    }

    @Test
    fun `test malformed UUID strings do not cause crashes or exceptions`() {
        val malformedNonHex = "Invalid 12345678-1234-1234-1234-123456789g00 identifier"
        assertEquals(malformedNonHex, ChatProtocolFilter.formatUuidReferences(malformedNonHex))

        val malformedTooShort = "Short 12345678-1234-1234-1234 identifier"
        assertEquals(malformedTooShort, ChatProtocolFilter.formatUuidReferences(malformedTooShort))
    }

    @Test
    fun `test system event string transformer maps codes to friendly text`() {
        assertEquals("Connected to region", ChatProtocolFilter.transformSystemEvent("SYS_CONNECTED"))
        assertEquals("Disconnected from server", ChatProtocolFilter.transformSystemEvent("SYS_DISCONNECTED"))
        assertEquals("Teleport failed", ChatProtocolFilter.transformSystemEvent("SYS_TELEPORT_FAILED"))
        assertEquals("Teleporting...", ChatProtocolFilter.transformSystemEvent("SYS_TELEPORT_PROGRESS"))
        assertEquals("Region Notification", ChatProtocolFilter.transformSystemEvent("1001"))
        assertEquals("Resource Not Found", ChatProtocolFilter.transformSystemEvent("404"))
        assertEquals("System Event (CUSTOM_EVENT)", ChatProtocolFilter.transformSystemEvent("SYS_CUSTOM_EVENT"))
    }

    @Test
    fun `test end-to-end filter with ChatData for object chat`() {
        val rawData = ChatData(
            fromName = "Script Object",
            sourceId = UUID.randomUUID(),
            ownerId = UUID.randomUUID(),
            sourceType = ChatSourceType.OBJECT,
            chatType = ChatType.NORMAL,
            audible = 1,
            position = LLVector3.zero(),
            message = "/0 Object 12345678-1234-1234-1234-123456789abc loaded"
        )

        val filtered = ChatProtocolFilter.filterChatData(rawData)

        assertNotNull(filtered)
        assertEquals("Object [UUID: 12345678-1234-1234-1234-123456789abc] loaded", filtered.message)
        assertEquals(rawData.fromName, filtered.fromName)
        assertEquals(rawData.sourceType, filtered.sourceType)
    }

    @Test
    fun `test end-to-end filter with ChatData for system message`() {
        val rawData = ChatData(
            fromName = "System",
            sourceId = UUID(0L, 0L),
            ownerId = UUID(0L, 0L),
            sourceType = ChatSourceType.SYSTEM,
            chatType = ChatType.NORMAL,
            audible = 1,
            position = LLVector3.zero(),
            message = "SYS_CONNECTED"
        )

        val filtered = ChatProtocolFilter.filterChatData(rawData)

        assertEquals("Connected to region", filtered.message)
    }

    @Test
    fun `test end-to-end filter preserves standard user chat messages`() {
        val userMsg = "Hello everyone! Check out my new build at http://secondlife.com/0"
        val rawData = ChatData(
            fromName = "Resident User",
            sourceId = UUID.randomUUID(),
            ownerId = UUID.randomUUID(),
            sourceType = ChatSourceType.AGENT,
            chatType = ChatType.NORMAL,
            audible = 1,
            position = LLVector3.zero(),
            message = userMsg
        )

        val filtered = ChatProtocolFilter.filterChatData(rawData)

        assertEquals(userMsg, filtered.message)
    }
}
