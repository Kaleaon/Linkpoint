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
    fun `message id extraction matches UDPConnectionFixed decoder`() {
        val text = javaClass.classLoader!!.getResource("fixtures/messages/parser_conformance_fixtures.json")!!.readText()
        val fixtures = JSONArray(text)
        val udpConnection = UDPConnectionFixed()

        val getMessageStartOffset = UDPConnectionFixed::class.java.getDeclaredMethod("getMessageStartOffset", ByteArray::class.java)
            .apply { isAccessible = true }
        val decodeMessageIdSLProtocol = UDPConnectionFixed::class.java
            .getDeclaredMethod("decodeMessageIdSLProtocol", ByteArray::class.java, Int::class.javaPrimitiveType)
            .apply { isAccessible = true }

        for (i in 0 until fixtures.length()) {
            val fixture = fixtures.getJSONObject(i)
            val packet = fixture.getString("rawPacketHex").hexToBytes()

            val startOffset = getMessageStartOffset.invoke(udpConnection, packet) as Int?
            val decodedId = if (startOffset == null) {
                Int.MIN_VALUE
            } else {
                val result = decodeMessageIdSLProtocol.invoke(udpConnection, packet, startOffset) as Pair<*, *>?
                (result?.first as Int?) ?: Int.MIN_VALUE
            }

            assertEquals(
                fixture.getString("name"),
                decodedId,
                MessageParser.extractMessageId(packet)
            )
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
