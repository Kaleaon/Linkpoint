package com.lumiyaviewer.lumiya.slproto.llsd

enum class LLSDNodeType(val tagName: String) {
    llsdRoot("llsd"),
    llsdUndef("undef"),
    llsdBoolean("boolean"),
    llsdInteger("integer"),
    llsdDouble("real"),
    llsdUUID("uuid"),
    llsdString("string"),
    llsdDate("date"),
    llsdURI("uri"),
    llsdBinary("binary"),
    llsdArray("array"),
    llsdMap("map"),
    llsdKey("key");

    companion object {
        private val tagMap: Map<String, LLSDNodeType> = values().associateBy { it.tagName }

        fun byTag(tag: String): LLSDNodeType? = tagMap[tag]
    }
}
