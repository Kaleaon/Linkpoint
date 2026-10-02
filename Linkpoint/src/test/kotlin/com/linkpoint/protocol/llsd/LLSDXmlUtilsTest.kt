package com.linkpoint.protocol.llsd

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

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
}
