package com.linkpoint.protocol.llsd

object LLSDXmlUtils {
    private const val XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd>"
    private const val XML_FOOTER = "</llsd>"

    fun wrap(value: LLSDValue): String = "$XML_HEADER${value.toXML()}$XML_FOOTER"
}
