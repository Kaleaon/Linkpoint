package com.linkpoint.protocol.llsd

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

@RunWith(RobolectricTestRunner::class)
class LLSDXmlUtilsTest {

    @Test
    fun `wrapToBytes matches UTF-8 byte array of wrap`() {
        val map = LLSDMap().apply {
            this["key1"] = LLSDString("value1")
            this["key2"] = LLSDInteger(42)
            this["key3"] = LLSDBoolean(true)
        }

        val xmlString = LLSDXmlUtils.wrap(map)
        val xmlBytes = LLSDXmlUtils.wrapToBytes(map)

        assertArrayEquals(xmlString.toByteArray(Charsets.UTF_8), xmlBytes)
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd><map><key>key1</key><string>value1</string><key>key2</key><integer>42</integer><key>key3</key><boolean>true</boolean></map></llsd>", xmlString)
    }

    @Test
    fun `wrapToBytes handles arrays correctly`() {
        val array = LLSDArray().apply {
            add(LLSDString("folder-1"))
            add(LLSDString("folder-2"))
        }

        val expectedBytes = LLSDXmlUtils.wrap(array).toByteArray(Charsets.UTF_8)
        val actualBytes = LLSDXmlUtils.wrapToBytes(array)

        assertArrayEquals(expectedBytes, actualBytes)
    }

    @Test
    fun `writeToStream streams output matching wrapToBytes`() {
        val array = LLSDArray().apply {
            add(LLSDString("item1"))
            add(LLSDInteger(100))
        }

        val baos = ByteArrayOutputStream()
        LLSDXmlUtils.writeToStream(array, baos)

        assertArrayEquals(LLSDXmlUtils.wrapToBytes(array), baos.toByteArray())
    }

    @Test
    fun `parse from InputStream and ByteArray reads XML LLSD correctly`() {
        val xmlBytes = "<?xml version=\"1.0\"?><llsd><map><key>count</key><integer>123</integer></map></llsd>".toByteArray(Charsets.UTF_8)

        val parsedFromStream = LLSDXmlUtils.parse(ByteArrayInputStream(xmlBytes))
        val parsedFromBytes = LLSDXmlUtils.parse(xmlBytes)

        val expected = LLSDMap().apply { this["count"] = LLSDInteger(123) }

        assertEquals(expected, parsedFromStream)
        assertEquals(expected, parsedFromBytes)
    }
}
