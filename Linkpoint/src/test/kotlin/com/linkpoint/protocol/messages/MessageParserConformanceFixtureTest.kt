package com.linkpoint.protocol.messages

import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MessageParserConformanceFixtureTest {

    @Test
    fun `message id and payload extraction fixtures conform`() {
        val text = javaClass.classLoader!!.getResource("fixtures/messages/parser_conformance_fixtures.json")!!.readText()
        val fixtures = JSONArray(text)

        for (i in 0 until fixtures.length()) {
            val fixture = fixtures.getJSONObject(i)
            val packet = fixture.getString("rawPacketHex").hexToBytes()
            val expectedId = fixture.getInt("expectedMessageId")
            assertEquals(fixture.getString("name"), expectedId, MessageParser.extractMessageId(packet))

            if (fixture.isNull("expectedPayloadHex")) {
                assertNull(fixture.getString("name"), MessageParser.extractPayload(packet))
            } else {
                val expectedPayload = fixture.getString("expectedPayloadHex").hexToBytes()
                assertEquals(
                    fixture.getString("name"),
                    expectedPayload.toList(),
                    MessageParser.extractPayload(packet)!!.toList()
                )
            }
        }
    }

    @Test
    fun `registry parses chat payload`() {
        val payload = buildList<Byte> {
            add(4); addAll("Test".encodeToByteArray().toList())
            repeat(16) { add(0) }
            repeat(16) { add(1) }
            add(1); add(1); add(2)
            addAll(ByteArray(12).toList())
            add(5); add(0); addAll("hello".encodeToByteArray().toList())
        }.toByteArray()

        val parsed = MessageParserRegistry.parse(MessageIds.CHAT_FROM_SIMULATOR, payload) as ChatData
        assertEquals("Test", parsed.fromName)
        assertEquals("hello", parsed.message)
    }

    private fun String.hexToBytes(): ByteArray {
        val clean = replace(" ", "")
        return ByteArray(clean.length / 2) { idx ->
            clean.substring(idx * 2, idx * 2 + 2).toInt(16).toByte()
        }
    }
}
